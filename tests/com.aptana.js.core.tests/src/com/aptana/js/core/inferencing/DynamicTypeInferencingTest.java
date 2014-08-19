/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.inferencing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Test;

import com.aptana.core.util.IOUtil;
import com.aptana.index.core.Index;
import com.aptana.js.core.index.JSIndexQueryHelper;
import com.aptana.js.core.model.PropertyElement;
import com.aptana.js.core.model.ReturnTypeElement;
import com.aptana.js.core.model.TypeElement;
import com.aptana.js.core.parsing.ast.JSParseRootNode;
import com.aptana.parsing.ast.IParseNode;

/**
 * DynamicTypeInferencingTests
 */
public class DynamicTypeInferencingTest extends InferencingTestsBase
{
	@Test
	public void testDynamicTypes() throws IOException, CoreException
	{
		String source = "one.two = 10;";

		File file = File.createTempFile("infer", ".js");
		file.deleteOnExit();
		IFileStore store = EFS.getLocalFileSystem().fromLocalFile(file);
		IOUtil.write(store.openOutputStream(EFS.NONE, new NullProgressMonitor()), source);

		IParseNode root = getParseRootNode(source);
		assertNotNull(root);
		assertTrue(root instanceof JSParseRootNode);

		Index index = getIndex();
		Indexer indexer = new Indexer();
		indexer.indexTree(store, source, index, (JSParseRootNode) root);

		JSIndexQueryHelper helper = new JSIndexQueryHelper(index);
		Collection<PropertyElement> globals = helper.getGlobals(file.getName(), "one");
		assertEquals(1, globals.size());
		PropertyElement global = globals.iterator().next();

		List<ReturnTypeElement> returnTypes = global.getTypes();
		assertEquals(1, returnTypes.size());
		ReturnTypeElement returnType = returnTypes.get(0);
		assertEquals("one", returnType.getType());

		Collection<TypeElement> types = helper.getTypes("one", true);
		assertEquals(1, types.size());
		TypeElement type = types.iterator().next();
		assertEquals("one", type.getName());
		List<PropertyElement> properties = type.getProperties();
		assertEquals(2, properties.size()); // prototype gets added too
		PropertyElement property = properties.get(0);
		assertEquals("two", property.getName());
		List<String> typeNames = property.getTypeNames();
		assertEquals(1, typeNames.size());
		assertEquals("Number", typeNames.get(0));
	}
}
