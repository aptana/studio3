/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.actions;

import org.eclipse.jface.action.ControlContribution;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.aptana.ui.widgets.SearchComposite;
import com.aptana.ui.widgets.SearchComposite.Client;

/**
 * @author Max Stepanov
 *
 */
public abstract class SearchToolbarControl extends ControlContribution implements Client {

	private static final String ID = "com.aptana.ui.toolbar.search"; //$NON-NLS-1$
	
	/**
	 * @param id
	 */
	public SearchToolbarControl() {
		super(ID);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.ControlContribution#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createControl(Composite parent) {
		SearchComposite searchComposite = new SearchComposite(parent, this);
		searchComposite.setSearchOnEnter(false);
		searchComposite.setInitialText(Messages.SearchToolbarControl_InitialText);
		return searchComposite;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.ControlContribution#computeWidth(org.eclipse.swt.widgets.Control)
	 */
	@Override
	protected int computeWidth(Control control) {
		return control.computeSize(200, SWT.DEFAULT, true).x;
	}

	/* (non-Javadoc)
	 * @see com.aptana.ui.widgets.SearchComposite.Client#search(java.lang.String, boolean, boolean)
	 */
	public abstract void search(String text, boolean isCaseSensitive, boolean isRegularExpression);

}
