package com.aptana.theme.internal;

import org.eclipse.core.runtime.Platform;
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

	public ControlThemer(Control control)
	{
		this.control = control;
	}

	@Override
	public void apply()
	{
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

	@Override
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
		return getColorManager().getColor(getThemeManager().getCurrentTheme().getSelection());
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
					
					// force foreground color. Otherwise on dark themes we get black FG (all the time on Win, on non-focus for Mac)
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

}
