package com.aptana.plist;
/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */


import java.io.File;
import java.io.IOException;
import java.util.Map;

public interface IPListParser
{
	public Map<String, Object> parse(File file) throws IOException;
}
