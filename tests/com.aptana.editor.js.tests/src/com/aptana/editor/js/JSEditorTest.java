/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js;

import java.io.File;

import junit.framework.TestCase;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.ui.util.UIUtils;

@SuppressWarnings("restriction")
public class JSEditorTest extends TestCase
{

	private ITextEditor editor;

	@Override
	protected void tearDown() throws Exception
	{
		if (editor != null)
		{
			if (editor != null)
			{
				if (Display.getCurrent() != null)
				{
					editor.getSite().getPage().closeEditor(editor, false);
				}
				else
				{
					editor.close(false);
				}
			}
			editor = null;
		}
	}

	public void testExecute() throws Exception
	{
		IWorkbenchPage page = UIUtils.getActivePage();
		editor = (ITextEditor) page.openEditor(new FileStoreEditorInput(getFileStore()), getEditorId());
		assertNotNull(editor);
		assertEquals(getClassName(), editor.getClass().getName());
	}

	public void testEditorPreferences()
	{
		String spacesForTabs;

		EditorsPlugin.getDefault().getPreferenceStore()
				.setValue(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS, false);

		spacesForTabs = JSSourceEditor.getChainedPreferenceStore().getString(
				AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS);
		assertEquals("false", spacesForTabs);

		JSPlugin.getDefault().getPreferenceStore()
				.setValue(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS, true);

		spacesForTabs = JSSourceEditor.getChainedPreferenceStore().getString(
				AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS);
		assertEquals("true", spacesForTabs);
	}
	
	protected IFileStore getFileStore() throws Exception
	{
		return EFS.getStore((new File("dojo.js.uncompressed.js")).toURI());
	}

	protected String getEditorId()
	{
		return "com.aptana.editor.js";
	}

	protected String getClassName()
	{
		return JSSourceEditor.class.getName();
	}
}
