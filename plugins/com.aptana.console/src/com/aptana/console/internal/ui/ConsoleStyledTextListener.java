/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.console.internal.ui;

import org.eclipse.swt.custom.LineBackgroundEvent;
import org.eclipse.swt.custom.LineBackgroundListener;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;

import com.aptana.console.ConsolePlugin;
import com.aptana.console.internal.expressions.ExpressionManager;

/**
 * @author Max Stepanov
 *
 */
public class ConsoleStyledTextListener implements LineStyleListener, LineBackgroundListener {

	private ExpressionManager expressionManager = ConsolePlugin.getDefault().getExpressionManager();
	private StyledText control;
	
	/**
	 * @param control
	 */
	public ConsoleStyledTextListener(StyledText control) {
		this.control = control;
		control.addLineStyleListener(this);
		control.addLineBackgroundListener(this);
	}

	/**
	 * Dispose listener and removed it from lists of listeners
	 */
	public void dispose() {
		if (!control.isDisposed()) {
			control.removeLineStyleListener(this);
			control.removeLineBackgroundListener(this);
		}
		control = null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.custom.LineStyleListener#lineGetStyle(org.eclipse.swt.custom.LineStyleEvent)
	 */
	public void lineGetStyle(LineStyleEvent event) {
		String lineText = event.lineText;
		if (lineText.length() != 0) {
			StyleRange[] styles = expressionManager.calculateStyles(event.lineOffset, lineText);
			if (styles != null) {
				event.styles = styles;
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.custom.LineBackgroundListener#lineGetBackground(org.eclipse.swt.custom.LineBackgroundEvent)
	 */
	public void lineGetBackground(LineBackgroundEvent event) {
		Color lineBackground = expressionManager.calculateBackground(event.lineText);
		if (lineBackground != null) {
			event.lineBackground = lineBackground;
		}
	}

}
