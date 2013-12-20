/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.internal.commands;

import org.junit.After;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
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
import com.aptana.editor.epl.tests.EditorTestHelper;

public class ExpandLevelHandlerTest
{

	private static final String HTML_EDITOR_ID = "com.aptana.editor.html";
	private static final String COMMAND_ID = "com.aptana.editor.commands.ExpandLevel";
	private static final String PROJECT_NAME = "expand_level_proj";
	private IProject project;
	private IFile file;
	private ITextEditor editor;
	private IViewPart outline;
	private TreeViewer treeViewer;

//	@Override
	@Before
	public void setUp() throws Exception
	{
//		super.setUp();
		Class.forName("com.aptana.editor.html.HTMLPlugin");
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

//	@Override
	@After
	public void tearDown() throws Exception
	{
		try
		{
			getActivePage().hideView(outline);
			outline.dispose();
			// Need to force the editor shut!
			EditorTestHelper.closeEditor(editor);
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
//			super.tearDown();
		}
	}

	@Test
	public void testExpandToLevel1() throws Exception
	{
		expandToLevel(outline, "1");
		assertEquals(0, treeViewer.getExpandedElements().length); // collapsed (root)
	}

	@Test
	public void testExpandtoLevel2() throws Exception
	{
		expandToLevel(outline, "2");
		assertEquals(1, treeViewer.getExpandedElements().length); // html
	}

	@Test
	public void testExpandtoLevel3() throws Exception
	{
		expandToLevel(outline, "3");
		assertEquals(3, treeViewer.getExpandedElements().length); // html, head, body
	}

	@Test
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
