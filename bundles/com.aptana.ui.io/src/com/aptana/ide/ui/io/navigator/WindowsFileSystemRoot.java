/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io.navigator;

import java.io.File;

import javax.swing.filechooser.FileSystemView;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;

import com.aptana.ide.core.io.LocalRoot;
import com.aptana.ide.ui.io.CoreIOImages;
import com.aptana.ide.ui.io.ImageUtils;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class WindowsFileSystemRoot implements IWorkbenchAdapter {

	private File rootFile;
	private String name;

	WindowsFileSystemRoot(File file) {
		rootFile = file;
		name = FileSystemView.getFileSystemView()
				.getSystemDisplayName(rootFile);
	}

	/**
	 * @see org.eclipse.ui.model.IWorkbenchAdapter#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object o) {
		return LocalRoot.createWindowsSubroots(rootFile);
	}

	/**
	 * @see org.eclipse.ui.model.IWorkbenchAdapter#getImageDescriptor(java.lang.Object)
	 */
	public ImageDescriptor getImageDescriptor(Object object) {
		ImageDescriptor imageDescriptor = ImageUtils.getImageDescriptor(rootFile);
		if (imageDescriptor != null) {
			return imageDescriptor;
		}
		return CoreIOImages.getImageDescriptor(CoreIOImages.IMG_OBJS_DRIVE);
	}

	/**
	 * @see org.eclipse.ui.model.IWorkbenchAdapter#getLabel(java.lang.Object)
	 */
	public String getLabel(Object o) {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.model.IWorkbenchAdapter#getParent(java.lang.Object)
	 */
	public Object getParent(Object o) {
		return null;
	}

	@Override
	public String toString() {
		return name;
	}
}
