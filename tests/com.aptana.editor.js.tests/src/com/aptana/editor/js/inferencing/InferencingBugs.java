/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.inferencing;

import java.util.List;

import com.aptana.editor.js.JSTypeConstants;
import com.aptana.editor.js.contentassist.index.JSIndexReader;
import com.aptana.editor.js.contentassist.index.JSIndexWriter;
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
}
