/*******************************************************************************
 * Copyright (c) 2009 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package com.aptana.formatter.preferences.profile;

import java.io.File;
import java.util.Collection;

import org.eclipse.core.runtime.CoreException;

public interface IProfileStore
{

	void writeProfilesToFile(Collection<IProfile> profiles, File file) throws CoreException;

	Collection<IProfile> readProfilesFromFile(File file) throws CoreException;

}
