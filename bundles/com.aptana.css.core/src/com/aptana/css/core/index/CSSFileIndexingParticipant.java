/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.index;

import java.net.URI;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.StringUtil;
import com.aptana.css.core.CSSColors;
import com.aptana.css.core.CSSCorePlugin;
import com.aptana.css.core.parsing.ast.CSSAttributeSelectorNode;
import com.aptana.css.core.parsing.ast.CSSRuleNode;
import com.aptana.css.core.parsing.ast.CSSTermNode;
import com.aptana.index.core.AbstractFileIndexingParticipant;
import com.aptana.index.core.Index;
import com.aptana.index.core.build.BuildContext;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.IParseRootNode;

public class CSSFileIndexingParticipant extends AbstractFileIndexingParticipant
{

	public void index(BuildContext context, Index index, IProgressMonitor monitor) throws CoreException
	{
		SubMonitor sub = SubMonitor.convert(monitor, 100);
		try
		{
			IParseRootNode ast = context.getAST();
			if (ast != null)
			{
				// TODO Pass along the monitor so we can provide very fine-grained detail on progress...
				walkNode(index, context.getURI(), ast);
			}
		}
		catch (CoreException e)
		{
			// ignores parser exception
		}
		catch (Throwable e)
		{
			IdeLog.logWarning(CSSCorePlugin.getDefault(), e);
		}
		finally
		{
			sub.done();
		}
	}

	/**
	 * @param index
	 * @param uri
	 * @param current
	 */
	public void walkNode(Index index, URI uri, IParseNode current)
	{
		if (current == null)
		{
			return;
		}

		if (current instanceof CSSAttributeSelectorNode)
		{
			CSSAttributeSelectorNode cssAttributeSelectorNode = (CSSAttributeSelectorNode) current;
			String text = cssAttributeSelectorNode.getText();
			if (!StringUtil.isEmpty(text) && text.charAt(0) == '.')
			{
				addIndex(index, uri, ICSSIndexConstants.CLASS, text.substring(1));
			}
			else if (!StringUtil.isEmpty(text) && text.charAt(0) == '#')
			{
				addIndex(index, uri, ICSSIndexConstants.IDENTIFIER, text.substring(1));
			}
		}

		if (current instanceof CSSTermNode)
		{
			CSSTermNode term = (CSSTermNode) current;
			String value = term.getText();
			if (CSSColors.isColor(value))
			{
				addIndex(index, uri, ICSSIndexConstants.COLOR, CSSColors.to6CharHexWithLeadingHash(value.trim()));
			}
		}

		if (current instanceof CSSRuleNode)
		{
			CSSRuleNode cssRuleNode = (CSSRuleNode) current;
			for (IParseNode child : cssRuleNode.getSelectors())
			{
				walkNode(index, uri, child);
			}
			for (IParseNode child : cssRuleNode.getDeclarations())
			{
				walkNode(index, uri, child);
			}
		}
		else
		{
			for (IParseNode child : current.getChildren())
			{
				walkNode(index, uri, child);
			}
		}
	}
}
