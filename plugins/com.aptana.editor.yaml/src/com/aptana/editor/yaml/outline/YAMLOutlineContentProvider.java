/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.yaml.outline;

import java.util.ArrayList;
import java.util.List;

import com.aptana.editor.common.outline.CommonOutlineContentProvider;
import com.aptana.editor.yaml.parsing.MapParseNode;
import com.aptana.editor.yaml.parsing.SequenceParseNode;
import com.aptana.parsing.ast.IParseNode;

public class YAMLOutlineContentProvider extends CommonOutlineContentProvider
{

	@Override
	protected Object[] filter(IParseNode[] nodes)
	{
		List<Object> items = new ArrayList<Object>();
		for (IParseNode node : nodes)
		{
			if (node instanceof MapParseNode || node instanceof SequenceParseNode)
			{
				Object[] result = filter(node.getChildren());
				for (Object item : result)
				{
					items.add(item);
				}
			}
			else
			{
				items.add(getOutlineItem(node));
			}
		}
		return items.toArray(new Object[items.size()]);
	}
}
