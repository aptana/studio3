package com.aptana.editor.common.internal.commands;

import java.io.ByteArrayInputStream;
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
import org.eclipse.ui.IWorkbenchWindow;
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

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		Class.forName("com.aptana.editor.html.Activator");
		project = createProject();
	}

	@Override
	protected void tearDown() throws Exception
	{
		try
		{
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
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			editor = (ITextEditor) IDE.openEditor(page, file, HTML_EDITOR_ID);
		}
		return editor;
	}

	public void testExecute() throws Exception
	{
		// Create an open an HTML file
		createAndOpenFile("example" + System.currentTimeMillis() + ".html",
				"<html>\n<head>\n<title>Title goes here</title>\n</head>\n<body>\n<div>\n<p>Hi</p>\n</div>\n</body>");
		// Open the Outline view
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		IViewPart outline = page.showView(IPageLayout.ID_OUTLINE);

		// Grab tree viewer for outline contents
		IPage curPage = ((ContentOutline) outline).getCurrentPage();
		CommonOutlinePage outlinePage = (CommonOutlinePage) curPage;
		Method m = ContentOutlinePage.class.getDeclaredMethod("getTreeViewer");
		m.setAccessible(true);
		TreeViewer treeViewer = (TreeViewer) m.invoke(outlinePage);

		// check expansion state
		Object[] expanded = treeViewer.getExpandedElements();
		assertEquals(0, expanded.length);

		expandToLevel(outline, "1");
		expanded = treeViewer.getExpandedElements();
		assertEquals(0, expanded.length); // collapsed (root)

		expandToLevel(outline, "2");
		expanded = treeViewer.getExpandedElements();
		assertEquals(1, expanded.length); // html

		expandToLevel(outline, "3");
		expanded = treeViewer.getExpandedElements();
		assertEquals(3, expanded.length); // html, head, body

		expandToLevel(outline, "4");
		expanded = treeViewer.getExpandedElements();
		assertEquals(4, expanded.length); // html, head, body, div

		expandToLevel(outline, "2");
		expanded = treeViewer.getExpandedElements();
		assertEquals(1, expanded.length); // html

		expandToLevel(outline, "4");
		expanded = treeViewer.getExpandedElements();
		assertEquals(4, expanded.length); // html, head, body, div

		expandToLevel(outline, "1");
		expanded = treeViewer.getExpandedElements();
		assertEquals(0, expanded.length); // collapsed (root)
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
