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
package com.aptana.editor.common.internal.commands;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;

public class NextPreviousEditorHandlerTest extends TestCase
{

	private static final String HTML_EDITOR_ID = "com.aptana.editor.html";
	private static final String NEXT_EDITOR_COMMAND_ID = "com.aptana.editor.NextEditorCommand";
	private static final String PREVIOUS_EDITOR_COMMAND_ID = "com.aptana.editor.PreviousEditorCommand";
	private static final String PROJECT_NAME = "editor_navigate";

	private IProject project;
	private List<IFile> files;
	private List<ITextEditor> editors;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		Class.forName("com.aptana.editor.html.Activator");
		project = createProject();
		files = new ArrayList<IFile>();
		editors = new ArrayList<ITextEditor>();
		// FIXME Make sure there are no other editors open!
	}

	@Override
	protected void tearDown() throws Exception
	{
		try
		{
			for (ITextEditor editor : editors)
			{
				// Need to force the editor shut!
				editor.close(false);
			}
			for (IFile file : files)
			{
				// Delete the generated file
				file.delete(true, new NullProgressMonitor());
			}
			// Delete the generated project
			project.delete(true, new NullProgressMonitor());
		}
		finally
		{
			editors = null;
			files = null;
			project = null;
			super.tearDown();
		}
	}

	protected IFile createFile(IProject project, String fileName, String contents) throws CoreException
	{
		IFile file = project.getFile(fileName);
		ByteArrayInputStream source = new ByteArrayInputStream(contents.getBytes());
		file.create(source, true, new NullProgressMonitor());
		return file;
	}

	protected IProject createProject() throws CoreException
	{
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProject project = workspace.getRoot().getProject(PROJECT_NAME);
		project.create(new NullProgressMonitor());
		project.open(new NullProgressMonitor());
		return project;
	}

	protected IFile createAndOpenFile(String fileName, String contents) throws CoreException, PartInitException
	{
		IFile file = createFile(project, fileName, contents);
		files.add(file);
		openEditor(file);
		return file;
	}

	private ITextEditor openEditor(IFile file) throws PartInitException
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		ITextEditor editor = (ITextEditor) IDE.openEditor(page, file, HTML_EDITOR_ID);
		editors.add(editor);
		return editor;
	}

	public void testExecute() throws Exception
	{
		// Open multiple files!!!
		createAndOpenFile("example1.html",
				"<html>\n<head>\n<title>Title goes here</title>\n</head>\n<body>\n<div>\n<p>Hi</p>\n</div>\n</body>");
		createAndOpenFile("example2.html",
				"<html>\n<head>\n<title>Title goes here</title>\n</head>\n<body>\n<div>\n</div>\n</body>");
		createAndOpenFile("example3.html",
				"<html>\n<head>\n<title>Title goes here</title>\n</head>\n<body>\n<h1>HEADING</h1>\n</body>");
		

		assertEquals("example3.html", getActiveEditor().getTitle());
		
		executeCommand(NEXT_EDITOR_COMMAND_ID);
		// wraps around to first editor. When whole suite is running we can't guarantee that these three files are the only ones open!
		assertFalse("example3.html".equals(getActiveEditor().getTitle()));

		executeCommand(PREVIOUS_EDITOR_COMMAND_ID);
		assertEquals("example3.html", getActiveEditor().getTitle());

		executeCommand(PREVIOUS_EDITOR_COMMAND_ID);
		assertEquals("example2.html", getActiveEditor().getTitle());
		
		executeCommand(PREVIOUS_EDITOR_COMMAND_ID);
		assertEquals("example1.html", getActiveEditor().getTitle());
		
		executeCommand(PREVIOUS_EDITOR_COMMAND_ID);
		// Goes back before 1, can't be sure no other editors are open
		assertFalse("example1.html".equals(getActiveEditor().getTitle()));
		
		executeCommand(NEXT_EDITOR_COMMAND_ID);
		assertEquals("example1.html", getActiveEditor().getTitle());
		
		executeCommand(NEXT_EDITOR_COMMAND_ID);
		assertEquals("example2.html", getActiveEditor().getTitle());
		
		executeCommand(NEXT_EDITOR_COMMAND_ID);
		assertEquals("example3.html", getActiveEditor().getTitle());
	}

	protected void executeCommand(String commandId) throws ExecutionException, NotDefinedException, NotEnabledException,
			NotHandledException
	{
		// Grab the handler service to execute our command
		IHandlerService service = (IHandlerService) getActiveEditor().getSite().getService(IHandlerService.class);
		service.executeCommand(commandId, null);
	}

	private IWorkbenchPart getActiveEditor()
	{
		// Grab ref to active editor
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		return page.getActiveEditor();
	}

}
