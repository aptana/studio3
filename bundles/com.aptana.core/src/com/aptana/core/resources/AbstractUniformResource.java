/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.resources;

import org.eclipse.core.runtime.PlatformObject;

/**
 * @author Max Stepanov
 *
 */
public abstract class AbstractUniformResource extends PlatformObject implements IUniformResource {

	/**
	 * 
	 */
	protected AbstractUniformResource() {
		super();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		return obj instanceof IUniformResource && getURI().equals(((IUniformResource)obj).getURI());
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return getURI().hashCode();
	}

}
