/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ide.ui.io;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.aptana.ide.core.io.ConnectionPointType;
import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.core.io.IConnectionPointCategory;
import com.aptana.ide.core.io.IConnectionPointManager;
import com.aptana.ide.core.io.events.ConnectionPointEvent;
import com.aptana.ide.core.io.events.IConnectionPointListener;
import com.aptana.ide.ui.io.navigator.FileSystemElementComparer;
import com.aptana.ide.ui.io.navigator.RemoteNavigatorView;
import com.aptana.theme.IThemeManager;
import com.aptana.theme.ThemePlugin;
import com.aptana.ui.util.UIUtils;

/**
 * The activator class controls the plug-in life cycle
 */
public class IOUIPlugin extends AbstractUIPlugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.ui.io"; //$NON-NLS-1$

	// The shared instance
	private static IOUIPlugin plugin;

	private IConnectionPointListener connectionListener = new IConnectionPointListener()
	{

		public void connectionPointChanged(ConnectionPointEvent event)
		{
			IConnectionPoint connection = event.getConnectionPoint();
			IConnectionPointManager manager = CoreIOPlugin.getConnectionPointManager();
			ConnectionPointType type = manager.getType(connection);
			if (type == null)
			{
				return;
			}

			switch (event.getKind())
			{
				case ConnectionPointEvent.POST_ADD:
					refreshNavigatorViewAndSelect(manager.getConnectionPointCategory(type.getCategory().getId()),
							connection);
					break;
				case ConnectionPointEvent.POST_DELETE:
					refreshNavigatorView(manager.getConnectionPointCategory(type.getCategory().getId()));
					break;
				case ConnectionPointEvent.POST_CHANGE:
					refreshNavigatorView(connection);
			}
		}

	};

	private IPreferenceChangeListener themeChangeListener = new IPreferenceChangeListener()
	{
		public void preferenceChange(PreferenceChangeEvent event)
		{
			if (event.getKey().equals(IThemeManager.THEME_CHANGED))
			{
				ImageUtils.themeChanged();
			}
		}
	};

	private final IPartListener fPartListener = new IPartListener()
	{

		public void partActivated(IWorkbenchPart part)
		{
		}

		public void partBroughtToTop(IWorkbenchPart part)
		{
		}

		public void partClosed(IWorkbenchPart part)
		{
		}

		public void partDeactivated(IWorkbenchPart part)
		{
		}

		public void partOpened(IWorkbenchPart part)
		{
			if (part instanceof ProjectExplorer)
			{
				CommonViewer viewer = ((ProjectExplorer) part).getCommonViewer();
				viewer.setComparer(new FileSystemElementComparer());
				final Tree tree = viewer.getTree();
				tree.addMouseListener(new MouseAdapter()
				{

					@Override
					public void mouseDown(MouseEvent e)
					{
						if (tree.getItem(new Point(e.x, e.y)) == null)
						{
							tree.deselectAll();
							tree.notifyListeners(SWT.Selection, new Event());
						}
					}
				});
			}
		}
	};

	private final IWindowListener fWindowListener = new IWindowListener()
	{

		public void windowActivated(IWorkbenchWindow window)
		{
		}

		public void windowClosed(IWorkbenchWindow window)
		{
			IPartService partService = window.getPartService();
			if (partService != null)
			{
				partService.removePartListener(fPartListener);
			}
		}

		public void windowDeactivated(IWorkbenchWindow window)
		{
		}

		public void windowOpened(IWorkbenchWindow window)
		{
			IPartService partService = window.getPartService();
			if (partService != null)
			{
				partService.addPartListener(fPartListener);
			}
		}
	};

	/**
	 * The constructor
	 */
	public IOUIPlugin()
	{
	}

	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
		plugin = this;
		CoreIOPlugin.getConnectionPointManager().addConnectionPointListener(connectionListener);
		new InstanceScope().getNode(ThemePlugin.PLUGIN_ID).addPreferenceChangeListener(themeChangeListener);
		addPartListener();
	}

	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		CoreIOPlugin.getConnectionPointManager().removeConnectionPointListener(connectionListener);
		new InstanceScope().getNode(ThemePlugin.PLUGIN_ID).removePreferenceChangeListener(themeChangeListener);
		removePartListener();
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static IOUIPlugin getDefault()
	{
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in relative path.
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path)
	{
		return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	/**
	 * Returns an image for the image file at the given plug-in relative path.
	 * 
	 * @param path
	 *            the path
	 * @return the image object
	 */
	public static Image getImage(String path)
	{
		ImageRegistry registry = plugin.getImageRegistry();
		Image image = registry.get(path);
		if (image == null)
		{
			ImageDescriptor id = getImageDescriptor(path);
			if (id == null)
			{
				return null;
			}
			registry.put(path, id);
			image = registry.get(path);
		}
		return image;
	}

	public static void refreshNavigatorView(Object element)
	{
		refreshNavigatorViewAndSelect(element, null);
	}

	public static void refreshNavigatorViewAndSelect(final Object element, final Object selection)
	{
		UIUtils.getDisplay().asyncExec(new Runnable()
		{

			public void run()
			{
				try
				{
					IViewPart view = findView(IPageLayout.ID_PROJECT_EXPLORER);
					refreshNavigatorInternal(view, element, selection);

					view = findView(RemoteNavigatorView.ID);
					// if the content of the remote category changed, refresh the root of Remote view
					if (element instanceof IConnectionPointCategory && ((IConnectionPointCategory) element).isRemote())
					{
						Object input = ((CommonNavigator) view).getCommonViewer().getInput();
						refreshNavigatorInternal(view, input, selection);
					}
					else
					{
						refreshNavigatorInternal(view, element, selection);
					}
				}
				catch (PartInitException e)
				{
				}
			}
		});
	}

	private static void refreshNavigatorInternal(IViewPart viewPart, Object element, Object selection)
	{
		if (viewPart == null)
		{
			return;
		}
		if (viewPart instanceof CommonNavigator)
		{
			CommonViewer viewer = ((CommonNavigator) viewPart).getCommonViewer();
			if (element == null)
			{
				// full refresh
				System.err.println("FIXME: full refresh for "+viewer.getClass().getSimpleName());
				viewer.refresh();
			}
			else
			{
				Widget widget = viewer.testFindItem(element);
				if (widget != null)
				{
					Object data = widget.getData();
					if (data != null)
					{
						viewer.refresh(data);
					}
				}
			}
		}

		if (selection != null && viewPart instanceof CommonNavigator)
		{
			// ensures the category's new content are loaded
			CommonViewer viewer = ((CommonNavigator) viewPart).getCommonViewer();
			viewer.expandToLevel(element, 1);
			viewer.setSelection(new StructuredSelection(selection));
		}
	}

	public static void logError(String msg, Exception e)
	{
		log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK, msg, e));
	}

	public static void logError(Exception e)
	{
		log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK, e.getMessage(), e));
	}

	public static void logImportant(String msg, Exception e)
	{
		log(new Status(IStatus.WARNING, PLUGIN_ID, IStatus.OK, msg, e));
	}

	private static void log(IStatus status)
	{
		getDefault().getLog().log(status);
	}

	private static IViewPart findView(String viewID) throws PartInitException
	{
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null)
		{
			IWorkbenchPage page = window.getActivePage();
			if (page != null)
			{
				return page.findView(viewID);
			}
		}
		return null;
	}

	private void addPartListener()
	{
		IWorkbench workbench = null;
		try
		{
			workbench = PlatformUI.getWorkbench();
		}
		catch (Exception e)
		{
			// ignore, may be running headless, like in tests
		}
		if (workbench != null)
		{
			IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
			IPartService partService;
			for (IWorkbenchWindow window : windows)
			{
				partService = window.getPartService();
				if (partService != null)
				{
					partService.addPartListener(fPartListener);
				}
			}

			// Listen on any future windows
			PlatformUI.getWorkbench().addWindowListener(fWindowListener);
		}
	}

	private void removePartListener()
	{
		IWorkbench workbench = null;
		try
		{
			workbench = PlatformUI.getWorkbench();
		}
		catch (Exception e)
		{
			// ignore, may be running headless, like in tests
		}
		if (workbench != null)
		{
			IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
			IPartService partService;
			for (IWorkbenchWindow window : windows)
			{
				partService = window.getPartService();
				if (partService != null)
				{
					partService.removePartListener(fPartListener);
				}
			}
			PlatformUI.getWorkbench().removeWindowListener(fWindowListener);
		}
	}
}
