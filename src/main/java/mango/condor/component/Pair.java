package mango.condor.component;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 李兴
 * @Date 2013年11月21日 上午9:49:41
 * @Description 
 */
public class Pair<T1, T2> {
	public T1 first;
	
	public T2 second;
	
	public Pair () {
		
	}
	
	public Pair (T1 first, T2 second) {
		this.first = first;
		this.second = second;
	}
	
	public static <T1, T2> Pair<T1, T2> makePair (T1 first, T2 second) {
		return new Pair<T1, T2>(first, second);
	}

	public T1 getFirst() {
		return first;
	}

	public void setFirst(T1 first) {
		this.first = first;
	}

	public T2 getSecond() {
		return second;
	}

	public void setSecond(T2 second) {
		this.second = second;
	}

	@Override
	public String toString() {
		return "Pair [first=" + first + ", second=" + second + "]";
	}
}
