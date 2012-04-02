/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scope;

import java.util.ArrayList;
import java.util.List;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;

/**
 * ScopeWalker
 */
public class ScopeUtil
{
	/**
	 * Convert the specified node to a lisp-like syntax to expose the tree structure of the node and its descendants in
	 * a form that is easy for unit testing
	 * 
	 * @param node
	 * @return
	 */
	public static String toTreeString(ISelectorNode node)
	{
		List<String> parts = new ArrayList<String>();

		toTreeString(parts, node);

		return StringUtil.concat(parts);
	}

	@SuppressWarnings("nls")
	private static void toTreeString(List<String> buffer, ISelectorNode node)
	{
		if (node instanceof BinarySelector)
		{
			BinarySelector selector = (BinarySelector) node;
			ISelectorNode left = selector.getLeftChild();
			ISelectorNode right = selector.getRightChild();
			String operator = (node instanceof DescendantSelector) ? ">" : selector.getOperator().trim();

			CollectionsUtil.addToList(buffer, "(", operator, " ", toTreeString(left), " ", toTreeString(right), ")");
		}
		else if (node instanceof GroupSelector)
		{
			GroupSelector selector = (GroupSelector) node;

			CollectionsUtil.addToList(buffer, "(GROUP ", toTreeString(selector.getChild()), ")");
		}
		else if (node instanceof NameSelector)
		{
			NameSelector selector = (NameSelector) node;

			buffer.add(selector.toString());
		}
	}

	private ScopeUtil()
	{
	}
}
