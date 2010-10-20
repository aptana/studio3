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
import com.aptana.editor.js.Activator;

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
			editor = (ITextEditor) page.openEditor(editorInput, Activator.PLUGIN_ID);
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
			URL url = FileLocator.find(Activator.getDefault().getBundle(), path, null);
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
