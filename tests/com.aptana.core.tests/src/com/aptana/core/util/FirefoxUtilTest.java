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
