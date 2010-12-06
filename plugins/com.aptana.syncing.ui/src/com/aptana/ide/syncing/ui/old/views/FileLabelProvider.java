/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
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
package com.aptana.ide.syncing.ui.old.views;

import java.util.HashMap;

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

	private HashMap<IEditorDescriptor, Image> images = new HashMap<IEditorDescriptor, Image>();

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