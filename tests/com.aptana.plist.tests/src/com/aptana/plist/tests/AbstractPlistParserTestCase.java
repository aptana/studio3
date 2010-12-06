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
package com.aptana.plist.tests;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import junit.framework.TestCase;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;

import com.aptana.plist.PListParserFactory;

public abstract class AbstractPlistParserTestCase extends TestCase
{

	protected File getFileInTestFragment(IPath path) throws URISyntaxException, IOException
	{
		URL url = FileLocator.find(Platform.getBundle("com.aptana.plist.tests"), path, null);
		url = FileLocator.toFileURL(url);
		return new File(url.toURI());
	}

	@SuppressWarnings("unchecked")
	public void testParseExample() throws Exception
	{
		Map<String, Object> result = PListParserFactory.parse(getFileInTestFragment(getExampleFilePath()));

		assertNotNull(result);
		assertEquals(5, result.size());
		assertEquals(123L, ((Long) result.get("my_number")).longValue());
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		cal.set(Calendar.YEAR, 2010);
		cal.set(Calendar.MONTH, Calendar.SEPTEMBER);
		cal.set(Calendar.DAY_OF_MONTH, 23);
		cal.set(Calendar.HOUR_OF_DAY, 14);
		cal.set(Calendar.MINUTE, 15);
		cal.set(Calendar.SECOND, 51);
		cal.set(Calendar.MILLISECOND, 0);
		assertEquals(cal.getTime(), result.get("my_date"));
		assertEquals("Hello world!", result.get("my_string"));
		assertTrue((Boolean) result.get("my_boolean"));
		List<Object> array = (List<Object>) result.get("my_array");
		assertEquals("array value 1", array.get(0));
		Map<String, Object> dict = (Map<String, Object>) array.get(1);
		assertEquals(1, dict.size());
		assertFalse((Boolean) dict.get("my_false_boolean"));
	}

	protected abstract IPath getExampleFilePath();
}
