/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.common.spelling;

import java.util.HashSet;
import java.util.Set;

import com.aptana.editor.common.scripting.QualifiedContentType;

/**
 * @author Max Stepanov
 *
 */
public final class SpellingPreferences {

	private static final Set<String> spellingContentTypes = new HashSet<String>();
	
	static {
		spellingContentTypes.add("comment.block.php");
		spellingContentTypes.add("comment.line.number-sign.php");
		spellingContentTypes.add("comment.line.double-slash.php");
		spellingContentTypes.add("comment.block.documentation.phpdoc.php");
	}
	
	/**
	 * 
	 */
	private SpellingPreferences() {
	}
	
	public static boolean isSpellingEnabledFor(QualifiedContentType contentType) {
		return spellingContentTypes.contains(contentType.getLastPart());
	}

}
