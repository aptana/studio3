package com.aptana.debug.core;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;

import com.aptana.core.util.ArrayUtil;

public class FilterProxyInputStream extends InputStream
{

	private static final char CR = '\n';
	private static final int BACKSPACE = 8;

	private IProcessOutputFilter processOutputFilter;
	private ByteArrayInputStream lineBuffer;
	private final BufferedReader reader;
	private Charset charset;

	public FilterProxyInputStream(InputStream in, String encoding, IProcessOutputFilter processOutputFilter)
	{
		super();
		this.processOutputFilter = processOutputFilter;
		charset = Charset.defaultCharset();
		try
		{
			charset = Charset.forName(encoding);
		}
		catch (IllegalCharsetNameException e)
		{
		}
		this.reader = new BufferedReader(new InputStreamReader(in, charset));
	}

	public void setProcessOutputFilter(IProcessOutputFilter processOutputFilter)
	{
		this.processOutputFilter = processOutputFilter;
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.FilterInputStream#read()
	 */
	@Override
	public int read() throws IOException
	{
		fillBuffer();
		int ch = lineBuffer.read();
		while (ch == BACKSPACE)
		{
			ch = lineBuffer.read();
		}
		return ch;
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.InputStream#read(byte[], int, int)
	 */
	@Override
	public int read(byte[] b, int off, int len) throws IOException
	{
		if (b == null)
		{
			throw new NullPointerException();
		}
		else if (off < 0 || len < 0 || len > b.length - off)
		{
			throw new IndexOutOfBoundsException();
		}
		else if (len == 0)
		{
			return 0;
		}

		fillBuffer();
		int c = lineBuffer.read();
		while (c == BACKSPACE)
		{
			c = lineBuffer.read();
		}
		if (c == -1)
		{
			return -1;
		}
		b[off] = (byte) c;

		int i = 1;
		for (; i < len; i++)
		{
			c = lineBuffer.read();
			while (c == BACKSPACE)
			{
				c = lineBuffer.read();
			}
			if (c == -1)
			{
				break;
			}
			b[off + i] = (byte) c;
		}
		return i;
	}

	private void fillBuffer() throws IOException
	{
		if (lineBuffer != null && lineBuffer.available() > 0)
		{
			return;
		}
		String line = null;
		do
		{
			line = reader.readLine();
			if (line == null)
			{
				break;
			}
			if (processOutputFilter != null)
			{
				line = processOutputFilter.filter(line);
			}
		}
		while (line == null);
		lineBuffer = new ByteArrayInputStream(line != null ? (line + CR).getBytes(charset.name()) : ArrayUtil.NO_BYTES);
	}

}