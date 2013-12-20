/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.inferencing;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.Path;
import org.junit.Test;

import com.aptana.js.core.JSTypeConstants;
import com.aptana.js.core.index.JSIndexQueryHelper;
import com.aptana.js.core.model.FunctionElement;
import com.aptana.js.core.model.PropertyElement;
import com.aptana.js.core.model.TypeElement;
import com.aptana.js.internal.core.index.JSIndexReader;
import com.aptana.js.internal.core.index.JSIndexWriter;
import com.aptana.js.internal.core.inferencing.JSSymbolTypeInferrer;

/**
 * InferencingBugs
 */
public class InferencingBugsTest extends InferencingTestsBase
{
	@Test
	public void testReadObject()
	{
		JSIndexWriter writer = new JSIndexWriter();
		JSIndexReader reader = new JSIndexReader();
		TypeElement type = new TypeElement();
		List<TypeElement> type2;

		type.setName(JSTypeConstants.OBJECT_TYPE);
		writer.writeType(this.getIndex(), type);
		type2 = reader.getType(this.getIndex(), JSTypeConstants.OBJECT_TYPE, false);

		// NOTE: The bug caused an exception in JSIndexReader#getType, so we wouldn't even get here
		assertNotNull(type2);
	}

	@Test
	public void testOverloadedVarTypesWithFunction()
	{
		IFileStore store = getFileStore(Path.fromPortableString("inferencing/shCore.js"));
		String source = getContent(store);

		JSScope globals = getGlobals(source);
		JSScope scope = globals.getScopeAtOffset(1125);

		JSSymbolTypeInferrer symbolInferrer = new JSSymbolTypeInferrer(scope, getIndex(), getLocation(),
				new JSIndexQueryHelper(getIndex()));
		PropertyElement property = symbolInferrer.getSymbolPropertyElement("m");
		FunctionElement function = (FunctionElement) property;

		// When we had the bad type names being generated, we returned:
		// [Function, Array, Object, Boolean, String, Number]
		List<String> typeNames = property.getTypeNames();
		assertTrue(typeNames.contains("Function"));

		// Bad behavior would return:
		// [Number, Function, Object, String, RegExp, Array, Window, Boolean, Array<Function, Boolean>, ]

		// This is what we return now, but I'm not sure it's right...
		List<String> returnTypeNames = function.getReturnTypeNames();
		assertTrue(returnTypeNames.contains("Number"));
		assertTrue(returnTypeNames.contains("Function<Object,Number>"));
		assertTrue(returnTypeNames.contains("Object"));
	}
}
