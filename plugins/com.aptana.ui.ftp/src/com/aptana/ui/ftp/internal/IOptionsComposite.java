/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable declareAsInterface

package com.aptana.ui.ftp.internal;

import com.aptana.ide.core.io.ConnectionContext;

/**
 * @author Max Stepanov
 *
 */
public interface IOptionsComposite {

	public static interface IListener {

		public boolean isValid();
		public void validate();
		public boolean testConnection(ConnectionContext context, IConnectionRunnable connectRunnable);
	}

	public void loadPropertiesFrom(Object element);
	
	/**
	 * 
	 * @param element
	 * @return true if element properties has been changed
	 */
	public boolean savePropertiesTo(Object element);

	public String isValid();
	public void setValid(boolean valid);
	public void lockUI(boolean lock);
}
