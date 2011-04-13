/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.common.internal.hover;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.expressions.EvaluationResult;
import org.eclipse.core.expressions.Expression;
import org.eclipse.core.expressions.ExpressionConverter;
import org.eclipse.core.expressions.ExpressionTagNames;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.ITextHover;

import com.aptana.editor.common.CommonEditorPlugin;

/**
 * @author Max Stepanov
 *
 */
public class TextHoverDescriptor {

	private static final String EXTENSION_POINT_ID = CommonEditorPlugin.PLUGIN_ID + ".textHovers"; //$NON-NLS-1$
	private static final String TAG_CONTENT_TYPE = "contentType"; //$NON-NLS-1$
	private static final String TAG_CONTENT_GROUP = "contentGroup"; //$NON-NLS-1$
	private static final String TAG_HOVER = "hover"; //$NON-NLS-1$
	private static final String ATT_CLASS = "class"; //$NON-NLS-1$
	private static final String ATT_TYPE = "type"; //$NON-NLS-1$
	private static final String ATT_PREFIX = "prefix"; //$NON-NLS-1$
	
	private static List<TextHoverDescriptor> descriptors = null;
	
	private final IConfigurationElement configurationElement;
	private final Set<String> contentTypes = new HashSet<String>();
	private final Set<String> contentGroups = new HashSet<String>();
	private final Expression enablementExpression; 

	/**
	 * @throws CoreException 
	 * 
	 */
	private TextHoverDescriptor(IConfigurationElement configurationElement) throws CoreException {
		this.configurationElement = configurationElement;
		for (IConfigurationElement element : configurationElement.getChildren(TAG_CONTENT_TYPE)) {
			readElement(element);
		}
		for (IConfigurationElement element : configurationElement.getChildren(TAG_CONTENT_GROUP)) {
			readElement(element);
		}
		IConfigurationElement[] elements = configurationElement.getChildren(ExpressionTagNames.ENABLEMENT);
		enablementExpression = elements.length > 0 ? ExpressionConverter.getDefault().perform(elements[0]) : null;
	}
	
	/**
	 * Returns list of available hovers
	 * @return
	 */
	public static List<TextHoverDescriptor> getContributedHovers() {
		if (descriptors == null) {
			readExtensionRegistry();			
		}
		return Collections.unmodifiableList(descriptors);
	}
	
	private static void readExtensionRegistry() {
		descriptors = new ArrayList<TextHoverDescriptor>();
		IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION_POINT_ID);
		for (IConfigurationElement element : elements) {
			if (TAG_HOVER.equals(element.getName())) {
				try {
					descriptors.add(new TextHoverDescriptor(element));
				} catch (CoreException e) {
					CommonEditorPlugin.logError(e);
				}
			}
		}
	}
	private void readElement(IConfigurationElement element) {
		if (TAG_CONTENT_TYPE.equals(element.getName())) {
			String type = element.getAttribute(ATT_TYPE);
			if (type == null || type.length() == 0) {
				return;
			}
			contentTypes.add(type);
			
		} else if (TAG_CONTENT_GROUP.equals(element.getName())) {
			String prefix = element.getAttribute(ATT_PREFIX);
			if (prefix == null || prefix.length() == 0) {
				return;
			}
			contentGroups.add(prefix);	
		}
	}
	
	/**
	 * Returns true if hover is enabled for current contentType/context
	 * @param contentType
	 * @param context
	 * @return
	 */
	public boolean isEnabledFor(String contentType, IEvaluationContext context) {
		if (!handlesContentType(contentType)) {
			return false;
		}
		if (enablementExpression != null) {
			try {
				return enablementExpression.evaluate(context) != EvaluationResult.FALSE;
			} catch (CoreException e) {
				CommonEditorPlugin.logError(e);
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Creates text hover
	 * @return
	 */
	public ITextHover createTextHover() {
		try {
			return (ITextHover) configurationElement.createExecutableExtension(ATT_CLASS);
		} catch (CoreException e) {
			CommonEditorPlugin.logError(e);
		}
		return null;
	}
	
	private boolean handlesContentType(String contentType) {
		if (contentTypes.contains(contentType)) {
			return true;
		}
		for (String prefix : contentGroups) {
			if (contentType.startsWith(prefix)) {
				return true;
			}
		}
		return false;
	}

}
