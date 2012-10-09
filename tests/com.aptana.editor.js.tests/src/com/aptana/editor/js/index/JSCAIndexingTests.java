/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.index;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension2;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import com.aptana.core.build.IProblem;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.js.IJSConstants;
import com.aptana.editor.js.contentassist.JSIndexQueryHelper;
import com.aptana.editor.js.contentassist.index.JSCAFileIndexingParticipant;
import com.aptana.editor.js.contentassist.model.EventElement;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.editor.js.contentassist.model.TypeElement;
import com.aptana.editor.js.tests.JSEditorBasedTests;
import com.aptana.editor.js.validator.JSParserValidator;
import com.aptana.index.core.FileStoreBuildContext;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;
import com.aptana.index.core.IndexPlugin;
import com.aptana.index.core.build.BuildContext;

/**
 * JSCAIndexingTests
 */
public class JSCAIndexingTests extends JSEditorBasedTests
{

	private JSIndexQueryHelper queryHelper;
	private URI uri;

	protected void assertProperties(Index index, String typeName, String... propertyNames)
	{
		for (String propertyName : propertyNames)
		{
			List<PropertyElement> property = queryHelper.getTypeMembers(index, typeName, propertyName);

			assertNotNull(typeName + "." + propertyName + " does not exist", property);
			assertFalse(typeName + "." + propertyName + " does not exist", property.isEmpty());
		}
	}

	protected void assertTypes(Index index, String... typeNames)
	{
		for (String typeName : typeNames)
		{
			List<TypeElement> type = queryHelper.getTypes(index, typeName, false);

			assertNotNull(type);
			assertFalse(type.isEmpty());
		}
	}

	protected TypeElement assertTypeInIndex(Index index, String typeName)
	{
		return assertTypeInIndex(index, typeName, false);
	}

	protected TypeElement assertTypeInIndex(Index index, String typeName, boolean includeMembers)
	{
		List<TypeElement> types = queryHelper.getTypes(index, typeName, includeMembers);
		assertNotNull("There should be at least one type for " + typeName, types);
		assertEquals("There should be one type for " + typeName, 1, types.size());

		return types.get(0);
	}

	protected void assertUserAgents(List<String> actual, String... expected)
	{
		Set<String> uas = CollectionsUtil.newSet(expected);
		List<String> missing = new ArrayList<String>(expected.length);
		for (String ua : actual)
		{
			if (!uas.contains(ua))
			{
				missing.add(ua);
			}
		}
		assertTrue("The following user agents were missing: " + StringUtil.join(", ", missing), missing.isEmpty());
	}

	protected void assertProblem(int line, int offset, int length, IProblem.Severity severity, String msg,
			IProblem problem)
	{
		assertEquals("line", line, problem.getLineNumber());
		assertEquals("offset", offset, problem.getOffset());
		assertEquals("length", length, problem.getLength());
		assertEquals("severity", severity, problem.getSeverity());
		assertEquals("message", msg, problem.getMessage());
	}

	protected Index indexResource(String resource) throws CoreException
	{
		IFileStore fileToIndex = getFileStore(resource);
		uri = fileToIndex.toURI();
		Index index = getIndexManager().getIndex(uri);
		JSCAFileIndexingParticipant indexer = new JSCAFileIndexingParticipant();

		indexer.index(new FileStoreBuildContext(fileToIndex), index, new NullProgressMonitor());

		return index;
	}

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		queryHelper = new JSIndexQueryHelper();
		uri = null;
	}

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception
	{
		if (uri != null)
		{
			getIndexManager().removeIndex(uri);
			uri = null;
		}

		queryHelper = null;

		super.tearDown();
	}

	public void testSimpleType() throws Exception
	{
		Index index = indexResource("metadata/typeOnly.jsca");

		// check type
		assertTypes(index, "SimpleType");

		// check for global
		List<PropertyElement> global = queryHelper.getGlobals(index, "SimpleType");
		assertNotNull(global);
		assertFalse(global.isEmpty());
	}

	public void testSimpleInternalType() throws Exception
	{
		Index index = indexResource("metadata/typeInternal.jsca");

		// check type
		assertTypes(index, "SimpleType");

		// check for global
		List<PropertyElement> global = queryHelper.getGlobals(index, "SimpleType");
		assertNotNull(global);
		assertTrue(global.isEmpty());
	}

	public void testNamespacedType() throws Exception
	{
		Index index = indexResource("metadata/namespacedType.jsca");

		// check types
		assertTypes(index, "com", "com.aptana", "com.aptana.SimpleType");

		// check for properties
		assertProperties(index, "Window", "com");
		assertProperties(index, "com", "aptana");
		assertProperties(index, "com.aptana", "SimpleType");
	}

	public void testNamespacedTypeInternal() throws Exception
	{
		Index index = indexResource("metadata/namespacedTypeInternal.jsca");

		// check types
		assertTypes(index, "com", "com.aptana", "com.aptana.SimpleType");

		// check for global
		List<PropertyElement> global = queryHelper.getGlobals(index, "com");
		assertNotNull(global);
		assertTrue(global.isEmpty());
	}

	public void testNamespacedTypeMixed() throws Exception
	{
		Index index = indexResource("metadata/namespacedTypeMixed.jsca");

		// check types
		assertTypes(index, "com", "com.aptana", "com.aptana.SimpleType", "com.aptana.SimpleType2");

		// check for properties
		assertProperties(index, "Window", "com");
		assertProperties(index, "com", "aptana");
		assertProperties(index, "com.aptana", "SimpleType2");
	}

	public void testIsInternalProposals() throws Exception
	{
		// grab source file URI
		IFileStore sourceFile = getFileStore("metadata/isInternalProperty.js");
		uri = sourceFile.toURI();

		// index jsca file
		IFileStore fileToIndex = getFileStore("metadata/namespacedTypeMixed.jsca");
		Index index = getIndexManager().getIndex(uri);
		JSCAFileIndexingParticipant indexer = new JSCAFileIndexingParticipant();
		indexer.index(new FileStoreBuildContext(fileToIndex), index, new NullProgressMonitor());

		// setup editor and CA context
		setupTestContext(sourceFile);

		// get proposals
		int offset = cursorOffsets.get(0);
		ITextViewer viewer = new TextViewer(new Shell(), SWT.NONE);
		viewer.setDocument(this.document);
		ICompletionProposal[] proposals = this.processor.computeCompletionProposals(viewer, offset, '\0', false);

		// build a list of display names
		ArrayList<String> names = new ArrayList<String>();

		for (ICompletionProposal proposal : proposals)
		{
			// we need to check if it is a valid proposal given the context
			if (proposal instanceof ICompletionProposalExtension2)
			{
				ICompletionProposalExtension2 p = (ICompletionProposalExtension2) proposal;
				if (p.validate(document, offset, null))
				{
					names.add(proposal.getDisplayString());
				}
			}
			else
			{
				names.add(proposal.getDisplayString());
			}
		}

		assertFalse("SimpleType should not exist in the proposal list", names.contains("SimpleType"));
		assertTrue("SimpleType2 does not exist in the proposal list", names.contains("SimpleType2"));
	}

	/**
	 * Test for TISTUD-1327
	 * 
	 * @throws CoreException
	 */
	public void testTypeUserAgentsOnProperty() throws CoreException
	{
		Index index = indexResource("metadata/userAgentOnType.jsca");

		// make sure target type exists
		assertTypeInIndex(index, "Titanium.API");

		// confirm parent type exists
		TypeElement t = assertTypeInIndex(index, "Titanium", true);

		// grab property for Titanium.API
		PropertyElement p = t.getProperty("API");
		assertNotNull(p);

		assertUserAgents(p.getUserAgentNames(), "android", "iphone", "ipad", "mobileweb");
	}

	public void testJSCADeprecatedTypeAsBoolean() throws CoreException
	{
		Index index = indexResource("metadata/deprecated_type_boolean.jsca");

		TypeElement t = assertTypeInIndex(index, "Titanium.DEPRECATED");
		assertTrue("deprecated", t.isDeprecated());
	}

	public void testJSCADeprecatedPropertyAsBoolean() throws CoreException
	{
		Index index = indexResource("metadata/deprecated_property_boolean.jsca");

		TypeElement t = assertTypeInIndex(index, "Titanium.XML.Entity", true);
		PropertyElement prop = t.getProperty("ATTRIBUTE_NODE");
		assertTrue("deprecated", prop.isDeprecated());
	}

	public void testJSCADeprecatedFunctionAsBoolean() throws Exception
	{
		Index index = indexResource("metadata/deprecated_function_boolean.jsca");

		TypeElement t = assertTypeInIndex(index, "Titanium.UI", true);
		PropertyElement prop = t.getProperty("convertUnits");
		assertTrue("deprecated", prop.isDeprecated());
	}

	public void testJSCADeprecatedFunctionEventAsBoolean() throws Exception
	{
		Index index = indexResource("metadata/deprecated_event_boolean.jsca");

		TypeElement t = assertTypeInIndex(index, "Titanium.UI", true);
		EventElement event = t.getEvent("update");
		assertTrue("deprecated", event.isDeprecated());
	}

	public void testMarkAdditionOfEventListenerForDeprecatedEventWithWarning() throws CoreException
	{
		Collection<IProblem> theProblems = build("metadata/deprecated_event_boolean.jsca",
				"metadata/add_listener_deprecated_event.js");
		assertEquals(1, theProblems.size());
		IProblem problem = theProblems.iterator().next();
		assertProblem(1, 29, 8, IProblem.Severity.WARNING, "The event update is deprecated", problem);
	}

	public void testMarkRemovalOfEventListenerForDeprecatedEventWithWarning() throws CoreException
	{
		Collection<IProblem> theProblems = build("metadata/deprecated_event_boolean.jsca",
				"metadata/remove_listener_deprecated_event.js");
		assertEquals(1, theProblems.size());
		IProblem problem = theProblems.iterator().next();
		assertProblem(1, 32, 8, IProblem.Severity.WARNING, "The event update is deprecated", problem);
	}

	// TODO Add test for deprecated event property

	public void testMarkConstructOfDeprecatedTypeWithWarning() throws CoreException
	{
		Collection<IProblem> theProblems = build("metadata/deprecated_type_boolean.jsca",
				"metadata/construct_deprecated_type.js");
		assertEquals(1, theProblems.size());
		IProblem problem = theProblems.iterator().next();
		assertProblem(1, 12, 19, IProblem.Severity.WARNING, "The type Titanium.DEPRECATED is deprecated", problem);
	}

	public void testMarkReferenceOfPropertyOnDeprecatedTypeWithWarning() throws CoreException
	{
		Collection<IProblem> theProblems = build("metadata/deprecated_type_boolean.jsca",
				"metadata/refer_property_on_deprecated_type.js");
		assertEquals(1, theProblems.size());
		IProblem problem = theProblems.iterator().next();
		assertProblem(2, 27, 13, IProblem.Severity.WARNING, "The type Titanium.DEPRECATED is deprecated", problem);
	}

	// simple straightforward Type.Name.property access.
	public void testMarkReferenceToDeprecatedPropertyWithWarning() throws CoreException
	{
		Collection<IProblem> theProblems = build("metadata/deprecated_property_boolean.jsca",
				"metadata/refer_deprecated_property.js");
		assertEquals(1, theProblems.size());
		IProblem problem = theProblems.iterator().next();
		assertProblem(1, 28, 14, IProblem.Severity.WARNING, "The property ATTRIBUTE_NODE is deprecated", problem);
	}

	// owner is not straightforward, need to resolve var assignment
	public void testMarkReferenceToDeprecatedPropertyWithWarning2() throws CoreException
	{
		Collection<IProblem> theProblems = build("metadata/deprecated_property_boolean.jsca",
				"metadata/refer_deprecated_property2.js");
		assertEquals(1, theProblems.size());
		IProblem problem = theProblems.iterator().next();
		assertProblem(2, 41, 14, IProblem.Severity.WARNING, "The property ATTRIBUTE_NODE is deprecated", problem);
	}

	// Handle ['property'] access too
	public void testMarkReferenceToDeprecatedPropertyWithWarning3() throws CoreException
	{
		Collection<IProblem> theProblems = build("metadata/deprecated_property_boolean.jsca",
				"metadata/refer_deprecated_property3.js");
		assertEquals(1, theProblems.size());
		IProblem problem = theProblems.iterator().next();
		assertProblem(1, 28, 16, IProblem.Severity.WARNING, "The property ATTRIBUTE_NODE is deprecated", problem);
	}

	// qualified reference to function called using dot notation
	public void testMarkReferenceToDeprecatedFunctionWithWarning() throws CoreException
	{
		Collection<IProblem> theProblems = build("metadata/deprecated_function_boolean.jsca",
				"metadata/invoke_deprecated_function.js");
		assertEquals(1, theProblems.size());
		IProblem problem = theProblems.iterator().next();
		assertProblem(1, 20, 12, IProblem.Severity.WARNING, "The function convertUnits is deprecated", problem);
	}

	// Top-level function hanging off global namespace/Window
	public void testMarkReferenceToDeprecatedFunctionWithWarning2() throws CoreException
	{
		Collection<IProblem> theProblems = build("metadata/deprecated_function_boolean.jsca",
				"metadata/invoke_deprecated_function2.js");
		assertEquals(1, theProblems.size());
		IProblem problem = theProblems.iterator().next();
		assertProblem(1, 0, 26, IProblem.Severity.WARNING, "The function deprecatedToplevelFunction is deprecated", problem);
	}

	// qualified reference to function called using ['name'] notation
	public void testMarkReferenceToDeprecatedFunctionWithWarning3() throws CoreException
	{
		Collection<IProblem> theProblems = build("metadata/deprecated_function_boolean.jsca",
				"metadata/invoke_deprecated_function3.js");
		assertEquals(1, theProblems.size());
		IProblem problem = theProblems.iterator().next();
		assertProblem(1, 20, 14, IProblem.Severity.WARNING, "The function convertUnits is deprecated", problem);
	}

	/**
	 * Indexes a JSCA file, then runs the parser validator on the given JS file and collects the problems added.
	 * 
	 * @param jscaFile
	 * @param jsFile
	 * @return
	 * @throws CoreException
	 */
	private Collection<IProblem> build(String jscaFile, String jsFile) throws CoreException
	{
		final Index index = indexResource(jscaFile);

		// Now let's look to add warnings for references to deprecated functions
		JSParserValidator validator = new JSParserValidator()
		{
			@Override
			protected Index getIndex(BuildContext context)
			{
				return index;
			}
		};

		IFileStore fileToIndex = getFileStore(jsFile);
		FileStoreBuildContext context = new FileStoreBuildContext(fileToIndex)
		{
			@Override
			public void putProblems(String markerType, Collection<IProblem> newItems)
			{
				problems.put(markerType, newItems);
			}
		};
		validator.buildFile(context, new NullProgressMonitor());
		Map<String, Collection<IProblem>> problems = context.getProblems();
		return problems.get(IJSConstants.JS_PROBLEM_MARKER_TYPE);
	}

	protected IndexManager getIndexManager()
	{
		return IndexPlugin.getDefault().getIndexManager();
	}
}
