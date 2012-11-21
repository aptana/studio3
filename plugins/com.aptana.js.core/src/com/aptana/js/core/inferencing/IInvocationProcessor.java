/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.inferencing;

import java.util.List;

import com.aptana.js.core.parsing.ast.JSInvokeNode;

public interface IInvocationProcessor
{
	/**
	 * getInvocationPatterns
	 * 
	 * @return
	 */
	List<String> getInvocationPatterns();

	/**
	 * processInvocation
	 * 
	 * @param scope
	 * @param node
	 * @return
	 */
	boolean processInvocation(JSScope scope, JSInvokeNode node);
}
