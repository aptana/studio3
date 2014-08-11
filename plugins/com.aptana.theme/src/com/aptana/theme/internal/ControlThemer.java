/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;

import com.aptana.core.util.PlatformUtil;
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

	protected static final boolean isWindows = PlatformUtil.isWindows();
	protected static final boolean isMacOSX = PlatformUtil.isMac();
	protected static final boolean isCocoa = Platform.getWS().equals(Platform.WS_COCOA);
	protected static final boolean isUbuntu = PlatformUtil.isOSName("Ubuntu") || PlatformUtil.isOSName("LinuxMint"); //$NON-NLS-1$

	private Control control;
	private Color defaultBg;

	private Listener selectionOverride;
	private IPreferenceChangeListener fThemeChangeListener;

	public ControlThemer(Control control)
	{
		this(control, null);
	}

	public ControlThemer(Control control, Color defaultBg)
	{
		this.control = control;
		this.defaultBg = defaultBg;
	}

	public void apply()
	{
		addThemeChangeListener();
		applyTheme();
	}

	protected void applyTheme()
	{
	}

	protected void applyControlColors()
	{
		getControl().setBackground(getBackground());
		getControl().setForeground(getForeground());
	}

	protected boolean controlIsDisposed()
	{
		Control control = getControl();
		if (control == null)
		{
			return true;
		}
		return control.isDisposed();
	}

	public void dispose()
	{
		unapplyTheme();
		removeThemeListener();
	}

	protected void unapplyTheme()
	{
		if (!controlIsDisposed())
		{
			getControl().setRedraw(false);
			unapplyControlColors();
			getControl().setRedraw(true);
		}
	}

	protected void unapplyControlColors()
	{
		getControl().setBackground(defaultBg);
		getControl().setForeground(null);
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
	}

	protected void removeSelectionOverride()
	{
		if (selectionOverride != null && !controlIsDisposed())
		{
			getControl().removeListener(SWT.EraseItem, selectionOverride);
		}
		selectionOverride = null;
	}

	private void addThemeChangeListener()
	{
		// TODO Just use one global listener that updates all instances?
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
		InstanceScope.INSTANCE.getNode(ThemePlugin.PLUGIN_ID).addPreferenceChangeListener(fThemeChangeListener);
	}

	private void removeThemeListener()
	{
		if (fThemeChangeListener != null)
		{
			InstanceScope.INSTANCE.getNode(ThemePlugin.PLUGIN_ID).removePreferenceChangeListener(fThemeChangeListener);
			fThemeChangeListener = null;
		}
	}

}
