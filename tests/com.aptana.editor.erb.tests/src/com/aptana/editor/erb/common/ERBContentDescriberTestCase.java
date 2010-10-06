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
package com.aptana.editor.erb.common;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import junit.framework.TestCase;

import org.eclipse.core.runtime.content.ITextContentDescriber;

public abstract class ERBContentDescriberTestCase extends TestCase
{

	private ERBContentDescriber describer;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		describer = createDescriber();
	}

	protected abstract ERBContentDescriber createDescriber();

	@Override
	protected void tearDown() throws Exception
	{
		describer = null;
		super.tearDown();
	}

	public void testDescribeEmptyContent() throws Exception
	{
		Reader contents = new StringReader("");
		assertEquals(ITextContentDescriber.INDETERMINATE, describer.describe(contents, null));
	}

	public void testDescribeWithPrefix() throws Exception
	{
		Reader contents = new StringReader(describer.getPrefix());
		assertEquals(ITextContentDescriber.VALID, describer.describe(contents, null));
	}

	public void testDescribeWithGarbage() throws Exception
	{
		Reader contents = new StringReader("gjfhjdhj");
		assertEquals(ITextContentDescriber.INDETERMINATE, describer.describe(contents, null));
	}

	// TODO Call describe(InputStream, IConentDescription)

	public void testDescribeInputStreamWithEmptyContent() throws Exception
	{
		InputStream stream = new ByteArrayInputStream("".getBytes());
		assertEquals(ITextContentDescriber.INDETERMINATE, describer.describe(stream, null));
	}

	public void testDescribeInputStreamWithPrefix() throws Exception
	{
		InputStream stream = new ByteArrayInputStream(describer.getPrefix().getBytes());
		assertEquals(ITextContentDescriber.VALID, describer.describe(stream, null));
	}

	public void testDescribeInputStreamWithGarbage() throws Exception
	{
		InputStream stream = new ByteArrayInputStream("gjfhjdhj".getBytes());
		assertEquals(ITextContentDescriber.INDETERMINATE, describer.describe(stream, null));
	}
}
