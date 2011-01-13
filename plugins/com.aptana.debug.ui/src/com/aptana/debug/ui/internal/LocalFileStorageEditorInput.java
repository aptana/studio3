/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
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
package com.aptana.debug.ui.internal;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.sourcelookup.containers.LocalFileStorage;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.editors.text.ILocationProvider;


/**
 * @author Max Stepanov
 */
public class LocalFileStorageEditorInput extends StorageEditorInput implements IPathEditorInput, ILocationProvider {
	/**
	 * Constructs an editor input for the given storage
	 * 
	 * @param storage
	 */
	public LocalFileStorageEditorInput(IStorage storage) {
		super(storage);
	}

	/**
	 * @see org.eclipse.ui.IEditorInput#exists()
	 */
	public boolean exists() {
		IStorage storage = getStorage();
		if (storage instanceof LocalFileStorage) {
			return ((LocalFileStorage) storage).getFile().exists();
		} else if (storage instanceof com.aptana.core.resources.LocalFileStorage) {
			return ((com.aptana.core.resources.LocalFileStorage) storage).getFile().exists();
		}
		return false;
	}

	/**
	 * @see org.eclipse.ui.IPathEditorInput#getPath()
	 */
	public IPath getPath() {
		IStorage storage = getStorage();
		if (storage instanceof LocalFileStorage) {
			return ((LocalFileStorage) getStorage()).getFullPath();
		} else if (storage instanceof com.aptana.core.resources.LocalFileStorage) {
			return ((com.aptana.core.resources.LocalFileStorage) storage).getFullPath();
		}
		return null;
	}

	/**
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class adapter) {
		if (ILocationProvider.class == adapter) {
			return this;
		}
		return super.getAdapter(adapter);
	}

	/**
	 * @see org.eclipse.ui.editors.text.ILocationProvider#getPath(java.lang.Object)
	 */
	public IPath getPath(Object element) {
		if (element instanceof LocalFileStorageEditorInput) {
			return ((LocalFileStorageEditorInput) element).getPath();
		}
		return null;
	}
}
