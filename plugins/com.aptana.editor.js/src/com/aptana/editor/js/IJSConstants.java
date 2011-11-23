/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.js;

import org.eclipse.core.resources.IMarker;

/**
 * IJSConstants
 */
public interface IJSConstants
{
	public String CONTENT_TYPE_JS = "com.aptana.contenttype.js"; //$NON-NLS-1$

	/**
	 * Marker type id used for JS problems. Extends {@link IMarker#PROBLEM}
	 */
	public String JS_PROBLEM_MARKER_TYPE = "com.aptana.editor.js.problem"; //$NON-NLS-1$
}
