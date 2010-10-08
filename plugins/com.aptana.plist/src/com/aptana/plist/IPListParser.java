/**
 * Copyright (c) 2005-2010 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of Modified BSD License
 * which accompanies this distribution. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.plist;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public interface IPListParser
{
	public Map<String, Object> parse(File file) throws IOException;
}
