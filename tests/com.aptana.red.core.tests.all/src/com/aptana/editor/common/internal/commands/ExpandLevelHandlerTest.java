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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.views.contentoutline.ContentOutline;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import com.aptana.editor.common.outline.CommonOutlinePage;

public class ExpandLevelHandlerTest extends TestCase
{

	private static final String HTML_EDITOR_ID = "com.aptana.editor.html";
	private static final String COMMAND_ID = "com.aptana.editor.commands.ExpandLevel";
	private static final String PROJECT_NAME = "expand_level_proj";
	private IProject project;
	private IFile file;
	private ITextEditor editor;
	private IViewPart outline;
	private TreeViewer treeViewer;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		Class.forName("com.aptana.editor.html.Activator");
		project = createProject();

		// Create and open an HTML file
		createAndOpenFile("example" + System.currentTimeMillis() + ".html",
				"<html>\n<head>\n<title>Title goes here</title>\n</head>\n<body>\n<div>\n<p>Hi</p>\n</div>\n</body>\n</html>");
		// Open the Outline view
		outline = getOutline();

		// Grab tree viewer for outline contents
		treeViewer = getTreeViewer(outline);
		// FIXME Tree viewer isn't set up with items before we start asking it to expand! Sleeping gives it some time...
		Thread.sleep(750);		
		// We're in the UI thread and this doesn't allow the treeviewer to refresh in a separate thread/job properly...
	}

	@Override
	protected void tearDown() throws Exception
	{
		try
		{
			getActivePage().hideView(outline);
			outline.dispose();
			// Need to force the editor shut!
			editor.close(false);
			// Delete the generated file
			file.delete(true, new NullProgressMonitor());
			// Delete the generated project
			project.delete(true, new NullProgressMonitor());
		}
		finally
		{
			editor = null;
			file = null;
			project = null;
			treeViewer = null;
			outline = null;
			super.tearDown();
		}
	}

	public void testExpandToLevel1() throws Exception
	{
		expandToLevel(outline, "1");
		assertEquals(0, treeViewer.getExpandedElements().length); // collapsed (root)
	}

	public void testExpandtoLevel2() throws Exception
	{
		expandToLevel(outline, "2");
		assertEquals(1, treeViewer.getExpandedElements().length); // html
	}

	public void testExpandtoLevel3() throws Exception
	{
		expandToLevel(outline, "3");
		assertEquals(3, treeViewer.getExpandedElements().length); // html, head, body
	}

	public void testExpandtoLevel4() throws Exception
	{
		expandToLevel(outline, "4");
		assertEquals(4, treeViewer.getExpandedElements().length); // html, head, body, div
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
		if (file == null)
		{
			file = createFile(project, fileName, contents);
			getEditor();
		}
		return file;
	}

	private ITextEditor getEditor() throws PartInitException
	{
		if (editor == null)
		{
			PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable()
			{

				public void run()
				{
					try
					{
						editor = (ITextEditor) IDE.openEditor(getActivePage(), file, HTML_EDITOR_ID);
					}
					catch (PartInitException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		}
		return editor;
	}

	protected IWorkbenchPage getActivePage()
	{
		final IWorkbenchPage[] result = new IWorkbenchPage[1];
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable()
		{

			public void run()
			{
				result[0] = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			}
		});
		return result[0];
	}

	protected TreeViewer getTreeViewer(IViewPart outline) throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException
	{
		IPage curPage = ((ContentOutline) outline).getCurrentPage();
		CommonOutlinePage outlinePage = (CommonOutlinePage) curPage;
		Method m = ContentOutlinePage.class.getDeclaredMethod("getTreeViewer");
		m.setAccessible(true);
		TreeViewer treeViewer = (TreeViewer) m.invoke(outlinePage);
		return treeViewer;
	}

	protected IViewPart getOutline() throws PartInitException
	{
		return getActivePage().showView(IPageLayout.ID_OUTLINE);
	}

	protected void expandToLevel(IViewPart outline, String level) throws ExecutionException, NotDefinedException,
			NotEnabledException, NotHandledException
	{
		// Grab the handler service to execute our command
		IHandlerService service = (IHandlerService) outline.getSite().getService(IHandlerService.class);
		ICommandService commandService = (ICommandService) outline.getSite().getService(ICommandService.class);
		Command command = commandService.getCommand(COMMAND_ID);
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("level", level);
		ParameterizedCommand pc = ParameterizedCommand.generateCommand(command, parameters);
		service.executeCommand(pc, null);
	}

}
