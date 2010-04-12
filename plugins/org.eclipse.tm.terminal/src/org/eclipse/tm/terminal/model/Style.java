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

import java.util.HashMap;
import java.util.Map;

/**
 * @author scharf
 * Flyweight
 * Threadsafe.
 * 
 */
// TODO add an Object for user data, use weak map to keep track of styles with associated
// user data
public class Style {
	private final StyleColor fForground;
	private final StyleColor fBackground;
	private final boolean fBold;
	private final boolean fBlink;
	private final boolean fUnderline;
	private final boolean fReverse;
	private final static Map fgStyles=new HashMap();
	private Style(StyleColor forground, StyleColor background, boolean bold, boolean blink, boolean underline, boolean reverse) {
		fForground = forground;
		fBackground = background;
		fBold = bold;
		fBlink = blink;
		fUnderline = underline;
		fReverse = reverse;
	}
	public static Style getStyle(StyleColor forground, StyleColor background, boolean bold, boolean blink, boolean underline, boolean reverse) {
		Style style = new Style(forground,background, bold, blink,underline,reverse);
		Style cached;
		synchronized (fgStyles) {
			cached=(Style) fgStyles.get(style);
			if(cached==null) {
				cached=style;
				fgStyles.put(cached, cached);
			}
		}
		return cached;
	}
	public static Style getStyle(String forground, String background) {
		return getStyle(StyleColor.getStyleColor(forground), StyleColor.getStyleColor(background),false,false,false,false);
	}
	public static Style getStyle(StyleColor forground, StyleColor background) {
		return getStyle(forground, background,false,false,false,false);
	}
	public Style setForground(StyleColor forground) {
		return getStyle(forground,fBackground,fBold,fBlink,fUnderline,fReverse);
	}
	public Style setBackground(StyleColor background) {
		return getStyle(fForground,background,fBold,fBlink,fUnderline,fReverse);
	}
	public Style setForground(String colorName) {
		return getStyle(StyleColor.getStyleColor(colorName),fBackground,fBold,fBlink,fUnderline,fReverse);
	}
	public Style setBackground(String colorName) {
		return getStyle(fForground,StyleColor.getStyleColor(colorName),fBold,fBlink,fUnderline,fReverse);
	}
	public Style setBold(boolean bold) {
		return getStyle(fForground,fBackground,bold,fBlink,fUnderline,fReverse);
	}
	public Style setBlink(boolean blink) {
		return getStyle(fForground,fBackground,fBold,blink,fUnderline,fReverse);
	}
	public Style setUnderline(boolean underline) {
		return getStyle(fForground,fBackground,fBold,fBlink,underline,fReverse);
	}
	public Style setReverse(boolean reverse) {
		return getStyle(fForground,fBackground,fBold,fBlink,fUnderline,reverse);
	}
	public StyleColor getBackground() {
		return fBackground;
	}
	public boolean isBlink() {
		return fBlink;
	}
	public boolean isBold() {
		return fBold;
	}
	public StyleColor getForground() {
		return fForground;
	}
	public boolean isReverse() {
		return fReverse;
	}
	public boolean isUnderline() {
		return fUnderline;
	}
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fBackground == null) ? 0 : fBackground.hashCode());
		result = prime * result + (fBlink ? 1231 : 1237);
		result = prime * result + (fBold ? 1231 : 1237);
		result = prime * result + ((fForground == null) ? 0 : fForground.hashCode());
		result = prime * result + (fReverse ? 1231 : 1237);
		result = prime * result + (fUnderline ? 1231 : 1237);
		return result;
	}
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Style other = (Style) obj;
		// background == is the same as equals
		if (fBackground != other.fBackground)
			return false;
		if (fBlink != other.fBlink)
			return false;
		if (fBold != other.fBold)
			return false;
		if (fForground != other.fForground)
			return false;
		if (fReverse != other.fReverse)
			return false;
		if (fUnderline != other.fUnderline)
			return false;
		return true;
	}
	public String toString() {
		StringBuffer result=new StringBuffer();
		result.append("Style(foreground="); //$NON-NLS-1$
		result.append(fForground);
		result.append(", background="); //$NON-NLS-1$
		result.append(fBackground);
		if(fBlink)
			result.append(", blink"); //$NON-NLS-1$
		if(fBold)
			result.append(", bold"); //$NON-NLS-1$
		if(fBlink)
			result.append(", blink"); //$NON-NLS-1$
		if(fReverse)
			result.append(", reverse"); //$NON-NLS-1$
		if(fUnderline)
			result.append(", underline"); //$NON-NLS-1$
		result.append(")"); //$NON-NLS-1$
		return result.toString();
	}
	
}