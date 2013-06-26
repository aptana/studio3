/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.test.internal.performance.results.ui;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 *  Defines the 'Performances' perspective.
 */
public class PerformanceResultsPerspective implements IPerspectiveFactory {

	private IPageLayout factory;

/*
 * (non-Javadoc)
 * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
 */
public void createInitialLayout(IPageLayout layout) {
	this.factory = layout;
	addViews();
}

/*
 * Add views to the perspective
 */
private void addViews() {

	// Component results view put on bottom
	IFolderLayout bottom =
		this.factory.createFolder(
			"bottomRight", //NON-NLS-1
			IPageLayout.BOTTOM,
			0.5f,
			this.factory.getEditorArea());
	bottom.addView("org.eclipse.test.internal.performance.results.ui.ComponentsResultsView");
	bottom.addView("org.eclipse.test.internal.performance.results.ui.BuildsComparisonView");

	// Components and Builds view put on perspective top left
	IFolderLayout topLeft =
		this.factory.createFolder(
			"topLeft", //NON-NLS-1
			IPageLayout.LEFT,
			0.5f,
			this.factory.getEditorArea());
	topLeft.addView("org.eclipse.test.internal.performance.results.ui.ComponentsView"); //NON-NLS-1
	topLeft.addView("org.eclipse.test.internal.performance.results.ui.BuildsView"); //NON-NLS-1
	topLeft.addView(IPageLayout.ID_PROJECT_EXPLORER); //NON-NLS-1

	// Properties view put on perspective top right
	IFolderLayout topRight =
		this.factory.createFolder(
			"topRight", //NON-NLS-1
			IPageLayout.RIGHT,
			0.5f,
			this.factory.getEditorArea());
	topRight.addView(IPageLayout.ID_PROP_SHEET); //NON-NLS-1

	this.factory.setEditorAreaVisible(false);
}

}
