package com.aptana.editor.common.internal.scripting;

import java.io.File;
import java.io.FileWriter;

import junit.framework.TestCase;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.ICommonConstants;
import com.aptana.editor.common.scripting.IContentTypeTranslator;
import com.aptana.editor.common.scripting.QualifiedContentType;
import com.aptana.editor.common.scripting.commands.TextEditorUtils;
import com.aptana.ui.UIUtils;

public class DocumentScopeManagerTest extends TestCase
{

	private DocumentScopeManager manager;

	protected void setUp() throws Exception
	{
		super.setUp();
		IContentTypeTranslator c = CommonEditorPlugin.getDefault().getContentTypeTranslator();
		c.addTranslation(new QualifiedContentType(ICommonConstants.CONTENT_TYPE_UKNOWN), new QualifiedContentType(
				"text")); //$NON-NLS-1$
		manager = new DocumentScopeManager();
	}

	protected void tearDown() throws Exception
	{
		manager = null;
		super.tearDown();
	}

	public void testGetScopeAtOffsetDocument() throws Exception
	{
		IDocument document = new Document("src");
		assertEquals("text", manager.getScopeAtOffset(document, 0));
	}

	public void testGetContentTypeNullDocument() throws Exception
	{
		assertEquals(new QualifiedContentType(ICommonConstants.CONTENT_TYPE_UKNOWN), manager.getContentType(null, 0));
	}

	public void testGetContentType() throws Exception
	{
		IDocument document = new Document("src")
		{
			@Override
			public String getContentType(int offset) throws BadLocationException
			{
				return "source.ruby";
			}
		};
		assertEquals(new QualifiedContentType(ICommonConstants.CONTENT_TYPE_UKNOWN).subtype("source.ruby"),
				manager.getContentType(document, 0));
	}

	public void testGetContentTypeAfterSettingDocumentScope() throws Exception
	{
		IDocument document = new Document("src")
		{
			@Override
			public String getContentType(int offset) throws BadLocationException
			{
				return "string.quoted.double.ruby";
			}
		};
		manager.setDocumentScope(document, "source.ruby", "chris.rb");
		assertEquals(new QualifiedContentType("source.ruby").subtype("string.quoted.double.ruby"),
				manager.getContentType(document, 0));
	}

	/**
	 * This version will return token level Scopes.
	 * 
	 * @throws Exception
	 */
	public void testGetScopeAtOffsetSourceViewer() throws Exception
	{
		ITextEditor editor = null;
		File file = null;
		try
		{
			IWorkbenchPage page = UIUtils.getActivePage();
			file = File.createTempFile("testing", ".rb");
			FileWriter writer = new FileWriter(file);
			writer.write("require 'something'\n\ndef method_name\n  @var = 1\nend\n");
			writer.close();

			IEditorPart part = IDE.openEditorOnFileStore(page, EFS.getLocalFileSystem().fromLocalFile(file));
			editor = (ITextEditor) part;
			ISourceViewer viewer = TextEditorUtils.getSourceViewer(editor);

			assertEquals("source.ruby.rails keyword.control.def.ruby", CommonEditorPlugin.getDefault()
					.getDocumentScopeManager().getScopeAtOffset(viewer, 22));
			assertEquals("source.ruby.rails variable.other.readwrite.instance.ruby", CommonEditorPlugin.getDefault()
					.getDocumentScopeManager().getScopeAtOffset(viewer, 41));
		}
		finally
		{
			if (editor != null)
			{
				editor.close(false);
			}
			if (file != null)
			{
				if (!file.delete())
				{
					file.deleteOnExit();
				}
			}
		}
	}

	/**
	 * This level gives back only partition level scopes.
	 * 
	 * @throws Exception
	 */
	public void testGetScopeAtOffsetDoc() throws Exception
	{
		ITextEditor editor = null;
		File file = null;
		try
		{
			IWorkbenchPage page = UIUtils.getActivePage();
			file = File.createTempFile("testing", ".rb");
			FileWriter writer = new FileWriter(file);
			writer.write("require 'something'\n\ndef method_name\n  @var = 1\nend\n");
			writer.close();

			IEditorPart part = IDE.openEditorOnFileStore(page, EFS.getLocalFileSystem().fromLocalFile(file));
			editor = (ITextEditor) part;
			ISourceViewer viewer = TextEditorUtils.getSourceViewer(editor);

			assertEquals("source.ruby.rails string.quoted.single.ruby", CommonEditorPlugin.getDefault()
					.getDocumentScopeManager().getScopeAtOffset(viewer.getDocument(), 12));
		}
		finally
		{
			if (editor != null)
			{
				editor.close(false);
			}
			if (file != null)
			{
				if (!file.delete())
				{
					file.deleteOnExit();
				}
			}
		}
	}
}
