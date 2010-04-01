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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;

/**
 * @author Kevin Lindsey
 */
public class TabledInputStream implements DataInput
{
	/*
	 * Fields
	 */
	private InputStream _inputStream;

	private DataInputStream _internalStream;
	private String _stringTable;
	private int[] _offsets;
	private int _stringTableIndex;
	private int[] _intTable;
	private int _intTableIndex;

	/*
	 * Constructors
	 */

	/**
	 * @param input
	 * @throws IOException
	 */
	public TabledInputStream(InputStream input) throws IOException
	{
		if (input == null)
		{
			throw new IllegalArgumentException(Messages.TabledInputStream_Input_Undefined);
		}

		this._inputStream = input;

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		this.copyInputStream(input, baos);
		baos.close();

		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		this._internalStream = new DataInputStream(bais);

		// check major/minor numbers
		int major = this._internalStream.readByte();
		int minor = this._internalStream.readByte();

		if (major != 1 || minor != 0)
		{
			Object[] messageArgs = new Object[] {};
			String message = MessageFormat.format(Messages.TabledInputStream_Incompatible_Format, messageArgs);
			
			throw new IllegalArgumentException(message);
		}

		// read offset table size
		int offsetCount = this._internalStream.readInt();

		// create offset table
		this._offsets = new int[offsetCount];

		// read table
		for (int i = 0; i < offsetCount; i++)
		{
			int value = this._internalStream.readInt();

			this._offsets[i] = value;
		}

		// read string table size
		int tableByteCount = this._internalStream.readInt();
		byte[] stringData = new byte[tableByteCount];

		// read string data
		this._internalStream.read(stringData);

		// save table
		this._stringTable = new String(stringData, "utf-8"); //$NON-NLS-1$
		
		// read int table size
		int intCount = this._internalStream.readInt();
		
		// create int table
		this._intTable = new int[intCount];
		
		// read table
		for (int i = 0; i < intCount; i++)
		{
			this._intTable[i] = this._internalStream.readInt();
		}
		
		// init indexes
		this._stringTableIndex = 0;
		this._intTableIndex = 0;
	}

	/*
	 * Methods
	 */

	/**
	 * Copy bytes from one stream to another.
	 * 
	 * @param in
	 *            The input stream
	 * @param out
	 *            The output stream
	 * @throws IOException
	 */
	private void copyInputStream(InputStream in, OutputStream out) throws IOException
	{
		byte[] buffer = new byte[1024 * 1024];
		int len = in.read(buffer);

		while (len >= 0)
		{
			out.write(buffer, 0, len);
			len = in.read(buffer);
		}

		// in.close();
		// out.close();
	}

	/**
	 * close
	 * @throws IOException 
	 */
	public void close() throws IOException
	{
		this._internalStream.close();
		this._inputStream.close();
	}

	/*
	 * DataInput Implementation
	 */

	/**
	 * @see java.io.DataInput#readByte()
	 */
	public byte readByte() throws IOException
	{
		return this._internalStream.readByte();
	}

	/**
	 * @see java.io.DataInput#readChar()
	 */
	public char readChar() throws IOException
	{
		return this._internalStream.readChar();
	}

	/**
	 * @see java.io.DataInput#readDouble()
	 */
	public double readDouble() throws IOException
	{
		return this._internalStream.readDouble();
	}

	/**
	 * @see java.io.DataInput#readFloat()
	 */
	public float readFloat() throws IOException
	{
		return this._internalStream.readFloat();
	}

	/**
	 * @see java.io.DataInput#readInt()
	 */
	public int readInt() throws IOException
	{
		int result = 0;
		
		if (this._intTableIndex < this._intTable.length)
		{
			result = this._intTable[this._intTableIndex];
			this._intTableIndex++;
		}
		
		return result;
	}

	/**
	 * @see java.io.DataInput#readUnsignedByte()
	 */
	public int readUnsignedByte() throws IOException
	{
		return this._internalStream.readUnsignedByte();
	}

	/**
	 * @see java.io.DataInput#readUnsignedShort()
	 */
	public int readUnsignedShort() throws IOException
	{
		return this._internalStream.readUnsignedShort();
	}

	/**
	 * @see java.io.DataInput#readLong()
	 */
	public long readLong() throws IOException
	{
		return this._internalStream.readLong();
	}

	/**
	 * @see java.io.DataInput#readShort()
	 */
	public short readShort() throws IOException
	{
		return this._internalStream.readShort();
	}

	/**
	 * @see java.io.DataInput#readBoolean()
	 */
	public boolean readBoolean() throws IOException
	{
		return this._internalStream.readBoolean();
	}

	/**
	 * @see java.io.DataInput#skipBytes(int)
	 */
	public int skipBytes(int n) throws IOException
	{
		return this._internalStream.skipBytes(n);
	}

	/**
	 * @see java.io.DataInput#readFully(byte[])
	 */
	public void readFully(byte[] b) throws IOException
	{
		this._internalStream.readFully(b);
	}

	/**
	 * @see java.io.DataInput#readFully(byte[], int, int)
	 */
	public void readFully(byte[] b, int off, int len) throws IOException
	{
		this._internalStream.readFully(b, off, len);
	}

	/**
	 * @see java.io.DataInput#readLine()
	 * @deprecated
	 */
	public String readLine() throws IOException
	{
		// return this._internalStream.readLine();
		return ""; //$NON-NLS-1$
	}

	/**
	 * @see java.io.DataInput#readUTF()
	 */
	public String readUTF() throws IOException
	{
		int length = this._offsets.length;
		String result = null;

		if (this._stringTableIndex < length)
		{
			// get starting offset
			int start = this._offsets[this._stringTableIndex];

			// advance index
			this._stringTableIndex++;

			// get ending offset
			int end = (this._stringTableIndex < length) ? this._offsets[this._stringTableIndex] : this._stringTable.length();

			result = this._stringTable.substring(start, end);
		}

		return result;
	}
}
