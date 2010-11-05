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

package com.aptana.ide.ui.io;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.aptana.ide.core.io.ConnectionPointType;
import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.core.io.IConnectionPointManager;
import com.aptana.ide.core.io.events.ConnectionPointEvent;
import com.aptana.ide.core.io.events.IConnectionPointListener;
import com.aptana.ide.ui.io.navigator.IRefreshableNavigator;
import com.aptana.ide.ui.io.navigator.RemoteNavigatorView;
import com.aptana.theme.IThemeManager;
import com.aptana.theme.ThemePlugin;
import com.aptana.ui.UIUtils;

/**
 * The activator class controls the plug-in life cycle
 */
public class IOUIPlugin extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "com.aptana.ui.io"; //$NON-NLS-1$

    // The shared instance
    private static IOUIPlugin plugin;

    private IConnectionPointListener connectionListener = new IConnectionPointListener() {

        public void connectionPointChanged(ConnectionPointEvent event) {
			IConnectionPoint connection = event.getConnectionPoint();
			IConnectionPointManager manager = CoreIOPlugin.getConnectionPointManager();
			ConnectionPointType type = manager.getType(connection);
			if (type == null) {
			    return;
			}
			
			switch (event.getKind()) {
			case ConnectionPointEvent.POST_ADD:
			    refreshNavigatorViewAndSelect(
			    		manager.getConnectionPointCategory(type.getCategory().getId()), connection);
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

    /**
     * The constructor
     */
    public IOUIPlugin() {
    }

    /**
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        CoreIOPlugin.getConnectionPointManager().addConnectionPointListener(connectionListener);
		new InstanceScope().getNode(ThemePlugin.PLUGIN_ID).addPreferenceChangeListener(themeChangeListener);
    }

    /**
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        CoreIOPlugin.getConnectionPointManager().removeConnectionPointListener(connectionListener);
        new InstanceScope().getNode(ThemePlugin.PLUGIN_ID).removePreferenceChangeListener(themeChangeListener);
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static IOUIPlugin getDefault() {
        return plugin;
    }

    /**
     * Returns an image descriptor for the image file at the given plug-in
     * relative path.
     * 
     * @param path
     *            the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path) {
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

    /**
     * Returns the active workbench window
     * 
     * @return the active workbench window
     */
    public static IWorkbenchWindow getActiveWorkbenchWindow() {
        return getDefault().getWorkbench().getActiveWorkbenchWindow();
    }

    /**
     * Returns the active workbench shell or <code>null</code> if none
     * 
     * @return the active workbench shell or <code>null</code> if none
     */
    public static Shell getActiveWorkbenchShell() {
        IWorkbenchWindow window = getActiveWorkbenchWindow();
        if (window != null) {
            return window.getShell();
        }
        return null;
    }

    /**
     * getActivePage
     * 
     * @return IWorkbenchPage
     */
    public static IWorkbenchPage getActivePage() {
        IWorkbenchWindow w = getActiveWorkbenchWindow();
        if (w != null) {
            return w.getActivePage();
        }
        return null;
    }

    public static void refreshNavigatorView(Object element) {
        refreshNavigatorViewAndSelect(element, null);
    }

    public static void refreshNavigatorViewAndSelect(final Object element, final Object selection) {
        UIUtils.getDisplay().asyncExec(new Runnable() {

            public void run() {
                try {
                    IViewPart view = findView(IPageLayout.ID_PROJECT_EXPLORER);
                    refreshNavigatorInternal(view, element, selection);

                    view = findView(RemoteNavigatorView.ID);
                    refreshNavigatorInternal(view, element, selection);
                } catch (PartInitException e) {
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
		if (viewPart instanceof IRefreshableNavigator)
		{
			((IRefreshableNavigator) viewPart).refresh(element);
		}
		else if (viewPart instanceof CommonNavigator)
		{
			CommonViewer viewer = ((CommonNavigator) viewPart).getCommonViewer();
			if (element == null)
			{
				// full refresh
				viewer.refresh();
			}
			else
			{
				viewer.refresh(element);
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

    public static void logError(String msg, Exception e) {
        log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK, msg, e));
    }

    public static void logImportant(String msg, Exception e) {
        log(new Status(IStatus.WARNING, PLUGIN_ID, IStatus.OK, msg, e));
    }

    private static void log(IStatus status) {
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
}
