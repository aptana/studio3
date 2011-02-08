/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.contentassist;

import org.eclipse.jface.internal.text.html.HTMLTextPresenter;
import org.eclipse.jface.text.AbstractReusableInformationControlCreator;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IInformationControlCreatorExtension;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Shell;

import com.aptana.theme.Theme;
import com.aptana.theme.ThemePlugin;

/**
 * Common information control creator.
 * 
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
@SuppressWarnings("restriction")
public class CommonInformationControlCreator implements IInformationControlCreator, IInformationControlCreatorExtension
{
	private DefaultInformationControl fControl;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.IInformationControlCreator#createInformationControl(org.eclipse.swt.widgets.Shell)
	 */
	public IInformationControl createInformationControl(Shell parent)
	{
		fControl = new CommonInformationControl(parent, Messages.CommonInformationControlCreator_clickToFocus,
				new HTMLTextPresenter(true))
		{
			public IInformationControlCreator getInformationPresenterControlCreator()
			{
				return new AbstractReusableInformationControlCreator()
				{
					protected IInformationControl doCreateInformationControl(Shell parent)
					{
						// initialize the control that will be active when clicking the default one.
						DefaultInformationControl control = new DefaultInformationControl(parent, true);
						control.setBackgroundColor(getThemeBackground());
						control.setForegroundColor(getThemeForeground());
						return control;
					}
				};
			}
		};
		fControl.setBackgroundColor(getThemeBackground());
		fControl.setForegroundColor(getThemeForeground());
		fControl.addDisposeListener(new DisposeListener()
		{
			public void widgetDisposed(DisposeEvent e)
			{
				fControl = null;
			}
		});
		return fControl;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.jface.text.IInformationControlCreatorExtension#canReuse(org.eclipse.jface.text.IInformationControl)
	 */
	public boolean canReuse(IInformationControl control)
	{
		return fControl == control && fControl != null;
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.eclipse.jface.text.IInformationControlCreatorExtension#canReplace(org.eclipse.jface.text.
	 * IInformationControlCreator)
	 */
	public boolean canReplace(IInformationControlCreator creator)
	{
		return (creator != null && getClass() == creator.getClass());
	}

	protected static Theme getCurrentTheme()
	{
		return ThemePlugin.getDefault().getThemeManager().getCurrentTheme();
	}

	protected static Color getThemeBackground()
	{
		RGB bg = getCurrentTheme().getBackground();
		return ThemePlugin.getDefault().getColorManager().getColor(bg);
	}

	protected static Color getThemeForeground()
	{
		RGB bg = getCurrentTheme().getForeground();
		return ThemePlugin.getDefault().getColorManager().getColor(bg);
	}
}
