/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package ch.randelshofer.quaqua.util;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.aptana.plist.tests.AbstractPlistParserTestCase;

public class BinaryPListParserTest extends AbstractPlistParserTestCase
{

	@Override
	protected IPath getExampleFilePath()
	{
		return Path.fromPortableString("plists/example.plist");
	}

}
