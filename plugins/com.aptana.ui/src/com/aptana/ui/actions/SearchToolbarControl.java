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
