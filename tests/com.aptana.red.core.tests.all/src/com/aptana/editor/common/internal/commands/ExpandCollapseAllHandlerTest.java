package com.aptana.editor.common.internal.commands;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;

import junit.framework.TestCase;

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
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.views.contentoutline.ContentOutline;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import com.aptana.editor.common.outline.CommonOutlinePage;

public class ExpandCollapseAllHandlerTest extends TestCase
{

	private static final String HTML_EDITOR_ID = "com.aptana.editor.html";
	private static final String EXPAND_ALL_COMMAND_ID = "com.aptana.editor.commands.ExpandAll";
	private static final String COLLAPSE_ALL_COMMAND_ID = "com.aptana.editor.commands.CollapseAll";
	private static final String PROJECT_NAME = "expand_collapse";

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
		createAndOpenFile("example" + System.currentTimeMillis() + ".html", "<html>\n<head>\n<title>Title goes here</title>\n</head>\n<body>\n<div>\n<p>Hi</p>\n</div>\n</body>");
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
		Thread.sleep(500);
		
		// Grab the handler service to execute our command
		IHandlerService service = (IHandlerService) outline.getSite().getService(IHandlerService.class);
		service.executeCommand(EXPAND_ALL_COMMAND_ID, null);

		// check expansion state, should be expanded
		Object[] expanded = treeViewer.getExpandedElements();
		assertEquals(4, expanded.length); // html, head, body, div

		// toggle expansion
		service.executeCommand(COLLAPSE_ALL_COMMAND_ID, null);

		// check expansion state
		expanded = treeViewer.getExpandedElements();
		assertEquals(0, expanded.length); // collapsed
		
		// toggle expansion
		service.executeCommand(EXPAND_ALL_COMMAND_ID, null);

		// check expansion state
		expanded = treeViewer.getExpandedElements();
		assertEquals(4, expanded.length);  // html, head, body, div
	}

}
