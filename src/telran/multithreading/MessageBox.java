package telran.multithreading;

import java.util.concurrent.*;


public class MessageBox {
	BlockingQueue<String> queue = new MyBlockingQueueImpl<>(1000); //FIXME replace with MyBlockingQueueImpl
	public void put(String message) throws InterruptedException {
		queue.put(message);
	}

	public String get() throws InterruptedException {
		return queue.take();

	}
	public String take() {
	
		return queue.poll();
		
	}

}
