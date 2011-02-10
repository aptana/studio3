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
import com.aptana.ui.util.UIUtils;

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
			file = File.createTempFile("testing", ".js");
			FileWriter writer = new FileWriter(file);
			writer.write("if(Object.isUndefined(Effect))\nthrow(\"dragdrop.js requires including script.aculo.us' effects.js library\");");
			writer.close();

			IEditorPart part = IDE.openEditorOnFileStore(page, EFS.getLocalFileSystem().fromLocalFile(file));
			editor = (ITextEditor) part;
			ISourceViewer viewer = TextEditorUtils.getSourceViewer(editor);

			assertEquals("source.js keyword.control.js", CommonEditorPlugin.getDefault().getDocumentScopeManager()
					.getScopeAtOffset(viewer, 1));
			assertEquals("source.js support.class.js", CommonEditorPlugin.getDefault().getDocumentScopeManager()
					.getScopeAtOffset(viewer, 7));
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
	
	public void testOffByOneBug() throws Exception
	{
		ITextEditor editor = null;
		File file = null;
		try
		{
			IWorkbenchPage page = UIUtils.getActivePage();
			file = File.createTempFile("testing", ".html");
			FileWriter writer = new FileWriter(file);
			writer.write("<html>\n  <head>\n" +
					"    <style type=\"text/css\">\n" +
					"    h1 { color: #f00; }\n" +
					"  </style>\n" +
					"</head>\n" +
					"<body>\n" +
					"</html>");
			writer.close();

			IEditorPart part = IDE.openEditorOnFileStore(page, EFS.getLocalFileSystem().fromLocalFile(file));
			editor = (ITextEditor) part;
			ISourceViewer viewer = TextEditorUtils.getSourceViewer(editor);

			assertEquals("text.html.basic meta.tag.block.any.html string.quoted.double.html", CommonEditorPlugin.getDefault().getDocumentScopeManager()
					.getScopeAtOffset(viewer, 32));
			assertEquals("text.html.basic meta.tag.block.any.html string.quoted.double.html", CommonEditorPlugin.getDefault().getDocumentScopeManager()
					.getScopeAtOffset(viewer, 41));
			assertEquals("text.html.basic meta.tag.block.any.html punctuation.definition.tag.end.html", CommonEditorPlugin.getDefault().getDocumentScopeManager()
					.getScopeAtOffset(viewer, 42));
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
			file = File.createTempFile("testing", ".js");
			FileWriter writer = new FileWriter(file);
			writer.write("if(Object.isUndefined(Effect))\nthrow(\"dragdrop.js requires including script.aculo.us' effects.js library\");");
			writer.close();

			IEditorPart part = IDE.openEditorOnFileStore(page, EFS.getLocalFileSystem().fromLocalFile(file));
			editor = (ITextEditor) part;
			ISourceViewer viewer = TextEditorUtils.getSourceViewer(editor);

			assertEquals("source.js",
					CommonEditorPlugin.getDefault().getDocumentScopeManager().getScopeAtOffset(viewer.getDocument(), 1));
			assertEquals("source.js",
					CommonEditorPlugin.getDefault().getDocumentScopeManager().getScopeAtOffset(viewer.getDocument(), 7));
			assertEquals("source.js string.quoted.double.js", CommonEditorPlugin.getDefault()
					.getDocumentScopeManager().getScopeAtOffset(viewer.getDocument(), 50));
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
