/*******************************************************************************
 * Copyright (c) 2007, 2009 Wind River Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Michael Scharf (Wind River) - initial API and implementation
 * Michael Scharf (Wind River) - [205260] Terminal does not take the font from the preferences
 * Michael Scharf (Wind River) - [209746] There are cases where some colors not displayed correctly
 * Michael Scharf (Wind River) - [206328] Terminal does not draw correctly with proportional fonts
 * Martin Oberhuber (Wind River) - [247700] Terminal uses ugly fonts in JEE package
 *******************************************************************************/
package org.eclipse.tm.internal.terminal.textcanvas;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.tm.terminal.model.Style;
import org.eclipse.tm.terminal.model.StyleColor;

@SuppressWarnings({"unchecked","rawtypes"})
public class StyleMap {
	protected static final String BLACK = "black"; //$NON-NLS-1$
	protected static final String WHITE = "white"; //$NON-NLS-1$
	protected static final String WHITE_FOREGROUND = "white_foreground"; //$NON-NLS-1$
	protected static final String GRAY = "gray"; //$NON-NLS-1$
	protected static final String MAGENTA = "magenta"; //$NON-NLS-1$
	protected static final String CYAN = "cyan"; //$NON-NLS-1$
	protected static final String YELLOW = "yellow"; //$NON-NLS-1$
	protected static final String BLUE = "blue"; //$NON-NLS-1$
	protected static final String GREEN = "green"; //$NON-NLS-1$
	protected static final String RED = "red"; //$NON-NLS-1$
	
	private static final String PREFIX = "org.eclipse.tm.internal."; //$NON-NLS-1$
	// TODO propagate the name of the font in the FontRegistry
	private static final String fDefaultFontName="terminal.views.view.font.definition"; //$NON-NLS-1$
	String fFontName=fDefaultFontName;
	protected Map fColorMapForeground=new HashMap();
	protected Map fColorMapBackground=new HashMap();
	Map fFontMap=new HashMap();
	private Point fCharSize;
	private final Style fDefaultStyle;
	private boolean fInvertColors;
	private boolean fProportional;
	private final int[] fOffsets=new int[256];
	protected StyleMap() {
		initColors();
		fDefaultStyle=Style.getStyle(StyleColor.getStyleColor(BLACK),StyleColor.getStyleColor(WHITE));
		updateFont();
	}
	private void initColors() {
		initForegroundColors();
		initBackgroundColors();
	}
	private void initForegroundColors() {
		if(fInvertColors) {
			setColor(fColorMapForeground, WHITE, 0, 0, 0);
			setColor(fColorMapForeground, WHITE_FOREGROUND, 50, 50, 50);
			setColor(fColorMapForeground, BLACK, 255, 255, 255);
		} else {
			setColor(fColorMapForeground, WHITE, 255, 255, 255);
			setColor(fColorMapForeground, WHITE_FOREGROUND, 229, 229, 229);
			setColor(fColorMapForeground, BLACK, 0, 0, 0);
		}
		setColor(fColorMapForeground, RED, 255, 128, 128);
		setColor(fColorMapForeground, GREEN, 128, 255, 128);
		setColor(fColorMapForeground, BLUE, 128, 128, 255);
		setColor(fColorMapForeground, YELLOW, 255, 255, 0);
		setColor(fColorMapForeground, CYAN, 0, 255, 255);
		setColor(fColorMapForeground, MAGENTA, 255, 255, 0);
		setColor(fColorMapForeground, GRAY, 128, 128, 128);
	}

	private void initBackgroundColors() {
		if(fInvertColors) {
			setColor(fColorMapBackground, WHITE, 0, 0, 0);
			setColor(fColorMapBackground, WHITE_FOREGROUND, 50, 50, 50); // only used when colors are inverse
			setColor(fColorMapBackground, BLACK, 255, 255, 255);
		} else {
			setColor(fColorMapBackground, WHITE, 255, 255, 255);
			setColor(fColorMapBackground, WHITE_FOREGROUND, 229, 229, 229);
			setColor(fColorMapBackground, BLACK, 0, 0, 0);
		}
		setColor(fColorMapBackground, RED, 255, 128, 128);
		setColor(fColorMapBackground, GREEN, 128, 255, 128);
		setColor(fColorMapBackground, BLUE, 128, 128, 255);
		setColor(fColorMapBackground, YELLOW, 255, 255, 0);
		setColor(fColorMapBackground, CYAN, 0, 255, 255);
		setColor(fColorMapBackground, MAGENTA, 255, 255, 0);
		setColor(fColorMapBackground, GRAY, 128, 128, 128);
	}
	private void setColor(Map colorMap, String name, int r, int g, int b) {
		String colorName=PREFIX+r+"-"+g+"-"+b;  //$NON-NLS-1$//$NON-NLS-2$
		Color color=JFaceResources.getColorRegistry().get(colorName);
		if(color==null) {
			JFaceResources.getColorRegistry().put(colorName, new RGB(r,g,b));
			color=JFaceResources.getColorRegistry().get(colorName);
		}
		colorMap.put(StyleColor.getStyleColor(name), color);
		colorMap.put(StyleColor.getStyleColor(name.toUpperCase()), color);
	}

	public Color getForegrondColor(Style style) {
		style = defaultIfNull(style);
		if(style.isReverse())
			return getColor(fColorMapForeground,style.getBackground());
		else
			return  getColor(fColorMapForeground,style.getForground());
	}
	public Color getBackgroundColor(Style style) {
		style = defaultIfNull(style);
		if(style.isReverse())
			return getColor(fColorMapBackground,style.getForground());
		else
			return getColor(fColorMapBackground,style.getBackground());
	}
	protected Color getColor(Map map,StyleColor color) {
		Color c=(Color) map.get(color);
		if(c==null) {
			c=Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);
		}
		return c;
	}
	private Style defaultIfNull(Style style) {
		if(style==null)
			style=fDefaultStyle;
		return style;
	}
	public void setInvertedColors(boolean invert) {
		if(invert==fInvertColors)
			return;
		fInvertColors=invert;
		initColors();
	}
//	static Font getBoldFont(Font font) {
//		FontData fontDatas[] = font.getFontData();
//		FontData data = fontDatas[0];
//		return new Font(Display.getCurrent(), data.getName(), data.getHeight(), data.getStyle()|SWT.BOLD);
//	}

	public Font getFont(Style style) {
		style = defaultIfNull(style);
		if(style.isBold()) {
			return  JFaceResources.getFontRegistry().getBold(fFontName);
		} else if(style.isUnderline()) {
			return  JFaceResources.getFontRegistry().getItalic(fFontName);

		}
		return  JFaceResources.getFontRegistry().get(fFontName);
	}

	public Font getFont() {
		return  JFaceResources.getFontRegistry().get(fFontName);

	}
	public int getFontWidth() {
		return fCharSize.x;
	}
	public int getFontHeight() {
		return fCharSize.y;
	}
	public void updateFont() {
		Display display=Display.getCurrent();
		GC gc = new GC (display);
		if (JFaceResources.getFontRegistry().hasValueFor(fDefaultFontName)) {
			fFontName = fDefaultFontName;
		} else if (JFaceResources.getFontRegistry().hasValueFor("REMOTE_COMMANDS_VIEW_FONT")) { //$NON-NLS-1$
			//try RSE Shell View Font
			fFontName = "REMOTE_COMMANDS_VIEW_FONT"; //$NON-NLS-1$
		} else {
			//fall back to "basic jface text font"
			fFontName = "org.eclipse.jface.textfont"; //$NON-NLS-1$
		}
		gc.setFont(getFont());
		fCharSize = gc.textExtent ("W"); //$NON-NLS-1$
		fProportional=false;
		
		for (char c = ' '; c <= '~'; c++) {
			// consider only the first 128 chars for deciding if a font
			// is proportional
			if(measureChar(gc, c, true))
				fProportional=true;
		}
		// TODO should we also consider the upper 128 chars??
		for (char c = ' '+128; c <= '~'+128; c++) {
			measureChar(gc, c,false);
		}
		if(fProportional) {
			fCharSize.x-=2; //works better on small fonts
		}
		for (int i = 0; i < fOffsets.length; i++) {
			fOffsets[i]=(fCharSize.x-fOffsets[i])/2;
		}
		if(!fProportional) {
			//measure font in boldface, too, and if wider then treat like proportional
			gc.setFont(getFont(fDefaultStyle.setBold(true)));
			Point charSizeBold = gc.textExtent("W"); //$NON-NLS-1$
			if (fCharSize.x != charSizeBold.x) {
				fProportional=true;
			}
		}
		gc.dispose ();
	}
	/**
	 * @param gc
	 * @param c
	 * @param updateMax
	 * @return true if the the font is proportional
	 */
	private boolean measureChar(GC gc, char c, boolean updateMax) {
		boolean proportional=false;
		Point ext=gc.textExtent(String.valueOf(c));
		if(ext.x>0 && ext.y>0 && (fCharSize.x!=ext.x || fCharSize.y!=ext.y)) {
			proportional=true;
			if(updateMax) {
				fCharSize.x=Math.max(fCharSize.x, ext.x);
				fCharSize.y=Math.max(fCharSize.y, ext.y);
			}
		}
		fOffsets[c]=ext.x;
		return proportional;
	}
	public boolean isFontProportional() {
		return fProportional;
	}
	/**
	 * Return the offset in pixels required to center a given character
	 * @param c the character to measure
	 * @return the offset in x direction to center this character
	 */
	public int getCharOffset(char c) {
		if(c>=fOffsets.length)
			return 0;
		return fOffsets[c];
	}
}
