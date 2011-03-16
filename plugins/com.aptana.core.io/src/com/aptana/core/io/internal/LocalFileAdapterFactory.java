/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.core.io.internal;

import java.io.File;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.internal.filesystem.local.LocalFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.aptana.core.io.CoreIOPlugin;

/**
 * @author Max Stepanov
 *
 */
@SuppressWarnings({"restriction", "rawtypes"})
public class LocalFileAdapterFactory implements IAdapterFactory {

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
	 */
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adapterType == File.class && adaptableObject instanceof LocalFile) {
			try {
				return ((LocalFile) adaptableObject).toLocalFile(EFS.NONE, null);
			} catch (CoreException e) {
				CoreIOPlugin.log(new Status(IStatus.WARNING, CoreIOPlugin.PLUGIN_ID, "", e)); //$NON-NLS-1$
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
	 */
	public Class[] getAdapterList() {
		return new Class[] { File.class };
	}
}
