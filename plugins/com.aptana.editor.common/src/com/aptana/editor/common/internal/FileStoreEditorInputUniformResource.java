/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.common.internal;

import java.net.URI;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.ide.FileStoreEditorInput;

import com.aptana.core.resources.AbstractUniformResource;
import com.aptana.core.resources.IUniformResource;

/**
 * @author Max Stepanov
 *
 */
@SuppressWarnings("rawtypes")
public final class FileStoreEditorInputUniformResource extends AbstractUniformResource {

	private final FileStoreEditorInput editorInput;
	
	public FileStoreEditorInputUniformResource(FileStoreEditorInput editorInput) {
		super();
		this.editorInput = editorInput;
	}

	public URI getURI() {
		return editorInput.getURI();
	}

	@Override
	public Object getAdapter(Class adapter) {
		Object object = super.getAdapter(adapter);
		if (object == null) {
			object = editorInput.getAdapter(adapter);
		}
		return object;
	}

	public static class Factory implements IAdapterFactory {

		/* (non-Javadoc)
		 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
		 */
		public Object getAdapter(final Object adaptableObject, Class adapterType) {
			if (IUniformResource.class == adapterType && adaptableObject instanceof FileStoreEditorInput) {
				return new FileStoreEditorInputUniformResource((FileStoreEditorInput) adaptableObject);
			}
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
		 */
		public Class[] getAdapterList() {
			return new Class[] { IUniformResource.class };
		}

	}

}