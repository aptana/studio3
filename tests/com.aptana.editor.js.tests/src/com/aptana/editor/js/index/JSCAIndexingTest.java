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

import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.js.tests.JSEditorBasedTestCase;
import com.aptana.index.core.FileStoreBuildContext;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;
import com.aptana.index.core.IndexPlugin;
import com.aptana.js.core.index.JSCAFileIndexingParticipant;
import com.aptana.js.core.index.JSIndexQueryHelper;
import com.aptana.js.core.model.PropertyElement;
import com.aptana.js.core.model.TypeElement;
import com.aptana.js.internal.core.index.JSIndexReader;

/**
 * JSCAIndexingTests
 */
public class JSCAIndexingTest extends JSEditorBasedTestCase
{

	private URI uri;

	private void assertProperties(Index index, String typeName, String... propertyNames)
	{
		for (String propertyName : propertyNames)
		{
			Collection<PropertyElement> property = new JSIndexQueryHelper(index).getTypeMembers(typeName, propertyName);

			assertNotNull(typeName + "." + propertyName + " does not exist", property);
			assertFalse(typeName + "." + propertyName + " does not exist", property.isEmpty());
		}
	}

	private void assertTypes(Index index, String... typeNames)
	{
		for (String typeName : typeNames)
		{
			Collection<TypeElement> type = new JSIndexReader().getType(index, typeName, false);

			assertNotNull(type);
			assertFalse("Expected at least one type matching name: " + typeName, type.isEmpty());
		}
	}

	private TypeElement assertTypeInIndex(Index index, String typeName)
	{
		return assertTypeInIndex(index, typeName, false);
	}

	private TypeElement assertTypeInIndex(Index index, String typeName, boolean includeMembers)
	{
		Collection<TypeElement> types = new JSIndexReader().getType(index, typeName, includeMembers);
		assertNotNull("There should be at least one type for " + typeName, types);
		assertEquals("There should be one type for " + typeName, 1, types.size());

		return types.iterator().next();
	}

	private void assertUserAgents(List<String> actual, String... expected)
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

	private Index indexResource(String resource) throws CoreException
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

		super.tearDown();
	}

	public void testSimpleType() throws Exception
	{
		Index index = indexResource("metadata/typeOnly.jsca");

		// check type
		assertTypes(index, "SimpleType");

		// check for global
		Collection<PropertyElement> global = new JSIndexQueryHelper(index).getGlobals(null, "SimpleType");
		assertNotNull(global);
		assertFalse(global.isEmpty());
	}

	public void testSimpleInternalType() throws Exception
	{
		Index index = indexResource("metadata/typeInternal.jsca");

		// check type
		assertTypes(index, "SimpleType");

		// check for global
		Collection<PropertyElement> global = new JSIndexQueryHelper(index).getGlobals(null, "SimpleType");
		assertNotNull(global);
		assertTrue("Expected no global property matching: 'SimpleType'", global.isEmpty());
	}

	public void testNamespacedType() throws Exception
	{
		Index index = indexResource("metadata/namespacedType.jsca");

		// check types
		assertTypes(index, "com", "com.aptana", "com.aptana.SimpleType");

		// check for properties
		assertProperties(index, "Global", "com");
		assertProperties(index, "com", "aptana");
		assertProperties(index, "com.aptana", "SimpleType");
	}

	public void testNamespacedTypeInternal() throws Exception
	{
		Index index = indexResource("metadata/namespacedTypeInternal.jsca");

		// check types
		assertTypes(index, "com", "com.aptana", "com.aptana.SimpleType");

		// check for global
		Collection<PropertyElement> global = new JSIndexQueryHelper(index).getGlobals(null, "com");
		assertNotNull(global);
		assertTrue("Expected no global property matching name 'com'", global.isEmpty());
	}

	public void testNamespacedTypeMixed() throws Exception
	{
		Index index = indexResource("metadata/namespacedTypeMixed.jsca");

		// check types
		assertTypes(index, "com", "com.aptana", "com.aptana.SimpleType", "com.aptana.SimpleType2");

		// check for properties
		assertProperties(index, "Global", "com");
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

	protected IndexManager getIndexManager()
	{
		return IndexPlugin.getDefault().getIndexManager();
	}
}
