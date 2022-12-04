package telran.multithreading;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.concurrent.*;
import java.util.stream.IntStream;

public class GarageImitatorAppl {
static final long MODEL_TIME = 10000; 
static final long MIN_SERVICE_TIME = 60;
static final long MAX_SERVICE_TIME = 600;
static final int N_WORKERS = 30;
static final int PROB_CAR_MIN = 10;
private static final int  CAPCITY = 15;
static int rejectsCounter = 0;
static int carsCounter = 0;
static BlockingQueue<Car> cars = new MyBlockingQueueImpl<>(CAPCITY); //FIXME replace with MyBlockingQueueImpl
	public static void main(String[] args) throws InterruptedException {
		
		Worker workers[] = new Worker[N_WORKERS];
		startWorkers(workers);
		Instant start = Instant.now();
		for (int i = 0; i < MODEL_TIME; i++) {
			Car car = getCar();
			if (car != null) {
				if (cars.offer(car)) {
					carsCounter++;
				} else {
					rejectsCounter++;
				}
			}
			Thread.sleep(1);
		}
		stopWorkers(workers);
		System.out.printf("modelling time %d hours; cars served %d; cars rejected %d",
				ChronoUnit.MILLIS.between(start, Instant.now()) / 60, carsCounter, rejectsCounter);
		

	}
	private static void stopWorkers(Worker[] workers) {
		Arrays.stream(workers).forEach(w -> {
			w.setRunning(false);
			w.interrupt();
		});
		
	}
	private static Car getCar() {
		Car car = null;
		if (getRandomNumber(0, 100) < PROB_CAR_MIN) {
			car = new Car(getRandomNumber(MIN_SERVICE_TIME, MAX_SERVICE_TIME));
		}
		return car;
	}
	private static long getRandomNumber(long min, long max) {
		
		return ThreadLocalRandom.current().nextLong(min, max);
	}
	private static void startWorkers(Worker[] workers) {
		IntStream.range(0, workers.length).forEach(i -> {
			workers[i] = new Worker(cars);
			workers[i].start();
		});
		
	}

}
