/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
//copied directly from org.eclipse.jface.internal.text.link.contentassist.HTML2TextReader;

package com.aptana.editor.common.contentassist;

import java.io.IOException;
import java.io.Reader;

/**
 * SingleCharReader
 */
abstract class SingleCharReader extends Reader
{
	/**
	 * @see Reader#read()
	 */
	public abstract int read() throws IOException;

	/**
	 * @see Reader#read(char[],int,int)
	 */
	public int read(char[] cbuf, int off, int len) throws IOException
	{
		int end = off + len;

		for (int i = off; i < end; i++)
		{
			int ch = read();

			if (ch == -1)
			{
				if (i == off)
				{
					return -1;
				}

				return i - off;
			}

			cbuf[i] = (char) ch;
		}

		return len;
	}

	/**
	 * @see Reader#ready()
	 */
	public boolean ready() throws IOException
	{
		return true;
	}

	/**
	 * Returns the readable content as string.
	 * 
	 * @return the readable content as string
	 * @exception IOException
	 *                in case reading fails
	 */
	public String getString() throws IOException
	{
		StringBuffer buf = new StringBuffer();
		int ch;

		while ((ch = read()) != -1)
		{
			buf.append((char) ch);
		}

		return buf.toString();
	}
}
