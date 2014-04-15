package com.aptana.editor.common.internal.scripting;

import org.junit.After;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.core.tests.TestProject;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.ICommonConstants;
import com.aptana.editor.common.scripting.IContentTypeTranslator;
import com.aptana.editor.common.scripting.IDocumentScopeManager;
import com.aptana.editor.common.scripting.QualifiedContentType;
import com.aptana.editor.common.scripting.commands.TextEditorUtils;
import com.aptana.editor.epl.tests.EditorTestHelper;
import com.aptana.ui.util.UIUtils;

public class DocumentScopeManagerTest
{

	private IDocumentScopeManager manager;
	private ITextEditor editor;
	// File we may be opening if not inside project
	private File file;
	private TestProject project;

	@Before
	public void setUp() throws Exception
	{
//		super.setUp();
		setUpBasicScopes();

		EditorTestHelper.closeAllEditors();
	}

	protected void setUpBasicScopes()
	{
		IContentTypeTranslator c = getContentTypeTranslator();
		c.addTranslation(new QualifiedContentType(ICommonConstants.CONTENT_TYPE_UKNOWN), new QualifiedContentType(
				"text")); //$NON-NLS-1$
		manager = new DocumentScopeManager();
	}

	protected void setUpStandardScopes()
	{
		manager = CommonEditorPlugin.getDefault().getDocumentScopeManager();
	}

	@After
	public void tearDown() throws Exception
	{
		if (editor != null)
		{
			EditorTestHelper.closeEditor(editor);
			editor = null;
		}

		if (file != null)
		{
			if (!file.delete())
			{
				file.deleteOnExit();
			}
		}

		if (project != null)
		{
			project.delete();
			project = null;
		}

		manager = null;
//		super.tearDown();
	}

	protected IContentTypeTranslator getContentTypeTranslator()
	{
		return CommonEditorPlugin.getDefault().getContentTypeTranslator();
	}

	protected IDocumentScopeManager getDocumentScopeManager()
	{
		return manager;
	}

	protected void assertScope(String scope, int offset) throws BadLocationException
	{
		assertEquals("Scope doesn't match", scope,
				getDocumentScopeManager().getScopeAtOffset(TextEditorUtils.getSourceViewer(editor), offset));
	}

	protected void assertScope(String scope, int offset, IDocument document) throws BadLocationException
	{
		assertEquals("Scope doesn't match", scope, getDocumentScopeManager().getScopeAtOffset(document, offset));
	}

	protected void createAndOpenFile(String filename, String extension, String content) throws IOException,
			PartInitException
	{
		file = File.createTempFile(filename, extension);
		FileWriter writer = new FileWriter(file);
		writer.write(content);
		writer.close();

		editor = (ITextEditor) IDE.openEditorOnFileStore(UIUtils.getActivePage(), EFS.getLocalFileSystem()
				.fromLocalFile(file));
	}

	// ---------------------------------------------------------------------------------------------------------

	@Test
	public void testGetScopeAtOffsetDocument() throws Exception
	{
		assertScope("text", 0, new Document("src"));
	}

	@Test
	public void testGetContentTypeNullDocument() throws Exception
	{
		assertEquals(new QualifiedContentType(ICommonConstants.CONTENT_TYPE_UKNOWN), getDocumentScopeManager()
				.getContentType(null, 0));
	}

	@Test
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
				getDocumentScopeManager().getContentType(document, 0));
	}

	@Test
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
		getDocumentScopeManager().setDocumentScope(document, "source.ruby", "chris.rb");
		assertEquals(new QualifiedContentType("source.ruby").subtype("string.quoted.double.ruby"),
				getDocumentScopeManager().getContentType(document, 0));
	}

	/**
	 * This version will return token level Scopes.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetScopeAtOffsetSourceViewer() throws Exception
	{
		setUpStandardScopes();

		createAndOpenFile("testing", ".js",
				"if(Object.isUndefined(Effect))\nthrow(\"dragdrop.js requires including script.aculo.us' effects.js library\");");

		editor = (ITextEditor) IDE.openEditorOnFileStore(UIUtils.getActivePage(), EFS.getLocalFileSystem()
				.fromLocalFile(file));

		assertScope("source.js keyword.control.js", 1);
		assertScope("source.js support.class.js", 7);
	}

	@Test
	public void testGetScopeWithMetaProjectNaturePrepended() throws Exception
	{
		setUpStandardScopes();

		project = new TestProject("scope_nature", new String[] { "com.aptana.projects.webnature" });

		IFile iFile = project
				.createFile("project_scope.js",
						"if(Object.isUndefined(Effect))\nthrow(\"dragdrop.js requires including script.aculo.us' effects.js library\");");
		editor = (ITextEditor) EditorTestHelper.openInEditor(iFile, true);

		assertScope("meta.project.com.aptana.projects.webnature source.js keyword.control.js", 1);
		assertScope("meta.project.com.aptana.projects.webnature source.js support.class.js", 7);
	}

	@Test
	public void testGetScopeAtEndOfFile() throws Exception
	{
		setUpStandardScopes();

		createAndOpenFile("eof_scope", ".js", "// This is a comment");

		assertScope("source.js comment.line.double-slash.js", 2);
		assertScope("source.js comment.line.double-slash.js", 20);
	}

	@Test
	public void testOffByOneBug() throws Exception
	{
		setUpStandardScopes();

		createAndOpenFile("testing", ".html", "<html>\n  <head>\n" + "    <style type=\"text/css\">\n"
				+ "    h1 { color: #f00; }\n" + "  </style>\n" + "</head>\n" + "<body>\n" + "</html>");

		editor = (ITextEditor) IDE.openEditorOnFileStore(UIUtils.getActivePage(), EFS.getLocalFileSystem()
				.fromLocalFile(file));

		assertScope("text.html.basic meta.tag.block.any.html string.quoted.double.html", 32);
		assertScope("text.html.basic meta.tag.block.any.html string.quoted.double.html", 41);
		assertScope("text.html.basic meta.tag.block.any.html punctuation.definition.tag.end.html", 42);
	}

	/**
	 * This level gives back only partition level scopes.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetScopeAtOffsetDoc() throws Exception
	{
		setUpStandardScopes();

		createAndOpenFile("testing", ".js",
				"if(Object.isUndefined(Effect))\nthrow(\"dragdrop.js requires including script.aculo.us' effects.js library\");");
		ISourceViewer viewer = TextEditorUtils.getSourceViewer(editor);

		assertScope("source.js", 1, viewer.getDocument());
		assertScope("source.js", 7, viewer.getDocument());
		assertScope("source.js string.quoted.double.js", 50, viewer.getDocument());
	}
}
