/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
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

package com.aptana.debug.internal.ui.util;

import java.net.URI;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.debug.core.sourcelookup.containers.LocalFileStorage;
import org.eclipse.debug.internal.ui.sourcelookup.SourceLookupManager;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.core.resources.IUniformResource;
import com.aptana.core.resources.IUniformResourceMarker;
import com.aptana.core.resources.UniformResourceStorage;
import com.aptana.debug.internal.ui.LocalFileStorageEditorInput;
import com.aptana.debug.internal.ui.UniformResourceStorageEditorInput;
import com.aptana.debug.ui.DebugUiPlugin;

/**
 * @author Max Stepanov
 */
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
				element = resource.getAdapter(IStorage.class);
				if (element == null)
				{
					element = resource;
				}
			}
			else
			{
				return new FileEditorInput((IFile) marker.getResource());
			}
		}
		if (element instanceof LocalFileStorage)
		{
			return new LocalFileStorageEditorInput((LocalFileStorage) element);
		}
		if (element instanceof UniformResourceStorage)
		{
			if (((UniformResourceStorage) element).getFullPath() != null)
			{
				return new LocalFileStorageEditorInput((UniformResourceStorage) element);
			}
			if (((UniformResourceStorage)element).exists()) {
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
			if (input instanceof UniformResourceStorageEditorInput) {
				UniformResourceStorage storage = (UniformResourceStorage)((UniformResourceStorageEditorInput)input).getStorage();
				URI uri = storage.getURI();
				String scheme = uri.getScheme();
				if ("http".equals(scheme) || "https".equals(scheme)) { //$NON-NLS-1$ //$NON-NLS-2$
					return IDE.getEditorDescriptor(input.getName()+".html").getId(); //$NON-NLS-1$
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
		openInEditor(DebugUiPlugin.getActivePage(), input, lineNumber);
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
				IDocument document = provider.getDocument(textEditor.getEditorInput());
				try
				{
					IRegion line = document.getLineInformation(lineNumber - 1); // documents start at 0
					textEditor.selectAndReveal(line.getOffset(), line.getLength());
				}
				catch (BadLocationException e)
				{
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
		return DebugUiPlugin.getActivePage().findEditor(input);
	}

	public static void displaySource(Object context, boolean forceSourceLookup) {
		displaySource(context, DebugUiPlugin.getActivePage(), forceSourceLookup);
	}

	public static void displaySource(Object context, IWorkbenchPage page, boolean forceSourceLookup) {
		SourceLookupManager.getDefault().displaySource(context, page, forceSourceLookup);
	}
}
