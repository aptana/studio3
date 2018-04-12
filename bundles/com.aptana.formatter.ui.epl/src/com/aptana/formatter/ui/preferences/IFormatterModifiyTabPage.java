/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package com.aptana.formatter.ui.preferences;

import org.eclipse.swt.widgets.Composite;

import com.aptana.formatter.ui.IFormatterControlManager;

public interface IFormatterModifiyTabPage
{

	Composite createContents(IFormatterControlManager manager, Composite parent);

	void updatePreview();

}
