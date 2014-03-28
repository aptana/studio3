/**
 * Appcelerator Titanium Studio
 * Copyright (c) 2014 by Appcelerator, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */

package com.aptana.editor.common.internal;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.jface.text.quickassist.IQuickAssistProcessor;

import com.aptana.core.IMap;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.IConfigurationElementProcessor;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.IQuickFixProcessorsRegistry;

public class QuickFixProcessorsRegistry implements IQuickFixProcessorsRegistry
{

	private static final String EXTENSION_POINT_ID = "quickFixProcessors"; //$NON-NLS-1$
	private static final String ELEMENT_TYPE = "processor"; //$NON-NLS-1$

	private Map<String, LazyQuickFixProcessor> processorsMap;

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.IQuickFixProcessorsRegistry#getQuickFixProcessor(java.lang.String)
	 */
	public synchronized IQuickAssistProcessor getQuickFixProcessor(String contentType)
	{
		if (StringUtil.isEmpty(contentType))
		{
			return null;
		}
		IdeLog.logInfo(CommonEditorPlugin.getDefault(), "GetQuickFixProcessor:" + contentType); //$NON-NLS-1$
		lazyLoad();
		LazyQuickFixProcessor processor = processorsMap.get(contentType);
		if (processor != null)
		{
			return processor.getQuickFixProcessor();
		}
		return null;
	}

	protected IExtensionPoint getExtensionPoint()
	{
		return EclipseUtil.getExtensionPoint(CommonEditorPlugin.PLUGIN_ID, EXTENSION_POINT_ID);
	}

	private synchronized void lazyLoad()
	{
		if (processorsMap == null)
		{
			IdeLog.logInfo(CommonEditorPlugin.getDefault(), "lazyload"); //$NON-NLS-1$
			final ArrayList<LazyQuickFixProcessor> temp = new ArrayList<LazyQuickFixProcessor>();
			IExtensionPoint extensionPoint = getExtensionPoint();
			EclipseUtil.processElements(extensionPoint, new IConfigurationElementProcessor()
			{
				public void processElement(IConfigurationElement element)
				{
					String elementName = element.getName();
					if (ELEMENT_TYPE.equals(elementName))
					{
						temp.add(new LazyQuickFixProcessor(element));
					}
				}

				public Set<String> getSupportElementNames()
				{
					return CollectionsUtil.newSet(ELEMENT_TYPE);
				}
			});

			processorsMap = CollectionsUtil.mapFromValues(temp, new IMap<LazyQuickFixProcessor, String>()
			{
				public String map(LazyQuickFixProcessor item)
				{
					return item.getContentType();
				}
			});
		}
	}

	class LazyQuickFixProcessor
	{
		private static final String ATTR_CLASS = "class"; //$NON-NLS-1$
		private static final String ATTR_CONTENT_ID = "contentType"; //$NON-NLS-1$

		private IConfigurationElement element;
		private IQuickAssistProcessor wrapped;

		private LazyQuickFixProcessor(IConfigurationElement element)
		{
			this.element = element;
		}

		public String getContentType()
		{
			return element.getAttribute(ATTR_CONTENT_ID);
		}

		public IQuickAssistProcessor getQuickFixProcessor()
		{
			if (wrapped == null)
			{
				try
				{
					wrapped = (IQuickAssistProcessor) element.createExecutableExtension(ATTR_CLASS);
				}
				catch (CoreException e)
				{
					IdeLog.logError(CommonEditorPlugin.getDefault(), e);
				}
			}
			return wrapped;
		}
	}
}