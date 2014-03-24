/**
 * Appcelerator Titanium Studio
 * Copyright (c) 2014 by Appcelerator, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */

package com.aptana.editor.xml.contentassist;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.quickassist.IQuickAssistAssistant;
import org.eclipse.jface.text.quickassist.IQuickAssistInvocationContext;
import org.eclipse.jface.text.quickassist.IQuickAssistProcessor;
import org.eclipse.jface.text.source.Annotation;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.IQuickFixProcessorsRegistry;
import com.aptana.editor.common.internal.QuickFixProcessorsRegistry;
import com.aptana.editor.xml.XMLSourceViewerConfiguration;

public class QuickFixProcessorsRegistryTest
{

	private static final String ERROR_MESSAGE = "error_message";
	private static final String CONTENT_TYPE = "org.eclipse.core.runtime.xml";
	private Mockery context;
	private IExtensionPoint extensionPoint;
	private IExtension extension;
	private IConfigurationElement element;
	private IQuickAssistProcessor quickFixProcessor;
	private AbstractThemeableEditor editor;
	private IQuickFixProcessorsRegistry registry;

	@Before
	public void setUp() throws CoreException
	{
		context = new Mockery()
		{
			{
				setImposteriser(ClassImposteriser.INSTANCE);
			}
		};
		extensionPoint = context.mock(IExtensionPoint.class);
		extension = context.mock(IExtension.class);
		element = context.mock(IConfigurationElement.class);
		quickFixProcessor = createProcessor();
		editor = context.mock(AbstractThemeableEditor.class);

		registry = new QuickFixProcessorsRegistry()
		{
			@Override
			protected IExtensionPoint getExtensionPoint()
			{
				return extensionPoint;
			}
		};
		context.checking(new Expectations()
		{
			{
				// oneOf(extensionPoint).getExtensions();
				// will(doAll(returnValue(new IExtension[] {})));
			}
		});
	}

	private IQuickAssistProcessor createProcessor()
	{
		return new IQuickAssistProcessor()
		{
			public String getErrorMessage()
			{
				return ERROR_MESSAGE;
			}

			public ICompletionProposal[] computeQuickAssistProposals(IQuickAssistInvocationContext invocationContext)
			{
				return null;
			}

			public boolean canFix(Annotation annotation)
			{
				return false;
			}

			public boolean canAssist(IQuickAssistInvocationContext invocationContext)
			{
				return false;
			}
		};
	}

	@Test
	public void testQuickFixValidContribution() throws Exception
	{
		context.checking(new Expectations()
		{
			{
				oneOf(extensionPoint).getExtensions();
				will(doAll(returnValue(new IExtension[] { extension })));

				oneOf(extension).getConfigurationElements();
				will(doAll(returnValue(new IConfigurationElement[] { element })));

				allowing(element).getName();
				will(doAll(returnValue("processor")));

				oneOf(element).getAttribute("contentType");
				will(doAll(returnValue(CONTENT_TYPE)));

				oneOf(element).createExecutableExtension("class");
				will(doAll(returnValue(quickFixProcessor)));

				allowing(editor).getContentType();
				will(returnValue(CONTENT_TYPE));
			}
		});
		XMLSourceViewerConfiguration viewerConfiguration = new XMLSourceViewerConfiguration(null, editor)
		{
			protected IQuickFixProcessorsRegistry getQuickFixRegistry()
			{
				return registry;
			}
		};
		IQuickAssistAssistant quickAssistant = viewerConfiguration.getQuickAssistAssistant(null);
		IQuickAssistProcessor expectedQuickFixProcessor = quickAssistant.getQuickAssistProcessor();
		Assert.assertNotNull(expectedQuickFixProcessor);
		Assert.assertEquals("Quick Fix Processors are not equal", quickFixProcessor, expectedQuickFixProcessor);

		context.assertIsSatisfied();
	}

	@Test
	public void testQuickFixInvalidContributions() throws Exception
	{
		context.checking(new Expectations()
		{
			{
				oneOf(extensionPoint).getExtensions();
				will(doAll(returnValue(new IExtension[] { extension })));

				oneOf(extension).getConfigurationElements();
				will(doAll(returnValue(new IConfigurationElement[] { element })));

				allowing(element).getName();
				will(doAll(returnValue("processor")));

				oneOf(element).getAttribute("contentType");
				will(doAll(returnValue("xyz")));

				never(element).createExecutableExtension("class");
				will(doAll(returnValue(quickFixProcessor)));

				allowing(editor).getContentType();
				will(returnValue(CONTENT_TYPE));
			}
		});
		XMLSourceViewerConfiguration viewerConfiguration = new XMLSourceViewerConfiguration(null, editor)
		{
			protected IQuickFixProcessorsRegistry getQuickFixRegistry()
			{
				return registry;
			}
		};
		IQuickAssistAssistant quickAssistant = viewerConfiguration.getQuickAssistAssistant(null);
		IQuickAssistProcessor assistProcessor = quickAssistant.getQuickAssistProcessor();
		Assert.assertNull(assistProcessor);

		context.assertIsSatisfied();
	}

	@Test
	public void testQuickFixEmptyContributions() throws Exception
	{
		context.checking(new Expectations()
		{
			{
				oneOf(extensionPoint).getExtensions();
				will(doAll(returnValue(new IExtension[] {})));

				allowing(editor).getContentType();
				will(returnValue(CONTENT_TYPE));
			}
		});
		XMLSourceViewerConfiguration viewerConfiguration = new XMLSourceViewerConfiguration(null, editor)
		{
			protected IQuickFixProcessorsRegistry getQuickFixRegistry()
			{
				return registry;
			}
		};
		IQuickAssistAssistant quickAssistant = viewerConfiguration.getQuickAssistAssistant(null);
		IQuickAssistProcessor assistProcessor = quickAssistant.getQuickAssistProcessor();
		Assert.assertNull(assistProcessor);

		context.assertIsSatisfied();
	}

	@After
	public void tearDown() throws CoreException
	{
		context = null;
		extensionPoint = null;
		extension = null;
		element = null;
		quickFixProcessor = null;
		editor = null;
	}
}