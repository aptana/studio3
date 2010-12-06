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
package com.aptana.theme.internal;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scrollable;

import com.aptana.theme.ColorManager;
import com.aptana.theme.IControlThemer;
import com.aptana.theme.IThemeManager;
import com.aptana.theme.Theme;
import com.aptana.theme.ThemePlugin;

/**
 * Base class for applying our theme to a Control. More specific subclasses exist for Tables/Trees.
 * 
 * @author cwilliams
 */
class ControlThemer implements IControlThemer
{

	protected static final boolean isWindows = Platform.getOS().equals(Platform.OS_WIN32);
	protected static final boolean isMacOSX = Platform.getOS().equals(Platform.OS_MACOSX);
	// use the hard-coded value for cocoa since the constant is not defined until Eclipse 3.5
	protected static final boolean isCocoa = Platform.getWS().equals("cocoa"); //$NON-NLS-1$

	private Control control;

	private Listener selectionOverride;
	private IPreferenceChangeListener fThemeChangeListener;

	public ControlThemer(Control control)
	{
		this.control = control;
	}

	public void apply()
	{
		addThemeChangeListener();
		applyTheme();
	}

	protected void applyTheme()
	{
		if (getControl() != null && !getControl().isDisposed())
		{
			getControl().setRedraw(false);
			getControl().setBackground(getBackground());
			getControl().setForeground(getForeground());
			getControl().setFont(getFont());
			getControl().setRedraw(true);
		}
	}

	public void dispose()
	{
		if (control != null && !control.isDisposed())
		{
			control.setRedraw(false);

			control.setBackground(null);
			control.setForeground(null);
			control.setFont(null);

			control.setRedraw(true);
		}

		removeThemeListener();
	}

	protected Font getFont()
	{
		Font font = JFaceResources.getFont(IThemeManager.VIEW_FONT_NAME);
		if (font == null)
		{
			font = JFaceResources.getTextFont();
		}
		return font;
	}

	protected Theme getCurrentTheme()
	{
		return getThemeManager().getCurrentTheme();
	}

	protected IThemeManager getThemeManager()
	{
		return ThemePlugin.getDefault().getThemeManager();
	}

	protected Color getBackground()
	{
		return getColorManager().getColor(getThemeManager().getCurrentTheme().getBackground());
	}

	protected Color getForeground()
	{
		return getColorManager().getColor(getThemeManager().getCurrentTheme().getForeground());
	}

	protected Color getSelection()
	{
		return getColorManager().getColor(getThemeManager().getCurrentTheme().getSelectionAgainstBG());
	}

	protected ColorManager getColorManager()
	{
		return ThemePlugin.getDefault().getColorManager();
	}

	protected Control getControl()
	{
		return control;
	}

	protected void addSelectionColorOverride()
	{
		final Control control = getControl();
		if (control == null || control.isDisposed())
		{
			return;
		}
		// Override selection color to match what is set in theme
		selectionOverride = new Listener()
		{
			public void handleEvent(Event event)
			{
				if ((event.detail & SWT.SELECTED) != 0)
				{
					Scrollable scrollable = (Scrollable) event.widget;
					Rectangle clientArea = scrollable.getClientArea();
					int clientWidth = clientArea.width;

					GC gc = event.gc;
					Color oldBackground = gc.getBackground();

					gc.setBackground(getSelection());
					gc.fillRectangle(clientArea.x, event.y, clientWidth, event.height);
					gc.setBackground(oldBackground);

					event.detail &= ~SWT.SELECTED;
					event.detail &= ~SWT.BACKGROUND;

					// force foreground color. Otherwise on dark themes we get black FG (all the time on Win, on
					// non-focus for Mac)
					gc.setForeground(getForeground());
				}
			}
		};
		control.addListener(SWT.EraseItem, selectionOverride);
	}

	protected void removeSelectionOverride()
	{
		if (selectionOverride != null && getControl() != null && !getControl().isDisposed())
		{
			getControl().removeListener(SWT.EraseItem, selectionOverride);
		}
		selectionOverride = null;
	}

	private void addThemeChangeListener()
	{
		fThemeChangeListener = new IPreferenceChangeListener()
		{
			public void preferenceChange(PreferenceChangeEvent event)
			{
				if (event.getKey().equals(IThemeManager.THEME_CHANGED))
				{
					applyTheme();
				}
			}
		};
		new InstanceScope().getNode(ThemePlugin.PLUGIN_ID).addPreferenceChangeListener(fThemeChangeListener);
	}

	private void removeThemeListener()
	{
		if (fThemeChangeListener != null)
		{
			new InstanceScope().getNode(ThemePlugin.PLUGIN_ID).removePreferenceChangeListener(fThemeChangeListener);
			fThemeChangeListener = null;
		}
	}

}
