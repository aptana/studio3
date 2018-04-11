/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.common.scripting;

import java.util.Arrays;

/**
 * @author Max Stepanov
 *
 */
public final class QualifiedContentType {

	private final String[] contentTypes;
	private String toString;

	/**
	 * 
	 */
	public QualifiedContentType(String... contentTypes) {
		this.contentTypes = contentTypes;
	}

	public QualifiedContentType subtype(String appendContentType) {
		String[] array = new String[contentTypes.length+1];
		System.arraycopy(contentTypes, 0, array, 0, contentTypes.length);
		array[array.length-1] = appendContentType;
		return new QualifiedContentType(array);		
	}

	public QualifiedContentType subtype(String[] appendContentTypes) {
		String[] array = new String[contentTypes.length+appendContentTypes.length];
		System.arraycopy(contentTypes, 0, array, 0, contentTypes.length);
		System.arraycopy(appendContentTypes, 0, array, contentTypes.length, appendContentTypes.length);
		return new QualifiedContentType(array);
	}
	
	public QualifiedContentType supertype() {
		String[] array = new String[contentTypes.length-1];
		System.arraycopy(contentTypes, 0, array, 0, array.length);
		return new QualifiedContentType(array);		
		
	}
	
	public int getPartCount() {
		return contentTypes.length;
	}
	
	public String getLastPart() {
		return contentTypes[contentTypes.length-1];
	}

	public String[] getParts() {
		return contentTypes;
	}

	public boolean contains(String contentType) {
		// TODO: possible speed optimization with HashSet
		for (String i : contentTypes) {
			if (i.equals(contentType)) {
				return true;
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(contentTypes);
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof QualifiedContentType)) {
			return false;
		}
		QualifiedContentType other = (QualifiedContentType) obj;
		if (!Arrays.equals(contentTypes, other.contentTypes)) {
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (toString == null) {
			StringBuilder sb = new StringBuilder();
			for (String i : contentTypes) {
				sb.append(i).append(' ');
			}
			toString = sb.delete(sb.length()-1, sb.length()).toString();
		}
		return toString;
	}

}
