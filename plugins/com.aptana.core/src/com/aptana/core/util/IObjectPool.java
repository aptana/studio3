package com.aptana.core.util;

public interface IObjectPool<T>
{

	public abstract T create();

	public abstract boolean validate(T o);

	public abstract void expire(T o);

	public abstract T checkOut();

	public abstract void checkIn(T t);

	public abstract void dispose();

}