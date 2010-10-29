/**
 * Copyright (c) 2005-2010 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.index.core;

public abstract class SearchPattern
{
	public static final int EXACT_MATCH = 0;
	public static final int PREFIX_MATCH = 0x0001;
	public static final int PATTERN_MATCH = 0x0002;
	public static final int CASE_SENSITIVE = 0x0008;
	public static final int REGEX_MATCH = 0x0010;

}
