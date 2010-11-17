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

import java.io.UnsupportedEncodingException;

import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.tm.internal.terminal.control.ITerminalListener;
import org.eclipse.tm.internal.terminal.provisional.api.ITerminalConnector;
import org.eclipse.tm.internal.terminal.textcanvas.ILinelRenderer;
import org.eclipse.tm.internal.terminal.textcanvas.ITextCanvasModel;

import com.aptana.theme.IThemeManager;
import com.aptana.theme.ThemePlugin;

public class VT100TerminalControl extends org.eclipse.tm.internal.terminal.emulator.VT100TerminalControl {

	private IPreferenceChangeListener preferenceChangeListener;
	
	public VT100TerminalControl(ITerminalListener target, Composite wndParent, ITerminalConnector[] connectors) {
		super(target, wndParent, connectors);
		getRootControl().setBackground(ThemedTextLineRenderer.getStyleMap().getBackgroundColor());
		preferenceChangeListener = new IPreferenceChangeListener() {
			public void preferenceChange(PreferenceChangeEvent event) {
				if (IThemeManager.THEME_CHANGED.equals(event.getKey())) {
					Control control = getRootControl();
					if (!control.isDisposed()) {
						control.setBackground(ThemedTextLineRenderer.getStyleMap().getBackgroundColor());
						getCtlText().redraw();
					}
				}
			}
		};
		new InstanceScope().getNode(ThemePlugin.PLUGIN_ID).addPreferenceChangeListener(preferenceChangeListener);
		getCtlText().addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent e) {
				if (e.button == 2) { //paste clipboard selection
					String text = (String) getClipboard().getContents(TextTransfer.getInstance(), DND.SELECTION_CLIPBOARD);
					if (text != null && text.length() > 0) {
						pasteString(text);
					}
				}
			}
		});
	}

	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.emulator.VT100TerminalControl#setEncoding(java.lang.String)
	 */
	@Override
	public void setEncoding(String encoding) throws UnsupportedEncodingException {
		if (encoding == null) {
			encoding = "UTF-8"; //$NON-NLS-1$
		}
		super.setEncoding(encoding);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.emulator.VT100TerminalControl#disposeTerminal()
	 */
	@Override
	public void disposeTerminal() {
		new InstanceScope().getNode(ThemePlugin.PLUGIN_ID).removePreferenceChangeListener(preferenceChangeListener);
		super.disposeTerminal();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.emulator.VT100TerminalControl#createTextCanvas(org.eclipse.swt.widgets.Composite, org.eclipse.tm.internal.terminal.textcanvas.ITextCanvasModel, org.eclipse.tm.internal.terminal.textcanvas.ILinelRenderer)
	 */
	@Override
	protected org.eclipse.tm.internal.terminal.textcanvas.TextCanvas createTextCanvas(Composite parent, ITextCanvasModel canvasModel, ILinelRenderer linelRenderer) {
		return new TextCanvas(parent, canvasModel, SWT.NONE, linelRenderer);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.emulator.VT100TerminalControl#createLineRenderer(org.eclipse.tm.internal.terminal.textcanvas.ITextCanvasModel)
	 */
	@Override
	protected ILinelRenderer createLineRenderer(ITextCanvasModel model) {
		return new ThemedTextLineRenderer(model);
	}

}
