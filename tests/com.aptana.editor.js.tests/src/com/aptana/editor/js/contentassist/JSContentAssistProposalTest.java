/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.contentassist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

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
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.texteditor.ITextEditor;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import com.aptana.core.tests.TestProject;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.contentassist.ICommonCompletionProposal;
import com.aptana.editor.epl.tests.EditorTestHelper;
import com.aptana.editor.js.tests.JSEditorBasedTestCase;
import com.aptana.index.core.IFileStoreIndexingParticipant;
import com.aptana.index.core.Index;
import com.aptana.index.core.build.BuildContext;
import com.aptana.js.core.index.JSFileIndexingParticipant;
import com.aptana.js.core.index.JSIndexQueryHelper;
import com.aptana.js.core.model.FunctionElement;
import com.aptana.js.core.model.PropertyElement;
import com.aptana.scripting.model.BundleElement;
import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.SnippetElement;

/**
 * JSContentAssistProposalTests
 */
public class JSContentAssistProposalTest extends JSEditorBasedTestCase
{

	@Rule
	public TestName name = new TestName();
	private TestProject project;

	@Override
	public void tearDown() throws Exception
	{
		if (project != null)
		{
			getIndexManager().removeIndex(project.getURI());
			project.delete();
		}
		super.tearDown();
	}

	private TestProject createTestProject() throws CoreException
	{
		return new TestProject(name.getMethodName(), new String[] { "com.aptana.projects.webnature" });
	}

	private void index(IFile... files) throws CoreException
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

	private ICompletionProposal[] openAndGetProposals(IFile file, int offset) throws PartInitException
	{
		// open JS editor on file
		editor = (ITextEditor) EditorTestHelper.openInEditor(file, "com.aptana.editor.js", true);
		ISourceViewer viewer = ((AbstractThemeableEditor) editor).getISourceViewer();

		EditorTestHelper.joinReconciler((SourceViewer) viewer, 100L, 2000L, 100L);

		// get proposals after "rocker."
		this.processor = new JSContentAssistProcessor((AbstractThemeableEditor) editor);
		ICompletionProposal[] proposals = processor.computeCompletionProposals(viewer, offset, '\0', false);
		return proposals;
	}

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

	@Test
	public void testAutoAactivationCommaWithSpace()
	{
		assertAutoActivation("a(abc, \t\r\n|", true);
	}

	@Test
	public void testAutoActivationComma()
	{
		assertAutoActivation("a(abc,|", true);
	}

	@Test
	public void testAutoActivationIdentifier()
	{
		assertAutoActivation("a|(abc,", false);
	}

	@Test
	public void testAutoActivationIdentifierWithSpace()
	{
		assertAutoActivation("a \t\r\n|(abc,", false);
	}

	@Test
	public void testAutoActivationLeftParen()
	{
		assertAutoActivation("a(|abc,", true);
	}

	@Test
	public void testAutoActivationLeftParenWithSpace()
	{
		assertAutoActivation("a( \t\r\n|abc,", true);
	}

	/**
	 * testBug_Math
	 */
	@Test
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
	@Test
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
	@Test
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
	@Test
	public void testAPSTUD2944() throws Exception
	{
		// Create a test project and file
		project = createTestProject();
		IFile file = project.createFile("apstud2944.js", "function delete_me() {}\n");

		// open JS editor on file
		editor = (ITextEditor) EditorTestHelper.openInEditor(file, "com.aptana.editor.js", true);
		ISourceViewer viewer = ((AbstractThemeableEditor) editor).getISourceViewer();

		EditorTestHelper.joinReconciler((SourceViewer) viewer, 100L, 2000L, 100L);

		// Verify initial contents
		Index index = getIndexManager().getIndex(project.getURI());

		JSIndexQueryHelper _indexHelper = new JSIndexQueryHelper(project.getInnerProject());
		Collection<PropertyElement> projectGlobals = _indexHelper.getGlobals("apstud2944.js");
		assertContainsFunctions(projectGlobals, "delete_me");
		assertDoesntContainFunctions(projectGlobals, "foo");

		// Set the working copy contents to some new valid JS
		IDocument document = EditorTestHelper.getDocument(editor);
		document.set("function foo() { var eight = 8; }");

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

	/**
	 * testStringCharCodeAt
	 */
	@Test
	public void testStringCharCodeAt()
	{
		this.checkProposals("contentAssist/string-charCodeAt.js", "charCodeAt");
	}

	/**
	 * testStringD
	 */
	@Test
	public void testStringDPrefix()
	{
		this.checkProposals("contentAssist/d-prefix.js", true, true, "decodeURI", "decodeURIComponent", "default",
				"defaultStatus", "delete", "do", "document", "Date");
	}

	/**
	 * testStringF
	 * 
	 * @throws IOException
	 */
	@Test
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
	@Test
	public void testStringFunction()
	{
		this.checkProposals("contentAssist/function.js", true, true, "function", "Function");
	}

	/**
	 * testStringFunction
	 */
	@Test
	@Ignore("Commented out for the moment until we resolve an issue with indexing")
	public void testStringFunctionCaseOrder()
	{
		this.checkProposals("contentAssist/function-case-order.js", true, true, "focus", "foo", "fooa", "foob", "for",
				"forward");
	}

	/**
	 * testStringFunction
	 * 
	 * @throws IOException
	 */
	@Test
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

	@Test
	public void testDottedConstructor()
	{
		this.checkProposals("contentAssist/dotted-constructor.js", "aptana");
	}

	@Test
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

	@Test
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

	@Test
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

	@Test
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

	@Test
	public void testThisInFunctionAndPrototypes2()
	{
		// @formatter:off
		this.checkProposals(
				"contentAssist/functions-prototype-with-this-2.js",
				"name",
				"id",
				"company",
				"zipcode"
				);
		// @formatter:on
	}

	@Test
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

	@Test
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

	@Test
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

	@Test
	public void testAPSTUD4538() throws Exception
	{
		final String fileName = "apstud4538.js";

		project = createTestProject();
		IFile file = project.createFile(fileName, "function foo() {}\n");

		ICompletionProposal[] proposals = openAndGetProposals(file, 18);

		// find proposal for "foo" function
		ICompletionProposal prop = findProposal(proposals, "foo");
		assertNotNull("Failed to find 'foo' function proposal in CA", prop);

		// Verify that the file location is the filename, not the owning type.
		ICommonCompletionProposal p2 = (ICommonCompletionProposal) prop;
		assertEquals("Expected 'location' to show filename, not owning type", fileName, p2.getFileLocation());
	}

	@Test
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
	@Test
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

	@Test
	public void testDontShowStringConstructorOffInstance()
	{
		ICompletionProposal[] proposals = getProposals("contentAssist/string-constructor-off-instance.js");

		assertDoesntContain(proposals, "String", "Object");
		assertContains(proposals, "charAt", "indexOf", "toLowerCase");
	}

	@Test
	public void testInstanceMethodDefinedOnPrototypeOffInstance()
	{
		ICompletionProposal[] proposals = getProposals("contentAssist/instance-method-off-instance.js");

		assertDoesntContain(proposals, "download");
		assertContains(proposals, "play");
	}

	@Test
	public void testDontShowStaticMethodOffInstance()
	{
		ICompletionProposal[] proposals = getProposals("contentAssist/static-method-off-instance.js");

		assertDoesntContain(proposals, "play");
		assertContains(proposals, "download");
	}

	// https://jira.appcelerator.org/browse/APSTUD-4017
	@Test
	public void testOffersCAOnMultipleTypesInferredForSameVariable() throws Exception
	{
		project = createTestProject();

		IFile number = project.createFile("apstud4017_number.js", "var abc = 10;");
		IFile string = project.createFile("apstud4017_string.js", "var abc = \"hello\";");
		index(number, string);

		IFile file = project.createFile("apstud4017.js", "abc.");
		ICompletionProposal[] proposals = openAndGetProposals(file, 4);

		// make sure we get Number proposals (from first file's defining type as number)
		assertContains(proposals, "toFixed", "toExponential", "toPrecision");
		// make sure we get String proposals (from second file's defining type as string)
		assertContains(proposals, "charAt", "concat", "indexOf", "length", "toUpperCase", "toLowerCase");
	}

	@Test
	public void testExportsWithNameFunctionAsProperty() throws Exception
	{
		project = createTestProject();

		IFile module = project.createFile("module_name.js", "exports.name = function() {\n"
				+ "    console.log('My name is Lemmy Kilmister');\n" + "};\n");
		index(module);

		IFile file = project.createFile("client0.js", "var rocker = require('module_name');\nrocker.");
		ICompletionProposal[] proposals = openAndGetProposals(file, 44);

		// make sure we get "name" as a proposal
		assertContains(proposals, "name");
	}

	@Test
	public void testModuleExportsAsInstanceOfArray() throws Exception
	{
		project = createTestProject();

		IFile module = project
				.createFile("module.js",
						"module.exports = ['Lemmy Kilmister', 'Ozzy Osbourne', 'Ronnie James Dio', 'Steven Tyler', 'Mick Jagger'];\n");
		index(module);

		IFile file = project.createFile("client1.js", "var rocker = require('module');\nrocker.");
		ICompletionProposal[] proposals = openAndGetProposals(file, 39);

		// make sure we get proposals we'd get for an array
		assertContains(proposals, "length", "push", "pop", "slice", "unshift", "join");
	}

	@Test
	public void testModuleExportsWithNameFunctionAsProperty() throws Exception
	{
		project = createTestProject();

		IFile module = project.createFile("lemmy.js", "module.exports.rock_me = function() {\n"
				+ "    console.log('My name is Lemmy Kilmister');\n" + "};\n");
		index(module);

		IFile file = project.createFile("client2.js", "var rocker = require('lemmy');\nrocker.");
		ICompletionProposal[] proposals = openAndGetProposals(file, 38);

		// make sure we get "rock_me" as a proposal
		assertContains(proposals, "rock_me");
	}

	@Test
	public void testModuleInstanceHasIdAndURIProperty() throws Exception
	{
		project = createTestProject();

		IFile module = project.createFile("module2.js", "module.exports.something = function() {\n"
				+ "    console.log('My name is Lemmy Kilmister');\n" + "};\n");
		index(module);

		IFile file = project.createFile("client3.js", "var rocker = require('module2');\nrocker.");
		ICompletionProposal[] proposals = openAndGetProposals(file, 40);

		// make sure we get "id" and "uri" as proposals
		assertContains(proposals, "id", "uri", "something");
	}

	@Test
	public void testRelativeSiblingModuleReference() throws Exception
	{
		project = createTestProject();

		IFile module = project.createFile("relative.js", "module.exports.relative_func = function() {\n"
				+ "    console.log('My name is Lemmy Kilmister');\n" + "};\n");
		index(module);

		IFile file = project.createFile("client.js", "var r = require('./relative');\nr.");
		ICompletionProposal[] proposals = openAndGetProposals(file, 33);

		// make sure we get "relative_func" as a proposal
		assertContains(proposals, "relative_func");
	}

	@Test
	public void testRelativeUpFolderModuleReference() throws Exception
	{
		project = createTestProject();
		project.createFolder("a");
		project.createFolder("a/b");
		project.createFolder("a/b/c");

		IFile module = project.createFile("a/b/relative.js", "module.exports.relative_func2 = function() {\n"
				+ "    console.log('My name is Lemmy Kilmister');\n" + "};\n");
		index(module);

		IFile file = project.createFile("a/b/c/client.js", "var r = require('../relative');\nr.");
		ICompletionProposal[] proposals = openAndGetProposals(file, 34);

		// make sure we get "relative_func2" as a proposal
		assertContains(proposals, "relative_func2");
	}

	@Test
	public void testAbsoluteNestedModuleReference() throws Exception
	{
		project = createTestProject();
		project.createFolder("a");
		project.createFolder("a/b");
		project.createFolder("a/b/c");

		IFile module = project.createFile("a/b/c/d.js", "module.exports.nested_func = function() {\n"
				+ "    console.log('My name is Lemmy Kilmister');\n" + "};\n");
		index(module);

		IFile file = project.createFile("nested.js", "var r = require('a/b/c/d');\nr.");
		ICompletionProposal[] proposals = openAndGetProposals(file, 30);

		// make sure we get "nested_func" as a proposal
		assertContains(proposals, "nested_func");
	}

	// FIXME I'm not sure this test is valid. I can't find any documentation stating that @module is used in any way by
	// CommonJs/NodeJS require loading.
	// public void testModuleIdDefinedByDocTag() throws Exception
	// {
	// project = createTestProject();
	// project.createFolder("a");
	// project.createFolder("a/b");
	// project.createFolder("a/b/c");
	//
	// IFile module = project.createFile("a/b/c/d.js",
	// "/** @module my/id */\nmodule.exports.my_id_func = function() {\n"
	// + "    console.log('My name is Lemmy Kilmister');\n" + "};\n");
	// index(module);
	//
	// IFile file = project.createFile("nested.js", "var r = require('my/id');\nr.");
	// ICompletionProposal[] proposals = openAndGetProposals(file, 28);
	//
	// // make sure we get "my_id_func" as a proposal
	// assertContains(proposals, "my_id_func");
	// }
	@Test
	public void testModuleIdDefinedByDocTagDoesntGetPickedUpByItsPath() throws Exception
	{
		project = createTestProject();
		project.createFolder("a");
		project.createFolder("a/b");
		project.createFolder("a/b/c");

		IFile module = project.createFile("a/b/c/d.js",
				"/** @module my/id */\nmodule.exports.my_id_func = function() {\n"
						+ "    console.log('My name is Lemmy Kilmister');\n" + "};\n");
		index(module);

		IFile file = project.createFile("nested.js", "var r = require('a/b/c/d');\nr.");
		ICompletionProposal[] proposals = openAndGetProposals(file, 28);

		assertDoesntContain(proposals, "my_id_func");
	}
}
