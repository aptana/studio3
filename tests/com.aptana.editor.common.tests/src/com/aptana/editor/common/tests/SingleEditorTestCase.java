/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.tests;

import org.junit.After;
import org.junit.Before;
import static org.junit.Assert.*;
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

import com.aptana.editor.epl.tests.EditorTestHelper;

public abstract class SingleEditorTestCase
{

	private IProject project;
	private IFile file;
	private ITextEditor editor;

//	@Override
	@Before
	public void setUp() throws Exception
	{
//		super.setUp();
		project = createProject();
	}

//	@Override
	@After
	public void tearDown() throws Exception
	{
		try
		{
			// Need to force the editor shut!
			if (editor != null)
			{
				EditorTestHelper.closeEditor(editor);
			}
			// Delete the generated file
			if (file != null)
				file.delete(true, new NullProgressMonitor());
			// Delete the generated project FIXME This hangs if we try to do it, probably because of UI thread running sync. So for now, comment it out
//			project.delete(true, new NullProgressMonitor());
		}
		finally
		{
			editor = null;
			file = null;
			project = null;
//			super.tearDown();
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
		IProject project = workspace.getRoot().getProject(getProjectName());
		if (!project.exists())
			project.create(new NullProgressMonitor());
		if (!project.isOpen())
			project.open(new NullProgressMonitor());
		return project;
	}

	/**
	 * Project name to use for the test we're setting up. Typically one project per test class.
	 * 
	 * @return
	 */
	protected abstract String getProjectName();

	protected void setCaretOffset(int offset) throws PartInitException
	{
		getTextWidget().setCaretOffset(offset);
	}

	protected StyledText getTextWidget() throws PartInitException
	{
		ITextViewer adapter = (ITextViewer) getEditor().getAdapter(ITextOperationTarget.class);
		return adapter.getTextWidget();
	}

	protected ITextEditor getEditor() throws PartInitException
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

	protected void select(int offset, int length) throws PartInitException
	{
		setCaretOffset(offset);
		getEditor().selectAndReveal(offset, length);
	}

	protected void assertContents(String expected) throws PartInitException
	{
		assertEquals(expected, getTextWidget().getText());
	}
}
