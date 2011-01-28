/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
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
