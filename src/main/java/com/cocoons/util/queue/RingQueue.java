package com.cocoons.util.queue;

/*
abstract class RingQueueLPad {
	protected long p1, p2, p3, p4, p5, p6, p7;
}

abstract class RingQueueFields<T> extends RingQueueLPad {
	private static final int BUFFER_PAD;
	private static final long REF_ARRAY_BASE;
	private static final int REF_ELEMENT_SHIFT;
	private static final Unsafe UNSAFE;

	static {
		UNSAFE = UnsafeUtil.getUnsafe();

		final int scale = UNSAFE.arrayIndexScale(Object[].class);
		if (4 == scale) {
			REF_ELEMENT_SHIFT = 2;
		} else if (8 == scale) {
			REF_ELEMENT_SHIFT = 3;
		} else {
			throw new IllegalStateException("unknown pointer size");
		}

		// TODO ... 斟酌下这里
		BUFFER_PAD = 128 / scale;

		// Including the buffer pad in the array base offset
		REF_ARRAY_BASE = UNSAFE.arrayBaseOffset(Object[].class)
				+ (BUFFER_PAD << REF_ELEMENT_SHIFT);
	}

	protected int bufferSizeMask;
	protected final Object[] entitys;
	protected int bufferSize;

	protected Sequence readSlotCursor;
	protected Sequence readableCursor;
	protected Sequence writeSlotCursor;
	protected Sequence writableCursor;

	protected RingQueueFields(final long initValue, int size) {
		if (size < 1 || Integer.bitCount(size) != 1) {
			throw new IllegalArgumentException(
					"RingQueue need a size of power of 2");
		}

		bufferSize = size;
		readSlotCursor = new Sequence(initValue);
		readableCursor = new Sequence(initValue);
		writeSlotCursor = new Sequence(initValue - size);
		writableCursor = new Sequence(initValue);
		entitys = new Object[bufferSize + 2 * BUFFER_PAD];
		bufferSizeMask = bufferSize - 1;
	}

	protected final T elementAt(long index) {
		return (T) UNSAFE.getObjectVolatile(entitys, REF_ARRAY_BASE
				+ ((index & bufferSizeMask) << REF_ELEMENT_SHIFT));
	}

	protected final void setElement(long index, T t) {
		UNSAFE.putObjectVolatile(entitys, REF_ARRAY_BASE
				+ ((index & bufferSizeMask) << REF_ELEMENT_SHIFT), t);
	}
}

*//**
 * @author qinguofeng
 *//*
// TODO ... unfinished
public class RingQueue<T> extends RingQueueFields<T> implements Queue<T> {
	private static final long INIT_CURSOR_VALUE = -1L;
	protected long p1, p2, p3, p4, p5, p6, p7;

	public RingQueue(int size) {
		super(INIT_CURSOR_VALUE, size);
	}

	@Override
	public int size() {
		return (int) (writeSlotCursor.get() - readSlotCursor.get() + bufferSize);
	}

	@Override
	public boolean isEmpty() {
		return size() <= 0;
	}

	@Override
	public boolean contains(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterator<T> iterator() {
		// TODO Auto-generated method stub
		return null;
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
	public boolean remove(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean add(T e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean offer(T e) {
		for (;;) {
			long currentWrite = writeSlotCursor.get();
			long nextWrite = currentWrite + 1;

			if (nextWrite + bufferSize < 0) {
				System.out.println("WWWWWWWWWTTTTTTTTFFFFFFFFF...");
			}

			long currentWritable = writableCursor.get();
			// can write
			if (nextWrite <= currentWritable) {
				if (writeSlotCursor.compareAndSet(currentWrite, nextWrite)) {
					setElement(nextWrite, e);
					readableCursor.incrementAndGet();
					return true;
				}
			} else {
				break;
			}
		}

		return false;
	}

	@Override
	public T remove() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T poll() {
		for (;;) {
			long currentRead = readSlotCursor.get();
			long nextRead = currentRead + 1;
			
			if (nextRead < 0) {
				System.out.println("WWWWWWWWWTTTTTTTTFFFFFFFFF...");
			}

			long currentReadable = readableCursor.get();
			// can read
			if (nextRead <= currentReadable) {
				if (readSlotCursor.compareAndSet(currentRead, nextRead)) {
					T t = elementAt(nextRead);
					writableCursor.incrementAndGet();
					return t;
				}
			} else {
				// System.out.println(nextRead + "-" + currentWrite);
				break;
			}
		}
		return null;
	}

	@Override
	public T element() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T peek() {
		// TODO Auto-generated method stub
		return null;
	}

	// TODO DEBUG METHOD...
	public long getWriteCursor() {
		return writeSlotCursor.get();
	}

	public long getReadCursor() {
		return readSlotCursor.get();
	}

	public static void testAdd(Queue<String> queue, int thread, int count) {
		for (int i = 0; i < count;) {
			if (queue.offer("Test-" + thread + "-" + i)) {
				i++;
			}
		}
	}

	// TODO DEBUG METHOD...

	public static void main(String[] args) {
		final RingQueue<String> queue = new RingQueue<String>(4);
//		final LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<String>();
		final Set<String> contains = new HashSet<String>();
		final int COUNT = 10000000;
		long start = System.currentTimeMillis();
		Thread t1 = new Thread() {
			@Override
			public void run() {
				for (int i = 0; i < COUNT;) {
					if (queue.offer("Test-" + i)) {
						// System.out.println("Test-" + i + ":" +
						// queue.getWriteCursor());
						i++;
					}

//					LockSupport.parkNanos(1000L);
				}
				// System.out.println("end put:" + queue.getWriteCursor());
			}
		};
		
		final AtomicLong size = new AtomicLong(0);
		Thread t2 = new Thread() {
			@Override
			public void run() {
				for (;;) {
					String str = queue.poll();
					if (str != null && str.length() > 0) {
//						System.out.println(str + ":" + queue.getReadCursor());
						size.incrementAndGet();
						if (contains.contains(str)) {
							System.out.println("WTF..." + str);
						}
						contains.add(str);
					}
					if (size.get() >= COUNT) {
						// System.out.println("end get");
						break;
					}
				}
			}
		};
		Thread t3 = new Thread() {
			@Override
			public void run() {
				for (;;) {
					String str = queue.poll();
					if (str != null && str.length() > 0) {
//						System.out.println(str + ":" + queue.getReadCursor());
						size.incrementAndGet();
						if (contains.contains(str)) {
							System.out.println("WTF..." + str);
						}
						contains.add(str);
					}
					if (size.get() >= COUNT) {
						// System.out.println("end get");
						break;
					}
				}
			}
		};
		t1.start();
		t2.start();
		t3.start();

		try {
			t1.join();
			t2.join();
			t3.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		System.out.println("total: " + (end - start));

		
		 * Thread t[] = new Thread[10]; for (int i = 0; i<10;i++) { final int
		 * index = i; t[i] = new Thread() {
		 * 
		 * @Override public void run() { RingQueue.testAdd(queue, index, 100); }
		 * }; t[i].start(); }
		 * 
		 * for (int i = 0; i<10;i++) { try { t[i].join(); } catch
		 * (InterruptedException e) { e.printStackTrace(); } }
		 * 
		 * System.out.println(queue.getWriteCursor());
		 
	}
}
*/
