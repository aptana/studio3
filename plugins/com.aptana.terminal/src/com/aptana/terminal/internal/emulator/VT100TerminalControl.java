/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.terminal.internal.emulator;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
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

import com.aptana.core.util.IOUtil;
import com.aptana.terminal.internal.hyperlink.HyperlinkManager;
import com.aptana.theme.IThemeManager;
import com.aptana.theme.ThemePlugin;

public class VT100TerminalControl extends org.eclipse.tm.internal.terminal.emulator.VT100TerminalControl
{

	private static final Set<Character> IGNORE_ALT_WITH_KEYS = new HashSet<Character>();

	static
	{
		for (char c : "@#\\|[]{}".toCharArray()) { //$NON-NLS-1$
			IGNORE_ALT_WITH_KEYS.add(c);
		}
	}

	private IPreferenceChangeListener preferenceChangeListener;
	private IPropertyChangeListener propertyChangeListener;
	private HyperlinkManager hyperlinkManager;

	public VT100TerminalControl(ITerminalListener target, Composite wndParent, ITerminalConnector[] connectors)
	{
		super(target, wndParent, connectors);
		getRootControl().setBackground(ThemedTextLineRenderer.getStyleMap().getBackgroundColor());
		preferenceChangeListener = new IPreferenceChangeListener()
		{
			public void preferenceChange(PreferenceChangeEvent event)
			{
				if (IThemeManager.THEME_CHANGED.equals(event.getKey()))
				{
					Control control = getRootControl();
					if (!control.isDisposed())
					{
						control.setBackground(ThemedTextLineRenderer.getStyleMap().getBackgroundColor());
						getCtlText().redraw();
					}
				}
			}
		};
		InstanceScope.INSTANCE.getNode(ThemePlugin.PLUGIN_ID).addPreferenceChangeListener(preferenceChangeListener);
		propertyChangeListener = new IPropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent event)
			{
				if (JFaceResources.TEXT_FONT.equals(event.getProperty()))
				{
					setFont(JFaceResources.getTextFont());
				}
			}
		};
		JFaceResources.getFontRegistry().addListener(propertyChangeListener);
		getCtlText().addMouseListener(new MouseAdapter()
		{
			public void mouseDown(MouseEvent e)
			{
				if (e.button == 2)
				{ // paste clipboard selection
					String text = (String) getClipboard().getContents(TextTransfer.getInstance(),
							DND.SELECTION_CLIPBOARD);
					if (text != null && text.length() > 0)
					{
						pasteString(text);
					}
				}
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.emulator.VT100TerminalControl#setEncoding(java.lang.String)
	 */
	@Override
	public void setEncoding(String encoding) throws UnsupportedEncodingException
	{
		if (encoding == null)
		{
			encoding = IOUtil.UTF_8; // $codepro.audit.disable questionableAssignment
		}
		super.setEncoding(encoding);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.emulator.VT100TerminalControl#sendChar(char, boolean)
	 */
	@Override
	protected void sendChar(char chKey, boolean altKeyPressed)
	{
		if (altKeyPressed && IGNORE_ALT_WITH_KEYS.contains(chKey))
		{
			altKeyPressed = false; // $codepro.audit.disable questionableAssignment
		}
		super.sendChar(chKey, altKeyPressed);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.emulator.VT100TerminalControl#disposeTerminal()
	 */
	@Override
	public void disposeTerminal()
	{
		JFaceResources.getFontRegistry().removeListener(propertyChangeListener);
		InstanceScope.INSTANCE.getNode(ThemePlugin.PLUGIN_ID).removePreferenceChangeListener(preferenceChangeListener);
		super.disposeTerminal();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.tm.internal.terminal.emulator.VT100TerminalControl#createTextCanvas(org.eclipse.swt.widgets.Composite
	 * , org.eclipse.tm.internal.terminal.textcanvas.ITextCanvasModel,
	 * org.eclipse.tm.internal.terminal.textcanvas.ILinelRenderer)
	 */
	@Override
	protected org.eclipse.tm.internal.terminal.textcanvas.TextCanvas createTextCanvas(Composite parent,
			ITextCanvasModel canvasModel, ILinelRenderer linelRenderer)
	{
		return new TextCanvas(parent, canvasModel, SWT.NONE, linelRenderer, hyperlinkManager);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.tm.internal.terminal.emulator.VT100TerminalControl#createLineRenderer(org.eclipse.tm.internal.terminal
	 * .textcanvas.ITextCanvasModel)
	 */
	@Override
	protected ILinelRenderer createLineRenderer(ITextCanvasModel model)
	{
		return new ThemedTextLineRenderer(model, hyperlinkManager = new HyperlinkManager(model.getTerminalText()));
	}

}
