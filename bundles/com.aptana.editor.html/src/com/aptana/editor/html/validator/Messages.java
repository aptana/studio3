/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.validator;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{

	private static final String BUNDLE_NAME = "com.aptana.editor.html.validator.messages"; //$NON-NLS-1$

	public static String HTMLTidyValidator_DeprecatedAttribute;
	public static String HTMLTidyValidator_DeprecatedElement;
	public static String HTMLTidyValidator_DoctypeAfterElements;
	public static String HTMLTidyValidator_ElementNotEmptyOrClosed;
	public static String HTMLTidyValidator_ElementNotInsideNoFrames;
	public static String HTMLTidyValidator_ElementNotRecognized;
	public static String HTMLTidyValidator_EntityMissingSemicolon;
	public static String HTMLTidyValidator_IdNameAttributeMismatch;
	public static String HTMLTidyValidator_InsertImplicitNoFrames;
	public static String HTMLTidyValidator_InsertMissingTitle;
	public static String HTMLTidyValidator_InvalidAttributeValue;
	public static String HTMLTidyValidator_MalformedDoctype;
	public static String HTMLTidyValidator_MissingCloseTag;
	public static String HTMLTidyValidator_MissingDoctype;
	public static String HTMLTidyValidator_MissingNoFrames;

	public static String HTMLTidyValidator_NonUniqueIdValue;
	public static String HTMLTidyValidator_ProprietaryAttribute;
	public static String HTMLTidyValidator_RepeatedFrameset;
	public static String HTMLTidyValidator_TrimEmptyElement;
	public static String HTMLTidyValidator_UnescapedAmpersand;
	public static String HTMLTidyValidator_UnknownEntity;
	public static String HTMLTidyValidator_UppercaseDoctype;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
