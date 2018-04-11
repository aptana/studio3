/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.junit.Test;

public class StreamUtilTest
{

	@Test
	public void testReadContent() throws Exception
	{
		InputStream stream = FileLocator.openStream(Platform.getBundle("com.aptana.core.tests"),
				Path.fromPortableString("resources/test.js"), false);
		String content = StreamUtil.readContent(stream);
		assertEquals("var foo = function() {\n\thello();\n}\n", content);
	}
}
