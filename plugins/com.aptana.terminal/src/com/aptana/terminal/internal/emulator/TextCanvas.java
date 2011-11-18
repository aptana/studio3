/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable variableDeclaredInLoop

package com.aptana.terminal.internal.emulator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.tm.internal.terminal.textcanvas.ILinelRenderer;
import org.eclipse.tm.internal.terminal.textcanvas.ITextCanvasModel;
import org.eclipse.tm.terminal.model.ITerminalTextData;
import org.eclipse.tm.terminal.model.ITerminalTextDataReadOnly;
import org.eclipse.tm.terminal.model.Style;

import com.aptana.terminal.TerminalPlugin;
import com.aptana.terminal.hyperlink.IHyperlinkDetector;

/**
 * @author Max Stepanov
 * @author Chris Williams
 */
public class TextCanvas extends org.eclipse.tm.internal.terminal.textcanvas.TextCanvas {

	private static final String HYPERLINK_DETECTOR_EXT_PT = TerminalPlugin.PLUGIN_ID + ".terminalHyperlinkDetectors"; //$NON-NLS-1$

	private Map<Integer, IHyperlink[]> fLinks = new HashMap<Integer, IHyperlink[]>();
	private int fLastHash;
	private IHyperlinkDetector[] fDetectors;

	/**
	 * @param parent
	 * @param model
	 * @param style
	 * @param cellRenderer
	 */
	public TextCanvas(Composite parent, ITextCanvasModel model, int style, ILinelRenderer cellRenderer) {
		super(parent, model, style, cellRenderer);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.textcanvas.TextCanvas#searchLineForHyperlinks(int)
	 */
	@Override
	protected synchronized void searchLineForHyperlinks(int line) {
		String text = getTerminalText(line);
		int hash = line * 31 + text.hashCode();
		if (hash == fLastHash) {
			return;
		}
		fLastHash = hash;

		if (text != null && text.trim().length() > 0) {
			// Detect new links
			List<IHyperlink> list = new ArrayList<IHyperlink>();
			IHyperlinkDetector[] detectors = getHyperlinkDetectors();
			for (int i = 0; i < detectors.length; i++) {
				IHyperlinkDetector detector = detectors[i];
				IHyperlink[] partialNewLinks = detector.detectHyperlinks(text);
				if (partialNewLinks != null) {
					list.addAll(Arrays.asList(partialNewLinks));
				}
			}
			IHyperlink[] oldLinks = fLinks.remove(line);
			IHyperlink[] newLinks = list.toArray(new IHyperlink[list.size()]);
			// Update map
			fLinks.put(Integer.valueOf(line), newLinks);
			// Only modify underlines if regions changed in any way...
			if (regionsChanged(oldLinks, newLinks)) {
				// Remove links that were on this line before...
				if (oldLinks != null) {
					for (int o = 0; o < oldLinks.length; o++) {
						IHyperlink link = oldLinks[o];
						setUnderlined(line, link.getHyperlinkRegion(), false);
					}
				}
				if (newLinks != null) {
					// Add underline to new set of links
					for (int l = 0; l < newLinks.length; l++) {
						IHyperlink link = newLinks[l];
						setUnderlined(line, link.getHyperlinkRegion(), true);
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.textcanvas.TextCanvas#findHyperlink(org.eclipse.swt.graphics.Point)
	 */
	@Override
	protected IHyperlink findHyperlink(Point cellCoords) {
		if (fLinks == null || cellCoords == null) {
			return null;
		}
		IHyperlink[] links = fLinks.get(cellCoords.y);
		if (links == null) {
			return null;
		}
		for (int i = 0; i < links.length; i++) {
			IHyperlink link = links[i];
			IRegion region = link.getHyperlinkRegion();

			int col = region.getOffset();
			int endCol = region.getOffset() + region.getLength() - 1;
			// clicked between start and end col
			if (cellCoords.x <= endCol && cellCoords.x >= col) {
				return link;
			}
		}
		return null;
	}

	private void setUnderlined(int line, IRegion region, boolean underlined) {
		int startCol = region.getOffset();
		int endCol = region.getOffset() + region.getLength() - 1;
		try {
			ITerminalTextDataReadOnly text = fCellCanvasModel.getTerminalText();
			Field f = text.getClass().getDeclaredField("fTerminal"); //$NON-NLS-1$
			f.setAccessible(true);
			ITerminalTextData data = (ITerminalTextData) f.get(text);

			for (int col = startCol; col <= endCol; col++) {
				char c = data.getChar(line, col);
				Style style = data.getStyle(line, col);
				if (style != null) {
					style = style.setUnderline(underlined);
					data.setChar(line, col, c, style);
				}
			}
		} catch (Exception ignore) {
			ignore.getCause();
		}
	}

	private synchronized IHyperlinkDetector[] getHyperlinkDetectors() {
		if (fDetectors == null) {
			IConfigurationElement[] config = RegistryFactory.getRegistry().getConfigurationElementsFor(
					HYPERLINK_DETECTOR_EXT_PT);
			List<IHyperlinkDetector> result = new ArrayList<IHyperlinkDetector>();
			for (int i = 0; i < config.length; i++) {
				try {
					result.add((IHyperlinkDetector) config[i].createExecutableExtension("class")); //$NON-NLS-1$
				} catch (CoreException e) {
					TerminalPlugin.log(e);
				}
			}
			fDetectors = result.toArray(new IHyperlinkDetector[result.size()]);
		}
		return fDetectors;
	}

	private String getTerminalText(int line) {
		char[] c = fCellCanvasModel.getTerminalText().getChars(line);
		if (c != null) {
			return new String(c);
		}
		return ""; //$NON-NLS-1$
	}

	private boolean regionsChanged(IHyperlink[] oldLinks, IHyperlink[] newLinks) {
		int oldLinkLength = (oldLinks == null) ? 0 : oldLinks.length;
		int newLinkLength = (newLinks == null) ? 0 : newLinks.length;
		// size changed, so we definitely have changes
		if (oldLinkLength != newLinkLength) {
			return true;
		}
		// Compare the links' regions...
		Set<Integer> oldUnderlines = new HashSet<Integer>();
		for (int i = 0; i < oldLinkLength; i++) {
			IHyperlink link = oldLinks[i];
			IRegion region = link.getHyperlinkRegion();
			for (int x = 0; x < region.getLength(); x++) {
				oldUnderlines.add(region.getOffset() + x);
			}
		}
		for (int i = 0; i < newLinkLength; i++) {
			IHyperlink link = newLinks[i];
			IRegion region = link.getHyperlinkRegion();
			for (int x = 0; x < region.getLength(); x++) {
				Integer integ = Integer.valueOf(region.getOffset() + x);
				if (oldUnderlines.contains(integ)) {
					oldUnderlines.remove(integ);
				} else {
					// hit an offset in new links that wasn't in old!
					return true;
				}
			}
		}
		// if there are any offsets left, then there was a change
		return !oldUnderlines.isEmpty();
	}

}
