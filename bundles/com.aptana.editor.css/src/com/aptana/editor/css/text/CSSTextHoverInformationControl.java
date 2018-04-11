/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.css.text;

import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Shell;

import com.aptana.editor.common.hover.ThemedInformationControl;

/**
 * @author Max Stepanov
 */
public class CSSTextHoverInformationControl extends ThemedInformationControl
{

	/**
	 * @param parent
	 * @param presenter
	 * @param status
	 */
	public CSSTextHoverInformationControl(Shell parent)
	{
		super(parent);

		GridData data = (GridData) getStyledTextWidget().getLayoutData();
		data.horizontalIndent = 0;
		data.verticalIndent = 0;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.contentassist.InformationControl#hasContents()
	 */
	@Override
	public boolean hasContents()
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.hover.ThemedInformationControl#setInput(java.lang.Object)
	 */
	@Override
	public void setInput(Object input)
	{
		if (input instanceof RGB)
		{
			setBackgroundColor(getColorManager().getColor((RGB) input));
		}
		else if (input instanceof String)
		{
			setInformation((String) input);
		}
	}

}
