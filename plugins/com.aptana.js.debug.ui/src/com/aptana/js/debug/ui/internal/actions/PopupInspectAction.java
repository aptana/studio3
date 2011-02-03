/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.ui.internal.actions;

import org.eclipse.debug.core.model.IWatchExpressionResult;
import org.eclipse.debug.ui.InspectPopupDialog;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.information.IInformationProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;

import com.aptana.debug.ui.DebugUiPlugin;
import com.aptana.js.debug.core.model.IJSInspectExpression;
import com.aptana.js.debug.core.model.JSDebugModel;
import com.aptana.js.debug.ui.JSDebugUIPlugin;

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
	 * see
	 * org.eclipse.jface.text.information.IInformationProvider#getInformation
	 * (org.eclipse.jface.text.ITextViewer, org.eclipse.jface.text.IRegion)
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
	 * @see org.eclipse.jface.text.information.IInformationProvider#getSubject(org.eclipse.jface.text.ITextViewer,
	 *      int)
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
		viewer = (ITextViewer) part.getAdapter(ITextViewer.class);
		if (viewer == null) {
			System.err.println("TODO: com.aptana.js.debug.ui.internal.actions.PopupInspectAction.displayResult()");
			JSDebugUIPlugin.log("TODO: com.aptana.js.debug.ui.internal.actions.PopupInspectAction.displayResult()");
		}
		if (viewer == null) {
			super.displayResult(result);
		} else {
			showPopup(result);
		}
	}

	private IWorkbenchPart getTargetPart() {
		// TODO
		return DebugUiPlugin.getActiveWorkbenchWindow().getActivePage().getActivePart();
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
		return DebugUiPlugin.getActiveWorkbenchShell();
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
		int midOffset = docRange.x + (docRange.y / 2);
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
