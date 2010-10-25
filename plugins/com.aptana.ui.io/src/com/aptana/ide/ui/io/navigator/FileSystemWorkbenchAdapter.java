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

package com.aptana.ide.ui.io.navigator;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.progress.IDeferredWorkbenchAdapter;
import org.eclipse.ui.progress.IElementCollector;

import com.aptana.ide.core.io.IBaseRemoteConnectionPoint;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.core.io.IConnectionPointCategory;
import com.aptana.ide.core.io.LocalConnectionPoint;
import com.aptana.ide.core.io.LocalRoot;
import com.aptana.ide.core.io.PermissionDeniedException;
import com.aptana.ide.core.io.WorkspaceConnectionPoint;
import com.aptana.ide.core.io.vfs.IExtendedFileInfo;
import com.aptana.ide.ui.io.CoreIOImages;
import com.aptana.ide.ui.io.FileSystemUtils;
import com.aptana.ide.ui.io.IOUIPlugin;
import com.aptana.ide.ui.io.ImageUtils;
import com.aptana.ui.ImageAssociations;
import com.aptana.ui.UIUtils;

/**
 * @author Max Stepanov
 *
 */
public class FileSystemWorkbenchAdapter implements IWorkbenchAdapter, IDeferredWorkbenchAdapter {

	private static FileSystemWorkbenchAdapter instance;
	
	private static final Object[] EMPTY = new Object[0];
	
	/**
	 * 
	 */
	protected FileSystemWorkbenchAdapter() {
	}
	
	/* package */ static synchronized FileSystemWorkbenchAdapter getInstance() {
		if (instance == null) {
			instance = new FileSystemWorkbenchAdapter();
		}
		return instance;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.model.IWorkbenchAdapter#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object object) {
		if (object instanceof IConnectionPoint) {
		    IConnectionPoint connectionPoint = (IConnectionPoint) object;
			if (object instanceof WorkspaceConnectionPoint) {
			    IContainer container = ((WorkspaceConnectionPoint) object).getResource();
			    if (container == null) {
			        return EMPTY;
			    }
				try {
					return container.members();
				} catch (CoreException e) {
					IOUIPlugin.logImportant(Messages.FileSystemWorkbenchAdapter_FailedToGetMembers, e);
				}
			} else {
				try {
					return fetchFileSystemChildren(connectionPoint.getRoot(), new NullProgressMonitor());
				} catch (CoreException e) {
				    // makes sure it's connected and tries again
				    try {
                        connectionPoint.connect(true, new NullProgressMonitor());
                        return fetchFileSystemChildren(connectionPoint.getRoot(), new NullProgressMonitor());
                    } catch (CoreException e1) {
                        IOUIPlugin.logError(Messages.FileSystemWorkbenchAdapter_FailedToFetchChildren, e);
                        UIUtils.showErrorMessage(Messages.FileSystemWorkbenchAdapter_FailedToFetchChildren, e);
                    }
				}
			}
		} else if (object instanceof IConnectionPointCategory) {
            return ((IConnectionPointCategory) object).getConnectionPoints();
		}
		return EMPTY;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.model.IWorkbenchAdapter#getImageDescriptor(java.lang.Object)
	 */
	public ImageDescriptor getImageDescriptor(Object object) {
		if (object instanceof FileSystemObject) {
			FileSystemObject fsObject = (FileSystemObject) object;
			if (fsObject.isDirectory()) {
				return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_FOLDER);
			}
			File file = (File) fsObject.getAdapter(File.class);
			if (file != null) {
				ImageDescriptor imageDescriptor = ImageUtils.getImageDescriptor(file);
				if (imageDescriptor != null) {
					return imageDescriptor;
				}
			}
			return ImageUtils.getImageDescriptor(fsObject.getName());
		} else if (object instanceof LocalRoot) {
			File file = ((LocalRoot) object).getFile();
			ImageDescriptor imageDescriptor = ImageUtils.getImageDescriptor(file);
			if (imageDescriptor != null) {
				return imageDescriptor;
			}
			if (file.getParentFile() == null) {
				return CoreIOImages.getImageDescriptor(CoreIOImages.IMG_OBJS_DRIVE);
			}
			return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_FOLDER);
		} else if (object instanceof WorkspaceConnectionPoint) {
			IContainer resource = ((WorkspaceConnectionPoint) object).getResource();
			if (resource != null) {
				IWorkbenchAdapter workbenchAdapter = (IWorkbenchAdapter) resource.getAdapter(IWorkbenchAdapter.class);
				if (workbenchAdapter != null) {
					return workbenchAdapter.getImageDescriptor(resource);
				}
			}
			return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_FOLDER);
		} else if (object instanceof IConnectionPoint) {
			ImageDescriptor image = ImageAssociations.getInstance().getImageDescriptor(object);
			if (image != null) {
				return image;
			}
			if (object instanceof LocalConnectionPoint) {
				return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_FOLDER);
			}
			return CoreIOImages.getImageDescriptor(CoreIOImages.IMG_OBJS_CONNECTION);
		} else if (object instanceof IConnectionPointCategory) {
			ImageDescriptor image = ImageAssociations.getInstance().getImageDescriptor(object);
			if (image != null) {
				return image;
			}
			return CoreIOImages.getImageDescriptor(CoreIOImages.IMG_OBJS_CONNECTION);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.model.IWorkbenchAdapter#getLabel(java.lang.Object)
	 */
	public String getLabel(Object object) {
		if (object instanceof FileSystemObject) {
			return ((FileSystemObject) object).getName();
		} else if (object instanceof IFileStore) {
			return ((IFileStore) object).getName();
		} else if (object instanceof IConnectionPoint) {
			if (object instanceof IBaseRemoteConnectionPoint) {
				IPath path = ((IBaseRemoteConnectionPoint) object).getPath();
				if (path.segmentCount() > 0) {
					return MessageFormat.format("{0} ({1})", new Object[] { ((IConnectionPoint) object).getName(), path.toPortableString() }); //$NON-NLS-1$
				}
			}
			return ((IConnectionPoint) object).getName();
		} else if (object instanceof IConnectionPointCategory) {
			return ((IConnectionPointCategory) object).getName();
		} else if (object instanceof LocalRoot) {
			return ((LocalRoot) object).getName();
		}
		return String.valueOf(object);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.model.IWorkbenchAdapter#getParent(java.lang.Object)
	 */
	public Object getParent(Object object) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.progress.IDeferredWorkbenchAdapter#fetchDeferredChildren(java.lang.Object, org.eclipse.ui.progress.IElementCollector, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void fetchDeferredChildren(Object object, IElementCollector collector, IProgressMonitor monitor) {
		try {
			if (object instanceof IConnectionPoint) {
			    // for deferred case, makes sure it is connected first
			    // ((IConnectionPoint) object).connect(monitor);
				collector.add(
						fetchFileSystemChildren(((IConnectionPoint) object).getRoot(), monitor),
						monitor);
			} else if (object instanceof FileSystemObject) {
				collector.add(
						fetchFileSystemChildren(((FileSystemObject) object).getFileStore(), monitor),
						monitor);
			} else if (object instanceof IFileStore) {
				collector.add(
						fetchFileSystemChildren((IFileStore) object, monitor),
						monitor);
			}
		} catch (CoreException e) {
            if (object instanceof IConnectionPoint) {
                // makes sure the connection point is connected and tries again
                IConnectionPoint connectionPoint = (IConnectionPoint) object;
                try {
                    connectionPoint.connect(true, monitor);
                    collector.add(fetchFileSystemChildren(connectionPoint.getRoot(), monitor),
                            monitor);
                    return;
                } catch (CoreException e1) {
                }
            }
			else if (object instanceof FileSystemObject && e.getCause() instanceof PermissionDeniedException)
			{
				IFileInfo fileInfo = FileSystemUtils.getFileInfo(object);
				if (fileInfo != null && fileInfo instanceof IExtendedFileInfo) {
					((IExtendedFileInfo) fileInfo).setPermissions(0);
				}
				return;
			}
			IOUIPlugin.logError(Messages.FileSystemWorkbenchAdapter_FailedToFetchDeferredChildren, e);
			UIUtils.showErrorMessage(Messages.FileSystemWorkbenchAdapter_FailedToFetchChildren, e);
		} finally {
			collector.done();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.progress.IDeferredWorkbenchAdapter#getRule(java.lang.Object)
	 */
	public ISchedulingRule getRule(Object object) {
		if (object instanceof IAdaptable) {
			return (ISchedulingRule) ((IAdaptable) object).getAdapter(ISchedulingRule.class);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.progress.IDeferredWorkbenchAdapter#isContainer()
	 */
	public boolean isContainer() {
		return true;
	}
	
	private static FileSystemObject[] fetchFileSystemChildren(IFileStore parent, IProgressMonitor monitor) throws CoreException {
		IFileInfo[] fileInfos = parent.childInfos(EFS.NONE, monitor);
		List<FileSystemObject> list = new ArrayList<FileSystemObject>();
		for (IFileInfo fi : fileInfos) {
			list.add(new FileSystemObject(parent.getChild(fi.getName()), fi));
		}
		return list.toArray(new FileSystemObject[list.size()]);
	}

	@SuppressWarnings("rawtypes")
	public static class Factory implements IAdapterFactory {
		
		/* (non-Javadoc)
		 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
		 */
		public Object getAdapter(Object adaptableObject, Class adapterType) {
			if (IWorkbenchAdapter.class == adapterType) {
				return getInstance();
			} else if (IDeferredWorkbenchAdapter.class == adapterType) {
				if (adaptableObject instanceof WorkspaceConnectionPoint
						|| adaptableObject instanceof LocalConnectionPoint
						|| adaptableObject instanceof LocalRoot) {
					return null;
				}
				return getInstance();
			}
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
		 */
		public Class[] getAdapterList() {
			return new Class[] { IWorkbenchAdapter.class, IDeferredWorkbenchAdapter.class };
		}
	}
}
