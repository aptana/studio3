/**
 * Appcelerator Titanium Studio
 * Copyright (c) 2011 by Appcelerator, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */

package com.aptana.editor.css.text;

import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Shell;

import com.aptana.editor.common.hover.ThemedInformationControl;

/**
 * @author Max Stepanov
 *
 */
public class CSSTextHoverInformationControl extends ThemedInformationControl {

	/**
	 * @param parent
	 * @param presenter
	 * @param status
	 */
	public CSSTextHoverInformationControl(Shell parent) {
		super(parent);
		
		GridData data = (GridData) getStyledTextWidget().getLayoutData();
		data.horizontalIndent = 0;
		data.verticalIndent = 0;
	}

	/* (non-Javadoc)
	 * @see com.aptana.editor.common.hover.ThemedInformationControl#setInput(java.lang.Object)
	 */
	@Override
	public void setInput(Object input) {
		if (input instanceof RGB) {
			setBackgroundColor(getColorManager().getColor((RGB) input));
		} else if (input instanceof String) {
			setInformation((String) input);
		}
	}

}
