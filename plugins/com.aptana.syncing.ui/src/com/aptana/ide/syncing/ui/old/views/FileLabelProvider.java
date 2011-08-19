/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.ui.old.views;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorDescriptor;

import com.aptana.ide.core.io.IConnectionPoint;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class FileLabelProvider extends LabelProvider
{

	private Map<IEditorDescriptor, Image> images = new HashMap<IEditorDescriptor, Image>();

	/**
	 * @see org.eclipse.jface.viewers.LabelProvider#dispose()
	 */
	public void dispose()
	{
		super.dispose();
		for (Image image : images.values())
		{
			if (image != null && !image.isDisposed())
			{
				image.dispose();
			}
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element)
	{
		String retVal = null;
		if (element instanceof IConnectionPoint)
		{
			retVal = ((IConnectionPoint) element).getName();
		}
		else if (element instanceof IFileStore)
		{
			IFileStore f = (IFileStore) element;
			retVal = f.getName();
		}
		// if (element == FileTreeContentProvider.LOADING)
		// {
		// return Messages.FileLabelProvider_Loading_msg;
		// }
		// if (retVal == null)
		// {
		// retVal = Messages.FileExplorerView_UnknownElement;
		// }
		return retVal;
	}

	/**
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object element)
	{
		Image image = null;

		// if (element instanceof ProtocolManager)
		// {
		// ProtocolManager pm = (ProtocolManager) element;
		// image = pm.getImage();
		// }
		// else if (element instanceof IVirtualFileManager)
		// {
		// IVirtualFileManager fm = (IVirtualFileManager) element;
		// image = fm.getImage();
		// }
		// else if (element instanceof IVirtualFile)
		// {
		// IVirtualFile f = (IVirtualFile) element;
		// image = f.getImage();
		// if (image == null)
		// {
		// IEditorDescriptor desc = EclipseUIUtils.getWorkbenchEditorRegistry().getDefaultEditor(f.getName());
		// if (desc == null || desc.getImageDescriptor() == null)
		// {
		// IWorkbench workbench = PlatformUI.getWorkbench();
		// if (workbench != null)
		// {
		// ISharedImages sharedImages = workbench.getSharedImages();
		// if (f.isDirectory())
		// {
		// image = sharedImages.getImage(ISharedImages.IMG_OBJ_FOLDER);
		// }
		// else
		// {
		// image = ImageUtils.getIcon(f.getExtension());
		// if (image == null)
		// {
		// image = sharedImages.getImage(ISharedImages.IMG_OBJ_FILE);
		// }
		// }
		// }
		// }
		// else
		// {
		// if (images.containsKey(desc))
		// {
		// image = (Image) images.get(desc);
		// }
		// else
		// {
		// image = desc.getImageDescriptor().createImage();
		// images.put(desc, image);
		// }
		// }
		// }
		// }
		// if (element == FileTreeContentProvider.LOADING)
		// {
		//	return FilePlugin.getImage("icons/hourglass.png"); //$NON-NLS-1$
		// }
		return image;
	}
}
