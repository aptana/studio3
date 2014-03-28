/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.common.hover;

import org.eclipse.jface.text.DefaultInformationControl.IInformationPresenter;
import org.eclipse.jface.text.IInformationControlExtension2;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Shell;

import com.aptana.editor.common.contentassist.InformationControl;
import com.aptana.theme.ColorManager;
import com.aptana.theme.Theme;
import com.aptana.theme.ThemePlugin;

/**
 * @author Max Stepanov
 *
 */
public class ThemedInformationControl extends InformationControl implements IInformationControlExtension2 {

	/**
	 * @param parent
	 * @param shellStyle
	 * @param style
	 * @param presenter
	 */
	public ThemedInformationControl(Shell parent, int shellStyle, int style, IInformationPresenter presenter) {
		super(parent, shellStyle, style, presenter);
	}

	/**
	 * @param parent
	 * @param shellStyle
	 * @param style
	 * @param presenter
	 * @param statusFieldText
	 */
	public ThemedInformationControl(Shell parent, int shellStyle, int style, IInformationPresenter presenter, String statusFieldText) {
		super(parent, shellStyle, style, presenter, statusFieldText);
	}

	/**
	 * @param parent
	 * @param style
	 * @param presenter
	 */
	public ThemedInformationControl(Shell parent, int style, IInformationPresenter presenter) {
		super(parent, style, presenter);
	}

	/**
	 * @param parent
	 * @param style
	 * @param presenter
	 * @param statusFieldText
	 */
	public ThemedInformationControl(Shell parent, int style, IInformationPresenter presenter, String statusFieldText) {
		super(parent, style, presenter, statusFieldText);
	}

	/**
	 * @param parent
	 */
	public ThemedInformationControl(Shell parent) {
		super(parent);
	}

	/**
	 * @param parent
	 * @param presenter
	 */
	public ThemedInformationControl(Shell parent, IInformationPresenter presenter) {
		super(parent, presenter);
	}

	/**
	 * @param parent
	 * @param presenter
	 * @param status
	 */
	public ThemedInformationControl(Shell parent, IInformationPresenter presenter, String status) {
		super(parent, presenter, status);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.IInformationControlExtension2#setInput(java.lang.Object)
	 */
	public void setInput(Object input) {
		setInformation(String.valueOf(input));
	}

	/* (non-Javadoc)
	 * @see com.aptana.editor.common.contentassist.InformationControl#getForeground()
	 */
	@Override
	protected Color getForeground() {
		return getColorManager().getColor(getCurrentTheme().getForeground());
	}

	/* (non-Javadoc)
	 * @see com.aptana.editor.common.contentassist.InformationControl#getBackground()
	 */
	@Override
	protected Color getBackground() {
		return getColorManager().getColor(getCurrentTheme().getBackground());
	}

	/* (non-Javadoc)
	 * @see com.aptana.editor.common.contentassist.InformationControl#getBorderColor()
	 */
	@Override
	protected Color getBorderColor() {
		return getColorManager().getColor(getCurrentTheme().getForeground());
	}

	protected ColorManager getColorManager() {
		return ThemePlugin.getDefault().getColorManager();
	}

	private Theme getCurrentTheme() {
		return ThemePlugin.getDefault().getThemeManager().getCurrentTheme();
	}

}
