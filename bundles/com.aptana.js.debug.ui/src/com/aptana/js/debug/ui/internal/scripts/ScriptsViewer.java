/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.ui.internal.scripts;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Max Stepanov
 */
public class ScriptsViewer extends TreeViewer {

	/**
	 * @param parent
	 */
	public ScriptsViewer(Composite parent) {
		super(parent);
	}

	/**
	 * @param parent
	 * @param style
	 */
	public ScriptsViewer(Composite parent, int style) {
		super(parent, style);
	}

}
