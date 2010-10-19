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
package com.aptana.deploy.dialogs;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.aptana.deploy.internal.wizard.FTPDeployComposite;
import com.aptana.ide.core.io.IBaseRemoteConnectionPoint;
import com.aptana.ide.syncing.ui.preferences.SyncPreferenceUtil;
import com.aptana.ide.ui.ftp.internal.FTPConnectionPropertyComposite;
import com.aptana.ide.ui.secureftp.dialogs.CommonFTPConnectionPointPropertyDialog;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
@SuppressWarnings("restriction")
public class FTPDeployPropertyDialog extends CommonFTPConnectionPointPropertyDialog
{

	private IProject fProject;

	public FTPDeployPropertyDialog(Shell parentShell)
	{
		super(parentShell);
	}

	public IProject getProject()
	{
		return fProject;
	}

	public void setProject(IProject project)
	{
		fProject = project;
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		Control control = super.createDialogArea(parent);
		FTPDeployComposite deployComposite = (FTPDeployComposite) getConnectionComposite();
		boolean autoSync = SyncPreferenceUtil.isAutoSync(fProject);
		deployComposite.setAutoSyncSelected(autoSync);
		if (autoSync)
		{
			deployComposite.setSyncDirection(SyncPreferenceUtil.getAutoSyncDirection(fProject));
		}
		return control;
	}

	@Override
	protected void okPressed()
	{
		// persists the auto-sync setting
		FTPDeployComposite deployComposite = (FTPDeployComposite) getConnectionComposite();
		boolean autoSync = deployComposite.isAutoSyncSelected();
		SyncPreferenceUtil.setAutoSync(fProject, autoSync);
		if (autoSync)
		{
			SyncPreferenceUtil.setAutoSyncDirection(fProject, deployComposite.getSyncDirection());
		}
		super.okPressed();
	}

	@Override
	protected FTPConnectionPropertyComposite createConnectionComposite(Composite parent,
			IBaseRemoteConnectionPoint connectionPoint)
	{
		return new FTPDeployComposite(parent, SWT.NONE, connectionPoint, this);
	}
}
