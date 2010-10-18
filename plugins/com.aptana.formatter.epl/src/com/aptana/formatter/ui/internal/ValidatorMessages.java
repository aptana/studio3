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
package com.aptana.formatter.ui.internal;

import org.eclipse.osgi.util.NLS;

public class ValidatorMessages
{
	private static final String BUNDLE_NAME = "com.aptana.formatter.ui.internal.ValidatorMessages";//$NON-NLS-1$

	static
	{
		NLS.initializeMessages(BUNDLE_NAME, ValidatorMessages.class);
	}

	private ValidatorMessages()
	{
		// Do not instantiate
	}

	public static String FileNameIsEmpty;

	// Path validator
	public static String FilePathIsEmpty;
	public static String FilePathNotExists;
	public static String FilePathIsInvalid;

	public static String DirPathIsEmpty;
	public static String DirPathNotExists;
	public static String DirPathIsInvalid;

	// Positive number validator
	public static String PositiveNumberIsEmpty;

	public static String PositiveNumberIsInvalid;

	// Port validator
	public static String PortIsEmpty;
	public static String PortShouldBeInRange;

	public static String MinValueInvalid;
}
