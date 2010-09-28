package com.aptana.ide.ui.io.navigator.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.progress.UIJob;

import com.aptana.ide.ui.io.IOUIPlugin;
import com.aptana.ide.ui.io.navigator.INavigatorDecorator;

public class NavigatorDecoratorLoader
{

	private static final String NAVIGATOR_ID = IPageLayout.ID_PROJECT_EXPLORER;

	private static final String EXTENSION_NAME = "decorator"; //$NON-NLS-1$
	private static final String EXTENSION_POINT = IOUIPlugin.PLUGIN_ID + "." + EXTENSION_NAME; //$NON-NLS-1$
	private static final String CLASS_ATTRIBUTE = "class"; //$NON-NLS-1$

	private static final IPartListener partListener = new IPartListener()
	{

		public void partOpened(IWorkbenchPart part)
		{
			if (part instanceof IViewPart)
			{
				IViewPart viewPart = (IViewPart) part;
				if (NAVIGATOR_ID.equals(viewPart.getSite().getId()))
				{
					IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(
							EXTENSION_POINT);
					for (IConfigurationElement element : elements)
					{
						if (!EXTENSION_NAME.equals(element.getName()))
						{
							continue;
						}

						String className = element.getAttribute(CLASS_ATTRIBUTE);
						if (className != null)
						{
							try
							{
								Object client = element.createExecutableExtension(CLASS_ATTRIBUTE);
								if (client instanceof INavigatorDecorator)
								{
									Tree tree = ((CommonNavigator) viewPart).getCommonViewer().getTree();
									((INavigatorDecorator) client).addDecorator(tree);
								}
							}
							catch (CoreException e)
							{
								// ignores the exception
							}
						}
					}
				}
			}
		}

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
	};

	public static void init()
	{
		Job job = new UIJob(PlatformUI.getWorkbench().getDisplay(), "Decorator Init") //$NON-NLS-1$
		{
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().addPartListener(partListener);
				return Status.OK_STATUS;
			}
		};
		job.setPriority(Job.INTERACTIVE);
		job.setSystem(true);
		job.schedule();
	}

	private NavigatorDecoratorLoader()
	{
	}
}
