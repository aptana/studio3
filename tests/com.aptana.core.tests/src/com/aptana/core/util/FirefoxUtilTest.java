/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import junit.framework.TestCase;

public class FirefoxUtilTest extends TestCase {

	private static final String PROFILES_INI = "[General]\n" +
			"StartWithLastProfile=1\n" +
			"\n" + 
			"[Profile0]\n" +
			"Name=default\n" +
			"IsRelative=1\n" +
			"Path=Profiles/0sw283qs.default\n" +
			"\n" +
			"[Profile1]\n" +
			"Name=additional\n" +
			"IsRelative=0\n" +
			"Path=/tmp/Profiles/0sw283qs.additional\n" +
			"";
	
	public void testReadProfiles() throws IOException {
		File dir = File.createTempFile(getClass().getSimpleName(), "temp");
		assertTrue(dir.delete());
		assertTrue(dir.mkdir());
		File file = new File(dir, "profiles.ini");
		assertTrue(file.createNewFile());
		OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(file));
		w.write(PROFILES_INI);
		w.close();

		File[] profiles = FirefoxUtil.readProfiles(dir);
		assertNotNull(profiles);
		assertEquals(2, profiles.length);
		assertEquals(new File(dir, "Profiles/0sw283qs.default"), profiles[0]);
		assertEquals(new File("/tmp/Profiles/0sw283qs.additional"), profiles[1]);
	}
}
