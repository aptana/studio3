/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.scripting.commands;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.junit.Test;

import com.aptana.editor.common.CommonEditorPlugin;

public class UtilitiesTest
{

	@Test
	public void testGetFile()
	{
		File file = Utilities.getFile();
		assertNotNull(file);
		assertFalse(file.exists());
		IPath path = CommonEditorPlugin.getDefault().getStateLocation();
		assertTrue(file.getAbsolutePath().startsWith(path.toOSString() + File.separator + "_"));
	}

	@Test
	public void testGetFileGeneratesUniqueNames()
	{
		Set<String> filePaths = new HashSet<String>();
		for (int i = 0; i < 1000; i++)
		{
			File file = Utilities.getFile();
			String path = file.getAbsolutePath();
			assertFalse("Generated a non-unique filename", filePaths.contains(path));
			try
			{
				file.createNewFile();
			}
			catch (IOException e)
			{
				// ignore
			}
			filePaths.add(path);
		}
	}

	@Test
	public void testCreateFileEditorInputCreatesFileStoreEditorInput()
	{
		File file = Utilities.getFile();
		IEditorInput input = Utilities.createFileEditorInput(file, "Untitled.txt");
		assertTrue(input instanceof FileStoreEditorInput);
	}
}
