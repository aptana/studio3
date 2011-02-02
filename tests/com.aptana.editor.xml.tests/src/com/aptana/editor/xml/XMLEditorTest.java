/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.xml;

import java.io.File;

import junit.framework.TestCase;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

public class XMLEditorTest extends TestCase
{

	private ITextEditor editor;

	@Override
	protected void tearDown() throws Exception
	{
		if (editor != null)
		{
			editor.close(false);
			editor = null;
		}
	}

	public void testExecute() throws Exception
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IFileStore fileStore = getFileStore();
		editor = (ITextEditor) page.openEditor(new FileStoreEditorInput(fileStore), getEditorId());
		assertNotNull(editor);
		assertEquals(getClassName(), editor.getClass().getName());
	}

	protected IFileStore getFileStore() throws Exception
	{
		return EFS.getStore((new File("test.xml")).toURI()); //$NON-NLS-1$
	}

	protected String getEditorId()
	{
		return "com.aptana.editor.xml"; //$NON-NLS-1$
	}

	protected String getClassName()
	{
		return XMLEditor.class.getName();
	}
}
