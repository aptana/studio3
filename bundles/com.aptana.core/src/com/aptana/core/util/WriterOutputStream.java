/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

/**
 * An output stream that channels the writing to a given writer.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class WriterOutputStream extends OutputStream
{
	protected Writer writer;
	protected String charset;
	private byte[] buff;

	/**
	 * Constructs a WriterOutputStream with a writer
	 * 
	 * @param writer
	 */
	public WriterOutputStream(Writer writer)
	{
		this(writer, null);
	}

	/**
	 * Constructs a WriterOutputStream with a writer and a charset.
	 * 
	 * @param writer
	 * @param charset
	 */
	public WriterOutputStream(Writer writer, String charset)
	{
		this.writer = writer;
		this.charset = charset;
		this.buff = new byte[1];
	}

	/**
	 * Closes the stream by closing the underlying writer.
	 */
	@Override
	public void close() throws IOException
	{
		writer.flush();
		writer.close();
		writer = null;
		charset = null;
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.OutputStream#flush()
	 */
	@Override
	public void flush() throws IOException
	{
		writer.flush();
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.OutputStream#write(int)
	 */
	@Override
	public void write(int b) throws IOException
	{
		buff[0] = (byte) b;
		write(buff);
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.OutputStream#write(byte[])
	 */
	@Override
	public void write(byte[] b) throws IOException
	{
		if (charset == null)
		{
			writer.write(new String(b));
		}
		else
		{
			writer.write(new String(b, charset));
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.OutputStream#write(byte[], int, int)
	 */
	@Override
	public void write(byte[] b, int off, int len) throws IOException
	{
		if (charset == null)
		{
			writer.write(new String(b, off, len));
		}
		else
		{
			writer.write(new String(b, off, len, charset));
		}
	}
}
