/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.ITextEditor;
import org.junit.After;
import org.junit.Test;

import com.aptana.editor.epl.tests.EditorTestHelper;

@SuppressWarnings("restriction")
public class CSSEditorTest
{

	private ITextEditor editor;

	@After
	public void tearDown() throws Exception
	{
		if (editor != null)
		{
			EditorTestHelper.closeEditor(editor);
			editor = null;
		}
	}

	@Test
	public void testExecute() throws Exception
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		editor = (ITextEditor) page.openEditor(new FileStoreEditorInput(getFileStore()), getEditorId());
		assertNotNull(editor);
		assertEquals(getClassName(), editor.getClass().getName());
	}

	@Test
	public void testEditorPreferences()
	{
		String spacesForTabs;

		EditorsPlugin.getDefault().getPreferenceStore()
				.setValue(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS, false);

		spacesForTabs = CSSSourceEditor.getChainedPreferenceStore()
				.getString(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS);
		assertEquals("false", spacesForTabs);

		CSSPlugin.getDefault().getPreferenceStore()
				.setValue(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS, true);

		spacesForTabs = CSSSourceEditor.getChainedPreferenceStore()
				.getString(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS);
		assertEquals("true", spacesForTabs);
	}

	protected IFileStore getFileStore() throws Exception
	{
		return EFS.getStore((new File("test.css")).toURI());
	}

	protected String getEditorId()
	{
		return "com.aptana.editor.css";
	}

	protected String getClassName()
	{
		return CSSSourceEditor.class.getName();
	}
}
