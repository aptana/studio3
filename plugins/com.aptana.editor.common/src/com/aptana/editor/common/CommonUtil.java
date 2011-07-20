/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.common;

import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

import com.aptana.theme.IThemeManager;
import com.aptana.theme.ThemePlugin;

/**
 * @author Max Stepanov
 *
 */
public final class CommonUtil {

	/**
	 * 
	 */
	private CommonUtil() {
	}

	public static IToken getToken(String tokenName) {
		return new Token(tokenName); //getThemeManager().getToken(tokenName);
	}

	private static IThemeManager getThemeManager() {
		return ThemePlugin.getDefault().getThemeManager();
	}

}
