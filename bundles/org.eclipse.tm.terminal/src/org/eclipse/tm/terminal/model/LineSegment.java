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
package org.eclipse.tm.terminal.model;


public class LineSegment {
	private final String fText;
	private final int fCol;
	private final Style fStyle;
	public LineSegment(int col, String text, Style style) {
		fCol = col;
		fText = text;
		fStyle = style;
	}
	public Style getStyle() {
		return fStyle;
	}
	public String getText() {
		return fText;
	}
	public int getColumn() {
		return fCol;
	}
	public String toString() {
		return "LineSegment("+fCol+", \""+fText+"\","+fStyle+")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}
}