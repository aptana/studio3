/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core.index;

import java.io.InputStream;

/**
 * @author cwilliams
 */
public interface IJSCAParser
{

	/**
	 * Parses an InputStream containing JSCA (JSON in a particular format), returns the model held within.
	 * 
	 * @param is
	 * @return
	 */
	public IJSCAModel parse(InputStream is);
}
