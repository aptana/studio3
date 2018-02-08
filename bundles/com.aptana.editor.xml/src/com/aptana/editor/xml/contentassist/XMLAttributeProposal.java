/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.xml.contentassist;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;

import com.aptana.core.logging.IdeLog;
import com.aptana.editor.common.contentassist.CommonCompletionProposal;
import com.aptana.editor.xml.XMLPlugin;
import com.aptana.xml.core.index.IXMLIndexConstants;
import com.aptana.xml.core.model.AttributeElement;

class XMLAttributeProposal extends CommonCompletionProposal
{

	private int[] _positions;

	XMLAttributeProposal(AttributeElement attribute, String replaceString, int offset, int length, int[] positions)
	{
		super(replaceString, offset, length, positions[0], XMLContentAssistProcessor.ATTRIBUTE_ICON, attribute
				.getName(), null, attribute.getDescription());
		setFileLocation(IXMLIndexConstants.CORE);
		this._positions = positions;
	}

	/**
	 * Special code added to allow tabstop positions so we can easily tab past the quotes for Events/Attributes.
	 */
	@Override
	public void apply(ITextViewer viewer, char trigger, int stateMask, int offset)
	{
		super.apply(viewer, trigger, stateMask, offset);

		// See if there are any positions that should be linked. Last is always exit, first is cursor position
		if (_positions != null && _positions.length > 0)
		{
			IDocument document = viewer.getDocument();
			boolean validPrefix = isValidPrefix(getPrefix(document, offset), getDisplayString());
			int shift = (validPrefix) ? offset - this._replacementOffset : 0;

			try
			{
				LinkedModeModel.closeAllModels(document); // Exit out of any existing linked mode

				LinkedModeModel model = new LinkedModeModel();
				int i = 0;
				for (int pos : _positions)
				{
					LinkedPositionGroup group = new LinkedPositionGroup();
					group.addPosition(new LinkedPosition(document, (offset - shift) + pos, 0, i++));
					model.addGroup(group);
				}

				model.forceInstall();
				LinkedModeUI ui = new LinkedModeUI(model, viewer);
				ui.setCyclingMode(LinkedModeUI.CYCLE_ALWAYS);
				ui.setExitPosition(viewer, (offset - shift) + _positions[_positions.length - 1], 0, Integer.MAX_VALUE);
				ui.enter();
			}
			catch (BadLocationException e)
			{
				IdeLog.logError(XMLPlugin.getDefault(), e);
			}
		}
	}
}
