/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.html.core;

import org.eclipse.core.resources.IMarker;

/**
 * @author Max Stepanov
 */
public interface IHTMLConstants
{

	public String CONTENT_TYPE_HTML = "com.aptana.contenttype.html"; //$NON-NLS-1$

	/**
	 * The annotation type/id used to mark tag pair occurrences.
	 */
	public String TAG_PAIR_OCCURRENCE_ID = "com.aptana.html.tagPair.occurrences"; //$NON-NLS-1$

	/**
	 * The marker type id for HTML problems. Extends {@link IMarker#PROBLEM}
	 */
	public String HTML_PROBLEM = "com.aptana.editor.html.problem"; //$NON-NLS-1$

	/**
	 * The marker type id for HTML Tidy problems. Extends {@link #HTML_PROBLEM}
	 */
	public String TIDY_PROBLEM = "com.aptana.editor.html.tidy_problem"; //$NON-NLS-1$

}
