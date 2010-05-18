/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * This file is part of Beaver Parser Generator.                       *
 * Copyright (C) 2003,2004 Alexander Demenchuk <alder@softanvil.com>.  *
 * All rights reserved.                                                *
 * See the file "LICENSE" for the terms and conditions for copying,    *
 * distribution and modification of Beaver.                            *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package beaver.comp.io;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import beaver.comp.util.IntArray;

/**
 *  
 */
public class SrcReader extends Reader
{
	public final File file;
	
	protected char[]   txt;
	protected IntArray lines;
	
	private int ptr;

	public SrcReader(File src_file) throws IOException
	{
		super();
		this.file = src_file;
		
		Reader txt_reader = new FileReader(src_file);
		try
		{
			txt = new char[(int) src_file.length()];
			txt_reader.read(txt);
		}
		finally
		{
			txt_reader.close();
		}
		lines = new IntArray(txt.length / 20 + 20);
		
		boolean eol = true, cr = false;
		for (int i = 0; i < txt.length; i++)
		{
			if (eol)
			{
				lines.add(i);
				eol = false;
			}
			switch (txt[i])
			{
				case '\u000B':
				case '\u000C':
				case '\u0085':
				case '\u2028':
				case '\u2029':
				{
					eol = true;
					cr = false;
					break;
				}	
				case '\r':
				{
					if (cr)
						eol = true;
					cr = true;
					break;
				}
				case '\n':
				{
					if (cr)
						cr = false;
					eol = true;
					break;
				}
				default:
				{
					if (cr)
						eol = true;
					cr = false;
				}
			}
		}
	}

	public int read(char[] buf, int off, int len)
	{
		int copy_max = txt.length - ptr;
		if (copy_max <= 0)
			return -1;
		int copy_len = Math.min(copy_max, len);
		if (copy_len > 0)
		{
			System.arraycopy(txt, ptr, buf, off, copy_len);
			ptr += copy_len;
		}
		return copy_len;
	}
	
	public void reset()
	{
		ptr = 0;
	}

	public void close()
	{
	}
	
	public String getLine(int n)
	{
		int line_start = lines.get(n - 1);
		int line_len = (n == lines.size() ? txt.length : lines.get(n)) - line_start;
		return new String(txt, line_start, line_len);
	}
}