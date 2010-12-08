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

import java.util.Iterator;
import java.util.Stack;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.ISourceLocator;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.launchConfigurations.LaunchConfigurationManager;
import org.eclipse.debug.internal.ui.launchConfigurations.LaunchHistory;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;

import com.aptana.core.CoreStrings;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.debug.core.ILaunchConfigurationConstants;
import com.aptana.debug.core.JSLaunchConfigurationHelper;
import com.aptana.debug.core.util.FirebugUtils;
import com.aptana.debug.ui.DebugUiPlugin;
import com.aptana.ui.PopupSchedulingRule;
import com.aptana.ui.util.WorkbenchBrowserUtil;

/**
 * @author Max Stepanov
 *
 */
@SuppressWarnings("restriction")
public class Startup implements IStartup {
	
	/**
	 * @see org.eclipse.ui.IStartup#earlyStartup()
	 */
	public void earlyStartup() {
		registerAsFirebugEditor();
        	UIJob job = new UIJob("Checking default launch configuration") { //$NON-NLS-1$
			
            		@Override
           		public IStatus runInUIThread(IProgressMonitor monitor) {
                		checkDefaultLaunchConfiguration();
                		WorkbenchCloseListener.init();
                		return Status.OK_STATUS;
            		}
            	
        	};
        	job.setRule(PopupSchedulingRule.INSTANCE);
        	job.setSystem(true);
        	job.schedule();
	}
	
	private void registerAsFirebugEditor() {
		IPath launcher = EclipseUtil.getApplicationLauncher();
		if ( launcher != null ) {
			FirebugUtils.registerEditor("Aptana", "Aptana Studio", launcher, StringUtil.EMPTY); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	private void checkDefaultLaunchConfiguration() {
		Stack<ILaunchConfiguration> defaultConfigurations = new Stack<ILaunchConfiguration>();
		ILaunchConfiguration configuration;
		LaunchConfigurationManager manager = DebugUIPlugin.getDefault().getLaunchConfigurationManager();
		ILaunchConfiguration[] history = manager.getLaunchHistory(IDebugUIConstants.ID_DEBUG_LAUNCH_GROUP).getHistory();
		
		/* Firefox */
		configuration = getOrCreateDefaultLaunchConfiguration(JSLaunchConfigurationHelper.FIREFOX);
		if ( configuration != null ) {
			defaultConfigurations.push(configuration);
		}
		
		/* IE */
		if ( Platform.OS_WIN32.equals(Platform.getOS()) )
		{
			configuration = getOrCreateDefaultLaunchConfiguration(JSLaunchConfigurationHelper.INTERNET_EXPLORER);
			if ( configuration != null ) {
				defaultConfigurations.push(configuration);
			}
		}
	
		for(int i = 0; i < history.length; ++i) {
			for(Iterator<ILaunchConfiguration> j = defaultConfigurations.iterator(); j.hasNext(); ) {
				if( history[i].equals(j.next()) ) {
					j.remove();
					break;
				}
			}
		}
		while ( !defaultConfigurations.empty() ) {
			configuration = (ILaunchConfiguration) defaultConfigurations.pop();
			setRecentLaunchHistory(IDebugUIConstants.ID_RUN_LAUNCH_GROUP, configuration);
			setRecentLaunchHistory(IDebugUIConstants.ID_DEBUG_LAUNCH_GROUP, configuration);
		}
	}
	
	private void setRecentLaunchHistory( String groupId, final ILaunchConfiguration configuration ) {
		LaunchConfigurationManager manager = DebugUIPlugin.getDefault().getLaunchConfigurationManager();
		LaunchHistory history = manager.getLaunchHistory(groupId);
		
		/* Launch history hack */
		history.launchAdded( new ILaunch() {

			public ILaunchConfiguration getLaunchConfiguration() {
				return configuration;
			}
			
			/* All other methods are stubs */
			public Object[] getChildren() {
				return null;
			}
			
			public IDebugTarget getDebugTarget() {
				return null;
			}
			
			public IProcess[] getProcesses() {
				return null;
			}
			
			public IDebugTarget[] getDebugTargets() {
				return null;
			}
			
			public void addDebugTarget(IDebugTarget target) {
			}
			
			public void removeDebugTarget(IDebugTarget target) {
			}
			
			public void addProcess(IProcess process) {
			}
			
			public void removeProcess(IProcess process) {
			}
			
			public ISourceLocator getSourceLocator() {
				return null;
			}
			
			public void setSourceLocator(ISourceLocator sourceLocator) {
			}
			
			public String getLaunchMode() {
				return null;
			}
			
			public void setAttribute(String key, String value) {
			}

			public String getAttribute(String key) {
				return null;
			}

			public boolean hasChildren() {
				return false;
			}

			public boolean canTerminate() {
				return false;
			}

			public boolean isTerminated() {
				return false;
			}

			public void terminate() throws DebugException {
			}

			@SuppressWarnings("rawtypes")
			public Object getAdapter(Class adapter) {
				return null;
			}	
		});
		
	}

	private ILaunchConfiguration getOrCreateDefaultLaunchConfiguration(String nature) {
		ILaunchConfigurationType configType = getLaunchConfigType();
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		try {
			ILaunchConfiguration[] configs = manager.getLaunchConfigurations(configType);
			for( int i = 0; i < configs.length; ++i ) {
				ILaunchConfiguration config = configs[i];
				if ( nature.equals(config.getAttribute(ILaunchConfigurationConstants.CONFIGURATION_BROWSER_NATURE, StringUtil.EMPTY)) ) {
					return config;
				}
			}
			ILaunchConfigurationWorkingCopy wc = configType.newInstance(
					null,
					DebugPlugin.getDefault().getLaunchManager()
						.generateUniqueLaunchConfigurationNameFrom(nature+" - Internal Server")); //$NON-NLS-1$
			JSLaunchConfigurationHelper.setDefaults(wc, nature);

			boolean showDownload = true;
			while( !JSLaunchConfigurationHelper.isBrowserDebugCompatible(
					wc.getAttribute(ILaunchConfigurationConstants.CONFIGURATION_BROWSER_EXECUTABLE,StringUtil.EMPTY)) ) {
				String browserPath = null;
				if (nature.equals(JSLaunchConfigurationHelper.FIREFOX)) {
					IPreferenceStore store = DebugUiPlugin.getDefault().getPreferenceStore();
					String pref = store.getString(com.aptana.debug.internal.ui.IDebugUIConstants.PREF_SKIP_FIREFOX_CHECK);
					if (pref == null || !pref.equals(MessageDialogWithToggle.ALWAYS))
					{
						browserPath = showBrowserNotFoundDialog(showDownload);
					}
				} 

				if ( browserPath == null ) {
					wc.delete();
					return null;
				}
				if ( browserPath.length() == 0 ) {
					JSLaunchConfigurationHelper.setBrowserDefaults(wc, nature);
				} else {
					wc.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_BROWSER_EXECUTABLE,browserPath);					
				}
				showDownload = false;
			}
			return wc.doSave();
		} catch (CoreException e) {
			DebugUiPlugin.log(e);
		}
		return null;
	}

	private ILaunchConfigurationType getLaunchConfigType() {
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		return manager.getLaunchConfigurationType(ILaunchConfigurationConstants.ID_JS_APPLICATION);
	}
		
	private String showBrowserNotFoundDialog(final boolean download) {
		final String[] path = new String[] { null };
		PlatformUI.getWorkbench().getDisplay().syncExec( new Runnable() {
			public void run() {
				
				if ( Display.getCurrent().getActiveShell() == null ) { // another message box is shown
					return;
				}
				MessageDialogWithToggle md = new MessageDialogWithToggle(
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
						Messages.Startup_Notification,
						null,
						Messages.Startup_AptanaRequiresFirefox,
						MessageDialog.INFORMATION,
						new String[] { IDialogConstants.PROCEED_LABEL, StringUtil.ellipsify(CoreStrings.BROWSE), download ? Messages.Startup_Download : Messages.Startup_CheckAgain },
						0,
						Messages.Startup_DontAskAgain,
						false
					);
				md.setPrefKey(com.aptana.debug.internal.ui.IDebugUIConstants.PREF_SKIP_FIREFOX_CHECK);
				md.setPrefStore(DebugUiPlugin.getDefault().getPreferenceStore());
				
				int returnCode = md.open();
				switch(returnCode) {
				case IDialogConstants.INTERNAL_ID:
					FileDialog fileDialog = new FileDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),SWT.OPEN);
					if ( Platform.OS_WIN32.equals(Platform.getOS()) ) {
						fileDialog.setFilterExtensions(new String[]{ "*.exe" }); //$NON-NLS-1$
						fileDialog.setFilterNames(new String[]{ Messages.Startup_ExecutableFiles });
					}
					path[0] = fileDialog.open();
					break;
				case IDialogConstants.INTERNAL_ID+1:
					if ( download ) {
						WorkbenchBrowserUtil.launchExternalBrowser("http://www.getfirefox.com"); //$NON-NLS-1$
					}
					path[0] = StringUtil.EMPTY;
					break;
				default:
				}
			}
		});
		return path[0];
	}

}
