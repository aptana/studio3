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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Map;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.editors.text.ILocationProvider;

import com.aptana.core.resources.UniformResourceStorage;
import com.aptana.debug.ui.DebugUiPlugin;

/**
 * @author Max Stepanov
 */
public class UniformResourceStorageEditorInput extends StorageEditorInput {

	private static UniformResourceStorageLocationProvider locationProvider = new UniformResourceStorageLocationProvider();

	/**
	 * UniformResourceStorageEditorInput
	 * 
	 * @param storage
	 */
	public UniformResourceStorageEditorInput(UniformResourceStorage storage) {
		super(storage);
		locationProvider.validate(this);
	}

	/*
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class adapter) {
		if (ILocationProvider.class == adapter) {
			return locationProvider;
		}
		return super.getAdapter(adapter);
	}

	/*
	 * @see org.eclipse.ui.IEditorInput#exists()
	 */
	public boolean exists() {
		IPath path = ((UniformResourceStorage) getStorage()).getFullPath();
		if (path != null) {
			return path.toFile().exists();
		}
		return true;
	}

	/*
	 * @see org.eclipse.ui.IEditorInput#getToolTipText()
	 */
	public String getToolTipText() {
		IPath path = ((UniformResourceStorage) getStorage()).getFullPath();
		if (path != null) {
			return path.toOSString();
		}
		return ((UniformResourceStorage) getStorage()).getURI().toString();
	}

}

/** XXX: temporary solution until UnifiedEditor issues with FileInfo resolved */
class UniformResourceStorageLocationProvider implements ILocationProvider {
	/** TODO: find a better way for remote->local file mapping */
	private Map<IStorage, String> map = new Hashtable<IStorage, String>();

	/*
	 * @see org.eclipse.ui.editors.text.ILocationProvider#getPath(java.lang.Object)
	 */
	public IPath getPath(Object element) {
		try {
			if (element instanceof IStorageEditorInput) {
				IStorage storage = ((IStorageEditorInput) element).getStorage();
				if (storage instanceof UniformResourceStorage) {
					IPath path = ((UniformResourceStorage) storage).getFullPath();
					if (path != null) {
						return path;
					}
					validateContents((UniformResourceStorage) storage, true);
					String filePath = (String) map.get(storage);
					if (filePath == null) {
						File file = getLocalFileForRemoteFileStorage((UniformResourceStorage) storage);
						if (file != null && file.exists()) {
							filePath = file.toString();
							map.put(storage, filePath);
						}
					}
					if (filePath != null) {
						return new Path(filePath);
					}
				}
			}
		} catch (CoreException e) {
			DebugUiPlugin.log(e);
		}
		return null;
	}

	/* package */ void validate(UniformResourceStorageEditorInput editorInput) {
		IStorage storage = editorInput.getStorage();
		IPath path = ((UniformResourceStorage) storage).getFullPath();
		if (path != null) {
			return;
		}
		String filePath = (String) map.get(storage);
		if (filePath != null) {
			if (!validateContents((UniformResourceStorage) storage, false)) {
				try {
					loadRemoteFileStorage((UniformResourceStorage) storage, new File(filePath));
				} catch (CoreException e) {
					DebugUiPlugin.log(e);
				}
			}
		}

	}

	private File getLocalFileForRemoteFileStorage(UniformResourceStorage storage) throws CoreException {
		File file = null;
		try {
			String name = storage.getName();
			if (name.length() < 3) {
				name = "file" + name; //$NON-NLS-1$
			}
			file = File.createTempFile(name, null);
			if (loadRemoteFileStorage(storage, file)) {
				file.deleteOnExit();
				return file;
			}
		} catch (IOException e) {
		}
		if (file != null) {
			file.delete();
		}
		return null;
	}

	private boolean loadRemoteFileStorage(UniformResourceStorage storage, File file) throws CoreException {
		InputStream in = storage.getContents();
		OutputStream out = null;
		try {
			if (!file.canWrite()) {
				file.delete();
			}
			out = new FileOutputStream(file);
			byte[] buffer = new byte[1024];
			int n;
			while ((n = in.read(buffer)) > 0) {
				out.write(buffer, 0, n);
			}
			out.close();
			out = null;
			file.setReadOnly();
			return true;
		} catch (IOException e) {
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException ignore) {
				}
			}
		}
		return false;
	}

	private boolean validateContents(UniformResourceStorage storage, boolean remove) {
		try {
			Method method = storage.getClass().getDeclaredMethod("isValid", new Class[0]); //$NON-NLS-1$
			Boolean result = (Boolean) method.invoke(storage, new Object[0]);
			if (remove && !result.booleanValue()) {
				map.remove(storage);
			}
			return result.booleanValue();
		} catch (Exception e) {
			DebugUiPlugin.log(e);
		}
		return false;
	}
}
