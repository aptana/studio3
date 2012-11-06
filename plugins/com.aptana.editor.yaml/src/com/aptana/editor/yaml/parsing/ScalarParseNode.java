/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.yaml.parsing;

import org.yaml.snakeyaml.nodes.ScalarNode;

import com.aptana.parsing.IParseState;

public class ScalarParseNode extends YAMLNode
{

	private ScalarNode node;

	public ScalarParseNode(ScalarNode node, IParseState parseState)
	{
		super();
		setLocation(YAMLParseRootNode.getStart(node, parseState), YAMLParseRootNode.getEnd(node, parseState));
		this.node = node;
	}

	@Override
	public String getText()
	{
		return node.getValue();
	}

}
