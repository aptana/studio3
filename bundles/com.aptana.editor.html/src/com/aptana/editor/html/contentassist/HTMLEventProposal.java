/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
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
