/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.ui.IEditorInput;

public interface IUniformFileStoreEditorInput extends IEditorInput
{

	public IFileStore getFileStore();

	public boolean isRemote();
}
