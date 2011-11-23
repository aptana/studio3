/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.contentassist;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.tests.util.TestProject;
import com.aptana.editor.js.contentassist.index.JSFileIndexingParticipant;
import com.aptana.editor.js.contentassist.model.FunctionElement;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.editor.js.tests.JSEditorBasedTests;
import com.aptana.index.core.IFileStoreIndexingParticipant;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;
import com.aptana.scripting.model.BundleElement;
import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.SnippetElement;
import com.aptana.ui.util.UIUtils;

/**
 * JSContentAssistProposalTests
 */
public class JSContentAssistProposalTests extends JSEditorBasedTests
{
	protected void assertAutoActivation(String sourceWithCursors, boolean expectedResult)
	{
		IFileStore fileStore = createFileStore("proposal_tests", "js", sourceWithCursors);

		setupTestContext(fileStore);
		assertEquals(expectedResult, processor.isValidAutoActivationLocation(' ', ' ', document, cursorOffsets.get(0)));
	}

	protected void assertContainsFunctions(Collection<PropertyElement> projectGlobals, String... functionNames)
	{
		Set<String> uniqueFunctionNames = new HashSet<String>(Arrays.asList(functionNames));
		for (PropertyElement element : projectGlobals)
		{
			if (!(element instanceof FunctionElement))
			{
				continue;
			}
			if (uniqueFunctionNames.contains(element.getName()))
			{
				uniqueFunctionNames.remove(element.getName());
			}
		}

		if (!uniqueFunctionNames.isEmpty())
		{
			// build a list of names
			List<String> names = new ArrayList<String>();
			for (PropertyElement element : projectGlobals)
			{
				if (!(element instanceof FunctionElement))
				{
					continue;
				}
				names.add(element.getName());
			}
			fail(MessageFormat.format(
					"Functions do not contain an entry for expected name(s): {0}.\nFunction list: {1}",
					uniqueFunctionNames, names));
		}
	}

	protected void assertDoesntContainFunctions(Collection<PropertyElement> projectGlobals, String... functionNames)
	{
		Set<String> uniqueFunctionNames = new HashSet<String>(Arrays.asList(functionNames));
		Set<String> matches = new HashSet<String>(uniqueFunctionNames.size());
		for (PropertyElement element : projectGlobals)
		{
			if (!(element instanceof FunctionElement))
			{
				continue;
			}
			if (uniqueFunctionNames.contains(element.getName()))
			{
				matches.add(element.getName());
			}
		}

		if (!matches.isEmpty())
		{
			fail(MessageFormat.format("Functions contain an entry for disallowed name(s): {0}", matches));
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.EditorBasedTests#createIndexer()
	 */
	@Override
	protected IFileStoreIndexingParticipant createIndexer()
	{
		return new JSFileIndexingParticipant();
	}

	public void testAutoAactivationCommaWithSpace()
	{
		assertAutoActivation("a(abc, \t\r\n|", true);
	}

	public void testAutoActivationComma()
	{
		assertAutoActivation("a(abc,|", true);
	}

	public void testAutoActivationIdentifier()
	{
		assertAutoActivation("a|(abc,", false);
	}

	public void testAutoActivationIdentifierWithSpace()
	{
		assertAutoActivation("a \t\r\n|(abc,", false);
	}

	public void testAutoActivationLeftParen()
	{
		assertAutoActivation("a(|abc,", true);
	}

	public void testAutoActivationLeftParenWithSpace()
	{
		assertAutoActivation("a( \t\r\n|abc,", true);
	}

	/**
	 * testBug_Math
	 */
	public void testBug_Math()
	{
		// @formatter:off
		this.checkProposals(
			"contentAssist/math.js",
			"E",
			"LN10",
			"LN2",
			"LOG10E",
			"LOG2E",
			"PI",
			"SQRT1_2",
			"SQRT2",
			"abs",
			"acos",
			"asin",
			"atan",
			"atan2",
			"ceil",
			"cos",
			"exp",
			"floor",
			"log",
			"max",
			"min",
			"pow",
			"random",
			"round",
			"sin",
			"sqrt",
			"tan"
		);
		// @formatter:on
	}

	/**
	 * testBug_VarAssignWithEndingDot
	 */
	public void testBug_VarAssignWithEndingDot()
	{
		// @formatter:off
		this.checkProposals(
			"contentAssist/var-assign-with-ending-dot.js",
			"E",
			"LN10",
			"LN2",
			"LOG10E",
			"LOG2E",
			"PI",
			"SQRT1_2",
			"SQRT2",
			"abs",
			"acos",
			"asin",
			"atan",
			"atan2",
			"ceil",
			"cos",
			"exp",
			"floor",
			"log",
			"max",
			"min",
			"pow",
			"random",
			"round",
			"sin",
			"sqrt",
			"tan"
		);
		// @formatter:on
	}

	/**
	 * testObjectLiteral
	 */
	public void testObjectLiteral()
	{
		// @formatter:off
		this.checkProposals(
			"contentAssist/object-literal.js",
			"flag",
			"number"
		);
		// @formatter:on
	}

	/**
	 * <pre>
	 * - We create a file with a function
	 * - open the JS editor on it
	 * - make some unsaved changes
	 * - let it reconcile
	 * - invoke CA to see that the unsaved contents are reflected in the CA
	 * - close the editor without saving those changes
	 * - wait for re-index of the underlying file to occur
	 * - verify that the index now reflects underlying file's contents and not the unsaved changes.
	 * </pre>
	 * 
	 * @throws Exception
	 */
	public void testAPSTUD2944() throws Exception
	{
		final String projectName = "APSTUD2944";
		final String fileName = "apstud2944.js";
		final String initialContents = "function delete_me() {}\n";
		final String workingContents = "function foo() { var eight = 8; }";

		TestProject project = null;
		try
		{
			// Create a test project and file
			project = new TestProject(projectName, new String[] { "com.aptana.projects.webnature" });
			IFile file = project.createFile(fileName, initialContents);

			// open JS editor on file
			editor = (ITextEditor) IDE.openEditor(UIUtils.getActivePage(), file, "com.aptana.editor.js", true);

			// Set the working copy contents to some valid JS
			IDocument document = editor.getDocumentProvider().getDocument(editor.getEditorInput());
			document.set(workingContents);
			// force reconciling? It should get triggered automatically...
			Thread.sleep(1500); // let reconciling finish...

			// get proposals at end of document
			this.processor = new JSContentAssistProcessor((AbstractThemeableEditor) editor);
			ISourceViewer viewer = ((AbstractThemeableEditor) editor).getISourceViewer();
			ICompletionProposal[] proposals = processor.computeCompletionProposals(viewer, 33, '\0', false);

			// verify that CA contains elements from unsaved JS in document!
			assertContains(proposals, "foo");
			assertDoesntContain(proposals, "delete_me");

			// TODO Verify "eight" is in CA inside foo?

			// Close the editor without saving, make sure we end up indexing underlying content again!
			UIUtils.getDisplay().syncExec(new Runnable()
			{

				public void run()
				{
					editor.getSite().getPage().closeEditor(editor, false);
					editor.dispose();
				}
			});
			Thread.sleep(1000); // FIXME Is there anyway to tell when indexing happens and is finished?

			// Now verify that our index reflects the file's contents and not the unsaved contents of the editor.
			Index index = IndexManager.getInstance().getIndex(project.getURI());
			JSIndexQueryHelper _indexHelper = new JSIndexQueryHelper();
			List<PropertyElement> projectGlobals = _indexHelper.getProjectGlobals(index);
			assertContainsFunctions(projectGlobals, "delete_me");
			assertDoesntContainFunctions(projectGlobals, "foo");
		}
		finally
		{
			if (project != null)
			{
				project.delete();
			}
		}
	}

	/**
	 * testStringCharCodeAt
	 */
	public void testStringCharCodeAt()
	{
		this.checkProposals("contentAssist/string-charCodeAt.js", "charCodeAt");
	}

	/**
	 * testStringD
	 */
	public void testStringDPrefix()
	{
		this.checkProposals("contentAssist/d-prefix.js", true, true, "default", "defaultStatus", "delete", "do",
				"document", "Date");
	}

	/**
	 * testStringF
	 * 
	 * @throws IOException
	 */
	public void testStringFPrefix() throws IOException
	{
		File bundleFile = File.createTempFile("editor_unit_tests", "rb");
		bundleFile.deleteOnExit();

		BundleElement bundleElement = new BundleElement(bundleFile.getAbsolutePath());
		bundleElement.setDisplayName("Editor Unit Tests");

		File f = File.createTempFile("snippet", "rb");
		SnippetElement se = createSnippet(f.getAbsolutePath(), "FunctionTemplate", "fun", "source.js");
		bundleElement.addChild(se);
		BundleManager.getInstance().addBundle(bundleElement);

		try
		{
			// note template is before true proposal, as we are ordering by trigger prefix
			this.checkProposals("contentAssist/f-prefix.js", true, true, "FunctionTemplate", "false", "finally",
					"focus", "for", "forward", "frames", "function", "Function");
		}
		finally
		{
			BundleManager.getInstance().unloadScript(f);
		}

	}

	/**
	 * testStringFunction
	 */
	public void testStringFunction()
	{
		this.checkProposals("contentAssist/function.js", true, true, "function", "Function");
	}

	/**
	 * testStringFunction
	 */
	public void testStringFunctionCaseOrder()
	{
		// Commented out for the moment until we resolve an issue with indexing
		// this.checkProposals("contentAssist/function-case-order.js", true, true, "focus", "foo", "fooa", "foob",
		// "for",
		// "forward");
	}

	/**
	 * testStringFunction
	 * 
	 * @throws IOException
	 */
	public void testStringThis() throws IOException
	{
		File bundleFile = File.createTempFile("editor_unit_tests", "rb");
		bundleFile.deleteOnExit();

		BundleElement bundleElement = new BundleElement(bundleFile.getAbsolutePath());
		bundleElement.setDisplayName("Editor Unit Tests");

		File f = File.createTempFile("snippet", "rb");
		SnippetElement se = createSnippet(f.getAbsolutePath(), "$(this)", "this", "source.js");
		bundleElement.addChild(se);
		BundleManager.getInstance().addBundle(bundleElement);

		this.checkProposals("contentAssist/this.js", true, true, "$(this)", "this", "throw");

		BundleManager.getInstance().unloadScript(f);

	}
}
