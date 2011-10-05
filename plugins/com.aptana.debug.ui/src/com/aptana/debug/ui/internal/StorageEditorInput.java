/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.debug.ui.internal;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;

/**
 * @author Max Stepanov
 */
public abstract class StorageEditorInput extends PlatformObject implements IStorageEditorInput {
	/**
	 * Storage associated with this editor input
	 */
	private IStorage fStorage;

	/**
	 * Constructs an editor input on the given storage
	 * 
	 * @param storage
	 */
	protected StorageEditorInput(IStorage storage) {
		fStorage = storage;
	}

	/*
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class adapter) {
		Object object = getStorage().getAdapter(adapter);
		if (object != null) {
			return object;
		}
		return super.getAdapter(adapter);
	}

	/*
	 * @see IStorageEditorInput#getStorage()
	 */
	public IStorage getStorage() {
		return fStorage;
	}

	/*
	 * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
	 */
	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	/*
	 * @see org.eclipse.ui.IEditorInput#getName()
	 */
	public String getName() {
		return getStorage().getName();
	}

	/*
	 * @see org.eclipse.ui.IEditorInput#getPersistable()
	 */
	public IPersistableElement getPersistable() {
		return null;
	}

	/*
	 * @see org.eclipse.ui.IEditorInput#getToolTipText()
	 */
	public String getToolTipText() {
		return getStorage().getFullPath().toOSString();
	}

	/*
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		return object instanceof StorageEditorInput && getStorage().equals(((StorageEditorInput) object).getStorage());
	}

	/*
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return getStorage().hashCode();
	}
}
