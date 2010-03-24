package com.aptana.editor.ruby;

import java.io.File;

import junit.framework.TestCase;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

public class RubyEditorTest extends TestCase
{

	private ITextEditor editor;

	@Override
	protected void tearDown() throws Exception
	{
		if (editor != null)
		{
			editor.close(false);
			editor = null;
		}
	}

	public void testExecute() throws Exception
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IFileStore fileStore = getFileStore();
		editor = (ITextEditor) page.openEditor(new FileStoreEditorInput(fileStore), getEditorId());
		assertNotNull(editor);
		assertEquals(getClassName(), editor.getClass().getName());
	}

	protected IFileStore getFileStore() throws Exception
	{
		return EFS.getStore((new File("test.rb")).toURI());
	}

	protected String getEditorId()
	{
		return "com.aptana.editor.ruby";
	}

	protected String getClassName()
	{
		return RubySourceEditor.class.getName();
	}
}
