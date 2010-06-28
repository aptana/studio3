/*******************************************************************************
 * Copyright (c) 2007 Wind River Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Michael Scharf (Wind River) - initial API and implementation
 *******************************************************************************/
package org.eclipse.tm.internal.terminal.textcanvas;

/**
 */
public interface ITextCanvasModelListener {
	void rangeChanged(int col, int line, int width, int height);
	void dimensionsChanged(int cols, int rows);
	/**
	 * Called when any text change happened. Used to scroll to the
	 * end of text in auto scroll mode. This does not get fired
	 * when the window of interest has changed!
	 */
	void terminalDataChanged();
}
