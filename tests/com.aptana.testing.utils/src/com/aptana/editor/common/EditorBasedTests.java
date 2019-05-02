/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;
import org.junit.After;
import org.osgi.framework.Bundle;

import com.aptana.core.util.FileUtil;
import com.aptana.core.util.IOUtil;
import com.aptana.core.util.ResourceUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.epl.tests.EditorTestHelper;
import com.aptana.index.core.FileStoreBuildContext;
import com.aptana.index.core.IFileStoreIndexingParticipant;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;
import com.aptana.index.core.IndexPlugin;
import com.aptana.index.core.build.BuildContext;
import com.aptana.scripting.model.SnippetElement;
import com.aptana.ui.util.UIUtils;

public abstract class EditorBasedTests
{
	private static final Pattern CURSOR = Pattern.compile("\\|");

	protected ITextEditor editor;
	protected IDocument document;
	protected String source;
	protected List<Integer> cursorOffsets;

	private URI fileUri;

	/**
	 * createEditor
	 * 
	 * @param createEditor
	 * @return
	 */
	protected ITextEditor createEditor(IFileStore fileStore)
	{
		return createEditor(new FileStoreEditorInput(fileStore));
	}

	/**
	 * createEditor
	 * 
	 * @param createEditor
	 * @return
	 */
	protected ITextEditor createEditor(IEditorInput editorInput)
	{
		return createEditor(editorInput, getEditorId());
	}

	/**
	 * createEditor
	 * 
	 * @param createEditor
	 * @param editorId
	 * @return
	 */
	protected ITextEditor createEditor(IEditorInput editorInput, String editorId)
	{
		IWorkbenchPage page = UIUtils.getActivePage();
		ITextEditor editor = null;

		try
		{
			editor = (ITextEditor) page.openEditor(editorInput, editorId);
		}
		catch (PartInitException e)
		{
			fail(e.getMessage());
		}

		assertTrue(editor instanceof AbstractThemeableEditor);

		return editor;
	}

	protected ISourceViewer getSourceViewer()
	{
		if (editor == null)
		{
			return null;
		}

		return ((AbstractThemeableEditor) editor).getISourceViewer();
	}

	/**
	 * Create a snippet
	 * 
	 * @param path
	 * @param displayName
	 * @param trigger
	 * @param scope
	 * @return
	 */
	protected SnippetElement createSnippet(String path, String displayName, String trigger, String scope)
	{
		return createSnippet(path, displayName, trigger, "", scope);
	}

	/**
	 * Create a snippet
	 * 
	 * @param path
	 * @param displayName
	 * @param trigger
	 * @param expansion
	 * @param scope
	 * @return
	 */
	protected SnippetElement createSnippet(String path, String displayName, String trigger, String expansion,
			String scope)
	{
		SnippetElement se = new SnippetElement(path);
		se.setDisplayName(displayName);
		se.setTrigger("prefix", new String[] { trigger });
		se.setExpansion(expansion);
		se.setScope(scope);

		return se;
	}

	/**
	 * getBundle
	 * 
	 * @return
	 */
	protected abstract Bundle getBundle();

	/**
	 * getFileStore
	 * 
	 * @param resource
	 * @return
	 */
	protected IFileStore createFileStore(String prefix, String extension, String contents)
	{
		File tempFile;
		IFileStore fileStore = null;
		if (extension.length() > 0 && extension.charAt(0) != '.')
		{
			extension = '.' + extension;
		}
		try
		{
			tempFile = FileUtil.createTempFile(prefix, extension);
			IOUtil.write(new FileOutputStream(tempFile), contents);
			fileStore = EFS.getStore(tempFile.toURI());
		}
		catch (IOException e)
		{
			fail();
		}
		catch (CoreException e)
		{
			fail();
		}

		return fileStore;
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
			URL url = FileLocator.find(this.getBundle(), path, null);
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

	/**
	 * getPluginId
	 * 
	 * @return
	 */
	protected abstract String getEditorId();

	/**
	 * Is we wish to index our files, the indexer we should use
	 * 
	 * @return
	 */
	protected IFileStoreIndexingParticipant createIndexer()
	{
		return null;
	}

	/**
	 * getIndex
	 * 
	 * @return
	 */
	protected Index getIndex()
	{
		if (this.fileUri == null)
		{
			return null;
		}
		return getIndexManager().getIndex(this.fileUri);

	}

	/**
	 * setupTestContext
	 * 
	 * @param resource
	 * @return
	 * @throws CoreException
	 */
	protected void setupTestContext(IFile file) throws CoreException
	{
		IFileStore store = EFS.getStore(file.getRawLocationURI());
		FileEditorInput editorInput = new FileEditorInput(file);
		setupTestContext(store, editorInput);
	}

	/**
	 * setupTestContext
	 * 
	 * @param resource
	 * @return
	 */
	protected void setupTestContext(IFileStore file)
	{
		FileStoreEditorInput editorInput = new FileStoreEditorInput(file);
		setupTestContext(file, editorInput);
	}

	/**
	 * setupTestContext
	 * 
	 * @param resource
	 * @return
	 */
	protected void setupTestContext(IFileStore store, IEditorInput editorInput)
	{
		this.fileUri = store.toURI();
		this.editor = this.createEditor(editorInput);
		this.document = editor.getDocumentProvider().getDocument(editor.getEditorInput());

		// find offsets
		handleCursorOffsets(document);
		this.source = document.get();

		IFileStoreIndexingParticipant indexer = this.createIndexer();
		if (indexer != null)
		{
			BuildContext context = new FileStoreBuildContext(store)
			{
				@Override
				public synchronized String getContents()
				{
					return source;
				}
			};
			try
			{
				indexer.index(context, this.getIndex(), new NullProgressMonitor());
			}
			catch (CoreException e)
			{
				fail("Error indexing file: " + e.getMessage());
			}
		}
	}

	protected void handleCursorOffsets(IDocument document)
	{
		String source = document.get();
		ArrayList cursorOffsets = new ArrayList<Integer>();
		int offset = source.indexOf('|');

		while (offset != -1)
		{
			// NOTE: we have to account for the deletion of previous offsets
			cursorOffsets.add(offset - cursorOffsets.size());
			offset = source.indexOf('|', offset + 1);
		}

		if (cursorOffsets.isEmpty())
		{
			// use last position if we didn't find any cursors
			cursorOffsets.add(source.length());
		}
		else
		{
			// clean source
			source = CURSOR.matcher(source).replaceAll(StringUtil.EMPTY);

			// update document
			document.set(source);
		}
		cursorOffsets.trimToSize();
		this.cursorOffsets = cursorOffsets;
	}

	/**
	 * Creates a verify key event
	 * 
	 * @param character
	 * @param keyCode
	 * @param offset
	 * @return
	 */
	protected VerifyEvent createVerifyKeyEvent(char character, int keyCode, int offset)
	{
		Event e = new Event();
		e.character = character;
		e.text = String.valueOf(character);
		e.start = offset;
		e.keyCode = keyCode;
		e.end = offset;
		e.doit = true;

		ITextViewer viewer = (ITextViewer) editor.getAdapter(ITextOperationTarget.class);
		e.widget = viewer.getTextWidget();
		viewer.setSelectedRange(offset, 0);
		return new VerifyEvent(e);
	}

	/**
	 * getFileInfo
	 * 
	 * @param resource
	 * @return
	 */
	protected void setupTestContext(String resource)
	{
		IFileStore file = this.getFileStore(resource);
		setupTestContext(file);
	}

	/**
	 * tearDownTestContext
	 * 
	 * @param resource
	 * @return
	 */
	protected void tearDownTestContext()
	{
		if (editor != null)
		{
			EditorTestHelper.closeEditor(editor);
		}

		if (fileUri != null)
		{
			getIndexManager().removeIndex(fileUri);
		}
	}

	protected IndexManager getIndexManager()
	{
		return IndexPlugin.getDefault().getIndexManager();
	}

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	// @Override
	@After
	public void tearDown() throws Exception
	{
		tearDownTestContext();

		editor = null;
		document = null;
		source = null;
		cursorOffsets = null;

		// super.tearDown();
	}
}
