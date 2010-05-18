package com.aptana.editor.js.contentassist;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import junit.framework.TestCase;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.core.util.ResourceUtil;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.contentassist.LexemeProvider;
import com.aptana.editor.js.Activator;
import com.aptana.editor.js.contentassist.JSContentAssistProcessor.Location;
import com.aptana.editor.js.parsing.lexer.JSTokenType;

public class LocationTests extends TestCase
{
	private static final String CURSOR_TAG = "${cursor}";

	/**
	 * createEditor
	 * 
	 * @return
	 * @throws PartInitException 
	 */
	protected ITextEditor createEditor(IFileStore file)
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		FileStoreEditorInput editorInput = new FileStoreEditorInput(file);
		ITextEditor editor = null;
		
		try
		{
			editor = (ITextEditor) page.openEditor(editorInput, Activator.PLUGIN_ID);
		}
		catch (PartInitException e)
		{
			fail(e.getMessage());
		}

		assertNotNull(editor);
		
		return editor;
	}

	/**
	 * getFileStore
	 * 
	 * @param resource
	 * @return
	 */
	protected IFileStore getFileStore(String resource)
	{
		Path path = new Path(resource);
		IFileStore result = null;

		try
		{
			URL url = FileLocator.find(Activator.getDefault().getBundle(), path, null);
			URL fileURL = FileLocator.toFileURL(url);
			URI fileURI = ResourceUtil.toURI(fileURL);

			result = EFS.getStore(fileURI);
		}
		catch (IOException e)
		{
			fail(e.getMessage());
		}
		catch (URISyntaxException e)
		{
			fail(e.getMessage());
		}
		catch (CoreException e)
		{
			fail(e.getMessage());
		}

		assertNotNull(result);

		return result;
	}

	/**
	 * testEmptyArgs
	 * 
	 * @throws BadLocationException 
	 */
	public void testEmptyArgs() throws BadLocationException
	{
		IFileStore file = this.getFileStore("locations/global_in_arg.js");
		ITextEditor editor = this.createEditor(file);
		assertTrue(editor instanceof AbstractThemeableEditor);
		
		IDocument document = editor.getDocumentProvider().getDocument(editor.getEditorInput());
		
		String source = document.get();
		int offset = source.lastIndexOf(")");
		assertTrue(offset != -1);
		
		JSContentAssistProcessor processor = new JSContentAssistProcessor((AbstractThemeableEditor) editor);
		LexemeProvider<JSTokenType> lexemeProvider = processor.createLexemeProvider(document, offset);

		Location location = processor.getLocation(lexemeProvider, offset);
		assertEquals(Location.IN_GLOBAL, location);
	}
}
