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

package com.aptana.ide.syncing.ui.navigator;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.progress.IDeferredWorkbenchAdapter;
import org.eclipse.ui.progress.IElementCollector;

import com.aptana.ide.syncing.core.ISiteConnection;
import com.aptana.ide.syncing.ui.SyncingUIPlugin;

/**
 * @author Max Stepanov
 *
 */
public class SiteConnectionWorkbenchAdapter implements IWorkbenchAdapter, IDeferredWorkbenchAdapter {

	private static SiteConnectionWorkbenchAdapter instance;

	private static final Object[] EMPTY = new Object[0];
	private static final ImageDescriptor IMAGE_DESCRIPTOR = SyncingUIPlugin.getImageDescriptor("icons/full/obj16/ftp.png"); //$NON-NLS-1$
	private static final ImageDescriptor ERROR_IMAGE_DESCRIPTOR = SyncingUIPlugin.getImageDescriptor("icons/full/obj16/error.png"); //$NON-NLS-1$

	/**
	 * 
	 */
	private SiteConnectionWorkbenchAdapter() {
	}
	
	/* package */static synchronized SiteConnectionWorkbenchAdapter getInstance() {
		if (instance == null) {
			instance = new SiteConnectionWorkbenchAdapter();
		}
		return instance;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.model.IWorkbenchAdapter#getImageDescriptor(java.lang.Object)
	 */
	public ImageDescriptor getImageDescriptor(Object object) {
		if (object instanceof ISiteConnection) {
		    ISiteConnection siteConnection = (ISiteConnection) object;
		    if (siteConnection.getSource() == null || siteConnection.getDestination() == null) {
		        // the connection is no longer valid
		        return ERROR_IMAGE_DESCRIPTOR;
		    }
			return IMAGE_DESCRIPTOR;
		} else if (object instanceof ProjectSiteConnection) {
			object = ((ProjectSiteConnection) object).getSiteConnection().getDestination();
			IWorkbenchAdapter workbenchAdapter = (IWorkbenchAdapter) Platform.getAdapterManager().getAdapter(object, IWorkbenchAdapter.class);
			if (workbenchAdapter != null) {
				return workbenchAdapter.getImageDescriptor(object);
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.model.IWorkbenchAdapter#getLabel(java.lang.Object)
	 */
	public String getLabel(Object object) {
		if (object instanceof ISiteConnection) {
			return ((ISiteConnection) object).getName();
		} else if (object instanceof ProjectSiteConnection) {
			object = ((ProjectSiteConnection) object).getSiteConnection().getDestination();
			IWorkbenchAdapter workbenchAdapter = (IWorkbenchAdapter) Platform.getAdapterManager().getAdapter(object, IWorkbenchAdapter.class);
			if (workbenchAdapter != null) {
				return workbenchAdapter.getLabel(object);
			}
		}
		return String.valueOf(object);
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.ui.io.navigator.FileSystemWorkbenchAdapter#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object object) {
		return EMPTY;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.model.IWorkbenchAdapter#getParent(java.lang.Object)
	 */
	public Object getParent(Object o) {
		return null;
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

	/* (non-Javadoc)
	 * @see com.aptana.ide.ui.io.navigator.FileSystemWorkbenchAdapter#fetchDeferredChildren(java.lang.Object, org.eclipse.ui.progress.IElementCollector, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void fetchDeferredChildren(Object object, IElementCollector collector, IProgressMonitor monitor) {
		if (object instanceof ProjectSiteConnection) {
			object = ((ProjectSiteConnection) object).getSiteConnection().getDestination();
			IDeferredWorkbenchAdapter deferredWorkbenchAdapter = (IDeferredWorkbenchAdapter) Platform.getAdapterManager().getAdapter(object, IDeferredWorkbenchAdapter.class);
			if (deferredWorkbenchAdapter != null) {
				deferredWorkbenchAdapter.fetchDeferredChildren(object, collector, monitor);				
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public static class Factory implements IAdapterFactory {
		
		/* (non-Javadoc)
		 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
		 */
		public Object getAdapter(Object adaptableObject, Class adapterType) {
			if (IWorkbenchAdapter.class == adapterType
					|| IDeferredWorkbenchAdapter.class == adapterType) {
				return getInstance();
			}
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
		 */
		public Class[] getAdapterList() {
			return new Class[] {
					IWorkbenchAdapter.class,
					IDeferredWorkbenchAdapter.class
				};
		}
	}
}
