/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.findbar.api;

import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.widgets.Composite;

/**
 * This is the interface for Find Bar decorator.
 * 
 * @see FindBarDecoratorFactory
 * @author schitale
 * @author Fabio Zadrozny
 */
public interface IFindBarDecorator
{

	/**
	 * This should be called from <code>createPartControl(Composite parent)<code>.
	 * 
	 * <pre>
	 * public void createPartControl(Composite parent) {
	 *     Composite findBarComposite = getfindBarDecorator().createFindBarComposite(parent, getStatusLineManager());
	 *     super.createPartControl(findBarComposite);
	 *     ...
	 * }
	 * </pre>
	 * 
	 * @param parent
	 *            The parent composite
	 * @return The composite configured for parenting Find Bar
	 */
	Composite createFindBarComposite(Composite parent);

	/**
	 * This creates the Find Bar.
	 * 
	 * @param sourceViewer
	 */
	void createFindBar(ISourceViewer sourceViewer);

	/**
	 * Delegates the context activation to the decorator
	 * 
	 * @param contextIds
	 */
	void activateContexts(String[] contextIds);

	/**
	 * Install actions.
	 */
	void installActions();

	/**
	 * Return the visibility state of Find Bar.
	 * 
	 * @return true if Find Bar is visible else false
	 */
	boolean isVisible();

	/**
	 * Shows/Hide Find Bar.
	 */
	void setVisible(boolean visible);

	/**
	 * Disposes of the find bar.
	 */
	void dispose();

}
