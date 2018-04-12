/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core.inferencing;

import java.util.ArrayList;
import java.util.List;

import com.aptana.js.core.inferencing.IInvocationProcessor;
import com.aptana.js.core.inferencing.JSScope;
import com.aptana.js.core.parsing.ast.JSInvokeNode;
import com.aptana.js.core.parsing.ast.JSObjectNode;
import com.aptana.parsing.ast.IParseNode;

public class JQueryInvocationProcessor implements IInvocationProcessor
{
	private static final List<String> PATTERNS;

	/**
	 * static initializer
	 */
	static
	{
		PATTERNS = new ArrayList<String>();

		PATTERNS.add("jQuery.extend"); //$NON-NLS-1$
		PATTERNS.add("jQuery.fn.extend"); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.inferencing.InvocationProcessor#getInvocationPatterns()
	 */
	public List<String> getInvocationPatterns()
	{
		return PATTERNS;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.editor.js.inferencing.InvocationProcessor#processInvocation(com.aptana.editor.js.inferencing.JSScope,
	 * com.aptana.editor.js.parsing.ast.JSInvokeNode)
	 */
	public boolean processInvocation(JSScope scope, JSInvokeNode node)
	{
		boolean processed = false;
		String jQuery = "jQuery"; //$NON-NLS-1$

		if (scope.hasSymbol(jQuery))
		{
			IParseNode args = node.getArguments();

			if (args.getChildCount() == 1)
			{
				IParseNode inheritedProperties = args.getFirstChild();

				if (inheritedProperties instanceof JSObjectNode)
				{
					JSPropertyCollector collector = new JSPropertyCollector(scope.getObject());
					collector.activateProperty(jQuery);
					collector.visit((JSObjectNode) inheritedProperties);
					processed = true;
				}
			}
		}

		return processed;
	}
}
