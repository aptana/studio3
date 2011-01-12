/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
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
package com.aptana.debug.internal.ui;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

import com.aptana.core.util.StringUtil;
import com.aptana.debug.ui.DebugUiPlugin;
import com.aptana.js.debug.core.ILaunchConfigurationConstants;
import com.aptana.js.debug.core.JSLaunchConfigurationHelper;

/**
 * @author Max Stepanov
 */
public class JSLaunchShortcut implements ILaunchShortcut
{
	/**
	 * @see org.eclipse.debug.ui.ILaunchShortcut#launch(org.eclipse.jface.viewers.ISelection, java.lang.String)
	 */
	public void launch(ISelection selection, String mode)
	{
		if (selection instanceof IStructuredSelection)
		{
			Object object = ((IStructuredSelection) selection).getFirstElement();
			if (object instanceof IResource)
			{
				launch(((IResource) object).getFullPath(), false, mode);
			}
		}
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchShortcut#launch(org.eclipse.ui.IEditorPart, java.lang.String)
	 */
	public void launch(IEditorPart editor, String mode)
	{
		IEditorInput input = editor.getEditorInput();
		if (input instanceof IFileEditorInput)
		{
			IPath path = ((IFileEditorInput) input).getFile().getFullPath();
			launch(path, true, mode);
		}
		else
		{
			launch(new Path(input.getName()), true, mode);
		}
	}

	/**
	 * launch
	 * 
	 * @param path
	 * @param current
	 * @param mode
	 */
	private void launch(IPath path, boolean current, String mode)
	{
		String ext = path.getFileExtension();
		// TODO: use content types
		if ("htm".equals(ext) || "html".equals(ext) || "xhtml".equals(ext)) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			ILaunchConfiguration config = findLaunchConfiguration(path, current, mode);
			if (config != null)
			{
				DebugUITools.launch(config, mode);
			}
		}
	}

	/**
	 * findLaunchConfiguration
	 * 
	 * @param path
	 * @param current
	 * @param mode
	 * @return ILaunchConfiguration
	 */
	private ILaunchConfiguration findLaunchConfiguration(IPath path, boolean current, String mode)
	{
		ILaunchConfigurationType configType = getLaunchConfigType();
		try
		{
			ILaunchConfiguration[] configs = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurations(
					configType);
			for (int i = 0; i < configs.length; i++)
			{
				ILaunchConfiguration config = configs[i];
				if (config.getAttribute(ILaunchConfigurationConstants.CONFIGURATION_SERVER_TYPE, -1) == ILaunchConfigurationConstants.SERVER_INTERNAL)
				{
					if (path != null
							&& config.getAttribute(ILaunchConfigurationConstants.CONFIGURATION_START_ACTION_TYPE, -1) == ILaunchConfigurationConstants.START_ACTION_SPECIFIC_PAGE)
					{
						if (path.toPortableString().equals(
								config.getAttribute(ILaunchConfigurationConstants.CONFIGURATION_START_PAGE_PATH,
										StringUtil.EMPTY)))
						{
							return config;
						}
					}
					if (current
							&& config.getAttribute(ILaunchConfigurationConstants.CONFIGURATION_START_ACTION_TYPE, -1) == ILaunchConfigurationConstants.START_ACTION_CURRENT_PAGE)
					{
						return config;
					}
				}
			}
		}
		catch (CoreException e)
		{
			DebugUiPlugin.log(e);
		}
		return createConfiguration(current ? null : path);
	}

	/**
	 * createConfiguration
	 * 
	 * @param path
	 * @return ILaunchConfiguration
	 */
	private ILaunchConfiguration createConfiguration(IPath path)
	{
		ILaunchConfiguration config = null;
		ILaunchConfigurationType configType = getLaunchConfigType();
		try
		{
			ILaunchConfigurationWorkingCopy wc = configType.newInstance(null, DebugPlugin.getDefault()
					.getLaunchManager().generateUniqueLaunchConfigurationNameFrom(
							path != null ? path.lastSegment() : "Default")); //$NON-NLS-1$
			JSLaunchConfigurationHelper.setDefaults(wc, null);
			if (path != null)
			{
				wc.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_START_ACTION_TYPE,
						ILaunchConfigurationConstants.START_ACTION_SPECIFIC_PAGE);
				wc.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_START_PAGE_PATH, path.toPortableString());
			}
			config = wc.doSave();
		}
		catch (CoreException e)
		{
			DebugUiPlugin.log(e);
		}
		return config;
	}

	/**
	 * getLaunchConfigType
	 * 
	 * @return ILaunchConfigurationType
	 */
	private ILaunchConfigurationType getLaunchConfigType()
	{
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		return manager.getLaunchConfigurationType(ILaunchConfigurationConstants.ID_JS_APPLICATION);
	}
}
