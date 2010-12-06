/*******************************************************************************
 * Copyright (c) 2007 Wind River Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Michael Scharf (Wind River) - initial API and implementation
 *******************************************************************************/
package org.eclipse.tm.internal.terminal.speedtest;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.tm.internal.terminal.provisional.api.ISettingsPage;

public class SpeedTestSettingsPage implements ISettingsPage {
	final SpeedTestSettings fSettings;
	Text fInputFile;
	Text fBufferSize;
	private Text fThrottle;
	SpeedTestSettingsPage(SpeedTestSettings settings) {
		fSettings=settings;
	}
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(2, false);

		composite.setLayout(gridLayout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		fInputFile=createTextField(composite, "Input File:");//$NON-NLS-1$
		fBufferSize=createTextField(composite, "Buffer Size:");//$NON-NLS-1$
		fThrottle=createTextField(composite, "Throttle:");//$NON-NLS-1$
		loadSettings();
	}
	private Text createTextField(Composite composite, String label) {
		new Label(composite, SWT.RIGHT).setText(label);
		Text text = new Text(composite, SWT.BORDER);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return text;
	}

	public void loadSettings() {
		setText(fInputFile, fSettings.getInputFile());
		setText(fBufferSize, fSettings.getBufferSizeString());
		setText(fThrottle, fSettings.getThrottleString());
	}
	private void setText(Text text, String value) {
		if(value==null)
			value="";
		text.setText(value);
	}

	public void saveSettings() {
		fSettings.setInputFile(fInputFile.getText());
		fSettings.setBufferSizeString(fBufferSize.getText());
		fSettings.setThrottleString(fThrottle.getText());
	}

	public boolean validateSettings() {
		return true;
	}

}
