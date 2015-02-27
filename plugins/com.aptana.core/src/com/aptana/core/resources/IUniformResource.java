/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.resources;

import java.net.URI;

import org.eclipse.core.runtime.IAdaptable;

/**
 * @author Max Stepanov
 */
public interface IUniformResource extends IAdaptable
{

	/**
	 * Resource URI
	 * 
	 * @return URI
	 */
	URI getURI();
}
