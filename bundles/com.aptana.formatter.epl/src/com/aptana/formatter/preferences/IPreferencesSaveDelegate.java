/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.formatter.preferences;

public interface IPreferencesSaveDelegate
{
	void setString(String qualifier, String key, String value);

	void setInt(String qualifier, String key, int value);

	void setBoolean(String qualifier, String key, boolean value);
}
