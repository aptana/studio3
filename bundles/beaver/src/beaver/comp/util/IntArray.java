/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * This file is part of Beaver Parser Generator.                       *
 * Copyright (C) 2003,2004 Alexander Demenchuk <alder@softanvil.com>.  *
 * All rights reserved.                                                *
 * See the file "LICENSE" for the terms and conditions for copying,    *
 * distribution and modification of Beaver.                            *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package beaver.comp.util;

/**
 *
 */
public class IntArray
{
	private int[] data;
	private int size;
	
	public IntArray(int capacity)
	{
		data = new int[capacity];
	}
	
	public IntArray()
	{
		this(16);
	}
	
	public void add(int value)
	{
		if (size == data.length)
		{
			int[] tmp = new int[size * 2];
			System.arraycopy(data, 0, tmp, 0, size);
			data = tmp;
		}
		data[size++] = value;
	}
	
	public void compact()
	{
		if (size < data.length)
		{
			int[] tmp = new int[size];
			System.arraycopy(data, 0, tmp, 0, size);
			data = tmp;
		}
	}

	public int get(int index)
	{
		return data[index];
	}
	
	public int size()
	{
		return size;
	}
}
