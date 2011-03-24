package com.aptana.editor.html.contentassist;

import org.eclipse.swt.graphics.Image;

import com.aptana.editor.html.contentassist.model.EventElement;

class HTMLEventProposal extends AttributeOrEventProposal
{

	HTMLEventProposal(EventElement event, String replaceString, Image[] userAgentIcons, int offset,
			int length, int[] positions)
	{
		super(event.getName(), event.getDescription(), HTMLContentAssistProcessor.EVENT_ICON,
				replaceString, userAgentIcons, offset, length, positions);
	}
}
