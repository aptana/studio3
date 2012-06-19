/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.outline;

import com.aptana.parsing.ast.IParseRootNode;

/**
 * This should be the input that's set at the outline page. It should always be
 * kept as the same instance. To update the ast, set a new version of the ast
 * in the instance created and call refresh on the viewer.
 * 
 * @author Fabio Zadrozny
 */
public class CommonOutlinePageInput
{
	public IParseRootNode ast;

	public CommonOutlinePageInput(IParseRootNode ast)
	{
		this.ast = ast;
	}
}