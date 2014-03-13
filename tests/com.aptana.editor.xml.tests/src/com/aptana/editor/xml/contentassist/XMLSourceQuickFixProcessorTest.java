/**
 * Appcelerator Titanium Studio
 * Copyright (c) 2014 by Appcelerator, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */

package com.aptana.editor.xml.contentassist;

import static org.junit.Assert.assertNotNull;

import org.eclipse.core.internal.registry.RegistryProviderFactory;
import org.eclipse.core.internal.registry.osgi.RegistryProviderOSGI;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.core.runtime.spi.IRegistryProvider;
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
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.xml.XMLSourceViewerConfiguration;

public class XMLSourceQuickFixProcessorTest
{

	private static final String ERROR_MESSAGE = "error_message";
	private static final String CONTENT_TYPE = "org.eclipse.core.runtime.xml";
	private Mockery context;

	@Before
	public void setUp() throws CoreException
	{
		context = new Mockery()
		{
			{
				setImposteriser(ClassImposteriser.INSTANCE);
			}
		};
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
	public void testQuickFixContribution() throws Exception
	{
		final IRegistryProvider registryProvider = context.mock(IRegistryProvider.class);
		final IExtensionRegistry extensionRegistry = context.mock(IExtensionRegistry.class);
		final IExtensionPoint extensionPoint = context.mock(IExtensionPoint.class);
		final IExtension extension = context.mock(IExtension.class);
		final IConfigurationElement element = context.mock(IConfigurationElement.class);
		final IQuickAssistProcessor quickFixProcessor = createProcessor();

		final AbstractThemeableEditor editor = context.mock(AbstractThemeableEditor.class);

		context.checking(new Expectations()
		{
			{
				allowing(registryProvider).getRegistry();
				will(returnValue(extensionRegistry));

				oneOf(extensionRegistry).getExtensionPoint(CommonEditorPlugin.PLUGIN_ID, "quickFixProcessors");
				will(returnValue(extensionPoint));

				oneOf(extensionPoint).getExtensions();
				will(returnValue(new IExtension[] { extension }));

				oneOf(extension).getConfigurationElements();
				will(returnValue(new IConfigurationElement[] { element }));

				allowing(element).getName();
				will(returnValue("processor"));

				oneOf(element).getAttribute("contentType");
				will(returnValue(CONTENT_TYPE));

				oneOf(element).createExecutableExtension("class");
				will(returnValue(quickFixProcessor));

				allowing(editor).getContentType();
				will(returnValue(CONTENT_TYPE));
			}
		});
		try
		{

			RegistryProviderFactory.releaseDefault();
			RegistryFactory.setDefaultRegistryProvider(registryProvider);

			XMLSourceViewerConfiguration viewerConfiguration = new XMLSourceViewerConfiguration(null, editor);
			IQuickAssistAssistant quickAssistant = viewerConfiguration.getQuickAssistAssistant(null);
			IQuickAssistProcessor assistProcessor = quickAssistant.getQuickAssistProcessor();
			assertNotNull(assistProcessor);
			Assert.assertEquals("Error messages are not equal", ERROR_MESSAGE, quickFixProcessor.getErrorMessage());

			context.assertIsSatisfied();
		}
		finally
		{
			RegistryProviderFactory.releaseDefault();
			RegistryFactory.setDefaultRegistryProvider(new RegistryProviderOSGI());
		}
	}

	@After
	public void tearDown() throws CoreException
	{
		context = null;
	}
}
