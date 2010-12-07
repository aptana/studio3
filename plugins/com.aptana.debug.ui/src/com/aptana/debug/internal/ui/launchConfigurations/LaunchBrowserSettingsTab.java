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
package com.aptana.debug.internal.ui.launchConfigurations;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceComparator;

import com.aptana.core.CoreStrings;
import com.aptana.core.util.StringUtil;
import com.aptana.debug.core.ILaunchConfigurationConstants;
import com.aptana.debug.core.JSDebugPlugin;
import com.aptana.debug.core.JSLaunchConfigurationHelper;
import com.aptana.debug.internal.ui.ActiveResourcePathGetterAdapter;
import com.aptana.debug.ui.DebugUiPlugin;

/**
 * Launch settings tab
 */
public class LaunchBrowserSettingsTab extends AbstractLaunchConfigurationTab
{
	private Listener dirtyListener;
	private Image image;

	private Text fBrowserExeText;
	private Text fCommandArgsText;
	private Button rbCurrentPage;
	private Button rbSpecificPage;
	private Button bSpecificPageBrowse;
	private Text fSpecificPageText;
	private Button rbStartUrl;
	private Text fStartUrlText;
	private Button rbInternalServer;
	private Button rbCustomServer;
	private Text fbaseUrlText;
	private Button fAddProjectName;

	private Button useServer;
	private Composite serverImage;
	private Label serverText;
	private ToolBar serversBar;
	private ToolItem selectedServer;
	private Menu serverMenu;
	private Image serverImg;

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		setControl(composite);

		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		composite.setFont(parent.getFont());

		dirtyListener = new Listener()
		{
			public void handleEvent(Event event)
			{
				updateEnablement();
				setDirty(true);
				updateLaunchConfigurationDialog();
			}
		};

		createBrowserSection(composite);
		createStartActionSection(composite);
		createServerSection(composite);

		// hook up event handlers to update the configuration dialog when settings change
		hookListeners(true);
	}

	private void createBrowserSection(Composite parent)
	{
		Group group = new Group(parent, SWT.FLAT | SWT.SHADOW_IN);
		group.setText(Messages.LaunchBrowserSettingsTab_WebBrowser);
		group.setFont(parent.getFont());

		GridData gData = new GridData(SWT.FILL, SWT.FILL, true, false);
		gData.widthHint = 500;
		group.setLayoutData(gData);
		GridLayout layout = new GridLayout(3, false);
		layout.marginHeight = 3;
		layout.marginWidth *= 2;
		group.setLayout(layout);

		GridData data;

		Label label = new Label(group, SWT.NONE);
		label.setText(Messages.LaunchBrowserSettingsTab_BrowserExecutable);
		data = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		label.setLayoutData(data);

		fBrowserExeText = new Text(group, SWT.SINGLE | SWT.BORDER);
		data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		fBrowserExeText.setLayoutData(data);

		Button bBrowserExeBrowse = new Button(group, SWT.PUSH);
		bBrowserExeBrowse.setText(StringUtil.ellipsify(CoreStrings.BROWSE));
		data = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		bBrowserExeBrowse.setLayoutData(data);

		label = new Label(group, SWT.NONE);
		label.setText(Messages.LaunchBrowserSettingsTab_Arguments);
		data = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		label.setLayoutData(data);

		fCommandArgsText = new Text(group, SWT.SINGLE | SWT.BORDER);
		data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		fCommandArgsText.setLayoutData(data);

		bBrowserExeBrowse.addSelectionListener(new SelectionAdapter()
		{
			/**
			 * Prompts the user to choose a location from the filesystem and sets the location as the full path of the
			 * selected file.
			 */
			public void widgetSelected(SelectionEvent e)
			{
				FileDialog fileDialog = new FileDialog(getShell(), SWT.OPEN);
				fileDialog.setFileName(fBrowserExeText.getText());
				if (Platform.OS_WIN32.equals(Platform.getOS()))
				{
					fileDialog.setFilterExtensions(new String[] { "*.exe" }); //$NON-NLS-1$
					fileDialog.setFilterNames(new String[] { Messages.LaunchBrowserSettingsTab_ExecutableFiles });
				}
				String text = fileDialog.open();
				if (text != null)
				{
					fBrowserExeText.setText(text);
				}
			}
		});
	}

	private void createStartActionSection(Composite parent)
	{
		Group group = new Group(parent, SWT.FLAT | SWT.SHADOW_IN);
		group.setText(Messages.LaunchBrowserSettingsTab_StartAction);
		group.setFont(parent.getFont());

		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		GridLayout layout = new GridLayout(3, false);
		layout.marginHeight = 3;
		layout.marginWidth *= 2;
		group.setLayout(layout);

		GridData data;

		/* row 1 */
		rbCurrentPage = new Button(group, SWT.RADIO);
		rbCurrentPage.setText(Messages.LaunchBrowserSettingsTab_UseCurrentPage);
		data = new GridData(SWT.LEFT, SWT.CENTER, true, false);
		data.horizontalSpan = 3;
		rbCurrentPage.setLayoutData(data);

		/* row 2 */
		rbSpecificPage = new Button(group, SWT.RADIO);
		rbSpecificPage.setText(Messages.LaunchBrowserSettingsTab_SpecificPage);
		data = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		rbSpecificPage.setLayoutData(data);

		fSpecificPageText = new Text(group, SWT.SINGLE | SWT.BORDER);
		data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		fSpecificPageText.setLayoutData(data);

		bSpecificPageBrowse = new Button(group, SWT.PUSH);
		bSpecificPageBrowse.setText(StringUtil.ellipsify(CoreStrings.BROWSE));
		data = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		bSpecificPageBrowse.setLayoutData(data);

		/* row 3 */
		rbStartUrl = new Button(group, SWT.RADIO);
		rbStartUrl.setText(Messages.LaunchBrowserSettingsTab_StartURL);
		data = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		rbStartUrl.setLayoutData(data);

		fStartUrlText = new Text(group, SWT.SINGLE | SWT.BORDER);
		data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		data.horizontalSpan = 2;
		fStartUrlText.setLayoutData(data);

		bSpecificPageBrowse.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				IResource resource = chooseWorkspaceLocation();
				if (resource != null)
				{
					fSpecificPageText.setText(resource.getFullPath().toPortableString());
				}
			}
		});
	}

	private void createServerSection(Composite parent)
	{
		Group group = new Group(parent, SWT.FLAT);
		group.setText(Messages.LaunchBrowserSettingsTab_Server);
		group.setFont(parent.getFont());

		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 3;
		layout.marginWidth *= 2;
		group.setLayout(layout);

		GridData data;

		rbInternalServer = new Button(group, SWT.RADIO);
		rbInternalServer.setText(Messages.LaunchBrowserSettingsTab_UseBuiltInWebServer);
		data = new GridData(SWT.FILL, SWT.FILL, false, false);
		data.horizontalSpan = 2;
		rbInternalServer.setLayoutData(data);

		rbCustomServer = new Button(group, SWT.RADIO);
		rbCustomServer.setText(Messages.LaunchBrowserSettingsTab_UseExternalWebServer);
		data = new GridData(SWT.FILL, SWT.FILL, false, false);
		rbCustomServer.setLayoutData(data);

		fbaseUrlText = new Text(group, SWT.SINGLE | SWT.BORDER);
		data = new GridData(SWT.FILL, SWT.FILL, true, false);
		fbaseUrlText.setLayoutData(data);

		useServer = new Button(group, SWT.RADIO);
		useServer.setText(Messages.LaunchBrowserSettingsTab_Use_Selected_Server);
		GridData usData = new GridData(SWT.FILL, SWT.FILL, false, false);
		useServer.setLayoutData(usData);

		createServersViewSection(group);

		fAddProjectName = new Button(group, SWT.CHECK);
		fAddProjectName.setText(Messages.LaunchBrowserSettingsTab_AppendProjectName);
		data = new GridData(SWT.FILL, SWT.FILL, true, false);
		data.horizontalSpan = 2;
		fAddProjectName.setLayoutData(data);
	}

	private void createServersViewSection(Composite parent)
	{
		final Composite serverComposite = new Composite(parent, SWT.NONE);
		GridLayout scLayout = new GridLayout(2, false);
		scLayout.marginHeight = 0;
		scLayout.marginWidth = 0;
		scLayout.horizontalSpacing = 0;
		scLayout.verticalSpacing = 0;
		serverComposite.setLayout(scLayout);
		GridData scData = new GridData(SWT.FILL, SWT.FILL, true, false);
		serverComposite.setLayoutData(scData);

		final Composite inner = new Composite(serverComposite, SWT.BORDER);
		inner.setBackground(inner.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		GridLayout iLayout = new GridLayout(3, false);
		iLayout.marginHeight = 0;
		iLayout.marginWidth = 0;
		GridData iData = new GridData(SWT.FILL, SWT.FILL, false, false);
		iData.widthHint = 200;
		inner.setLayout(iLayout);
		inner.setLayoutData(iData);

		serverImage = new Composite(inner, SWT.NONE);
		serverImage.setBackground(inner.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		serverImage.addPaintListener(new PaintListener()
		{

			public void paintControl(PaintEvent e)
			{
				if (serverImg != null)
				{
					e.gc.drawImage(serverImg, 2, 2);
				}

			}

		});
		GridData siData = new GridData(SWT.FILL, SWT.FILL, false, false);
		siData.heightHint = 16;
		siData.widthHint = 20;
		serverImage.setLayoutData(siData);
		serverText = new Label(inner, SWT.LEFT);
		serverText.setBackground(inner.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		GridData stData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		serverText.setLayoutData(stData);
		serversBar = new ToolBar(inner, SWT.FLAT);
		serversBar.setBackground(inner.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		GridLayout sbLayout = new GridLayout(1, false);
		sbLayout.marginHeight = 0;
		sbLayout.marginWidth = 0;
		sbLayout.horizontalSpacing = 0;
		scLayout.verticalSpacing = 0;
		serversBar.setLayout(sbLayout);
		serversBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		selectedServer = new ToolItem(serversBar, SWT.PUSH);
		Image arrow = EclipseUtils.getArrowImage();
		selectedServer.setImage(arrow);
		serverMenu = new Menu(serverComposite);
		selectedServer.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				Rectangle rect = inner.getBounds();
				Point pt = new Point(rect.x, rect.y + rect.height);
				pt = serverComposite.toDisplay(pt);
				serverMenu.setLocation(pt.x, pt.y);
				serverMenu.setVisible(true);
			}

		});
		IServer[] servers = ServerCore.getServerManager().getServers();
		for (int i = 0; i < servers.length; i++)
		{
			final IServer curr = servers[i];
			if (curr.isWebServer())
			{
				final MenuItem server = new MenuItem(serverMenu, SWT.PUSH);
				server.setText(curr.getName());
				final Image img = ServerImagesRegistry.getInstance().getImage(curr);
				if (img != null)
				{
					server.setImage(img);
				}
				server.addSelectionListener(new SelectionAdapter()
				{

					public void widgetSelected(SelectionEvent e)
					{
						serverText.setText(server.getText());
						serverText.setData(curr);
						serverImg = server.getImage();
						serverImage.redraw();
						serverImage.update();
						updateEnablement();
						setDirty(true);
						updateLaunchConfigurationDialog();
					}

				});
			}
		}
	}

	private void hookListeners(boolean hook)
	{
		if (hook)
		{
			fBrowserExeText.addListener(SWT.Modify, dirtyListener);
			fCommandArgsText.addListener(SWT.Modify, dirtyListener);

			fSpecificPageText.addListener(SWT.Modify, dirtyListener);
			fStartUrlText.addListener(SWT.Modify, dirtyListener);
			rbCurrentPage.addListener(SWT.Selection, dirtyListener);
			rbSpecificPage.addListener(SWT.Selection, dirtyListener);
			rbStartUrl.addListener(SWT.Selection, dirtyListener);

			fbaseUrlText.addListener(SWT.Modify, dirtyListener);
			rbInternalServer.addListener(SWT.Selection, dirtyListener);
			rbCustomServer.addListener(SWT.Selection, dirtyListener);
			fAddProjectName.addListener(SWT.Selection, dirtyListener);
			useServer.addListener(SWT.Selection, dirtyListener);
		}
		else
		{
			fBrowserExeText.removeListener(SWT.Modify, dirtyListener);
			fCommandArgsText.removeListener(SWT.Modify, dirtyListener);

			fSpecificPageText.removeListener(SWT.Modify, dirtyListener);
			fStartUrlText.removeListener(SWT.Modify, dirtyListener);
			rbCurrentPage.removeListener(SWT.Selection, dirtyListener);
			rbSpecificPage.removeListener(SWT.Selection, dirtyListener);
			rbStartUrl.removeListener(SWT.Selection, dirtyListener);

			fbaseUrlText.removeListener(SWT.Modify, dirtyListener);
			rbInternalServer.removeListener(SWT.Selection, dirtyListener);
			rbCustomServer.removeListener(SWT.Selection, dirtyListener);
			fAddProjectName.removeListener(SWT.Selection, dirtyListener);
			useServer.removeListener(SWT.Selection, dirtyListener);
		}
	}

	private void updateEnablement()
	{
		fSpecificPageText.setEnabled(rbSpecificPage.getSelection());
		bSpecificPageBrowse.setEnabled(rbSpecificPage.getSelection());
		fStartUrlText.setEnabled(rbStartUrl.getSelection());
		fbaseUrlText.setEnabled(rbCustomServer.getSelection());
		fAddProjectName.setEnabled((rbCustomServer.getSelection() || useServer.getSelection())
				&& (rbCurrentPage.getSelection() || rbSpecificPage.getSelection()));
		serversBar.setEnabled(useServer.getSelection());
		serverText.setEnabled(useServer.getSelection());
	}

	private IResource chooseWorkspaceLocation()
	{
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getShell(), new WorkbenchLabelProvider(),
				new WorkbenchContentProvider());
		dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
		dialog.setComparator(new ResourceComparator(ResourceComparator.NAME));
		dialog.setMessage(Messages.LaunchBrowserSettingsTab_ChooseFile);
		if (dialog.open() == Window.OK)
		{
			return (IResource) dialog.getFirstResult();
		}

		return null;
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#setDefaults(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration)
	{
		JSLaunchConfigurationHelper.setBrowserDefaults(configuration, null);
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#initializeFrom(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	public void initializeFrom(ILaunchConfiguration configuration)
	{
		hookListeners(false);
		try
		{
			fBrowserExeText.setText(configuration.getAttribute(
					ILaunchConfigurationConstants.CONFIGURATION_BROWSER_EXECUTABLE, StringUtil.EMPTY));
			fCommandArgsText.setText(configuration.getAttribute(
					ILaunchConfigurationConstants.CONFIGURATION_BROWSER_COMMAND_LINE, StringUtil.EMPTY));

			int startActionType = configuration.getAttribute(
					ILaunchConfigurationConstants.CONFIGURATION_START_ACTION_TYPE,
					ILaunchConfigurationConstants.DEFAULT_START_ACTION_TYPE);
			rbCurrentPage.setSelection(startActionType == ILaunchConfigurationConstants.START_ACTION_CURRENT_PAGE);
			rbSpecificPage.setSelection(startActionType == ILaunchConfigurationConstants.START_ACTION_SPECIFIC_PAGE);
			fSpecificPageText.setText(configuration.getAttribute(
					ILaunchConfigurationConstants.CONFIGURATION_START_PAGE_PATH, StringUtil.EMPTY));
			rbStartUrl.setSelection(startActionType == ILaunchConfigurationConstants.START_ACTION_START_URL);
			fStartUrlText.setText(configuration.getAttribute(
					ILaunchConfigurationConstants.CONFIGURATION_START_PAGE_URL, StringUtil.EMPTY));

			int serverType = configuration.getAttribute(ILaunchConfigurationConstants.CONFIGURATION_SERVER_TYPE,
					ILaunchConfigurationConstants.DEFAULT_SERVER_TYPE);
			rbInternalServer.setSelection(serverType == ILaunchConfigurationConstants.SERVER_INTERNAL);
			rbCustomServer.setSelection(serverType == ILaunchConfigurationConstants.SERVER_EXTERNAL);
			fbaseUrlText.setText(configuration.getAttribute(
					ILaunchConfigurationConstants.CONFIGURATION_EXTERNAL_BASE_URL, StringUtil.EMPTY));
			fAddProjectName.setSelection(configuration.getAttribute(
					ILaunchConfigurationConstants.CONFIGURATION_APPEND_PROJECT_NAME, false));
			useServer.setSelection(serverType == ILaunchConfigurationConstants.SERVER_MANAGED);
			if (serverType == ILaunchConfigurationConstants.SERVER_MANAGED)
			{
				String serverID = configuration.getAttribute(ILaunchConfigurationConstants.CONFIGURATION_SERVER_ID,
						StringUtil.EMPTY);
				if (serverID.length() > 0)
				{
					IServer[] servers = ServerCore.getServerManager().getServers();
					for (int i = 0; i < servers.length; i++)
					{
						final IServer curr = servers[i];
						if (curr.isWebServer())
						{
							if (curr.getId().equals(serverID))
							{
								serverText.setText(curr.getName());
								serverText.setData(curr);
								Image img = ServerImagesRegistry.getInstance().getImage(curr);
								if (img != null)
								{
									serverImg = img;
									serverImage.redraw();
									serverImage.update();
								}
								break;
							}
						}
					}
				}
			}
		}
		catch (CoreException e)
		{
			DebugUiPlugin.log("Reading launch configuration fails", e); //$NON-NLS-1$
		}
		finally
		{
			hookListeners(true);
			updateEnablement();
		}
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#performApply(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	public void performApply(ILaunchConfigurationWorkingCopy configuration)
	{
		String value;
		value = fBrowserExeText.getText();
		configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_BROWSER_EXECUTABLE, value);

		value = fCommandArgsText.getText();
		configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_BROWSER_COMMAND_LINE, value);

		int startActionType = 0;
		if (rbCurrentPage.getSelection())
		{
			startActionType = ILaunchConfigurationConstants.START_ACTION_CURRENT_PAGE;
		}
		else if (rbSpecificPage.getSelection())
		{
			startActionType = ILaunchConfigurationConstants.START_ACTION_SPECIFIC_PAGE;
		}
		else if (rbStartUrl.getSelection())
		{
			startActionType = ILaunchConfigurationConstants.START_ACTION_START_URL;
		}
		configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_START_ACTION_TYPE, startActionType);

		value = fSpecificPageText.getText();
		configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_START_PAGE_PATH, value);
		value = fStartUrlText.getText();
		configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_START_PAGE_URL, value);

		int serverType = 0;
		if (rbInternalServer.getSelection())
		{
			serverType = ILaunchConfigurationConstants.SERVER_INTERNAL;
		}
		else if (rbCustomServer.getSelection())
		{
			serverType = ILaunchConfigurationConstants.SERVER_EXTERNAL;
		}
		else if (useServer.getSelection())
		{
			serverType = ILaunchConfigurationConstants.SERVER_MANAGED;
		}
		configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_SERVER_TYPE, serverType);

		value = fbaseUrlText.getText();
		configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_EXTERNAL_BASE_URL, value);

		Object data = serverText.getData();
		if (data instanceof IServer)
		{
			configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_SERVER_ID, ((IServer) data).getId());
		}

		configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_APPEND_PROJECT_NAME, fAddProjectName
				.getSelection());

	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#isValid(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	public boolean isValid(ILaunchConfiguration launchConfig)
	{
		setErrorMessage(null);
		setMessage(null);

		String value = fBrowserExeText.getText();
		if (!new File(value).exists())
		{
			setErrorMessage(Messages.LaunchBrowserSettingsTab_BrowserExecutableShouldBeSpecified);
			return false;
		}
		if (rbSpecificPage.getSelection())
		{
			value = fSpecificPageText.getText();
			if (value.length() == 0 || ResourcesPlugin.getWorkspace().getRoot().findMember(value) == null)
			{
				setErrorMessage(Messages.LaunchBrowserSettingsTab_StartPageShouldBeSpecified);
				return false;
			}
		}
		else if (rbStartUrl.getSelection())
		{
			value = fStartUrlText.getText();
			try
			{
				new URL(value);
			}
			catch (MalformedURLException e)
			{
				setErrorMessage(Messages.LaunchBrowserSettingsTab_ValidStartPageURLShouldBeSpecified);
				return false;
			}
		}

		if (rbCustomServer.getSelection())
		{
			value = fbaseUrlText.getText();
			try
			{
				new URL(value);
			}
			catch (MalformedURLException e)
			{
				setErrorMessage(Messages.LaunchBrowserSettingsTab_ValidBaseURLShouldBeSpecified);
				return false;
			}
		}
		if (rbCurrentPage.getSelection())
		{
			Object activeResource = new ActiveResourcePathGetterAdapter().getActiveResourcePath();
			if (activeResource == null)
			{
				setMessage(Messages.LaunchBrowserSettingsTab_NoFilesOpenedInEditor);
				return false;
			}
		}

		if (useServer.getSelection())
		{
			Object data = serverText.getData();
			if (data == null || !(data instanceof IServer))
			{
				setErrorMessage(Messages.LaunchBrowserSettingsTab_Server_Must_Be_Selected);
				return false;
			}
		}

		return true;
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getName()
	 */
	public String getName()
	{
		return Messages.LaunchBrowserSettingsTab_Main;
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getImage()
	 */
	public Image getImage()
	{
		if (image == null)
		{
			image = DebugUiPlugin.getImageDescriptor("icons/full/obj16/launch-main.gif").createImage(); //$NON-NLS-1$
		}
		return image;
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#dispose()
	 */
	public void dispose()
	{
		if (image != null)
		{
			image.dispose();
		}
		super.dispose();
	}
}
