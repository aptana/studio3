/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.ui.internal;

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

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.StringUtil;
import com.aptana.js.debug.core.ILaunchConfigurationConstants;
import com.aptana.js.debug.core.JSLaunchConfigurationHelper;
import com.aptana.js.debug.ui.JSDebugUIPlugin;

/**
 * @author Max Stepanov
 */
public class JSLaunchShortcut implements ILaunchShortcut {
	/**
	 * @see org.eclipse.debug.ui.ILaunchShortcut#launch(org.eclipse.jface.viewers.ISelection, java.lang.String)
	 */
	public void launch(ISelection selection, String mode) {
		if (selection instanceof IStructuredSelection) {
			Object object = ((IStructuredSelection) selection).getFirstElement();
			if (object instanceof IResource) {
				launch(((IResource) object).getFullPath(), false, mode);
			}
		}
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchShortcut#launch(org.eclipse.ui.IEditorPart, java.lang.String)
	 */
	public void launch(IEditorPart editor, String mode) {
		IEditorInput input = editor.getEditorInput();
		if (input instanceof IFileEditorInput) {
			IPath path = ((IFileEditorInput) input).getFile().getFullPath();
			launch(path, true, mode);
		} else {
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
	private void launch(IPath path, boolean current, String mode) {
		String ext = path.getFileExtension();
		// TODO: use content types
		if ("htm".equals(ext) || "html".equals(ext) || "xhtml".equals(ext)) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			ILaunchConfiguration config = findLaunchConfiguration(path, current, mode);
			if (config != null) {
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
	private ILaunchConfiguration findLaunchConfiguration(IPath path, boolean current, String mode) {
		ILaunchConfigurationType configType = getLaunchConfigType();
		try {
			ILaunchConfiguration[] configs = DebugPlugin.getDefault().getLaunchManager()
					.getLaunchConfigurations(configType);
			for (ILaunchConfiguration config : configs) {
				if (config.getAttribute(ILaunchConfigurationConstants.CONFIGURATION_SERVER_TYPE, -1) == ILaunchConfigurationConstants.SERVER_INTERNAL) {
					if (path != null
							&& config.getAttribute(ILaunchConfigurationConstants.CONFIGURATION_START_ACTION_TYPE, -1) == ILaunchConfigurationConstants.START_ACTION_SPECIFIC_PAGE) {
						if (path.toPortableString().equals(
								config.getAttribute(ILaunchConfigurationConstants.CONFIGURATION_START_PAGE_PATH,
										StringUtil.EMPTY))) {
							return config;
						}
					}
					if (current
							&& config.getAttribute(ILaunchConfigurationConstants.CONFIGURATION_START_ACTION_TYPE, -1) == ILaunchConfigurationConstants.START_ACTION_CURRENT_PAGE) {
						return config;
					}
				}
			}
		} catch (CoreException e) {
			IdeLog.logError(JSDebugUIPlugin.getDefault(), e);
		}
		return createConfiguration(current ? null : path);
	}

	/**
	 * createConfiguration
	 * 
	 * @param path
	 * @return ILaunchConfiguration
	 */
	private ILaunchConfiguration createConfiguration(IPath path) {
		ILaunchConfiguration config = null;
		ILaunchConfigurationType configType = getLaunchConfigType();
		try {
			ILaunchConfigurationWorkingCopy wc = configType.newInstance(
					null,
					DebugPlugin.getDefault().getLaunchManager()
							.generateLaunchConfigurationName((path != null) ? path.lastSegment() : "Default")); //$NON-NLS-1$
			JSLaunchConfigurationHelper.setDefaults(wc, null);
			if (path != null) {
				wc.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_START_ACTION_TYPE,
						ILaunchConfigurationConstants.START_ACTION_SPECIFIC_PAGE);
				wc.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_START_PAGE_PATH, path.toPortableString());
			}
			config = wc.doSave();
		} catch (CoreException e) {
			IdeLog.logError(JSDebugUIPlugin.getDefault(), e);
		}
		return config;
	}

	/**
	 * getLaunchConfigType
	 * 
	 * @return ILaunchConfigurationType
	 */
	private ILaunchConfigurationType getLaunchConfigType() {
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		return manager.getLaunchConfigurationType(ILaunchConfigurationConstants.ID_JS_APPLICATION);
	}
}
