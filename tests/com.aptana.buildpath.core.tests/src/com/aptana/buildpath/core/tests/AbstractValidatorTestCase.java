/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.buildpath.core.tests;

import java.net.URI;
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

import com.aptana.core.IFilter;
import com.aptana.core.IMap;
import com.aptana.core.build.IBuildParticipant;
import com.aptana.core.build.IProblem;
import com.aptana.core.build.ReconcileContext;
import com.aptana.core.tests.TestProject;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.index.core.build.BuildContext;
import com.aptana.parsing.IParseState;

public abstract class AbstractValidatorTestCase extends TestCase
{

	protected IBuildParticipant fValidator;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		fValidator = createValidator();
	}

	@Override
	protected void tearDown() throws Exception
	{
		if (fValidator != null)
		{
			fValidator.restoreDefaults();
			fValidator = null;
		}
		super.tearDown();
	}

	protected abstract IBuildParticipant createValidator();

	protected List<IProblem> getParseErrors(String source, IParseState ps, String markerType) throws CoreException
	{
		BuildContext context = new ReconcileContext(getContentType(), URI.create("file:/" + markerType), source);
		fValidator.buildFile(context, new NullProgressMonitor());

		Map<String, Collection<IProblem>> problems = context.getProblems();
		Collection<IProblem> daProblems = problems.get(markerType);
		if (daProblems == null)
		{
			return Collections.emptyList();
		}
		return new ArrayList<IProblem>(daProblems);
	}

	protected abstract String getContentType();

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

	protected void assertProblem(IProblem item, String msg, int line, int severity, int offset)
	{
		assertEquals("message", msg, item.getMessage());
		assertEquals("line", line, item.getLineNumber());
		assertEquals("severity", severity, item.getSeverity().intValue());
		assertEquals("offset", offset, item.getOffset());
	}

	protected void assertProblem(IProblem item, String msg, int line, int severity, int offset, int length)
	{
		assertProblem(item, msg, line, severity, offset);
		assertEquals("length", length, item.getLength());
	}

	protected void assertContainsProblem(List<IProblem> items, String msg, int severity, int line, int offset,
			int length)
	{
		IProblem problem = assertContains(items, msg);
		assertProblem(problem, msg, line, severity, offset, length);
	}

	protected List<IProblem> getProblems(List<IProblem> items, final String message)
	{
		return CollectionsUtil.filter(items, new IFilter<IProblem>()
		{
			public boolean include(IProblem item)
			{
				return message.equals(item.getMessage());
			}
		});
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

	protected void assertProblemExists(List<IProblem> items, String msg, int line, int severity, int offset)
	{
		IProblem item = assertContains(items, msg);
		assertProblem(item, msg, line, severity, offset);
	}

	protected void assertCountOfProblems(List<IProblem> items, int count, final String msg)
	{
		List<IProblem> filtered = CollectionsUtil.filter(items, new IFilter<IProblem>()
		{
			public boolean include(IProblem item)
			{
				return msg.equals(item.getMessage());
			}
		});
		assertEquals(MessageFormat.format("number of problems of type {0}", msg), count, filtered.size());
	}

	/**
	 * "js", "css", "txt", "rb", etc.
	 * 
	 * @return
	 */
	protected abstract String getFileExtension();
}
