/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.build.ui.internal.preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

class NoParticipantPreferenceComposite extends Composite
{

	NoParticipantPreferenceComposite(Composite parent)
	{
		super(parent, SWT.NONE);
		setLayout(new FillLayout());
		Label label = new Label(this, SWT.WRAP);
		label.setText(Messages.ValidationPreferencePage_Filter_SelectParticipant);
	}

}
