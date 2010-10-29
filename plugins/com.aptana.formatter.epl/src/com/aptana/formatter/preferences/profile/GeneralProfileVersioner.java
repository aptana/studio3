/******************************************************************************* 
 * Copyright (c) 2008 xored software, Inc.  
 * 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 * 
 * Contributors: 
 *     xored software, Inc. - initial API and Implementation (Yuri Strot) 
 *******************************************************************************/
package com.aptana.formatter.preferences.profile;


/**
 * Default implementation of the <code>IProfileVersioner</code>
 */
public class GeneralProfileVersioner implements IProfileVersioner
{

	private static final int DEFAULT_VERSION = 1;
	private String formatter;

	public GeneralProfileVersioner(String formatter)
	{
		this.formatter = formatter;
	}

	public int getCurrentVersion()
	{
		return DEFAULT_VERSION;
	}

	public int getFirstVersion()
	{
		return DEFAULT_VERSION;
	}

	public String getFormatterId()
	{
		return formatter;
	}

	public void update(IProfile profile)
	{
		if (profile instanceof CustomProfile)
			((CustomProfile) profile).setVersion(DEFAULT_VERSION);
	}
}
