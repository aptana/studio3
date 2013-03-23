/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.contentassist.model;

import org.eclipse.osgi.util.NLS;

/**
 * Messages
 */
public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.editor.js.contentassist.model.messages"; //$NON-NLS-1$

	public static String ClassGroupElement_Averages;
	public static String ClassGroupElement_ClassCount;
	public static String ClassGroupElement_Counts;
	public static String ClassGroupElement_MaxLengths;
	public static String ClassGroupElement_Medians;
	public static String ClassGroupElement_MethodAverageLength;
	public static String ClassGroupElement_MethodCount;
	public static String ClassGroupElement_MethodLengthSums;
	public static String ClassGroupElement_MethodMaxLength;
	public static String ClassGroupElement_MethodMedianLength;
	public static String ClassGroupElement_MethodMinLength;
	public static String ClassGroupElement_MinLengths;
	public static String ClassGroupElement_PropertyAverageLength;
	public static String ClassGroupElement_PropertyCount;
	public static String ClassGroupElement_PropertyLengthSums;
	public static String ClassGroupElement_PropertyMaxLength;
	public static String ClassGroupElement_PropertyMedianLength;
	public static String ClassGroupElement_PropertyMinLength;
	public static String ClassGroupElement_Sums;
	public static String ClassGroupElement_TypeAverageLength;
	public static String ClassGroupElement_TypeCount;
	public static String ClassGroupElement_TypeLengthSums;
	public static String ClassGroupElement_TypeMaxLength;
	public static String ClassGroupElement_TypeMedianLength;
	public static String ClassGroupElement_TypeMinLength;

	public static String EventElement_Name;
	public static String EventElement_OwningType;
	public static String EventElement_PropertyCount;

	public static String EventPropertyElement_Name;
	public static String EventPropertyElement_Type;

	public static String JSElement_ChildCount;
	public static String JSElement_IndexFile;
	public static String JSElement_IndexFileSizeLabel;
	public static String JSElement_IndexLabel;
	public static String JSElement_Name;
	public static String JSElement_Version;

	public static String PropertyElement_Description;
	public static String PropertyElement_Documents;
	public static String PropertyElement_InstanceProperty;
	public static String PropertyElement_Name;
	public static String PropertyElement_OwningType;
	public static String PropertyElement_ReturnTypes;
	public static String PropertyElement_StaticProperty;
	public static String PropertyElement_Types;

	public static String TypeElement_Deprecated;
	public static String TypeElement_Description;
	public static String TypeElement_Documents;
	public static String TypeElement_EventCount;
	public static String TypeElement_Name;
	public static String TypeElement_ParentTypes;
	public static String TypeElement_PropertyCount;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
