package com.aptana.editor.erb.common;

import junit.framework.TestCase;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

public abstract class ERBEditorTestCase extends TestCase
{

	private ITextEditor editor;

	@Override
	protected void tearDown() throws Exception
	{
		if (editor != null)
		{
			editor.close(false);
		}
	}

	public void testExecute() throws Exception
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		editor = (ITextEditor) page.openEditor(new FileStoreEditorInput(getFileStore()), getEditorId(), false,
				IWorkbenchPage.MATCH_INPUT);
		assertNotNull(editor);
		assertEquals(getClassName(), editor.getClass().getName());
	}

	protected abstract IFileStore getFileStore() throws Exception;

	protected abstract String getEditorId();

	protected abstract String getClassName();
}
