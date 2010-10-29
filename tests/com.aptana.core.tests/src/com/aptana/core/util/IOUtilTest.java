/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
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
package com.aptana.core.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;

import com.aptana.core.util.IOUtil;

public class IOUtilTest extends TestCase
{
	private Mockery context = new Mockery()
	{
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	public void testRead() throws Exception
	{
		final byte[] b = new byte[8192];
		final int off = 0;
		final int len = 8192;
		final InputStream stream = context.mock(InputStream.class);
		context.checking(new Expectations()
		{
			{
				oneOf(stream).read(b, off, len);
				will(throwException(new IOException("")));
				oneOf(stream).close();
			}
		});
		IOUtil.read(stream);
		context.assertIsSatisfied();
	}

	public void testReadWithNullInputReturnsNull() throws Exception
	{
		assertNull(IOUtil.read(null));
	}

	public void testReadWithUnicodeCharacters() throws Exception
	{
		String stringwithUnicode = "abc\u5639\u563b";
		InputStream stream = new ByteArrayInputStream(stringwithUnicode.getBytes("UTF-8"));
		assertEquals(stringwithUnicode, IOUtil.read(stream, "UTF-8"));
	}

	public void testReadWithMultipleLinesWithSlashN() throws Exception
	{
		String multilineString = "line one\nline two\nline three";
		InputStream stream = new ByteArrayInputStream(multilineString.getBytes("UTF-8"));
		assertEquals(multilineString, IOUtil.read(stream, "UTF-8"));
	}
	
	public void testReadWithMultipleLinesWithSlashRSlashN() throws Exception
	{
		String multilineString = "line one\r\nline two\r\nline three";
		InputStream stream = new ByteArrayInputStream(multilineString.getBytes("UTF-8"));
		assertEquals("line one\nline two\nline three", IOUtil.read(stream, "UTF-8"));
	}
	
	public void testReadWithMultipleLinesWithSlashR() throws Exception
	{
		String multilineString = "line one\rline two\rline three";
		InputStream stream = new ByteArrayInputStream(multilineString.getBytes("UTF-8"));
		assertEquals("line one\nline two\nline three", IOUtil.read(stream, "UTF-8"));
	}
}
