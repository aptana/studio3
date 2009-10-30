package com.aptana.radrails.editor.common;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * Generic color manager.
 */
public class ColorManager {	
	
	private static ColorManager instance;
	
	private ColorManager() {
	}
	
	public static ColorManager getDefault() {
		if (instance == null) {
			instance = new ColorManager();
		}
		return instance;
	}
	
	protected Map<RGB, Color> fColorTable = new HashMap<RGB, Color>(10);
	
	public Color getColor(RGB rgb) {
		Color color = (Color) fColorTable.get(rgb);
		if (color == null) {
			color = new Color(Display.getCurrent(), rgb);
			fColorTable.put(rgb, color);
		}
		return color;
	}
	
	public void dispose() {
		for (Color i : fColorTable.values()) {
			i.dispose();
		}
	}
}


