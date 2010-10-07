/*******************************************************************************
 * Copyright (c) 2007, 2008 Wind River Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Michael Scharf (Wind River) - initial API and implementation
 * Martin Oberhuber (Wind River) - [168197] Fix Terminal for CDC-1.1/Foundation-1.1
 *******************************************************************************/
package org.eclipse.tm.internal.terminal.model;

import org.eclipse.tm.terminal.model.ITerminalTextData;
import org.eclipse.tm.terminal.model.ITerminalTextDataReadOnly;
import org.eclipse.tm.terminal.model.Style;
import org.eclipse.tm.terminal.model.StyleColor;

public class TerminalTextTestHelper {
	static public String toSimple(ITerminalTextDataReadOnly term) {
		return toSimple(toMultiLineText(term));
	}
	static public String toMultiLineText(ITerminalTextDataReadOnly term) {
		StringBuffer buff=new StringBuffer();
		int width=term.getWidth();
		for (int line = 0; line < term.getHeight(); line++) {
			if(line>0)
				buff.append("\n"); //$NON-NLS-1$
			for (int column = 0; column < width; column++) {
				buff.append(term.getChar(line, column));
			}
		}
		return buff.toString();
	}
	static public String toSimple(String str) {
		//return str.replaceAll("\000", " ").replaceAll("\n", "");
		// <J2ME CDC-1.1 Foundation-1.1 variant>
		StringBuffer buf = new StringBuffer(str.length());
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			switch (c) {
			case '\000':
				buf.append(' ');
				break;
			case '\n':
				break;
			default:
				buf.append(c);
				break;
			}
		}
		return buf.toString();
		// </J2ME CDC-1.1 Foundation-1.1 variant>
	}
	/**
	 * @param term
	 * @param s each character is one line
	 */
	static public void fillSimple(ITerminalTextData term, String s) {
		Style style=Style.getStyle(StyleColor.getStyleColor("fg"), StyleColor.getStyleColor("bg"), false, false, false, false);
		term.setDimensions(s.length(), 1);
		for (int i = 0; i < s.length(); i++) {
			char c=s.charAt(i);
			term.setChar(i, 0, c, style.setForground(StyleColor.getStyleColor(""+c)));
		}
	}
	/**
	 * @param term
	 * @param s lines separated by \n. The terminal will automatically
	 * resized to fit the text.
	 */
	static public void fill(ITerminalTextData term, String s) {
		int width=0;
		int len=0;
		int height=0;
		for (int i = 0; i < s.length(); i++) {
			char c=s.charAt(i);
			if(c=='\n') {
				width=Math.max(width,len);
				len=0;
			} else {
				if(len==0)
					height++;
				len++;
			}
		}
		width=Math.max(width,len);
		term.setDimensions(height, width);
		fill(term,0,0,s);
	}

	static public void fill(ITerminalTextData term, int column, int line, String s) {
		int xx=column;
		int yy=line;
		Style style=Style.getStyle(StyleColor.getStyleColor("fg"), StyleColor.getStyleColor("bg"), false, false, false, false);
		for (int i = 0; i < s.length(); i++) {
			char c=s.charAt(i);
			if(c=='\n') {
				yy++;
				xx=column;
			} else {
				term.setChar(yy, xx, c, style.setForground(StyleColor.getStyleColor(""+c)));
				xx++;
			}
		}
	}

}
