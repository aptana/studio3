package com.aptana.explorer;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.State;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class ExplorerPlugin extends AbstractUIPlugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.explorer"; //$NON-NLS-1$

	private static final String COMMAND_ID = "com.aptana.explorer.commands.toggleAppExplorer"; //$NON-NLS-1$
	private static final String COMMAND_STATE = "org.eclipse.ui.commands.toggleState"; //$NON-NLS-1$

	// The shared instance
	private static ExplorerPlugin plugin;

	private final IPerspectiveListener fPerspectiveListener = new IPerspectiveListener()
	{

		@Override
		public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective)
		{
			setCommandState(findView(page, IExplorerUIConstants.VIEW_ID) != null);
		}

		@Override
		public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, String changeId)
		{
			if (changeId.equals(IWorkbenchPage.CHANGE_VIEW_HIDE))
			{
				if (findView(page, IExplorerUIConstants.VIEW_ID) == null)
				{
					setCommandState(false);
				}
			}
			else if (changeId.equals(IWorkbenchPage.CHANGE_VIEW_SHOW))
			{
				if (findView(page, IExplorerUIConstants.VIEW_ID) != null)
				{
					setCommandState(true);
				}
			}
		}

		private void setCommandState(boolean state)
		{
			ICommandService service = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
			Command command = service.getCommand(COMMAND_ID);
			State commandState = command.getState(COMMAND_STATE);
			if (((Boolean) commandState.getValue()) != state)
			{
				commandState.setValue(state);
				service.refreshElements(COMMAND_ID, null);
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
			window.removePerspectiveListener(fPerspectiveListener);
		}

		public void windowDeactivated(IWorkbenchWindow window)
		{
		}

		public void windowOpened(IWorkbenchWindow window)
		{
			window.addPerspectiveListener(fPerspectiveListener);
		}
	};

	/**
	 * The constructor
	 */
	public ExplorerPlugin()
	{
	}

	protected IViewReference findView(IWorkbenchPage page, String viewId)
	{
		IViewReference refs[] = page.getViewReferences();
		for (int i = 0; i < refs.length; i++)
		{
			IViewReference ref = refs[i];
			if (viewId.equals(ref.getId()))
			{
				return ref;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
		plugin = this;
		addPartListener();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		removePartListener();
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static ExplorerPlugin getDefault()
	{
		return plugin;
	}

	public static Image getImage(String string)
	{
		if (getDefault().getImageRegistry().get(string) == null)
		{
			ImageDescriptor id = imageDescriptorFromPlugin(PLUGIN_ID, string);
			if (id != null)
				getDefault().getImageRegistry().put(string, id);
		}
		return getDefault().getImageRegistry().get(string);
	}

	public static void logError(CoreException e)
	{
		getDefault().getLog().log(e.getStatus());
	}

	public static void logError(String msg, Throwable e)
	{
		getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, msg, e));
	}

	private void addPartListener()
	{
		IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
		for (IWorkbenchWindow window : windows)
		{
			window.addPerspectiveListener(fPerspectiveListener);
		}
		// Listen on any future windows
		PlatformUI.getWorkbench().addWindowListener(fWindowListener);
	}

	private void removePartListener()
	{
		IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
		for (IWorkbenchWindow window : windows)
		{
			window.removePerspectiveListener(fPerspectiveListener);
		}
		PlatformUI.getWorkbench().removeWindowListener(fWindowListener);
	}
}
