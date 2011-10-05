/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.console.internal.expressions;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;

import com.aptana.console.ConsolePlugin;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.StringUtil;

/**
 * @author Max Stepanov
 * 
 */
public final class ExpressionManager {

	private static final String EXTENSION_POINT_ID = ConsolePlugin.PLUGIN_ID + ".consoleExpressions"; //$NON-NLS-1$
	private static final String TAG_EXPRESSION = "expression"; //$NON-NLS-1$
	private static final String TAG_GROUP = "group"; //$NON-NLS-1$
	private static final String ATT_NAME = "name"; //$NON-NLS-1$
	private static final String ATT_PATTERN = "pattern"; //$NON-NLS-1$
	private static final String ATT_ID = "id"; //$NON-NLS-1$
	private static final String ATT_SCOPE = "scope"; //$NON-NLS-1$

	private List<Expression> expressions = new ArrayList<Expression>();

	/**
	 * 
	 */
	public ExpressionManager() {
		readExtensionRegistry();
		loadExpressions();
	}

	/**
	 * Calculate line styles
	 * 
	 * @param lineOffset
	 * @param lineText
	 * @return
	 */
	public StyleRange[] calculateStyles(int lineOffset, String lineText) {
		if (lineText.length() > 0) {
			for (Expression expression : expressions) {
				StyleRange[] result = expression.calculateStyleRanges(lineOffset, lineText);
				// TODO: FEATURE - collect ranges from all expressions and merge them
				if (result != null) {
					return result;
				}
			}
		}
		return null;
	}

	/**
	 * Calculate line background
	 * 
	 * @param lineText
	 * @return
	 */
	public Color calculateBackground(String lineText) {
		if (lineText.length() > 0) {
			for (Expression expression : expressions) {
				Color result = expression.calculateBackground(lineText);
				if (result != null) {
					return result;
				}
			}
		}
		return null;
	}

	private void readExtensionRegistry() {
		IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION_POINT_ID);
		for (IConfigurationElement element : elements) {
			if (TAG_EXPRESSION.equals(element.getName())) {
				readExpressionElement(element);
			}
		}
	}
	
	private void readExpressionElement(IConfigurationElement element) {
		String name = element.getAttribute(ATT_NAME);
		String pattern = element.getAttribute(ATT_PATTERN);
		String scope = element.getAttribute(ATT_SCOPE);
		if (StringUtil.isEmpty(name) || StringUtil.isEmpty(pattern) || StringUtil.isEmpty(scope)) {
			return;
		}
		SortedMap<Integer, String> groupScopes = new TreeMap<Integer, String>();
		groupScopes.put(0, scope);
		for (IConfigurationElement child : element.getChildren(TAG_GROUP)) {
			try {
				int id = Integer.valueOf(child.getAttribute(ATT_ID));
				scope = child.getAttribute(ATT_SCOPE);
				groupScopes.put(id, scope);
			} catch (NumberFormatException e) {
				IdeLog.logWarning(ConsolePlugin.getDefault(), e);
			}
		}
		expressions.add(new Expression(name, pattern, groupScopes));
	}

	private void loadExpressions() {
		// TODO: load user customized expressions here
	}

}
