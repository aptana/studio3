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

import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.PlatformUtil;
import com.aptana.theme.ColorManager;
import com.aptana.theme.IControlThemer;
import com.aptana.theme.IThemeManager;
import com.aptana.theme.Theme;
import com.aptana.theme.ThemePlugin;
import com.aptana.theme.preferences.IPreferenceConstants;

/**
 * Base class for applying our theme to a Control. More specific subclasses exist for Tables/Trees.
 * 
 * @author cwilliams
 */
class ControlThemer implements IControlThemer
{

	protected static final boolean isWindows = Platform.getOS().equals(Platform.OS_WIN32);
	protected static final boolean isMacOSX = Platform.getOS().equals(Platform.OS_MACOSX);
	protected static final boolean isCocoa = Platform.getWS().equals(Platform.WS_COCOA);
	protected static final boolean isUbuntu = PlatformUtil.isOSName("Ubuntu"); //$NON-NLS-1$

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
		if (invasiveThemesEnabled() && getControl() != null && !getControl().isDisposed())
		{
			getControl().setRedraw(false);
			getControl().setBackground(getBackground());
			getControl().setForeground(getForeground());
			if (useEditorFont())
			{
				getControl().setFont(getFont());
			}
			getControl().setRedraw(true);
		}
	}

	protected boolean invasiveThemesEnabled()
	{
		return getCurrentTheme().isInvasive();
	}

	protected boolean useEditorFont()
	{
		return Platform.getPreferencesService().getBoolean(ThemePlugin.PLUGIN_ID, IPreferenceConstants.INVASIVE_FONT,
				false, null);
	}

	public void dispose()
	{
		unapplyTheme();
		removeThemeListener();
	}

	protected void unapplyTheme()
	{
		if (control != null && !control.isDisposed())
		{
			control.setRedraw(false);

			control.setBackground(null);
			control.setForeground(null);
			if (useEditorFont())
			{
				control.setFont(null);
			}
			control.setRedraw(true);
		}
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
				if (!invasiveThemesEnabled())
				{
					return;
				}
				GC gc = event.gc;
				Color oldBackground = gc.getBackground();
				if ((event.detail & SWT.SELECTED) != 0)
				{
					Scrollable scrollable = (Scrollable) event.widget;
					Rectangle clientArea = scrollable.getClientArea();

					gc.setBackground(getSelection());
					// The +2 on width is for Linux, since clientArea begins at [-2,-2] and
					// without it we don't properly color full width (see broken coloring when scrolling horizontally)
					gc.fillRectangle(clientArea.x, event.y, clientArea.width + 2, event.height);

					event.detail &= ~SWT.SELECTED;
					event.detail &= ~SWT.BACKGROUND;

					gc.setBackground(oldBackground);
				}
				else
				{
					// Draw normal background color. This seems to only be necessary for some variants of Linux,
					// and is the correct way to force custom painting of background when setBackground() doesn't work
					// properly.
					if (!isWindows && !isMacOSX)
					{
						Color controlBG = control.getBackground();
						if (controlBG.getRGB().equals(oldBackground.getRGB()))
						{
							gc.setBackground(getBackground());
							gc.fillRectangle(event.x, event.y, event.width, event.height);
							event.detail &= ~SWT.BACKGROUND;
							gc.setBackground(oldBackground);
						}
					}
				}

				// force foreground color. Otherwise on dark themes we get black FG (all the time on Win, on
				// non-focus for Mac)
				gc.setForeground(getForeground());
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
		// TODO Just use one global listener that updates all instances?
		fThemeChangeListener = new IPreferenceChangeListener()
		{
			public void preferenceChange(PreferenceChangeEvent event)
			{
				if (event.getKey().equals(IThemeManager.THEME_CHANGED))
				{
					applyTheme();
				}
				else if (event.getKey().equals(IPreferenceConstants.INVASIVE_FONT))
				{
					// Handle the invasive font setting change
					if (Boolean.parseBoolean((String) event.getNewValue()))
					{
						getControl().setFont(getFont());
					}
					else
					{
						getControl().setFont(null);
					}
				}
				else if (event.getKey().equals(IPreferenceConstants.INVASIVE_THEMES))
				{
					if (Boolean.parseBoolean((String) event.getNewValue()))
					{
						applyTheme();
					}
					else
					{
						unapplyTheme();
					}
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
