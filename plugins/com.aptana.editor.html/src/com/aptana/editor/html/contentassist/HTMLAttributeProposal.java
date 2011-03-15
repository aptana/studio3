package com.aptana.editor.html.contentassist;

import org.eclipse.swt.graphics.Image;

import com.aptana.editor.html.contentassist.model.AttributeElement;

class HTMLAttributeProposal extends AttributeOrEventProposal
{

	HTMLAttributeProposal(AttributeElement attribute, String replaceString, Image[] userAgentIcons, int offset,
			int length, int[] positions)
	{
		super(attribute.getName(), attribute.getDescription(), HTMLContentAssistProcessor.ATTRIBUTE_ICON,
				replaceString, userAgentIcons, offset, length, positions);
	}
}
