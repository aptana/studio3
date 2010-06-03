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

public class SynchronizationPropertyPage extends PreferencePage implements IWorkbenchPropertyPage {

    private IContainer fResource;

    private Combo fSitesCombo;
    private Button fUseAsDefaultButton;

    public SynchronizationPropertyPage() {
    }

    @Override
    protected Control createContents(Composite parent) {
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
        for (ISiteConnection site : sites) {
            fSitesCombo.add(site.getDestination().getName());
        }
        fSitesCombo.select(0);

        String connection = ResourceSynchronizationUtils.getLastSyncConnection(fResource);
        if (connection != null && !connection.equals("")) { //$NON-NLS-1$
            fSitesCombo.setText(connection);
        }

        fUseAsDefaultButton = new Button(main, SWT.CHECK);
        fUseAsDefaultButton.setText(Messages.SynchronizationPropertyPage_useConnectionsAsDefault);
        fUseAsDefaultButton
                .setSelection(ResourceSynchronizationUtils.isRememberDecision(fResource));
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
        gridData.horizontalSpan = 2;
        fUseAsDefaultButton.setLayoutData(gridData);

        return main;
    }

    @Override
    protected void performDefaults() {
        fUseAsDefaultButton.setSelection(false);
        fSitesCombo.select(0);

        super.performDefaults();
    }

    @Override
    public boolean performOk() {
        ResourceSynchronizationUtils.setRememberDecision(fResource, fUseAsDefaultButton
                .getSelection());
        ResourceSynchronizationUtils.setLastSyncConnection(fResource, fSitesCombo.getText());

        return super.performOk();
    }

    public IAdaptable getElement() {
        return fResource;
    }

    public void setElement(IAdaptable element) {
        fResource = (IContainer) element.getAdapter(IContainer.class);
        if (fResource == null) {
            IResource resource = (IResource) element.getAdapter(IResource.class);
            if (resource != null) {
                fResource = resource.getProject();
            }
        }
    }
}
