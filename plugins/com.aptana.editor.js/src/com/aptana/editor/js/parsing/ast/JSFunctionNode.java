/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.parsing.ast;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.aptana.editor.js.sdoc.model.DocumentationBlock;
import com.aptana.editor.js.sdoc.model.Tag;
import com.aptana.editor.js.sdoc.model.TagType;
import com.aptana.editor.js.sdoc.model.TagWithTypes;
import com.aptana.editor.js.sdoc.model.Type;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.IParseNodeAttribute;
import com.aptana.parsing.ast.ParseNode;
import com.aptana.parsing.ast.ParseNodeAttribute;

public class JSFunctionNode extends JSNode
{
	private List<String> fReturnTypes;

	/**
	 * JSFunctionNode
	 * 
	 * @param children
	 */
	public JSFunctionNode(JSNode... children)
	{
		super(JSNodeTypes.FUNCTION, children);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNode#accept(com.aptana.editor.js.parsing.ast.JSTreeWalker)
	 */
	@Override
	public void accept(JSTreeWalker walker)
	{
		walker.visit(this);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseBaseNode#getAttributes()
	 */
	public IParseNodeAttribute[] getAttributes()
	{
		String name = this.getName().getText();

		if (name != null && name.length() > 0)
		{
			// TODO: possibly cache this
			return new IParseNodeAttribute[] { new ParseNodeAttribute(this, "name", name) //$NON-NLS-1$
			};
		}
		else
		{
			return ParseNode.NO_ATTRIBUTES;
		}
	}

	/**
	 * getBody
	 * 
	 * @return
	 */
	public IParseNode getBody()
	{
		return this.getChild(2);
	}

	/**
	 * getName
	 * 
	 * @return
	 */
	public IParseNode getName()
	{
		return this.getChild(0);
	}

	/**
	 * getParameters
	 * 
	 * @return
	 */
	public IParseNode getParameters()
	{
		return this.getChild(1);
	}

	/**
	 * getReturnNodes
	 * 
	 * @return
	 */
	public List<JSReturnNode> getReturnNodes()
	{
		List<JSReturnNode> result = new ArrayList<JSReturnNode>();

		// Using a linked list since it provides a queue interface
		Queue<IParseNode> queue = new LinkedList<IParseNode>();

		// prime the queue
		queue.add(this.getBody());

		while (queue.size() > 0)
		{
			IParseNode current = queue.poll();

			if (current instanceof JSReturnNode)
			{
				result.add((JSReturnNode) current);
			}
			else if (current instanceof JSFunctionNode == false)
			{
				for (IParseNode child : current)
				{
					queue.offer(child);
				}
			}
		}

		return result;
	}

	/**
	 * getReturnTypes
	 * 
	 * @return
	 */
	public List<String> getReturnTypes()
	{
		if (fReturnTypes == null)
		{
			fReturnTypes = new LinkedList<String>();
			DocumentationBlock docs = this.getDocumentation();

			if (docs != null && docs.hasTags())
			{
				for (Tag tag : docs.getTags())
				{
					if (tag.getType() == TagType.RETURN)
					{
						TagWithTypes tagWithTypes = (TagWithTypes) tag;

						for (Type type : tagWithTypes.getTypes())
						{
							fReturnTypes.add(type.getName());
						}
					}
				}
			}
		}

		return fReturnTypes;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseBaseNode#getText()
	 */
	@Override
	public String getText()
	{
		return this.getName().getText();
	}
}
