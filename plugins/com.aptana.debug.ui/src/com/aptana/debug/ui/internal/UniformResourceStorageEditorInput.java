/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable staticFieldNamingConvention

package com.aptana.debug.ui.internal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Map;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.editors.text.ILocationProvider;

import com.aptana.core.logging.IdeLog;
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
		if (ILocationProvider.class.equals(adapter)) {
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

/** XXX: use ILocationProviderExtension + EFS */
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
			IdeLog.logError(DebugUiPlugin.getDefault(), e);
		}
		return null;
	}

	/* package */void validate(UniformResourceStorageEditorInput editorInput) {
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
					IdeLog.logError(DebugUiPlugin.getDefault(), e);
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
			IdeLog.logError(DebugUiPlugin.getDefault(), e);
		}
		if (file != null) {
			if (!file.delete()) {
				file.deleteOnExit();
			}
		}
		return null;
	}

	private boolean loadRemoteFileStorage(UniformResourceStorage storage, File file) throws CoreException {
		InputStream in = storage.getContents();
		OutputStream out = null;
		try {
			if (!file.canWrite()) {
				if (!file.delete()) {
					file.deleteOnExit();
				}
			}
			out = new FileOutputStream(file);
			byte[] buffer = new byte[1024];
			int n;
			while ((n = in.read(buffer)) > 0) { // $codepro.audit.disable assignmentInCondition
				out.write(buffer, 0, n);
			}
			file.setReadOnly();
			return true;
		} catch (IOException e) {
			IdeLog.logWarning(DebugUiPlugin.getDefault(), e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException ignore) {
					ignore.getCause();
				}
			}
		}
		return false;
	}

	private boolean validateContents(UniformResourceStorage storage, boolean remove) {
		boolean valid = storage.isValid();
		if (remove && !valid) {
			map.remove(storage);
		}
		return valid;
	}
}
