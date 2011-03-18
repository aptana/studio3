/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;
import org.osgi.framework.Bundle;

import com.aptana.core.util.ResourceUtil;
import com.aptana.core.util.StringUtil;

public abstract class EditorBasedTests<T extends CommonContentAssistProcessor> extends TestCase
{
	private static final Pattern CURSOR = Pattern.compile("\\|");

	protected ITextEditor editor;
	protected IDocument document;
	protected String source;
	protected T processor;
	protected List<Integer> cursorOffsets;

	/**
	 * checkProposals
	 * 
	 * @param resource
	 * @param displayNames
	 */
	protected void checkProposals(String resource, String... displayNames)
	{
		this.setupTestContext(resource);

		ITextViewer viewer = new TextViewer(new Shell(), SWT.NONE);
		viewer.setDocument(this.document);

		for (int offset : this.cursorOffsets)
		{
			// get proposals
			ICompletionProposal[] proposals = this.processor.doComputeCompletionProposals(viewer, offset, '\0', false);

			// build a list of display names
			Set<String> names = new HashSet<String>();

			for (ICompletionProposal proposal : proposals)
			{
				names.add(proposal.getDisplayString());
			}

			// verify each specified name is in the resulting proposal list
			for (String displayName : displayNames)
			{
				assertTrue("Did not find " + displayName + " in the proposal list", names.contains(displayName));
			}
		}
	}

	/**
	 * createContentAssistProcessor
	 * 
	 * @param editor
	 * @return
	 */
	protected abstract T createContentAssistProcessor(AbstractThemeableEditor editor);

	/**
	 * createEditor
	 * 
	 * @param file
	 * @return
	 */
	protected ITextEditor createEditor(IFileStore file)
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		FileStoreEditorInput editorInput = new FileStoreEditorInput(file);
		ITextEditor editor = null;

		try
		{
			editor = (ITextEditor) page.openEditor(editorInput, this.getPluginId());
		}
		catch (PartInitException e)
		{
			fail(e.getMessage());
		}

		assertTrue(editor instanceof AbstractThemeableEditor);

		return editor;
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
	protected abstract String getPluginId();

	/**
	 * getFileInfo
	 * 
	 * @param resource
	 * @return
	 */
	protected void setupTestContext(String resource)
	{
		IFileStore file = this.getFileStore(resource);

		this.editor = this.createEditor(file);
		this.document = editor.getDocumentProvider().getDocument(editor.getEditorInput());
		this.source = document.get();
		this.processor = this.createContentAssistProcessor((AbstractThemeableEditor) this.editor);

		// find offsets
		this.cursorOffsets = new ArrayList<Integer>();
		int offset = this.source.indexOf('|');

		while (offset != -1)
		{
			// NOTE: we have to account for the deletion of previous offsets
			this.cursorOffsets.add(offset - this.cursorOffsets.size());
			offset = this.source.indexOf('|', offset + 1);
		}

		if (this.cursorOffsets.isEmpty())
		{
			// use last position if we didn't find any cursors
			this.cursorOffsets.add(source.length());
		}
		else
		{
			// clean source
			this.source = CURSOR.matcher(this.source).replaceAll(StringUtil.EMPTY);

			// update document
			document.set(this.source);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception
	{
		if (editor != null)
		{
			editor.close(false);
		}
		editor = null;
		document = null;
		source = null;
		processor = null;
		cursorOffsets = null;

		super.tearDown();
	}
}
