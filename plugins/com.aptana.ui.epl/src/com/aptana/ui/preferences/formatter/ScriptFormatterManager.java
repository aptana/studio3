/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package com.aptana.ui.preferences.formatter;

import org.eclipse.core.resources.IProject;

public class ScriptFormatterManager extends DLTKContributionExtensionManager {

	private static ScriptFormatterManager instance = null;

	public static synchronized ScriptFormatterManager getInstance() {
		if (instance == null) {
			instance = new ScriptFormatterManager();
		}
		return instance;
	}

	private static final String EXTPOINT = DLTKUIPlugin.PLUGIN_ID
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

	public static boolean hasFormatterFor(final String natureId) {
		return getInstance().hasContributions(natureId);
	}

	public static IScriptFormatterFactory getSelected(IScriptProject project) {
		final IDLTKLanguageToolkit toolkit = DLTKLanguageManager
				.getLanguageToolkit(project);
		return (IScriptFormatterFactory) getInstance().getSelectedContribution(
				project.getProject(), toolkit.getNatureId());
	}

	public static IScriptFormatterFactory getSelected(String natureId,
			IProject project) {
		return (IScriptFormatterFactory) getInstance().getSelectedContribution(
				project, natureId);
	}

}
