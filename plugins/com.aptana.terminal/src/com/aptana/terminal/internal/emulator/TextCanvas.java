/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
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

import com.aptana.terminal.Activator;
import com.aptana.terminal.hyperlink.IHyperlinkDetector;

/**
 * @author Chris Williams
 * @author Max Stepanov
 *
 */
public class TextCanvas extends org.eclipse.tm.internal.terminal.textcanvas.TextCanvas {

	private static final String HYPERLINK_DETECTOR_EXT_PT = Activator.PLUGIN_ID + ".terminalHyperlinkDetectors"; //$NON-NLS-1$
	
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

	/* (non-Javadoc)
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
			IHyperlink[] newLinks = list.toArray(new IHyperlink[0]);
			// Update map
			fLinks.put(new Integer(line), newLinks);
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

	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.textcanvas.TextCanvas#findHyperlink(org.eclipse.swt.graphics.Point)
	 */
	@Override
	protected IHyperlink findHyperlink(Point cellCoords) {
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
					Activator.log(e);
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
		int oldLinkLength = oldLinks == null ? 0 : oldLinks.length;
		int newLinkLength = newLinks == null ? 0 : newLinks.length;
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
				Integer integ = new Integer(region.getOffset() + x);
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
