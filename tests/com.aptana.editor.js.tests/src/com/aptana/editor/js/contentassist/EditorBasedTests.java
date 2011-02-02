/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.contentassist;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import junit.framework.TestCase;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.core.util.ResourceUtil;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.js.JSPlugin;

public class EditorBasedTests extends TestCase
{
	/**
	 * TestContext
	 */
	static class TestContext
	{
		public final ITextEditor editor;
		public final IDocument document;
		public final String source;
		public final JSContentAssistProcessor processor;
		
		public TestContext(ITextEditor editor, IDocument document, String source, JSContentAssistProcessor processor)
		{
			this.editor = editor;
			this.document = document;
			this.source = source;
			this.processor = processor;
		}
	}
	
	/**
	 * createEditor
	 * 
	 * @return
	 * @throws PartInitException 
	 */
	protected ITextEditor createEditor(IFileStore file)
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		FileStoreEditorInput editorInput = new FileStoreEditorInput(file);
		ITextEditor editor = null;
		
		try
		{
			editor = (ITextEditor) page.openEditor(editorInput, JSPlugin.PLUGIN_ID);
		}
		catch (PartInitException e)
		{
			fail(e.getMessage());
		}

		assertTrue(editor instanceof AbstractThemeableEditor);
		
		return editor;
	}
	
	/**
	 * getFileInfo
	 * 
	 * @param resource
	 * @return
	 */
	protected TestContext getTestContext(String resource)
	{
		IFileStore file = this.getFileStore(resource);
		ITextEditor editor = this.createEditor(file);
		IDocument document = editor.getDocumentProvider().getDocument(editor.getEditorInput());
		String source = document.get();
		JSContentAssistProcessor processor = new JSContentAssistProcessor((AbstractThemeableEditor) editor);
		
		return new TestContext(editor, document, source, processor);
	}

	/**
	 * getFileStore
	 * 
	 * @param resource
	 * @return
	 */
	protected IFileStore getFileStore(String resource)
	{
		Path path = new Path(resource);
		IFileStore result = null;

		try
		{
			URL url = FileLocator.find(JSPlugin.getDefault().getBundle(), path, null);
			URL fileURL = FileLocator.toFileURL(url);
			URI fileURI = ResourceUtil.toURI(fileURL);

			result = EFS.getStore(fileURI);
		}
		catch (IOException e)
		{
			fail(e.getMessage());
		}
		catch (URISyntaxException e)
		{
			fail(e.getMessage());
		}
		catch (CoreException e)
		{
			fail(e.getMessage());
		}

		assertNotNull(result);

		return result;
	}
}
