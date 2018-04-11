/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.ui.internal.propertypages;

import org.eclipse.osgi.util.NLS;

/**
 * @author Ingo Muschenetz
 */
public final class Messages extends NLS {
	private static final String BUNDLE_NAME = "com.aptana.js.debug.ui.internal.propertypages.messages"; //$NON-NLS-1$

	private Messages() {
	}

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	/**
	 * JSLineBreakpointPropertyPage_HitCountMustBePositiveInteger
	 */
	public static String JSLineBreakpointPropertyPage_HitCountMustBePositiveInteger;

	/**
	 * JSLineBreakpointPropertyPage_EnterCondition
	 */
	public static String JSLineBreakpointPropertyPage_EnterCondition;

	/**
	 * JSLineBreakpointPropertyPage_File
	 */
	public static String JSLineBreakpointPropertyPage_File;

	/**
	 * JSLineBreakpointPropertyPage_Enabled
	 */
	public static String JSLineBreakpointPropertyPage_Enabled;

	/**
	 * JSLineBreakpointPropertyPage_HitCount
	 */
	public static String JSLineBreakpointPropertyPage_HitCount;

	/**
	 * JSLineBreakpointPropertyPage_LineNumber
	 */
	public static String JSLineBreakpointPropertyPage_LineNumber;

	/**
	 * JSLineBreakpointPropertyPage_EnableCondition
	 */
	public static String JSLineBreakpointPropertyPage_EnableCondition;

	/**
	 * JSLineBreakpointPropertyPage_conditionIsTrue
	 */
	public static String JSLineBreakpointPropertyPage_conditionIsTrue;

	/**
	 * JSLineBreakpointPropertyPage_valueOfConditionChanges
	 */
	public static String JSLineBreakpointPropertyPage_valueOfConditionChanges;

	/**
	 * JSLineBreakpointPropertyPage_ExceptionWhileSavingBreakpointProperties
	 */
	public static String JSLineBreakpointPropertyPage_ExceptionWhileSavingBreakpointProperties;

	/**
	 * JSLineBreakpointPropertyPage_PageAllowedInputOfInvalidStringForHitCountValue_0
	 */
	public static String JSLineBreakpointPropertyPage_PageAllowedInputOfInvalidStringForHitCountValue_0;
}
