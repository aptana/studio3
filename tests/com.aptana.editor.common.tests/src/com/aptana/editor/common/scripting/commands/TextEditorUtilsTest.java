package com.aptana.editor.common.scripting.commands;

import java.io.ByteArrayInputStream;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;

public class TextEditorUtilsTest extends TestCase
{
	private static final String PROJECT_NAME = "text_editors_util";

	private IProject project;
	private IFile file;
	private ITextEditor editor;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		project = createProject();
	}

	@Override
	protected void tearDown() throws Exception
	{
		try
		{
			// Need to force the editor shut!
			if (editor != null)
				editor.close(false);
			// Delete the generated file
			if (file != null)
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

	public void testNonZeroCaretOffset() throws Exception
	{
		createAndOpenFile("non_zero_caret.txt", "Hello world!");
		setCaretOffset(5);
		assertEquals(5, TextEditorUtils.getCaretOffset(editor));
	}

	public void testCaretOffsetWithNull()
	{
		assertEquals(-1, TextEditorUtils.getCaretOffset(null));
	}

	public void testCaretOffset() throws Exception
	{
		createAndOpenFile("newfile.txt", "This is a brand new file!");
		assertEquals(0, TextEditorUtils.getCaretOffset(editor));
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
		if (!project.exists())
			project.create(new NullProgressMonitor());
		if (!project.isOpen())
			project.open(new NullProgressMonitor());
		return project;
	}

	protected void setCaretOffset(int offset) throws PartInitException
	{
		getTextWidget().setCaretOffset(offset);
	}

	protected StyledText getTextWidget() throws PartInitException
	{
		ITextViewer adapter = (ITextViewer) getEditor().getAdapter(ITextOperationTarget.class);
		return adapter.getTextWidget();
	}

	private ITextEditor getEditor() throws PartInitException
	{
		if (editor == null)
		{
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			editor = (ITextEditor) IDE.openEditor(page, file);
		}
		return editor;
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
}
