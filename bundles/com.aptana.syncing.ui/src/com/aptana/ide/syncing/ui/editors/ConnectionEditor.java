/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.ui.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import com.aptana.ide.syncing.core.ISiteConnection;
import com.aptana.ide.syncing.ui.views.FTPManagerComposite;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class ConnectionEditor extends EditorPart implements FTPManagerComposite.Listener
{

	/**
	 * ID of the editor
	 */
	public static final String ID = "com.aptana.ide.syncing.ui.editors.ConnectionEditor"; //$NON-NLS-1$

	private ConnectionEditorInput fInput;
	private FTPManagerComposite fConnectionComposite;

	public ConnectionEditor()
	{
	}

	public void setSelectedSite(ISiteConnection site)
	{
		fConnectionComposite.setSelectedSite(site);
	}

	@Override
	public void dispose()
	{
		fConnectionComposite.dispose();
		super.dispose();
	}

	@Override
	public void doSave(IProgressMonitor monitor)
	{
	}

	@Override
	public void doSaveAs()
	{
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException
	{
		setSite(site);
		if (input instanceof ConnectionEditorInput)
		{
			setInput(input);
			fInput = (ConnectionEditorInput) input;
			setPartName(fInput.getName());
		}
		else
		{
			throw new PartInitException("Incorrect editor input for ConnectionEditor"); //$NON-NLS-1$
		}
	}

	@Override
	public boolean isDirty()
	{
		return false;
	}

	@Override
	public boolean isSaveAsAllowed()
	{
		return false;
	}

	@Override
	public void createPartControl(Composite parent)
	{
		fConnectionComposite = new FTPManagerComposite(parent);
		fConnectionComposite.setSelectedSite(fInput.getConnection());
		fConnectionComposite.addListener(this);
	}

	@Override
	public void setFocus()
	{
		fConnectionComposite.getControl().setFocus();
	}

	public void siteConnectionChanged(ISiteConnection site)
	{
		fInput.setConnection(site);
		setPartName(fInput.getName());
		setTitleToolTip(fInput.getToolTipText());
	}
}
