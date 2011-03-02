/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.debug.core;

/**
 * Information about a detail formatter.
 */
public final class DetailFormatter implements Comparable<Object> {

	private boolean fEnabled;

	private String fTypeName;

	private String fSnippet;

	/**
	 * DetailFormatter
	 * 
	 * @param typeName
	 * @param snippet
	 * @param enabled
	 */
	public DetailFormatter(String typeName, String snippet, boolean enabled) {
		fTypeName = typeName;
		fSnippet = snippet;
		fEnabled = enabled;
	}

	/**
	 * Indicate if this pretty should be used or not.
	 * 
	 * @return boolean
	 */
	public boolean isEnabled() {
		return fEnabled;
	}

	/**
	 * Returns the code snippet.
	 * 
	 * @return String
	 */
	public String getSnippet() {
		return fSnippet;
	}

	/**
	 * Returns the type name.
	 * 
	 * @return String
	 */
	public String getTypeName() {
		return fTypeName;
	}

	/**
	 * Sets the enabled flag.
	 * 
	 * @param enabled
	 *            the new value of the flag
	 */
	public void setEnabled(boolean enabled) {
		fEnabled = enabled;
	}

	/**
	 * Sets the code snippet.
	 * 
	 * @param snippet
	 *            the snippet to set
	 */
	public void setSnippet(String snippet) {
		fSnippet = snippet;
	}

	/**
	 * Sets the type name.
	 * 
	 * @param typeName
	 *            the type name to set
	 */
	public void setTypeName(String typeName) {
		fTypeName = typeName;
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object another) {
		DetailFormatter detailFormatter = (DetailFormatter) another;
		if (fTypeName == null) {
			if (detailFormatter.fTypeName == null) {
				return 0;
			}
			return detailFormatter.fTypeName.compareTo(fTypeName);
		}
		return fTypeName.compareTo(detailFormatter.fTypeName);
	}

}
