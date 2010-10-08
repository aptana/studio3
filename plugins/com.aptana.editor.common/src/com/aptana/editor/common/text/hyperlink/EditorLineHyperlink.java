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
package com.aptana.editor.common.text.hyperlink;

import java.io.File;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.terminal.editor.TerminalEditor;
import com.aptana.terminal.views.TerminalView;

public class EditorLineHyperlink implements IHyperlink
{

	private Region region;
	private String filepath;
	private int lineNumber;

	public EditorLineHyperlink(Region region, String filepath, int lineNumber)
	{
		this.region = region;
		this.filepath = filepath;
		this.lineNumber = lineNumber;
	}

	public IRegion getHyperlinkRegion()
	{
		return region;
	}

	public String getTypeLabel()
	{
		return null;
	}

	public String getHyperlinkText()
	{
		return this.filepath + ":" + lineNumber; //$NON-NLS-1$
	}

	public void open()
	{
		try
		{
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IFileStore store = EFS.getStore(getFile().toURI());
			IEditorPart editor = IDE.openEditorOnFileStore(page, store);
			setEditorToLine(editor);
		}
		catch (PartInitException e)
		{
			CommonEditorPlugin.logError(e);
		}
		catch (CoreException e)
		{
			CommonEditorPlugin.logError(e);
		}
	}

	private File getFile()
	{
		if (!filepath.startsWith("/")) //$NON-NLS-1$
		{
			// Assume relative to current project.
			// FIXME this really should be getting the active project like ExplorerContributorContext does!
			String activeProject = Platform.getPreferencesService().getString("com.aptana.explorer", "activeProject", //$NON-NLS-1$ //$NON-NLS-2$
					null, null);
			if (activeProject != null)
			{
				IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(activeProject);
				IFile file = project.getFile(new Path(filepath));
				if (file.exists())
				{
					return file.getLocation().toFile();
				}
			}

			// That didn't work. Now let's try getting active terminals and getting the working directory!
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IEditorReference[] refs = page.getEditorReferences();
			for (IEditorReference ref : refs)
			{
				if (TerminalEditor.ID.equals(ref.getId()))
				{
					File relative = getFileRelativeToWorkingDir(ref.getPart(false));
					if (relative != null)
					{
						return relative;
					}
				}
			}

			// Try Terminal Views
			IViewReference[] viewRefs = page.getViewReferences();
			for (IViewReference ref : viewRefs)
			{
				if (TerminalView.ID.equals(ref.getId()))
				{
					File relative = getFileRelativeToWorkingDir(ref.getPart(false));
					if (relative != null)
					{
						return relative;
					}
				}
			}
		}
		return new File(filepath);
	}

	private File getFileRelativeToWorkingDir(IWorkbenchPart part)
	{
		if (part == null)
		{
			return null;
		}
		IPath workingDir = null;
		if (part instanceof TerminalView)
		{
			workingDir = ((TerminalView) part).getWorkingDirectory();
		}
		else if (part instanceof TerminalEditor)
		{
			workingDir = ((TerminalEditor) part).getWorkingDirectory();
		}
		if (workingDir == null)
		{
			return null;
		}
		File relative = workingDir.append(filepath).toFile();
		if (relative.exists())
		{
			return relative;
		}
		return null;
	}

	private void setEditorToLine(IEditorPart editorPart) throws CoreException
	{
		if (!(editorPart instanceof ITextEditor))
		{
			return;
		}
		// documents start at 0
		if (lineNumber > 0)
		{
			lineNumber--;
		}
		if (lineNumber == 0)
		{
			return;
		}
		ITextEditor textEditor = (ITextEditor) editorPart;
		IDocumentProvider provider = textEditor.getDocumentProvider();
		IEditorInput pInput = editorPart.getEditorInput();
		provider.connect(pInput);
		IDocument document = provider.getDocument(pInput);
		try
		{
			IRegion line = document.getLineInformation(lineNumber);
			textEditor.selectAndReveal(line.getOffset(), line.getLength());
		}
		catch (BadLocationException e)
		{
			CommonEditorPlugin.logError(e);
		}
		provider.disconnect(pInput);
	}
}