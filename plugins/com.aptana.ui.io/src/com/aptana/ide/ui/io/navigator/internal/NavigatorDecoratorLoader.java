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
