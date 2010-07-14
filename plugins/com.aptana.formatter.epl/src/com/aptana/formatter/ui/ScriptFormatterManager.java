/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package com.aptana.formatter.ui;

import org.eclipse.core.resources.IProject;

import com.aptana.formatter.epl.FormatterPlugin;
import com.aptana.ui.ContributionExtensionManager;

public class ScriptFormatterManager extends ContributionExtensionManager {

	private static ScriptFormatterManager instance = null;

	public static synchronized ScriptFormatterManager getInstance() {
		if (instance == null) {
			instance = new ScriptFormatterManager();
		}
		return instance;
	}

	private static final String EXTPOINT = FormatterPlugin.PLUGIN_ID
			+ ".formatterFactory"; //$NON-NLS-1$

	protected String getContributionElementName() {
		return "formatterFactory"; //$NON-NLS-1$
	}

	protected String getExtensionPoint() {
		return EXTPOINT;
	}

	protected boolean isValidContribution(Object object) {
		return object instanceof IScriptFormatterFactory
				&& ((IScriptFormatterFactory) object).isValid();
	}

	public static boolean hasFormatterFor(final String contentType) {
		return getInstance().hasContributions(contentType);
	}

	public static IScriptFormatterFactory getSelected(IProject project) {
		final IDLTKLanguageToolkit toolkit = DLTKLanguageManager
				.getLanguageToolkit(project);
		return (IScriptFormatterFactory) getInstance().getSelectedContribution(
				project.getProject(), toolkit.getContentType());
	}

	public static IScriptFormatterFactory getSelected(String contentType,
			IProject project) {
		return (IScriptFormatterFactory) getInstance().getSelectedContribution(
				project, contentType);
	}

}
