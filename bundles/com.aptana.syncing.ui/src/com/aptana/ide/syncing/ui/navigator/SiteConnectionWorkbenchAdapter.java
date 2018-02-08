/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ide.syncing.ui.navigator;

import org.eclipse.core.filesystem.IFileStore;
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
 */
public class SiteConnectionWorkbenchAdapter implements IWorkbenchAdapter, IDeferredWorkbenchAdapter
{

	private static SiteConnectionWorkbenchAdapter instance;

	private static final Object[] EMPTY = new Object[0];
	private static final ImageDescriptor IMAGE_DESCRIPTOR = SyncingUIPlugin
			.getImageDescriptor("icons/full/obj16/ftp.png"); //$NON-NLS-1$
	private static final ImageDescriptor ERROR_IMAGE_DESCRIPTOR = SyncingUIPlugin
			.getImageDescriptor("icons/full/obj16/error.png"); //$NON-NLS-1$

	/**
	 * 
	 */
	private SiteConnectionWorkbenchAdapter()
	{
	}

	/* package */static synchronized SiteConnectionWorkbenchAdapter getInstance()
	{
		if (instance == null)
		{
			instance = new SiteConnectionWorkbenchAdapter();
		}
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.model.IWorkbenchAdapter#getImageDescriptor(java.lang.Object)
	 */
	public ImageDescriptor getImageDescriptor(Object object)
	{
		if (object instanceof ISiteConnection)
		{
			ISiteConnection siteConnection = (ISiteConnection) object;
			if (siteConnection.getSource() == null || siteConnection.getDestination() == null)
			{
				// the connection is no longer valid
				return ERROR_IMAGE_DESCRIPTOR;
			}
			return IMAGE_DESCRIPTOR;
		}
		else if (object instanceof ProjectSiteConnection)
		{
			object = ((ProjectSiteConnection) object).getSiteConnection().getDestination();
			IWorkbenchAdapter workbenchAdapter = (IWorkbenchAdapter) Platform.getAdapterManager().getAdapter(object,
					IWorkbenchAdapter.class);
			if (workbenchAdapter != null)
			{
				return workbenchAdapter.getImageDescriptor(object);
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.model.IWorkbenchAdapter#getLabel(java.lang.Object)
	 */
	public String getLabel(Object object)
	{
		if (object instanceof ISiteConnection)
		{
			return ((ISiteConnection) object).getName();
		}
		else if (object instanceof ProjectSiteConnection)
		{
			object = ((ProjectSiteConnection) object).getSiteConnection().getDestination();
			IWorkbenchAdapter workbenchAdapter = (IWorkbenchAdapter) Platform.getAdapterManager().getAdapter(object,
					IWorkbenchAdapter.class);
			if (workbenchAdapter != null)
			{
				return workbenchAdapter.getLabel(object);
			}
		}
		return String.valueOf(object);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.ui.io.navigator.FileSystemWorkbenchAdapter#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object object)
	{
		return EMPTY;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.model.IWorkbenchAdapter#getParent(java.lang.Object)
	 */
	public Object getParent(Object o)
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.progress.IDeferredWorkbenchAdapter#getRule(java.lang.Object)
	 */
	public ISchedulingRule getRule(Object object)
	{
		if (object instanceof IAdaptable)
		{
			return (ISchedulingRule) ((IAdaptable) object).getAdapter(ISchedulingRule.class);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.progress.IDeferredWorkbenchAdapter#isContainer()
	 */
	public boolean isContainer()
	{
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.ui.io.navigator.FileSystemWorkbenchAdapter#fetchDeferredChildren(java.lang.Object,
	 * org.eclipse.ui.progress.IElementCollector, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void fetchDeferredChildren(Object object, IElementCollector collector, IProgressMonitor monitor)
	{
		if (object instanceof ProjectSiteConnection)
		{
			object = ((ProjectSiteConnection) object).getSiteConnection().getDestination();
			IDeferredWorkbenchAdapter deferredWorkbenchAdapter = (IDeferredWorkbenchAdapter) Platform
					.getAdapterManager().getAdapter(object, IDeferredWorkbenchAdapter.class);
			if (deferredWorkbenchAdapter != null)
			{
				deferredWorkbenchAdapter.fetchDeferredChildren(object, collector, monitor);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public static class Factory implements IAdapterFactory
	{

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
		 */
		public Object getAdapter(Object adaptableObject, Class adapterType)
		{
			if (IWorkbenchAdapter.class == adapterType || IDeferredWorkbenchAdapter.class == adapterType)
			{
				return getInstance();
			}
			if (IFileStore.class == adapterType && adaptableObject instanceof ProjectSiteConnection)
			{
				return ((ProjectSiteConnection) adaptableObject).getAdapter(adapterType);
			}
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
		 */
		public Class[] getAdapterList()
		{
			return new Class[] { IWorkbenchAdapter.class, IDeferredWorkbenchAdapter.class };
		}
	}
}
