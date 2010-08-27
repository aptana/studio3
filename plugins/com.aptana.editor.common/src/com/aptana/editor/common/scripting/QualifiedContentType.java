/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
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
