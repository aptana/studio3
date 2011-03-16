/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ide.core.io;

/**
 * @author Max Stepanov
 *
 */
public final class ConnectionPointType {

	private String type;
	private String name;
	private IConnectionPointCategory category;
	
	/**
	 * 
	 */
	/* package */ ConnectionPointType(String type, String name, IConnectionPointCategory category) {
		this.type = type;
		this.name = name;
		this.category = category;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the category that the connection type belongs to
	 */
	public IConnectionPointCategory getCategory() {
	    return category;
	}
}
