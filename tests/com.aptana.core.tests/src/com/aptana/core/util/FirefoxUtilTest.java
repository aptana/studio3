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
import java.net.URL;

import junit.framework.TestCase;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

import com.aptana.core.CorePlugin;

@SuppressWarnings("nls")
public class FirefoxUtilTest extends TestCase {

	private static final String PROFILES_INI = "[General]\n" + "StartWithLastProfile=1\n" + "\n" + "[Profile0]\n"
			+ "Name=default\n" + "IsRelative=1\n" + "Path=Profiles/0sw283qs.default\n" + "\n" + "[Profile1]\n"
			+ "Name=additional\n" + "IsRelative=0\n" + "Path=/tmp/Profiles/0sw283qs.additional\n" + "";

	private static final String INSTALL_RDF = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
			+ "<RDF xmlns=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:em=\"http://www.mozilla.org/2004/em-rdf#\">\n"
			+ "<Description about=\"urn:mozilla:install-manifest\">\n" + "<em:id>mytest.ext</em:id>\n"
			+ "<em:version>1.7.3</em:version>\n" + "<em:type>2</em:type>\n" + "</Description>\n" + "</RDF>\n" + "";

	public void testReadProfiles() throws IOException {
		File dir = File.createTempFile(getClass().getSimpleName(), "temp");
		try {
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
		finally {
			FileUtil.deleteRecursively(dir);
		}

	}

	public void testFindDefaultProfileLocation() throws IOException {
		File profileDir = File.createTempFile(getClass().getSimpleName(), "profile");
		try {
			assertTrue(profileDir.delete());
			assertTrue(profileDir.mkdir());
			File dir = File.createTempFile(getClass().getSimpleName(), "temp");
			assertTrue(dir.delete());
			assertTrue(dir.mkdir());
			File file = new File(dir, "profiles.ini");
			assertTrue(file.createNewFile());
			OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(file));
			w.write(PROFILES_INI + "[Profile2]\nName=real\nIsRelative=0\nPath=" + profileDir.getAbsolutePath() + "\n");
			w.close();

			assertNotNull(FirefoxUtil.findDefaultProfileLocation(new String[] { dir.getAbsolutePath() }));
		}
		finally {
			FileUtil.deleteRecursively(profileDir);
		}

	}

	public void testFindDefaultProfileLocationNoProfiles() throws IOException {
		File dir = File.createTempFile(getClass().getSimpleName(), "temp");
		try {
			assertTrue(dir.delete());
			assertTrue(dir.mkdir());
			File file = new File(dir, "profiles.ini");
			assertTrue(file.createNewFile());
			OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(file));
			w.write("\n");
			w.close();

			assertNull(FirefoxUtil.findDefaultProfileLocation(new String[] { dir.getAbsolutePath() }));
		}
		finally {
			FileUtil.deleteRecursively(dir);
		}

	}

	public void testGetExtensionVersion() throws IOException {
		File profileDir = File.createTempFile(getClass().getSimpleName(), "profile");
		try {
			assertTrue(profileDir.delete());
			assertTrue(profileDir.mkdir());
			File file = new File(profileDir, "extensions");
			assertTrue(file.mkdir());
			file = new File(file, "mytest.ext");
			assertTrue(file.mkdir());
			file = new File(file, "install.rdf");
			OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(file));
			w.write(INSTALL_RDF);
			w.close();
			assertEquals("1.7.3",
					FirefoxUtil.getExtensionVersion("mytest.ext", Path.fromOSString(profileDir.getAbsolutePath())));
		}
		finally {
			FileUtil.deleteRecursively(profileDir);
		}

	}

	public void testGetExtensionVersionXPI() throws IOException {
		URL url = FileLocator.find(Platform.getBundle("com.aptana.core.tests"), Path.fromPortableString("/resources"),
				null);
		File dir = ResourceUtil.resourcePathToFile(url);
		assertEquals("1.7.0", FirefoxUtil.getExtensionVersion("test", Path.fromOSString(dir.getAbsolutePath())));
	}

	public void testInstallLinkedExtension() throws IOException {
		File dir = File.createTempFile(getClass().getSimpleName(), "temp");
		try {
			assertTrue(dir.delete());
			assertTrue(dir.mkdir());
			URL url = FileLocator.find(Platform.getBundle("com.aptana.core.tests"),
					Path.fromPortableString("/resources/extensions/test.xpi"), null);
			assertTrue(FirefoxUtil.installLinkedExtension(url, "test", dir));
			assertTrue(FirefoxUtil.installLinkedExtension(url, "test", dir));
		}
		finally {
			FileUtil.deleteRecursively(CorePlugin.getDefault().getStateLocation().append("test").toFile());
		}
	}

	public void testInstallExtensionNoFile() throws IOException {
		File dir = File.createTempFile(getClass().getSimpleName(), "temp");
		try {
			assertTrue(dir.delete());
			assertTrue(dir.mkdir());
			URL url = FileLocator.find(Platform.getBundle("com.aptana.core.tests"),
					Path.fromPortableString("/resources/extensions/test.xpi"), null);
			assertFalse(FirefoxUtil.installLinkedExtension(new URL(url, "test1.xpi"), "test", dir));
		}
		finally {
			FileUtil.deleteRecursively(CorePlugin.getDefault().getStateLocation().append("test").toFile());
		}
	}

	public void testInstallExtensionExistingFile() throws IOException {
		File dir = File.createTempFile(getClass().getSimpleName(), "temp");
		try {
			assertTrue(dir.delete());
			assertTrue(dir.mkdir());
			URL url = FileLocator.find(Platform.getBundle("com.aptana.core.tests"),
					Path.fromPortableString("/resources/extensions/test.xpi"), null);
			assertTrue(new File(dir, "test").createNewFile());
			assertTrue(FirefoxUtil.installExtension(url, "test", dir));
		}
		finally {
			FileUtil.deleteRecursively(dir);
		}
	}

	public void testInstallExtensionCorruptedUnzip() throws IOException {
		File dir = File.createTempFile(getClass().getSimpleName(), "temp");
		try {
			assertTrue(dir.delete());
			assertTrue(dir.mkdir());
			URL url = FileLocator.find(Platform.getBundle("com.aptana.core.tests"),
					Path.fromPortableString("/resources/extensions/test_corrupted.xpi"), null);
			FirefoxUtil.installExtension(url, "test", dir);
		}
		finally {
			FileUtil.deleteRecursively(dir);
		}
	}

}
