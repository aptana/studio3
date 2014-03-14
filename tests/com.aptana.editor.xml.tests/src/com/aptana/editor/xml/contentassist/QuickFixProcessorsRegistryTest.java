/**
 * Appcelerator Titanium Studio
 * Copyright (c) 2014 by Appcelerator, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */

package com.aptana.editor.xml.contentassist;

import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.eclipse.core.internal.registry.RegistryProviderFactory;
import org.eclipse.core.internal.registry.osgi.RegistryProviderOSGI;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.core.runtime.spi.IRegistryProvider;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.quickassist.IQuickAssistAssistant;
import org.eclipse.jface.text.quickassist.IQuickAssistInvocationContext;
import org.eclipse.jface.text.quickassist.IQuickAssistProcessor;
import org.eclipse.jface.text.source.Annotation;
import org.hamcrest.Description;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Action;
import org.jmock.api.Invocation;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.logging.IdeLog.StatusLevel;
import com.aptana.core.util.EclipseUtil;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.QuickFixProcessorsRegistry;
import com.aptana.editor.xml.XMLSourceViewerConfiguration;

public class QuickFixProcessorsRegistryTest
{

	private static final String ERROR_MESSAGE = "error_message";
	private static final String CONTENT_TYPE = "org.eclipse.core.runtime.xml";
	private Mockery context;

	@BeforeClass
	public static void turnUpLogging()
	{
		System.err.println("Turning logging to INFO");
		IdeLog.setCurrentSeverity(StatusLevel.INFO);
		System.err.println("Turning on all debug options");
		Map<String, String> currentOptions = EclipseUtil.getTraceableItems();
		EclipseUtil.setBundleDebugOptions(currentOptions.keySet().toArray(new String[currentOptions.size()]), true);
		System.err.println("Turning on platform debugging flag");
		EclipseUtil.setPlatformDebugging(true);
	}

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
	@SuppressWarnings("restriction")
	public void testQuickFixContribution() throws Exception
	{
		final IRegistryProvider registryProvider = context.mock(IRegistryProvider.class);
		final IExtensionRegistry extensionRegistry = context.mock(IExtensionRegistry.class);
		final IExtensionPoint extensionPoint = context.mock(IExtensionPoint.class);
		final IExtension extension = context.mock(IExtension.class);
		final IConfigurationElement element = context.mock(IConfigurationElement.class);
		final IQuickAssistProcessor quickFixProcessor = createProcessor();

		final Action printLogAction = new Action()
		{
			public Object invoke(Invocation invocation) throws Throwable
			{
				System.err.println("Invoking:: " + invocation);
				return null;
			}

			public void describeTo(Description description)
			{
			}
		};

		final Action getExtensionPointAction = new Action()
		{
			public Object invoke(Invocation invocation) throws Throwable
			{
				System.err.println("Invoking:: " + invocation);
				String pluginId = (String) invocation.getParameter(0);
				String extnPoint = (String) invocation.getParameter(1);
				if (pluginId.equals(CommonEditorPlugin.PLUGIN_ID) && extnPoint.equals("quickFixProcessors"))
				{
					return extensionPoint;
				}
				else
				{
					IExtensionRegistry registry = new RegistryProviderOSGI().getRegistry();
					return registry.getExtensionPoint(pluginId, extnPoint);
				}
			}

			public void describeTo(Description description)
			{
			}
		};

		final AbstractThemeableEditor editor = context.mock(AbstractThemeableEditor.class);

		context.checking(new Expectations()
		{
			{
				allowing(registryProvider).getRegistry();
				will(doAll(printLogAction, returnValue(extensionRegistry)));

				oneOf(extensionRegistry).getExtensionPoint(with(any(String.class)), with(any(String.class)));
				will(doAll(getExtensionPointAction));

				oneOf(extensionPoint).getExtensions();
				will(doAll(printLogAction, returnValue(new IExtension[] { extension })));

				oneOf(extension).getConfigurationElements();
				will(doAll(printLogAction, returnValue(new IConfigurationElement[] { element })));

				allowing(element).getName();
				will(doAll(printLogAction, returnValue("processor")));

				oneOf(element).getAttribute("contentType");
				will(doAll(printLogAction, returnValue(CONTENT_TYPE)));

				oneOf(element).getAttributeNames();
				will(returnValue(new String[] {}));

				oneOf(element).createExecutableExtension("class");
				will(doAll(printLogAction, returnValue(quickFixProcessor)));

				allowing(editor).getContentType();
				will(returnValue(CONTENT_TYPE));
			}
		});
		try
		{

			RegistryProviderFactory.releaseDefault();
			RegistryFactory.setDefaultRegistryProvider(registryProvider);
			Assert.assertEquals(extensionRegistry, Platform.getExtensionRegistry());
			final QuickFixProcessorsRegistry quickFixProcessorsRegistry = context
					.mock(QuickFixProcessorsRegistry.class);
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
