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

import java.util.Map;


/**
 * Represents a profile with a unique ID, a name and a map containing the code formatter settings.
 */
public abstract class Profile implements IProfile
{

	public boolean equalsTo(Map<String, String> otherMap)
	{
		return getSettings().equals(otherMap);
	}

	public boolean isBuiltInProfile()
	{
		return false;
	}
}
