package com.aptana.editor.html.contentassist;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.swt.graphics.Image;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.contentassist.CommonCompletionProposal;
import com.aptana.editor.html.contentassist.index.HTMLIndexConstants;

abstract class AttributeOrEventProposal extends CommonCompletionProposal
{

	private int[] _positions;

	AttributeOrEventProposal(String displayName, String description, Image icon, String replaceString,
			Image[] userAgentIcons, int offset, int length, int[] positions)
	{
		super(replaceString, offset, length, positions[0], icon, displayName, null, description);
		setFileLocation(HTMLIndexConstants.CORE);
		setUserAgentImages(userAgentIcons);
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
				CommonEditorPlugin.logError(e);
			}
		}
	}

}
