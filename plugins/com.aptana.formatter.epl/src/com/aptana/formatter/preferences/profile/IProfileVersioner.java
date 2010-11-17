/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     xored software, Inc. - initial API and Implementation (Yuri Strot) 
 *******************************************************************************/
package com.aptana.formatter.preferences.profile;


/**
 * This interface uses to correctly update old profiles to current version.
 * 
 * @see GeneralProfileVersioner
 */
public interface IProfileVersioner
{

	public int getFirstVersion();

	public int getCurrentVersion();

	/**
	 * @return formatter identifier
	 */
	public String getFormatterId();

	/**
	 * Update the <code>profile</code> to the current version number
	 */
	public void update(IProfile profile);

}
