/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.inferencing;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.aptana.core.util.StringUtil;
import com.aptana.js.core.parsing.ast.JSArgumentsNode;
import com.aptana.js.core.parsing.ast.JSGetPropertyNode;
import com.aptana.js.core.parsing.ast.JSIdentifierNode;
import com.aptana.js.core.parsing.ast.JSInvokeNode;
import com.aptana.js.core.parsing.ast.JSStringNode;
import com.aptana.parsing.ast.IParseNode;

/**
 * Resolves a module id relative to a base module namespace root, ore if the path is explicitly relative, relative to
 * current location.
 * 
 * @author cwilliams
 */
public class CommonJSResolver implements IRequireResolver
{

	public IPath resolve(String moduleId, IProject project, IPath currentLocation, IPath indexRoot)
	{
		if (currentLocation == null || !currentLocation.toFile().isDirectory())
		{
			throw new IllegalArgumentException("current location must be a directory"); //$NON-NLS-1$
		}
		if (indexRoot == null || !indexRoot.toFile().isDirectory())
		{
			throw new IllegalArgumentException("module namespace root must be a directory"); //$NON-NLS-1$
		}

		IPath modulePath = Path.fromPortableString(moduleId);
		if (modulePath.getFileExtension() == null)
		{
			modulePath = modulePath.addFileExtension("js"); //$NON-NLS-1$
		}
		if (moduleId.startsWith(".")) //$NON-NLS-1$
		{
			// relative
			return currentLocation.append(modulePath);
		}
		// absolute, so resolve relative to index root
		return indexRoot.append(modulePath);
	}

	public boolean applies(IProject project, IPath currentDirectory, IPath indexRoot)
	{
		// CommonJS will be our fallback. Should always apply.
		return true;
	}

	// Grab the args of the parent and try to turn them into a string
	// i.e. turn path.join('something', 'else.js') into 'something/else.js'
	// path.join(__dirname, '..', 'blah.js') into './../blah.js'
	/**
	 * This is a helper method for determining the moduleId being referred to inside require calls. The easiest case is
	 * a string argument, where we just return the value. The heuristic also accounts for path.join calls (looks for an
	 * arg that invokes join on some owner with string arguments). The node being passed in is expected to be one of the
	 * children of a JSArgumentsNode.
	 * 
	 * @param child
	 * @return
	 */
	public static String getModuleId(IParseNode child)
	{
		// Try an heuristic for path.join calls
		if (child instanceof JSInvokeNode)
		{
			JSInvokeNode invoke = (JSInvokeNode) child;
			if (invoke.getChildCount() == 2)
			{
				IParseNode node = invoke.getChild(0);
				if (node instanceof JSGetPropertyNode)
				{
					JSGetPropertyNode getProp = (JSGetPropertyNode) node;
					IParseNode right = getProp.getRightHandSide();
					if (right.getNameNode().getName().equals("join")) //$NON-NLS-1$
					{
						JSArgumentsNode joinArgs = (JSArgumentsNode) invoke.getChild(1);
						List<String> items = new ArrayList<String>();
						for (IParseNode joinArg : joinArgs)
						{
							String arg = getStringValue(joinArg);
							items.add(arg);
						}
						return StringUtil.join("/", items); //$NON-NLS-1$
					}
				}
			}
		}
		return getStringValue(child);
	}

	/**
	 * For a given argument, what is teh string value to use when we build up the full path. Treat __dirname as
	 * equivalent to "."
	 * 
	 * @param node
	 * @return
	 */
	private static String getStringValue(IParseNode node)
	{
		if (node instanceof JSStringNode)
		{
			JSStringNode string = (JSStringNode) node;
			return StringUtil.stripQuotes(string.getText());
		}
		if (node instanceof JSIdentifierNode)
		{
			JSIdentifierNode identifier = (JSIdentifierNode) node;
			if (identifier.getNameNode().getName().equals("__dirname")) //$NON-NLS-1$
			{
				return "."; //$NON-NLS-1$
			}
		}
		return null;
	}

}
