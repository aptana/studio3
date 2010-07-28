package com.aptana.editor.js.inferencing;

import java.util.ArrayList;
import java.util.List;

import com.aptana.editor.js.parsing.ast.JSInvokeNode;
import com.aptana.editor.js.parsing.ast.JSObjectNode;
import com.aptana.parsing.ast.IParseNode;

public class JQueryInvocationProcessor implements InvocationProcessor
{
	private static final List<String> PATTERNS;

	/**
	 * static initializer
	 */
	static
	{
		PATTERNS = new ArrayList<String>();

		PATTERNS.add("jQuery.extend");
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.inferencing.InvocationProcessor#getInvocationPatterns()
	 */
	@Override
	public List<String> getInvocationPatterns()
	{
		return PATTERNS;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.inferencing.InvocationProcessor#processInvocation(com.aptana.editor.js.inferencing.JSScope, com.aptana.editor.js.parsing.ast.JSInvokeNode)
	 */
	@Override
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
