package com.aptana.editor.yaml.outline;

import com.aptana.editor.common.outline.CommonOutlineLabelProvider;
import com.aptana.parsing.ast.IParseNode;

public class YAMLOutlineLabelProvider extends CommonOutlineLabelProvider
{
	
	@Override
	public String getText(Object element)
	{
		if (element instanceof IParseNode)
		{
			IParseNode parseNode = (IParseNode) element;
			return parseNode.getText();
		}
		return super.getText(element);
	}

}
