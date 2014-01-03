package com.aptana.buildpath.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URI;

import org.junit.Test;

import com.aptana.core.util.FileUtil;

public class BuildPathEntryTest
{

	@Test
	public void testEqualsWithNullArg() throws Exception
	{
		String displayName = "name";
		File tmpFile = FileUtil.createTempFile("for_uri", null);
		URI path = tmpFile.toURI();
		BuildPathEntry entry = new BuildPathEntry(displayName, path);
		assertFalse(entry.equals(null));
	}

	@Test
	public void testEquals() throws Exception
	{
		String displayName = "name";
		File tmpFile = FileUtil.createTempFile("for_uri", null);
		URI path = tmpFile.toURI();
		BuildPathEntry entry = new BuildPathEntry(displayName, path);
		assertTrue(entry.equals(entry));
		assertTrue(new BuildPathEntry(displayName, path).equals(entry));
		assertTrue(entry.equals(new BuildPathEntry(displayName, path)));
	}

	@Test
	public void testGetters() throws Exception
	{
		String displayName = "name";
		File tmpFile = FileUtil.createTempFile("for_uri", null);
		URI path = tmpFile.toURI();
		BuildPathEntry entry = new BuildPathEntry(displayName, path);
		assertEquals(displayName, entry.getDisplayName());
		assertEquals(path, entry.getPath());
	}

	@Test
	public void testIsSelected() throws Exception
	{
		String displayName = "name";
		File tmpFile = FileUtil.createTempFile("for_uri", null);
		URI path = tmpFile.toURI();
		BuildPathEntry entry = new BuildPathEntry(displayName, path);
		assertFalse(entry.isSelected());
		entry.setSelected(true);
		assertTrue(entry.isSelected());
		entry.setSelected(false);
		assertFalse(entry.isSelected());
	}
}
