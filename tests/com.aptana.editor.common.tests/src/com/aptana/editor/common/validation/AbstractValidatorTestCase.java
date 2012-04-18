/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.validation;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.aptana.core.IMap;
import com.aptana.core.build.AbstractBuildParticipant;
import com.aptana.core.build.IProblem;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.tests.util.TestProject;
import com.aptana.index.core.build.BuildContext;
import com.aptana.parsing.IParseState;

public abstract class AbstractValidatorTestCase extends TestCase
{

	protected AbstractBuildParticipant fValidator;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		fValidator = createValidator();
	}

	@Override
	protected void tearDown() throws Exception
	{
		fValidator = null;
		super.tearDown();
	}

	protected abstract AbstractBuildParticipant createValidator();

	protected List<IProblem> getParseErrors(String source, IParseState ps, String markerType) throws CoreException
	{
		TestProject project = new TestProject("Test", new String[] { "com.aptana.projects.webnature" });
		IFile file = project.createFile("parseErrorTest." + getFileExtension(), source);

		BuildContext context = new BuildContext(file);
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

	protected IProblem assertContains(List<IProblem> items, String message)
	{
		for (IProblem item : items)
		{
			if (message.equals(item.getMessage()))
			{
				return item;
			}
		}
		Collection<String> strings = CollectionsUtil.map(items, new IMap<IProblem, String>()
		{
			public String map(IProblem item)
			{
				return item.getMessage();
			}
		});
		fail(MessageFormat.format("Was unable to find an IProblem with message: {0}. Found problems: {1}", message,
				StringUtil.join(", ", strings)));
		return null;
	}

	protected void assertDoesntContain(List<IProblem> items, String message)
	{
		for (IProblem item : items)
		{
			if (message.equals(item.getMessage()))
			{
				fail("Found unexpected IProblem with message: " + message);
				return;
			}
		}
	}

	/**
	 * "js", "css", "txt", "rb", etc.
	 * 
	 * @return
	 */
	protected abstract String getFileExtension();
}
