/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.plist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.junit.Test;

public abstract class AbstractPlistParserTestCase
{

	protected File getFileInTestFragment(IPath path) throws URISyntaxException, IOException
	{
		URL url = FileLocator.find(Platform.getBundle("com.aptana.core.tests"), path, null);
		url = FileLocator.toFileURL(url);
		return new File(url.toURI());
	}

	@SuppressWarnings("unchecked")
	@Test
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
