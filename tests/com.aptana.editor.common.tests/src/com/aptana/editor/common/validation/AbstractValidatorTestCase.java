/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import com.aptana.core.build.AbstractBuildParticipant;
import com.aptana.core.build.IProblem;
import com.aptana.core.util.EclipseUtil;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.preferences.IPreferenceConstants;
import com.aptana.editor.common.tests.util.TestProject;
import com.aptana.index.core.build.BuildContext;
import com.aptana.parsing.IParseState;
import com.aptana.parsing.ParseState;

public abstract class AbstractValidatorTestCase extends TestCase
{

	protected abstract AbstractBuildParticipant createValidator();

	protected void setEnableParseError(boolean enabled, String language)
	{
		// Set enable parse errors preference
		IEclipsePreferences store = EclipseUtil.instanceScope().getNode(CommonEditorPlugin.PLUGIN_ID);
		store.putBoolean(getEnableParseErrorPrefKey(language), enabled);
	}

	protected List<IProblem> getParseErrors(String source) throws CoreException
	{
		return getParseErrors(source, new ParseState());
	}

	protected List<IProblem> getParseErrors(String source, IParseState ps) throws CoreException
	{
		return getParseErrors(source, ps, IMarker.PROBLEM);
	}

	protected List<IProblem> getParseErrors(String source, IParseState ps, String markerType) throws CoreException
	{
		TestProject project = new TestProject("Test", new String[] { "com.aptana.projects.webnature" });
		IFile file = project.createFile("parseErrorTest." + getFileExtension(), source);

		BuildContext context = new BuildContext(file);
		AbstractBuildParticipant fValidator = createValidator();
		fValidator.buildFile(context, new NullProgressMonitor());

		project.delete();

		Map<String, Collection<IProblem>> problems = context.getProblems();
		Collection<IProblem> daProblems = problems.get(markerType);
		if (daProblems == null)
		{
			return Collections.emptyList();
		}
		return new ArrayList<IProblem>(daProblems);
	}

	/**
	 * "js", "css", "txt", "rb", etc.
	 * 
	 * @return
	 */
	protected abstract String getFileExtension();

	protected String getEnableParseErrorPrefKey(String language)
	{
		return language + ":" + IPreferenceConstants.PARSE_ERROR_ENABLED;
	}
}
