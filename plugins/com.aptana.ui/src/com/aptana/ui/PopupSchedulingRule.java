/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ui;

import org.eclipse.core.runtime.jobs.ISchedulingRule;

/**
 * @author Max Stepanov
 *
 */
public final class PopupSchedulingRule implements ISchedulingRule {

	public static final PopupSchedulingRule INSTANCE = new PopupSchedulingRule();
	
	/**
	 * 
	 */
	private PopupSchedulingRule() {
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.ISchedulingRule#contains(org.eclipse.core.runtime.jobs.ISchedulingRule)
	 */
	public boolean contains(ISchedulingRule rule) {
		return rule == this;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.ISchedulingRule#isConflicting(org.eclipse.core.runtime.jobs.ISchedulingRule)
	 */
	public boolean isConflicting(ISchedulingRule rule) {
		return rule == this;
	}

}
