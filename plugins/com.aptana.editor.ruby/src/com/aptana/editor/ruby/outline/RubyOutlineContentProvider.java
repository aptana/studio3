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
package com.aptana.editor.ruby.outline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.aptana.editor.common.outline.CommonOutlineContentProvider;
import com.aptana.editor.common.outline.CommonOutlineItem;
import com.aptana.editor.ruby.core.IRubyElement;
import com.aptana.editor.ruby.core.IRubyMethod;
import com.aptana.parsing.ast.IParseNode;

public class RubyOutlineContentProvider extends CommonOutlineContentProvider
{

	@Override
	protected Object[] filter(IParseNode[] nodes)
	{
		List<IRubyElement> list = new ArrayList<IRubyElement>();
		for (IParseNode node : nodes)
		{
			if (!(node instanceof IRubyElement))
			{
				continue;
			}
			IRubyElement element = (IRubyElement) node;
			// filters out block elements
			if (element.getNodeType() == IRubyElement.BLOCK)
			{
				continue;
			}
			list.add(element);
		}
		// Sort within this level of the hierarchy
		Collections.sort(list, new Comparator<IRubyElement>()
		{
			public int compare(IRubyElement o1, IRubyElement o2)
			{
				return sortPriority(o1) - sortPriority(o2);
			}

			private int sortPriority(IRubyElement element)
			{
				switch (element.getNodeType())
				{
					case IRubyElement.SCRIPT:
						return -2;
					case IRubyElement.GLOBAL:
						return -1;
					case IRubyElement.IMPORT_CONTAINER:
						return 0;
					case IRubyElement.IMPORT_DECLARATION:
						return 1;
					case IRubyElement.TYPE:
						return 2;
					case IRubyElement.CONSTANT:
						return 3;
					case IRubyElement.CLASS_VAR:
						return 4;
					case IRubyElement.INSTANCE_VAR:
					case IRubyElement.FIELD:
						return 5;
					case IRubyElement.METHOD:
						IRubyMethod method = (IRubyMethod) element;
						if (method.isSingleton())
						{
							return 6;
						}
						if (method.isConstructor())
						{
							return 7;
						}
						return 8;
					case IRubyElement.LOCAL_VAR:
						return 9;
					case IRubyElement.BLOCK:
					case IRubyElement.DYNAMIC_VAR:
						return 10;
					default:
						return 5;
				}
			}
		});

		// Turn into outline items
		List<CommonOutlineItem> outlineItems = new ArrayList<CommonOutlineItem>(list.size());
		for (IRubyElement element : list)
		{
			outlineItems.add(getOutlineItem(element));
		}

		return outlineItems.toArray(new CommonOutlineItem[outlineItems.size()]);
	}
}
