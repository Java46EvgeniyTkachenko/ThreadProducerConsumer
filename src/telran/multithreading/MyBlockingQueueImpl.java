package telran.multithreading;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MyBlockingQueueImpl<E> implements BlockingQueue<E> {
	private Queue<E> queue = new LinkedList<>();
	private int capacity;
	private static final String ILLEGAL_STATE = "Element cannot be added";
	private static final String NO_SUCH_ELEMENT = "Can not get element";
	private Lock monitor = new ReentrantLock();
	private Condition cosumersWaitingCondition = monitor.newCondition();
	private Condition producerWaitingCondition = monitor.newCondition();
	public MyBlockingQueueImpl(int capacity) {
		this.capacity = capacity;
	}
	public MyBlockingQueueImpl() {
		this(Integer.MAX_VALUE);
	}
	@Override
	public E remove() {
		try {
			monitor.lock();
			if (queue.isEmpty())
				throw new NoSuchElementException();
			producerWaitingCondition.signal();
			return queue.remove();
		} finally {
			monitor.unlock();
		}
	}

	@Override
	public E poll() {
		try {
			monitor.lock();
			if (queue.isEmpty()) {
				return null;
			}
			producerWaitingCondition.signal();
			return queue.poll();
		} finally {
			monitor.unlock();
		}
		
	}

	@Override
	public E element() {
		try {
			monitor.lock();
			if (queue.isEmpty()) {
				throw new NoSuchElementException(NO_SUCH_ELEMENT);
			}
			return queue.element();
		} finally {
			monitor.unlock();
		}
	}

	@Override
	public E peek() {
		try {
			monitor.lock();
			if (queue.isEmpty()) {
				return null;
			}
			return queue.element();
		} finally {
			monitor.unlock();
		}
	}

	@Override
	public int size() {
		try {
			monitor.lock();
			return queue.size();
		} finally {
			monitor.unlock();
		}
	}

	@Override
	public boolean isEmpty() {
		try {
			monitor.lock();
			return queue.isEmpty();
		} finally {
			monitor.unlock();
		}
	}

	@Override
	public Iterator<E> iterator() {
		try {
			monitor.lock();
			return queue.iterator();
		} finally {
			monitor.unlock();
		}
		
	}

	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		// TODO Auto-generated method stub
		
		return null;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		try {
			monitor.lock();
			return queue.containsAll(c);
		} finally {
			monitor.unlock();
		}
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		try {
			monitor.lock();
			return queue.addAll(c);
		} finally {
			monitor.unlock();
		}		
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		try {
			monitor.lock();
			return queue.removeAll(c);
		} finally {
			monitor.unlock();
		}
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		try {
			monitor.lock();
			return queue.retainAll(c);
		} finally {
			monitor.unlock();
		}
		
	}

	@Override
	public void clear() {
		try {
			monitor.lock();
			queue.clear();
		} finally {
			monitor.unlock();
		}

	}

	@Override
	public boolean add(E e) {
		Objects.requireNonNull(e);
		try {
			monitor.lock();
			if (capacity == queue.size()) {
				throw new IllegalStateException(ILLEGAL_STATE);
			}
			cosumersWaitingCondition.signal();

			return queue.add(e);
		} finally {
			monitor.unlock();
		}
	}

	@Override
	public boolean offer(E e) {
		Objects.requireNonNull(e);
		try {
			monitor.lock();
			if (capacity == queue.size()) {
				return false;
			}
			queue.add(e);
			cosumersWaitingCondition.signal();
			return true;
		} finally {
			monitor.unlock();
		}
	}

	@Override
	public void put(E e) throws InterruptedException {
		Objects.requireNonNull(e);
		try {
			monitor.lock();
			while (queue.size() == capacity) {
				producerWaitingCondition.await();
			}
			queue.add(e);
			cosumersWaitingCondition.signal();
		} finally {
			monitor.unlock();
		}

	}

	@Override
	public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
		Objects.requireNonNull(e);
		try {
			monitor.lock();
			Date deadline = new Date(System.currentTimeMillis() + unit.convert(timeout, TimeUnit.MILLISECONDS));
			boolean stillWaiting = true;
			while (capacity == queue.size()) {
				if (!stillWaiting)
					return false;
				stillWaiting = producerWaitingCondition.awaitUntil(deadline);
			}
			queue.add(e);
			return true;
		} finally {
			monitor.unlock();
		}
	}

	@Override
	public E take() throws InterruptedException {
		try {
			monitor.lock();
			while (queue.isEmpty()) {
				cosumersWaitingCondition.await();
			}
			producerWaitingCondition.signal();
			return queue.remove();
		} finally {
			monitor.unlock();
		}
	}

	@Override
	public E poll(long timeout, TimeUnit unit) throws InterruptedException {
		try {
			monitor.lock();
			Date deadline = new Date(System.currentTimeMillis() + unit.convert(timeout, TimeUnit.MILLISECONDS));
			boolean stillWaiting = true;
			while (queue.isEmpty()) {
				if (!stillWaiting)
					return null;
				stillWaiting = cosumersWaitingCondition.awaitUntil(deadline);
			}
			producerWaitingCondition.signal();
			return queue.poll();
		} finally {
			monitor.unlock();
		}
	}

	@Override
	public int remainingCapacity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean remove(Object o) {
		Objects.requireNonNull(o);
		try {
			monitor.lock();
			producerWaitingCondition.signal();
			return queue.remove(o);
		} finally {
			monitor.unlock();
		}
	}

	@Override
	public boolean contains(Object o) {
		try {
			monitor.lock();
			return queue.contains(o);
		} finally {
			monitor.unlock();
		}
		
	}

	@Override
	public int drainTo(Collection<? super E> c) {
		//No implement
		return 0;
	}

	@Override
	public int drainTo(Collection<? super E> c, int maxElements) {
		//No implement
		return 0;
	}
}