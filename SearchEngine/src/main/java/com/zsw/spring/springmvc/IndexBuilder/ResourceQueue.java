package com.zsw.spring.springmvc.IndexBuilder;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ResourceQueue<E> {

	private int MaxSize =5000;
	
	private static final int defaultSize = 100;
	
	private Object[] objs;
	
	//队头
	private int front;
	
	//队尾
	private int rear;
	
	//元素的个数
	private int nItems;
	
	final ReentrantLock lock;
	
	private final Condition notEmpty;
	
	private final Condition notFull;
	
	private int useSize = 0;
	
	public ResourceQueue(){
		this(defaultSize);
		useSize = defaultSize;
		
	}
	public ResourceQueue(int size) {
        if(size < 0)
            throw new IndexOutOfBoundsException();
        size = size > MaxSize ? MaxSize : size;
        useSize = size;
        objs = new Object[size];
        front = 0;
        rear = -1;
        nItems = 0;

        lock = new ReentrantLock(true);
        notEmpty = lock.newCondition();
        notFull =  lock.newCondition();

    }
	/**进队*/
	private void queue(E e) {
        if(rear == useSize - 1)
            rear = -1;
        objs[++rear] = e;
        nItems++;
        notEmpty.signal();
    }

    /**出队*/
    private E dequeue() {
        E e = (E)objs[front++];
        if(front == useSize)
            front = 0;
        nItems--;
        notFull.signal();
        return e;
    }
    
    /**进队 资源池满会将入队线程挂起*/ 
    public void offer(E e) throws InterruptedException {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            while(nItems == objs.length) 
                notFull.await();
            queue(e);
            System.out.println("文件进队，当前池中有 " + nItems + " 份待处理文件" );
        } finally {
            lock.unlock();
        }

    }
    
    /**出队 资源池空会将出队线程挂起*/
    public E poll() throws InterruptedException {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            while(nItems == 0) 
                notEmpty.await();
            E e = dequeue();
            System.out.println("文件出队，当前池中有 " + nItems + " 份待处理文件" );
            return e;
        } finally {
            lock.unlock();
        }
    }
    
    /**是否满*/
    public boolean isFull() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return nItems == MaxSize ? true : false;
        } finally {
            lock.unlock();
        }

    }
    
    /**返回队列中元素个数*/
    public int size() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return this.nItems;
        } finally {
            lock.unlock();
        }

    } 
}
