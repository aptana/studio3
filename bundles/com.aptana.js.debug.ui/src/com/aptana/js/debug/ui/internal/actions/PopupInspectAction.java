/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.ui.internal.actions;

import org.eclipse.debug.core.model.IWatchExpressionResult;
import org.eclipse.debug.ui.InspectPopupDialog;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.information.IInformationProvider;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;

import com.aptana.core.logging.IdeLog;
import com.aptana.js.debug.core.model.IJSInspectExpression;
import com.aptana.js.debug.core.model.JSDebugModel;
import com.aptana.js.debug.ui.JSDebugUIPlugin;
import com.aptana.ui.util.UIUtils;

/**
 * @author Max Stepanov
 */
public class PopupInspectAction extends InspectAction implements IInformationProvider {
	/**
	 * ACTION_DEFININIITION_ID
	 */
	private static final String ACTION_DEFININIITION_ID = "com.aptana.debug.ui.commands.Inspect"; //$NON-NLS-1$

	private ITextViewer viewer;
	private IJSInspectExpression expression;

	/**
	 * see org.eclipse.jface.text.information.IInformationProvider#getInformation (org.eclipse.jface.text.ITextViewer,
	 * org.eclipse.jface.text.IRegion)
	 * 
	 * @param textViewer
	 * @param subject
	 * @return String
	 */
	public String getInformation(ITextViewer textViewer, IRegion subject) {
		// the ExpressionInformationControlAdapter was constructed with
		// everything that it needs
		// returning null would result in popup not being displayed
		return "not null"; //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.jface.text.information.IInformationProvider#getSubject(org.eclipse.jface.text.ITextViewer, int)
	 */
	public IRegion getSubject(ITextViewer textViewer, int offset) {
		return getRegion();
	}

	/**
	 * showPopup
	 * 
	 * @param result
	 */
	protected void showPopup(final IWatchExpressionResult result) {
		expression = JSDebugModel.createInspectExpression(result);
		Window displayPopup = new InspectPopupDialog(getShell(), getPopupAnchor(viewer), ACTION_DEFININIITION_ID,
				expression);
		if (displayPopup != null) {
			displayPopup.open();
		}
	}

	/**
	 * @see com.aptana.js.debug.ui.internal.actions.InspectAction#displayResult(org.eclipse.debug.core.model.IWatchExpressionResult)
	 */
	protected void displayResult(final IWatchExpressionResult result) {
		IWorkbenchPart part = getTargetPart();
		viewer = (ISourceViewer) part.getAdapter(ISourceViewer.class);
		if (viewer == null) {
			IdeLog.logInfo(JSDebugUIPlugin.getDefault(),
					"TODO: com.aptana.js.debug.ui.internal.actions.PopupInspectAction.displayResult()"); //$NON-NLS-1$
		}
		if (viewer == null) {
			super.displayResult(result);
		} else {
			showPopup(result);
		}
	}

	private IWorkbenchPart getTargetPart() {
		// TODO
		return UIUtils.getActivePart();
	}

	/**
	 * getRegion
	 * 
	 * @return IRegion
	 */
	protected IRegion getRegion() {
		Point point = viewer.getSelectedRange();
		return new Region(point.x, point.y);
	}

	private Shell getShell() {
		if (getTargetPart() != null) {
			return getTargetPart().getSite().getShell();
		}
		return UIUtils.getActiveShell();
	}

	/**
	 * Computes an anchor point for a popup dialog on top of a text viewer.
	 * 
	 * @param viewer
	 * @return desired anchor point
	 */
	private static Point getPopupAnchor(ITextViewer viewer) {
		StyledText textWidget = viewer.getTextWidget();
		Point docRange = textWidget.getSelectionRange();
		int midOffset = docRange.x + (docRange.y >> 1);
		Point point = textWidget.getLocationAtOffset(midOffset);
		point = textWidget.toDisplay(point);

		GC gc = new GC(textWidget);
		gc.setFont(textWidget.getFont());
		int height = gc.getFontMetrics().getHeight();
		gc.dispose();
		point.y += height;
		return point;
	}
}
