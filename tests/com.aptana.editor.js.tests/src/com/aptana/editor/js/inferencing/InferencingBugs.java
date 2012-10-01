/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.inferencing;

import java.util.List;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.Path;

import com.aptana.editor.js.JSTypeConstants;
import com.aptana.editor.js.contentassist.index.JSIndexReader;
import com.aptana.editor.js.contentassist.index.JSIndexWriter;
import com.aptana.editor.js.contentassist.model.FunctionElement;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.editor.js.contentassist.model.TypeElement;

/**
 * InferencingBugs
 */
public class InferencingBugs extends InferencingTestsBase
{
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

	public void testOverloadedVarTypesWithFunction()
	{
		IFileStore store = getFileStore(Path.fromPortableString("inferencing/shCore.js"));
		String source = getContent(store);

		JSScope globals = getGlobals(source);
		JSScope scope = globals.getScopeAtOffset(1125);

		JSSymbolTypeInferrer symbolInferrer = new JSSymbolTypeInferrer(scope, getIndex(), getLocation());
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
