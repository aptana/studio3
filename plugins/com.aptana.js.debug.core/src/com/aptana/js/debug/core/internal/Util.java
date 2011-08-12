/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.core.internal;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

/**
 * @author Max Stepanov
 */
public final class Util {
	private static final Pattern CHECK_VARIABLE = Pattern.compile("\\A\\w+(?:\\.\\w+)*\\z"); //$NON-NLS-1$

	private Util() {
	}

	/**
	 * fixupURI
	 * 
	 * @param string
	 * @return String
	 */
	public static String fixupURI(String string) {
		URI uri = null;
		try {
			uri = new URI(string);
		} catch (URISyntaxException e) {
		}
		if (uri == null || uri.getScheme() == null) {
			return new File(string).toURI().toString();
		}
		return string;
	}

	/**
	 * encodeData
	 * 
	 * @param data
	 * @return String
	 */
	public static String encodeData(String data) {
		return data.replaceAll("#", "#0").replaceAll("\\|", "#1").replaceAll("\\*", "#2"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
	}

	/**
	 * decodeData
	 * 
	 * @param data
	 * @return String
	 */
	public static String decodeData(String data) {
		return data.replaceAll("#2", "*").replaceAll("#1", "|").replaceAll("#0", "#"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
	}

	/**
	 * checkVariable
	 * 
	 * @param variableName
	 * @return boolean
	 */
	public static boolean checkVariable(String variableName) {
		return CHECK_VARIABLE.matcher(variableName).matches();
	}

	public static String[] removeArrayElement(String[] array, int index) {
		if (array.length == 0 || index >= array.length) {
			return array;
		}
		String[] result = new String[array.length - 1];
		System.arraycopy(array, 0, result, 0, index);
		System.arraycopy(array, index + 1, result, index, array.length - index - 1);
		return result;
	}
}
