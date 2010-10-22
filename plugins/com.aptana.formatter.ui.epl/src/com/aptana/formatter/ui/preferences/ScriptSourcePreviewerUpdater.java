/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     		IBM Corporation - initial API and implementation
 * 			Alex Panchenko <alex@xored.com>
 *******************************************************************************/

package com.aptana.formatter.ui.preferences;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Font;

/**
 * Handles dltk editor font changes for script source preview viewers.
 */
public class ScriptSourcePreviewerUpdater
{

	/**
	 * Creates a script source preview updater for the given viewer, configuration and preference store.
	 * 
	 * @param viewer
	 *            the viewer
	 * @param configuration
	 *            the configuration
	 * @param preferenceStore
	 *            the preference store
	 */
	public ScriptSourcePreviewerUpdater(final SourceViewer viewer, final SourceViewerConfiguration configuration,
			final IPreferenceStore preferenceStore)
	{
		Assert.isNotNull(viewer);
		Assert.isNotNull(configuration);
		Assert.isNotNull(preferenceStore);
		final IPropertyChangeListener fontChangeListener = new IPropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent event)
			{
				final String fontKey = JFaceResources.TEXT_FONT;
				if (fontKey.equals(event.getProperty()))
				{
					final Font font = JFaceResources.getFont(fontKey);
					viewer.getTextWidget().setFont(font);
				}
			}
		};
		final IPropertyChangeListener propertyChangeListener = new IPropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent event)
			{
				// if (configuration.affectsTextPresentation(event)) {
				// configuration.handlePropertyChangeEvent(event);
				viewer.invalidateTextPresentation();
				// }
			}
		};
		viewer.getTextWidget().addDisposeListener(new DisposeListener()
		{
			public void widgetDisposed(DisposeEvent e)
			{
				preferenceStore.removePropertyChangeListener(propertyChangeListener);
				JFaceResources.getFontRegistry().removeListener(fontChangeListener);
			}
		});

		JFaceResources.getFontRegistry().addListener(fontChangeListener);
		preferenceStore.addPropertyChangeListener(propertyChangeListener);
	}
}
