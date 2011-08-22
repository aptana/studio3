/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common;

import java.io.File;
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
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension2;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;
import org.osgi.framework.Bundle;

import com.aptana.core.util.FileUtil;
import com.aptana.core.util.ResourceUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.index.core.IFileStoreIndexingParticipant;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;
import com.aptana.scripting.model.SnippetElement;

public abstract class EditorBasedTests<T extends CommonContentAssistProcessor> extends TestCase
{
	private static final Pattern CURSOR = Pattern.compile("\\|");

	protected ITextEditor editor;
	protected IDocument document;
	protected String source;
	protected T processor;
	protected List<Integer> cursorOffsets;

	private URI fileUri;

	/**
	 * checkProposals
	 * 
	 * @param resource
	 * @param displayNames
	 */
	protected void checkProposals(String resource, String... displayNames)
	{
		checkProposals(resource, false, false, displayNames);
	}

	/**
	 * checkProposals
	 * 
	 * @param resource
	 * @param displayNames
	 */
	protected void checkProposals(String resource, boolean enforceOrder, boolean enforceSize, String... displayNames)
	{
		this.setupTestContext(resource);

		ITextViewer viewer = new TextViewer(new Shell(), SWT.NONE);
		viewer.setDocument(this.document);

		for (int offset : this.cursorOffsets)
		{
			// get proposals
			ICompletionProposal[] proposals = this.processor.computeCompletionProposals(viewer, offset, '\0', false);

			// build a list of display names
			ArrayList<String> names = new ArrayList<String>();

			for (ICompletionProposal proposal : proposals)
			{
				// we need to check if it is a valid proposal given the context
				if (proposal instanceof ICompletionProposalExtension2)
				{
					ICompletionProposalExtension2 p = (ICompletionProposalExtension2) proposal;
					if (p.validate(document, offset, null))
					{
						names.add(proposal.getDisplayString());
					}
				}
				else
				{
					names.add(proposal.getDisplayString());
				}
			}

			if (enforceOrder || enforceSize)
			{
				assertTrue(
						StringUtil.format(
								"Length of expected proposal list and actual proposal list did not match.\nExpected: <{0}> Actual: <{1}>",
								new Object[] { StringUtil.join(", ", displayNames), StringUtil.join(", ", names) }),
						displayNames.length == names.size());
			}

			// this only really makes sense with enforce size
			if (enforceOrder)
			{
				for (int i = 0; i < displayNames.length; i++)
				{
					String displayName = displayNames[i];
					assertEquals("Did not find " + displayName + " in the proposal list at the expected spot",
							displayName, names.get(i));
				}
			}
			else
			{
				// verify each specified name is in the resulting proposal list
				for (String displayName : displayNames)
				{
					assertTrue("Did not find " + displayName + " in the proposal list", names.contains(displayName));
				}
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
	protected ITextEditor createEditor(IEditorInput editorInput)
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
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
		SnippetElement se = new SnippetElement(path);
		se.setDisplayName(displayName);
		se.setTrigger("prefix", new String[] { trigger });
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
		try
		{
			tempFile = File.createTempFile(prefix, extension);
			FileUtil.writeStringToFile(contents, tempFile);
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
	protected abstract String getPluginId();

	/**
	 * Is we wish to index our files, the index we should use
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
		URI indexURI = this.fileUri;
		Index result = null;

		if (indexURI != null)
		{
			result = IndexManager.getInstance().getIndex(indexURI);
		}

		return result;
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
		this.source = document.get();
		this.processor = this.createContentAssistProcessor((AbstractThemeableEditor) this.editor);

		IFileStoreIndexingParticipant indexer = this.createIndexer();
		if (indexer != null)
		{
			Set<IFileStore> set = new HashSet<IFileStore>();
			set.add(store);
			try
			{
				indexer.index(set, this.getIndex(), new NullProgressMonitor());
			}
			catch (CoreException e)
			{
				fail("Error indexing file");
			}
		}

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
			editor.close(false);
		}

		if (processor != null)
		{
			processor.dispose();
		}

		if (fileUri != null)
		{
			IndexManager.getInstance().removeIndex(fileUri);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception
	{
		tearDownTestContext();

		editor = null;
		document = null;
		source = null;
		processor = null;
		cursorOffsets = null;

		super.tearDown();
	}
}
