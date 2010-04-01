/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.parsing.io;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kevin Lindsey
 */
public class TabledOutputStream implements DataOutput
{
	private OutputStream _outputStream;

	private ByteArrayOutputStream _buffer;
	private DataOutputStream _internalStream;
	private StringBuffer _strings;
	private List<Integer> _offsets;
	private int _offset;
	private List<Integer> _ints;

	/**
	 * StringTableOutput
	 * 
	 * @param output
	 */
	public TabledOutputStream(OutputStream output)
	{
		if (output == null)
		{
			throw new IllegalArgumentException(Messages.TabledOutputStream_Output_Undefined);
		}
		
		this._outputStream = output;

		this._buffer = new ByteArrayOutputStream();
		this._internalStream = new DataOutputStream(this._buffer);
		this._strings = new StringBuffer();
		this._offsets = new ArrayList<Integer>();
		this._offset = 0;
		this._ints = new ArrayList<Integer>();
	}

	/**
	 * close
	 */
	public void close()
	{
		try
		{
			this._internalStream.close();

			DataOutputStream out = new DataOutputStream(this._outputStream);

			// write out major and minor version numbers
			out.writeByte(1);
			out.writeByte(0);

			// write number of string entries
			out.writeInt(this._offsets.size());

			// write out offsets
			for (int i = 0; i < this._offsets.size(); i++)
			{
				int value = this._offsets.get(i).intValue();
				
				out.writeInt(value);
			}

			// write out string table
			byte[] stringData = this._strings.toString().getBytes("utf-8"); //$NON-NLS-1$

			out.writeInt(stringData.length);
			out.write(stringData);

			// write out number of ints
			out.writeInt(this._ints.size());
			
			// write out ints
			for (int i = 0; i < this._ints.size(); i++)
			{
				int value = this._ints.get(i).intValue();
				
				out.writeInt(value);
			}
			
			// write the rest of the stream
			out.write(this._buffer.toByteArray());
			
			// close streams
			out.close();
		}
		catch (IOException e)
		{
			//e.printStackTrace();
		}
	}

	/*
	 * DataOutput implementation
	 */

	/**
	 * @see java.io.DataOutput#writeDouble(double)
	 */
	public void writeDouble(double v) throws IOException
	{
		this._internalStream.writeDouble(v);
	}

	/**
	 * @see java.io.DataOutput#writeFloat(float)
	 */
	public void writeFloat(float v) throws IOException
	{
		this._internalStream.writeFloat(v);
	}

	/**
	 * @see java.io.DataOutput#write(int)
	 */
	public void write(int b) throws IOException
	{
		this._internalStream.write(b);
	}

	/**
	 * @see java.io.DataOutput#writeByte(int)
	 */
	public void writeByte(int v) throws IOException
	{
		this._internalStream.writeByte(v);
	}

	/**
	 * @see java.io.DataOutput#writeChar(int)
	 */
	public void writeChar(int v) throws IOException
	{
		this._internalStream.writeChar(v);
	}

	/**
	 * @see java.io.DataOutput#writeInt(int)
	 */
	public void writeInt(int v) throws IOException
	{
		this._ints.add(new Integer(v));
	}

	/**
	 * @see java.io.DataOutput#writeShort(int)
	 */
	public void writeShort(int v) throws IOException
	{
		this._internalStream.writeShort(v);
	}

	/**
	 * @see java.io.DataOutput#writeLong(long)
	 */
	public void writeLong(long v) throws IOException
	{
		this._internalStream.writeLong(v);
	}

	/**
	 * @see java.io.DataOutput#writeBoolean(boolean)
	 */
	public void writeBoolean(boolean v) throws IOException
	{
		this._internalStream.writeBoolean(v);
	}

	/**
	 * @see java.io.DataOutput#write(byte[])
	 */
	public void write(byte[] b) throws IOException
	{
		this._internalStream.write(b);
	}

	/**
	 * @see java.io.DataOutput#write(byte[], int, int)
	 */
	public void write(byte[] b, int off, int len) throws IOException
	{
		this._internalStream.write(b, off, len);
	}

	/**
	 * @see java.io.DataOutput#writeBytes(java.lang.String)
	 */
	public void writeBytes(String s) throws IOException
	{
		this._internalStream.writeBytes(s);
	}

	/**
	 * @see java.io.DataOutput#writeChars(java.lang.String)
	 */
	public void writeChars(String s) throws IOException
	{
		this._internalStream.writeChars(s);
	}

	/**
	 * @see java.io.DataOutput#writeUTF(java.lang.String)
	 */
	public void writeUTF(String str) throws IOException
	{
		if (str == null)
		{
			str = ""; //$NON-NLS-1$
		}

		this._strings.append(str);
		this._offsets.add(new Integer(this._offset));

		this._offset += str.length();
	}
}
