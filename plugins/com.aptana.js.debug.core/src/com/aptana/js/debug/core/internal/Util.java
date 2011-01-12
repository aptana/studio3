/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.core.internal;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.regex.Pattern;



/**
 * @author Max Stepanov
 */
public final class Util {
	private static final Pattern CHECK_VARIABLE = Pattern.compile("\\A\\w+(?:\\.\\w+)*\\z"); //$NON-NLS-1$

	private Util() {
	}

	/**
	 * decodeURL
	 * 
	 * @param url
	 * @return String
	 */
	public static String decodeURL(String url) {
		try {
			return URLDecoder.decode(url, "UTF-8"); //$NON-NLS-1$
		} catch (Exception ignore) {
		}
		try {
			return URLDecoder.decode(url, "ASCII"); //$NON-NLS-1$
		} catch (UnsupportedEncodingException ignore) {
		}
		return url;
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
}
