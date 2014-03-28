/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.theme.internal;

import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Control;

import com.aptana.core.util.EclipseUtil;
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

	private Control fControl;
	private Color fDefaultBg;

	private IPreferenceChangeListener fThemeChangeListener;

	public ControlThemer(Control control)
	{
		this(control, null);
	}

	private ControlThemer(Control control, Color defaultBg)
	{
		this.fControl = control;
		this.fDefaultBg = defaultBg;
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

	private void unapplyTheme()
	{
		if (!controlIsDisposed())
		{
			getControl().setRedraw(false);
			unapplyControlColors();
			getControl().setRedraw(true);
		}
	}

	private void unapplyControlColors()
	{
		getControl().setBackground(fDefaultBg);
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

	private Color getBackground()
	{
		return getColorManager().getColor(getThemeManager().getCurrentTheme().getBackground());
	}

	protected Color getForeground()
	{
		return getColorManager().getColor(getThemeManager().getCurrentTheme().getForeground());
	}

	protected ColorManager getColorManager()
	{
		return ThemePlugin.getDefault().getColorManager();
	}

	protected Control getControl()
	{
		return fControl;
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
		EclipseUtil.instanceScope().getNode(ThemePlugin.PLUGIN_ID).addPreferenceChangeListener(fThemeChangeListener);
	}

	private void removeThemeListener()
	{
		if (fThemeChangeListener != null)
		{
			EclipseUtil.instanceScope().getNode(ThemePlugin.PLUGIN_ID)
					.removePreferenceChangeListener(fThemeChangeListener);
			fThemeChangeListener = null;
		}
	}

}
