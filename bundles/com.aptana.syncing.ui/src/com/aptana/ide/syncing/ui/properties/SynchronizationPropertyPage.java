/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.ui.properties;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPropertyPage;

import com.aptana.ide.syncing.core.ISiteConnection;
import com.aptana.ide.syncing.core.ResourceSynchronizationUtils;
import com.aptana.ide.syncing.core.SiteConnectionUtils;

public class SynchronizationPropertyPage extends PreferencePage implements IWorkbenchPropertyPage
{

	private IContainer fResource;

	private Combo fSitesCombo;
	private Button fUseAsDefaultButton;

	public SynchronizationPropertyPage()
	{
	}

	@Override
	protected Control createContents(Composite parent)
	{
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		main.setLayout(layout);

		Label label = new Label(main, SWT.LEFT);
		label.setText(Messages.SynchronizationPropertyPage_lastSyncConnection);
		fSitesCombo = new Combo(main, SWT.DROP_DOWN | SWT.READ_ONLY);
		// adds the sites that have the selected resource as the source
		ISiteConnection[] sites = SiteConnectionUtils.findSitesForSource(fResource);
		for (ISiteConnection site : sites)
		{
			fSitesCombo.add(site.getDestination().getName());
		}
		fSitesCombo.select(0);

		String connection = ResourceSynchronizationUtils.getLastSyncConnection(fResource);
		if (connection != null && !connection.equals("")) { //$NON-NLS-1$
			fSitesCombo.setText(connection);
		}

		fUseAsDefaultButton = new Button(main, SWT.CHECK);
		fUseAsDefaultButton.setText(Messages.SynchronizationPropertyPage_useConnectionsAsDefault);
		fUseAsDefaultButton.setSelection(ResourceSynchronizationUtils.isRememberDecision(fResource));
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		gridData.horizontalSpan = 2;
		fUseAsDefaultButton.setLayoutData(gridData);

		return main;
	}

	@Override
	protected void performDefaults()
	{
		fUseAsDefaultButton.setSelection(false);
		fSitesCombo.select(0);

		super.performDefaults();
	}

	@Override
	public boolean performOk()
	{
		ResourceSynchronizationUtils.setRememberDecision(fResource, fUseAsDefaultButton.getSelection());
		ResourceSynchronizationUtils.setLastSyncConnection(fResource, fSitesCombo.getText());

		return super.performOk();
	}

	public IAdaptable getElement()
	{
		return fResource;
	}

	public void setElement(IAdaptable element)
	{
		fResource = (IContainer) element.getAdapter(IContainer.class);
		if (fResource == null)
		{
			IResource resource = (IResource) element.getAdapter(IResource.class);
			if (resource != null)
			{
				fResource = resource.getProject();
			}
		}
	}
}
