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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.contentassist.ICommonCompletionProposal;
import com.aptana.editor.common.tests.util.TestProject;
import com.aptana.editor.epl.tests.EditorTestHelper;
import com.aptana.editor.js.contentassist.index.JSFileIndexingParticipant;
import com.aptana.editor.js.contentassist.model.FunctionElement;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.editor.js.tests.JSEditorBasedTests;
import com.aptana.index.core.IFileStoreIndexingParticipant;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;
import com.aptana.index.core.IndexPlugin;
import com.aptana.index.core.build.BuildContext;
import com.aptana.scripting.model.BundleElement;
import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.SnippetElement;

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
			editor = (ITextEditor) EditorTestHelper.openInEditor(file, "com.aptana.editor.js", true);
			ISourceViewer viewer = ((AbstractThemeableEditor) editor).getISourceViewer();

			EditorTestHelper.joinReconciler((SourceViewer) viewer, 100L, 2000L, 100L);

			// Verify initial contents
			Index index = getIndexManager().getIndex(project.getURI());

			JSIndexQueryHelper _indexHelper = new JSIndexQueryHelper();
			List<PropertyElement> projectGlobals = _indexHelper.getProjectGlobals(index);
			assertContainsFunctions(projectGlobals, "delete_me");
			assertDoesntContainFunctions(projectGlobals, "foo");

			// Set the working copy contents to some new valid JS
			IDocument document = EditorTestHelper.getDocument(editor);
			document.set(workingContents);

			// Wait for reconcile
			EditorTestHelper.joinReconciler((SourceViewer) viewer, 100L, 2000L, 100L);

			// get proposals at end of document
			this.processor = new JSContentAssistProcessor((AbstractThemeableEditor) editor);
			ICompletionProposal[] proposals = processor.computeCompletionProposals(viewer, 33, '\0', false);

			// verify that CA contains elements from unsaved JS in document!
			assertContains(proposals, "foo");
			assertDoesntContain(proposals, "delete_me");

			// TODO Verify "eight" is in CA inside foo?

			// Close the editor without saving, make sure we end up indexing underlying content again!
			EditorTestHelper.closeEditor(editor);

			Thread.sleep(1000); // FIXME Is there anyway to tell when indexing happens and is finished?

			// Now verify that our index reflects the file's contents and not the unsaved contents of the editor.
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

	protected IndexManager getIndexManager()
	{
		return IndexPlugin.getDefault().getIndexManager();
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

	public void testDottedConstructor()
	{
		this.checkProposals("contentAssist/dotted-constructor.js", "aptana");
	}

	public void testAPSTUD3694()
	{
		// @formatter:off
		this.checkProposals(
			"contentAssist/document.js",
			"anchors",
			"applets",
			"body",
			"cookie",
			"documentMode",
			"domain",
			"forms",
			"images",
			"lastModified",
			"links",
			"readyState",
			"referrer",
			"title",
			"URL",
			"close",
			"getElementsByName",
			"open",
			"write",
			"writeln"
		);
		// @formatter:on
	}

	public void testAPSTUD3695()
	{
		// @formatter:off
		this.checkProposals(
			"contentAssist/window.js",
			"closed",
			"defaultStatus",
			"document",
			"frames",
			"history",
			"innerHeight",
			"innerWidth",
			"length",
			"location",
			"name",
			"navigator",
			"opener",
			"outerHeight",
			"outerWidth",
			"pageXOffset",
			"pageYOffset",
			"parent",
			"screen",
			"screenLeft",
			"screenTop",
			"screenX",
			"screenY",
			"self",
			"status",
			"top",
			"alert",
			"blur",
			"clearInterval",
			"clearTimeout",
			"close",
			"confirm",
			"createPopup",
			"focus",
			"moveBy",
			"moveTo",
			"open",
			"print",
			"prompt",
			"resizeBy",
			"resizeTo",
			"scroll",
			"scrollBy",
			"scrollTo",
			"setInterval",
			"setTimeout"
		);
		// @formatter:on
	}

	public void testThisInFunction()
	{
		// @formatter:off
		this.checkProposals(
			"contentAssist/function-with-this.js",
			"property",
			"method"
		);
		// @formatter:on
	}

	public void testThisInFunctionAndPrototypes()
	{
		// @formatter:off
		this.checkProposals(
				"contentAssist/functions-prototype-with-this.js",
				"name",
				"id",
				"company",
				"zipcode"
				);
		// @formatter:on
	}

	public void testUnnamedFunctionWithThis()
	{
		// @formatter:off
		this.checkProposals(
				"contentAssist/unnamed-function-with-this.js",
				"name",
				"id"
				);
		// @formatter:on
	}

	public void testThisInCurrentFunctionOnly()
	{
		// @formatter:off
		this.checkProposals(
			"contentAssist/functions-with-this.js",
			"property",
			"method"
		);
		// @formatter:on
	}

	public void testThisInNestedFunction()
	{
		// @formatter:off
		this.checkProposals(
			"contentAssist/nested-functions-with-this.js",
			"ghi",
			"jkl"
		);
		// @formatter:on
	}

	public void testAPSTUD4538() throws Exception
	{
		final String projectName = "APSTUD4538";
		final String fileName = "apstud4538.js";
		final String initialContents = "function foo() {}\n";

		TestProject project = null;
		try
		{
			// Create a test project and file
			project = new TestProject(projectName, new String[] { "com.aptana.projects.webnature" });
			IFile file = project.createFile(fileName, initialContents);

			// open JS editor on file
			editor = (ITextEditor) EditorTestHelper.openInEditor(file, "com.aptana.editor.js", true);
			ISourceViewer viewer = ((AbstractThemeableEditor) editor).getISourceViewer();

			EditorTestHelper.joinReconciler((SourceViewer) viewer, 100L, 2000L, 100L);

			// get proposals at end of document
			this.processor = new JSContentAssistProcessor((AbstractThemeableEditor) editor);
			ICompletionProposal[] proposals = processor.computeCompletionProposals(viewer, 18, '\0', false);

			// find proposal for "foo" function
			ICompletionProposal prop = findProposal(proposals, "foo");
			assertNotNull("Failed to find 'foo' function proposal in CA", prop);

			// Verify that the file location is the filename, not the owning type.
			ICommonCompletionProposal p2 = (ICommonCompletionProposal) prop;
			assertEquals("Expected 'location' to show filename, not owning type", fileName, p2.getFileLocation());
		}
		finally
		{
			if (project != null)
			{
				project.delete();
			}
		}
	}

	public void testParameterInsideFunction()
	{
		// @formatter:off
		this.checkProposals(
			"contentAssist/param-inside-function.js",
			"myParam",
			"myFunction"
		);
		// @formatter:on
	}

	// APSTUD-4206
	public void testUndefinedFunctionReturnShowsObjectProposals()
	{
		// @formatter:off
		this.checkProposals(
			"contentAssist/undefined-function-return.js",
			"constructor",
			"eval",
			"hasOwnProperty",
			"isPrototypeOf",
			"propertyIsEnumerable",
			"toSource",
			"toLocaleString",
			"toString",
			"unwatch",
			"valueOf",
			"watch"
		);
		// @formatter:on
	}

	public void testDontShowStringConstructorOffInstance()
	{
		ICompletionProposal[] proposals = getProposals("contentAssist/string-constructor-off-instance.js");

		assertDoesntContain(proposals, "String", "Object");
		assertContains(proposals, "charAt", "indexOf", "toLowerCase");
	}

	public void testInstanceMethodDefinedOnPrototypeOffInstance()
	{
		ICompletionProposal[] proposals = getProposals("contentAssist/instance-method-off-instance.js");

		assertDoesntContain(proposals, "download");
		assertContains(proposals, "play");
	}

	public void testDontShowStaticMethodOffInstance()
	{
		ICompletionProposal[] proposals = getProposals("contentAssist/static-method-off-instance.js");

		assertDoesntContain(proposals, "play");
		assertContains(proposals, "download");
	}

	// https://jira.appcelerator.org/browse/APSTUD-4017
	public void testOffersCAOnMultipleTypesInferredForSameVariable() throws Exception
	{
		TestProject project = null;
		try
		{
			// Create a test project and files
			project = new TestProject("APSTUD4017", new String[] { "com.aptana.projects.webnature" });
			IFile number = project.createFile("apstud4017_number.js", "var abc = 10;");
			IFile string = project.createFile("apstud4017_string.js", "var abc = \"hello\";");

			// Index the files
			index(number, string);

			// Now create a third file to open in the editor
			IFile file = project.createFile("apstud4017.js", "abc.");

			// open JS editor on file
			editor = (ITextEditor) EditorTestHelper.openInEditor(file, "com.aptana.editor.js", true);
			ISourceViewer viewer = ((AbstractThemeableEditor) editor).getISourceViewer();

			EditorTestHelper.joinReconciler((SourceViewer) viewer, 100L, 2000L, 100L);

			// get proposals after "abc."
			this.processor = new JSContentAssistProcessor((AbstractThemeableEditor) editor);
			ICompletionProposal[] proposals = processor.computeCompletionProposals(viewer, 4, '\0', false);

			// make sure we get Number proposals (from first file's defining type as number)
			assertContains(proposals, "toFixed", "toExponential", "toPrecision");
			// make sure we get String proposals (from second file's defining type as string)
			assertContains(proposals, "charAt", "concat", "indexOf", "length", "toUpperCase", "toLowerCase");
		}
		finally
		{
			if (project != null)
			{
				project.delete();
			}
		}
	}

	protected void index(IFile... files) throws CoreException
	{
		IFileStoreIndexingParticipant part = createIndexer();
		if (part != null)
		{
			for (IFile file : files)
			{
				part.index(new BuildContext(file), getIndexManager().getIndex(file.getProject().getLocationURI()), null);
			}
		}
	}

}
