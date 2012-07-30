/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.debug.ui;

import java.net.URI;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.debug.internal.ui.sourcelookup.SourceLookupManager;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.resources.IUniformResource;
import com.aptana.core.resources.IUniformResourceMarker;
import com.aptana.core.resources.UniformResourceStorage;
import com.aptana.core.util.ArrayUtil;
import com.aptana.debug.ui.internal.UniformResourceStorageEditorInput;
import com.aptana.ui.util.UIUtils;

/**
 * @author Max Stepanov
 */
@SuppressWarnings("restriction")
public final class SourceDisplayUtil
{

	private SourceDisplayUtil()
	{
	}

	/**
	 * getEditorInput
	 * 
	 * @param element
	 * @return IEditorInput
	 */
	public static IEditorInput getEditorInput(Object element)
	{
		if (element instanceof IFile)
		{
			return new FileEditorInput((IFile) element);
		}
		if (element instanceof ILineBreakpoint)
		{
			IMarker marker = ((ILineBreakpoint) element).getMarker();
			if (marker instanceof IUniformResourceMarker)
			{
				IUniformResource resource = ((IUniformResourceMarker) marker).getUniformResource();
				element = resource.getAdapter(IStorage.class); // $codepro.audit.disable questionableAssignment
				if (element == null)
				{
					element = resource; // $codepro.audit.disable questionableAssignment
				}
			}
			else
			{
				return new FileEditorInput((IFile) marker.getResource());
			}
		}
		if (element instanceof IFileStore)
		{
			IFileStore fileStore = (IFileStore) element;
			IFile[] files = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(fileStore.toURI());
			if (ArrayUtil.isEmpty(files))
			{
				return new FileStoreEditorInput(fileStore);
			}
			return new FileEditorInput(files[0]);
		}
		if (element instanceof UniformResourceStorage)
		{
			if (((UniformResourceStorage) element).getFullPath() != null)
			{
				// TODO return new LocalFileStorageEditorInput((UniformResourceStorage) element);
				element.hashCode();
			}
			if (((UniformResourceStorage) element).exists())
			{
				return new UniformResourceStorageEditorInput((UniformResourceStorage) element);
			}
			return null;
		}
		if (element instanceof IAdaptable)
		{
			return (IEditorInput) ((IAdaptable) element).getAdapter(IEditorInput.class);
		}
		return null;
	}

	/**
	 * getEditorId
	 * 
	 * @param input
	 * @param element
	 * @return String
	 */
	public static String getEditorId(IEditorInput input, Object element)
	{
		try
		{
			/*
			 * Use configured HTMLEditor for all externally loaded files
			 */
			if (input instanceof UniformResourceStorageEditorInput)
			{
				UniformResourceStorage storage = (UniformResourceStorage) ((UniformResourceStorageEditorInput) input)
						.getStorage();
				URI uri = storage.getURI();
				String scheme = uri.getScheme();
				if ("http".equals(scheme) || "https".equals(scheme)) { //$NON-NLS-1$ //$NON-NLS-2$
					return IDE.getEditorDescriptor(input.getName() + ".html").getId(); //$NON-NLS-1$
				}
			}
			IEditorDescriptor descriptor = IDE.getEditorDescriptor(input.getName());
			return descriptor.getId();
		}
		catch (PartInitException e)
		{
			return null;
		}
	}

	/**
	 * openInEditor
	 * 
	 * @param input
	 * @param lineNumber
	 * @throws PartInitException
	 */
	public static void openInEditor(IEditorInput input, int lineNumber) throws PartInitException
	{
		openInEditor(UIUtils.getActivePage(), input, lineNumber);
	}

	/**
	 * openInEditor
	 * 
	 * @param page
	 * @param input
	 * @param lineNumber
	 * @throws PartInitException
	 */
	public static void openInEditor(IWorkbenchPage page, IEditorInput input, int lineNumber) throws PartInitException
	{
		IEditorPart editorPart = IDE.openEditor(page, input, getEditorId(input, null));
		revealLineInEditor(editorPart, lineNumber);
	}

	/**
	 * revealLineInEditor
	 * 
	 * @param editorPart
	 * @param lineNumber
	 */
	public static void revealLineInEditor(IEditorPart editorPart, int lineNumber)
	{
		if (lineNumber > 0)
		{
			ITextEditor textEditor = null;
			if (editorPart instanceof ITextEditor)
			{
				textEditor = (ITextEditor) editorPart;
			}
			else
			{
				textEditor = (ITextEditor) editorPart.getAdapter(ITextEditor.class);
			}
			if (textEditor != null)
			{
				IDocumentProvider provider = textEditor.getDocumentProvider();
				try
				{
					provider.connect(textEditor.getEditorInput());
				}
				catch (CoreException e)
				{
					e.getCause();
					return;
				}
				IDocument document = provider.getDocument(textEditor.getEditorInput());
				try
				{
					IRegion line = document.getLineInformation(lineNumber - 1); // documents start at 0
					textEditor.selectAndReveal(line.getOffset(), line.getLength());
				}
				catch (BadLocationException e)
				{
					IdeLog.logWarning(DebugUiPlugin.getDefault(), e);
				}
				finally
				{
					provider.disconnect(document);
				}
			}
		}
		IWorkbenchPage page = editorPart.getSite().getPage();
		if (!page.isPartVisible(editorPart))
		{
			page.activate(editorPart);
		}
	}

	/**
	 * findEditor
	 * 
	 * @param input
	 * @return IEditorPart
	 */
	public static IEditorPart findEditor(IEditorInput input)
	{
		return UIUtils.getActivePage().findEditor(input);
	}

	public static void displaySource(Object context, boolean forceSourceLookup)
	{
		displaySource(context, UIUtils.getActivePage(), forceSourceLookup);
	}

	public static void displaySource(Object context, IWorkbenchPage page, boolean forceSourceLookup)
	{
		SourceLookupManager.getDefault().displaySource(context, page, forceSourceLookup);
	}
}
