/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.contentassist.model;

import org.eclipse.osgi.util.NLS;

/**
 * Messages
 */
public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.editor.css.contentassist.model.messages"; //$NON-NLS-1$

	public static String ClassGroupElement_CountLabel;
	public static String ClassGroupElement_NameLabel;
	public static String ColorGroupElement_CountLabel;
	public static String ColorGroupElement_NameLabel;
	public static String CSSElement_ChildCountLabel;
	public static String CSSElement_IndexFileLabel;
	public static String CSSElement_IndexFileSizeLabel;
	public static String CSSElement_IndexLabel;
	public static String CSSElement_NameLabel;
	public static String CSSElement_VersionLabel;
	public static String ElementElement_NameLabel;
	public static String IdGroupElement_CountLabel;
	public static String IdGroupElement_NameLabel;
	public static String PropertyElement_NameLabel;
	public static String PseudoClassElement_NameLabel;
	public static String PseudoElementElement_NameLabel;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
