/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.ui.dialogs;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.aptana.core.util.FileUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.syncing.core.ISiteConnection;
import com.aptana.ui.UIPlugin;
import com.aptana.ui.util.SWTUtils;

/**
 * @author Ingo Muschenetz
 * @author Michael Xia (modified to extend from JFace Dialog)
 */
public class ChooseSiteConnectionDialog extends TrayDialog implements SelectionListener
{

	private Combo fSiteCombo;
	private Label fSiteDescriptionLabel;
	private Button fRememberMyDecisionButton;

	private ISiteConnection[] fSites;
	private ISiteConnection fSelectedSite;

	// Show the remember my decision check box
	private boolean fShowRememberMyDecision;
	private boolean fRememberMyDecision;

	/**
	 * @param parent
	 *            the parent shell
	 * @param sites
	 *            the array of available sites
	 */
	public ChooseSiteConnectionDialog(Shell parent, ISiteConnection[] sites)
	{
		this(parent, sites, false);
	}

	/**
	 * @param parent
	 *            the parent shell
	 * @param sites
	 *            the array of available sites
	 * @param showRememberMyDecision
	 *            true if to display the "remember my decision" checkbox, false otherwise
	 */
	public ChooseSiteConnectionDialog(Shell parent, ISiteConnection[] sites, boolean showRememberMyDecision)
	{
		super(parent);
		fSites = sites;
		fShowRememberMyDecision = showRememberMyDecision;

		setShellStyle(getShellStyle() | SWT.RESIZE);
		setHelpAvailable(false);
	}

	/**
	 * @return the selected site
	 */
	public ISiteConnection getSelectedSite()
	{
		return fSelectedSite;
	}

	/**
	 * @return true if the decision should be remembered, false otherwise
	 */
	public boolean isRememberMyDecision()
	{
		if (fShowRememberMyDecision)
		{
			return fRememberMyDecision;
		}
		return false;
	}

	/**
	 * Sets the selected site.
	 * 
	 * @param site
	 *            the site connection to be selected
	 */
	public void setSelectedSite(ISiteConnection site)
	{
		fSelectedSite = site;
	}

	public void setShowRememberMyDecision(boolean show)
	{
		fShowRememberMyDecision = show;
	}

	/**
	 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetDefaultSelected(SelectionEvent e)
	{
	}

	/**
	 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetSelected(SelectionEvent e)
	{
		Object source = e.getSource();

		if (source == fSiteCombo)
		{
			updateDescriptiveText();
		}
		else if (source == fRememberMyDecisionButton)
		{
			fRememberMyDecision = fRememberMyDecisionButton.getSelection();
		}
	}

	/**
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	protected void configureShell(Shell newShell)
	{
		super.configureShell(newShell);
		newShell.setText(Messages.ChooseSiteConnectionDialog_Title);
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent)
	{
		Control control = super.createContents(parent);

		Shell shell = getShell();
		Shell parentShell = getShell().getParent().getShell();
		SWTUtils.centerAndPack(shell, parentShell);

		return control;
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent)
	{
		Composite main = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		main.setLayout(layout);
		main.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Composite comp = new Composite(main, SWT.NONE);
		comp.setLayout(new GridLayout(2, false));
		comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		Label label = new Label(comp, SWT.RIGHT);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		label.setAlignment(SWT.RIGHT);
		label.setImage(SWTUtils.getImage(UIPlugin.getDefault(), "icons/aptana_dialog_tag.png")); //$NON-NLS-1$

		label = new Label(comp, SWT.WRAP);
		label.setFont(SWTUtils.getDefaultSmallFont());
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.heightHint = 50;
		gridData.widthHint = 400;
		gridData.horizontalIndent = 5;
		gridData.verticalIndent = 3;
		label.setLayoutData(gridData);
		label.setText(Messages.ChooseSiteConnectionDialog_LBL_Message);

		comp = new Composite(main, SWT.NONE);
		comp.setLayout(new GridLayout(2, false));
		comp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		label = new Label(comp, SWT.NONE);
		label.setText(StringUtil.makeFormLabel(Messages.ChooseSiteConnectionDialog_LBL_Connection));
		fSiteCombo = new Combo(comp, SWT.DROP_DOWN | SWT.READ_ONLY);
		fSiteCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		fSiteCombo.addSelectionListener(this);
		fSiteCombo.addModifyListener(new ModifyListener()
		{

			public void modifyText(ModifyEvent e)
			{
				validate();
			}
		});

		// left padding
		new Label(comp, SWT.NONE);

		fSiteDescriptionLabel = new Label(comp, SWT.NONE);
		gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData.widthHint = 450;
		fSiteDescriptionLabel.setLayoutData(gridData);

		if (fShowRememberMyDecision)
		{
			fRememberMyDecisionButton = new Button(comp, SWT.CHECK);
			fRememberMyDecisionButton.setText(Messages.ChooseSiteConnectionDialog_LBL_RememberMyDecision);
			gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
			gridData.horizontalSpan = 2;
			fRememberMyDecisionButton.setLayoutData(gridData);
			fRememberMyDecisionButton.addSelectionListener(this);

			label = new Label(comp, SWT.WRAP);
			label.setText(Messages.ChooseSiteConnectionDialog_LBL_PropertyPage);
			gridData = new GridData(SWT.BEGINNING, SWT.CENTER, true, false);
			gridData.horizontalSpan = 2;
			label.setLayoutData(gridData);
		}

		initializeDefaultValues();

		return main;
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	protected void okPressed()
	{
		if (!validate())
		{
			return;
		}

		// records the selected site
		int index = fSiteCombo.getSelectionIndex();
		if (index > -1)
		{
			fSelectedSite = fSites[index];
		}
		super.okPressed();
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#cancelPressed()
	 */
	protected void cancelPressed()
	{
		fSelectedSite = null;
		super.cancelPressed();
	}

	/**
	 * Validates the fields to see if they are complete.
	 * 
	 * @return boolean true if the validation passed, false otherwise
	 */
	private boolean validate()
	{
		boolean success = true;
		if (!SWTUtils.validateCombo(fSiteCombo, 0))
		{
			success = false;
		}

		return success;
	}

	private void initializeDefaultValues()
	{
		int currentIndex = 0;
		int selectIndex = 0;
		IConnectionPoint destination;
		for (ISiteConnection site : fSites)
		{
			destination = site.getDestination();
			if (destination == null)
			{
				continue;
			}
			fSiteCombo.add(site.getName() + ": " + destination.getName()); //$NON-NLS-1$
			if (site == fSelectedSite)
			{
				selectIndex = currentIndex;
			}
			currentIndex++;
		}

		fSiteCombo.select(selectIndex);
		updateDescriptiveText();
	}

	private void updateDescriptiveText()
	{
		int index = fSiteCombo.getSelectionIndex();
		if (index == -1)
		{
			return;
		}
		fSelectedSite = fSites[index];

		IConnectionPoint source = fSelectedSite.getSource();
		IConnectionPoint target = fSelectedSite.getDestination();

		try
		{
			fSiteDescriptionLabel.setText(FileUtil.compressPath(source.getRoot().toString(), 25)
					+ " <-> " + FileUtil.compressPath(target.getRoot().toString(), 25)); //$NON-NLS-1$
		}
		catch (CoreException e)
		{
			fSiteDescriptionLabel.setText(FileUtil.compressPath(source.getName(), 25) + " <-> " //$NON-NLS-1$
					+ FileUtil.compressPath(target.getName(), 25));
		}
	}
}
