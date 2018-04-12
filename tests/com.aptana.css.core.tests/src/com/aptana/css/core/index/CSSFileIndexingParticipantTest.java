/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.index;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aptana.core.util.FileUtil;
import com.aptana.core.util.IOUtil;
import com.aptana.css.core.CSSColors;
import com.aptana.index.core.FileStoreBuildContext;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;
import com.aptana.index.core.IndexPlugin;
import com.aptana.index.core.build.BuildContext;

public class CSSFileIndexingParticipantTest
{

	private CSSFileIndexingParticipant indexer;

	@Before
	public void setUp() throws Exception
	{
		indexer = new CSSFileIndexingParticipant();
	}

	@After
	public void tearDown() throws Exception
	{
		indexer = null;
	}

	@Test
	public void testIsColorWithNames() throws Exception
	{
		assertTrue(CSSColors.isColor("aqua"));
		assertTrue(CSSColors.isColor("black"));
		assertTrue(CSSColors.isColor("blue"));
		assertTrue(CSSColors.isColor("fuchsia"));
		assertTrue(CSSColors.isColor("gray"));
		assertTrue(CSSColors.isColor("green"));
		assertTrue(CSSColors.isColor("lime"));
		assertTrue(CSSColors.isColor("maroon"));
		assertTrue(CSSColors.isColor("navy"));
		assertTrue(CSSColors.isColor("olive"));
		assertTrue(CSSColors.isColor("purple"));
		assertTrue(CSSColors.isColor("red"));
		assertTrue(CSSColors.isColor("silver"));
		assertTrue(CSSColors.isColor("teal"));
		assertTrue(CSSColors.isColor("white"));
		assertTrue(CSSColors.isColor("yellow"));

		// Non-standard names don't work
		assertFalse(CSSColors.isColor("grey"));
	}

	@Test
	public void testIsColorWithHexValues() throws Exception
	{
		// various correct hex values
		assertTrue(CSSColors.isColor("#c9c9c9"));
		assertTrue(CSSColors.isColor("#ccc"));
		assertTrue(CSSColors.isColor("#000"));
		assertTrue(CSSColors.isColor("#000000"));

		// empty and just hash
		assertFalse(CSSColors.isColor(""));
		assertFalse(CSSColors.isColor("#"));

		// letter outside hex
		assertFalse(CSSColors.isColor("#g00000"));

		// Wrong lengths with digits
		assertFalse(CSSColors.isColor("#0"));
		assertFalse(CSSColors.isColor("#00"));
		assertFalse(CSSColors.isColor("#0000"));
		assertFalse(CSSColors.isColor("#00000"));
		assertFalse(CSSColors.isColor("#0000000"));

		// wrong lengths with letters
		assertFalse(CSSColors.isColor("#a"));
		assertFalse(CSSColors.isColor("#aa"));
		assertFalse(CSSColors.isColor("#aaaa"));
		assertFalse(CSSColors.isColor("#aaaaa"));
		assertFalse(CSSColors.isColor("#aaaaaaa"));
	}

	@Test
	public void testDetectTaskTagWithUnicodeCharacters() throws Exception
	{
		File tmpDir = null;
		try
		{
			// @formatter:off
			String src = 
			"body {\n" +
			"	/* TODO: Привет */\n" +
			"}\n";
			// @formatter:on

			// Generate some files to index!
			tmpDir = new File(FileUtil.getTempDirectory().toOSString(), "testIndex_" + System.currentTimeMillis());
			tmpDir.mkdirs();

			File coffeeFile = new File(tmpDir, "index_me.css");
			IOUtil.write(new FileOutputStream(coffeeFile), src);
			IFileStore fileStore = EFS.getStore(coffeeFile.toURI());
			BuildContext context = new FileStoreBuildContext(fileStore);

			Index index = getIndexManager().getIndex(tmpDir.toURI());
			indexer.index(context, index, new NullProgressMonitor());
		}
		finally
		{
			// Clean up the generated files!
			FileUtil.deleteRecursively(tmpDir);
		}
	}

	protected IndexManager getIndexManager()
	{
		return IndexPlugin.getDefault().getIndexManager();
	}
}
