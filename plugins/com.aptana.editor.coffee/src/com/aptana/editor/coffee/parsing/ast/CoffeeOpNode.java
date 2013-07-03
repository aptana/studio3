/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.coffee.parsing.ast;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("nls")
public class CoffeeOpNode extends CoffeeNode
{

	private static final Map<String, String> CONVERSIONS = new HashMap<String, String>();
	static
	{
		CONVERSIONS.put("==", "===");
		CONVERSIONS.put("!=", "!==");
		CONVERSIONS.put("of", "in");
	}

	private static final Map<String, String> INVERSIONS = new HashMap<String, String>();
	static
	{
		INVERSIONS.put("!==", "===");
		INVERSIONS.put("===", "!==");
	}
	private String operator;
	@SuppressWarnings("unused")
	private boolean postOp;
	private CoffeeNode first;
	private CoffeeNode second;
	private boolean invert;

	public CoffeeOpNode(String operator, CoffeeNode expression)
	{
		this(operator, expression, null, false);
	}

	public CoffeeOpNode(String operator, CoffeeNode left, CoffeeNode right)
	{
		this(operator, left, right, false);
	}

	/**
	 * For ++ and --
	 * 
	 * @param operator
	 * @param left
	 * @param object
	 * @param postOp
	 *            if true, this op happens after (i.e. i++, i-- rather than --i or ++i)
	 */
	public CoffeeOpNode(String operator, CoffeeNode left, CoffeeNode right, boolean postOp)
	{
		super(CoffeeNodeTypes.OP);
		// FIXME Just store them as children and get them back out using getChild(int)
		this.first = left;
		this.second = right;

		addChild(left);
		if (right != null)
		{
			addChild(right);
		}
		this.postOp = postOp;
		this.operator = operator;
		if (CONVERSIONS.containsKey(operator))
		{
			this.operator = CONVERSIONS.get(operator);
		}
	}

	public static CoffeeNode create(String operator, CoffeeNode first, CoffeeNode second)
	{
		if ("in".equals(operator))
		{
			return new CoffeeInNode(first, second);
		}
		if ("do".equals(operator))
		{
			List<CoffeeParamNode> params = new ArrayList<CoffeeParamNode>();
			if (first instanceof CoffeeCodeNode)
			{
				params = ((CoffeeCodeNode) first).params();
			}

			CoffeeCallNode call = new CoffeeCallNode(first, params, false);
			call.isDo = true;
			return call;
		}
		if ("new".equals(operator))
		{
			if (first instanceof CoffeeCallNode && !((CoffeeCallNode) first).isDo)
			{
				return ((CoffeeCallNode) first).newInstance();
			}
			if (first instanceof CoffeeCodeNode && ((CoffeeCodeNode) first).bound || first instanceof CoffeeCallNode
					&& ((CoffeeCallNode) first).isDo)
			{
				first = new CoffeeParensNode(first);
			}
		}
		return new CoffeeOpNode(operator, first, second);
	}

	public CoffeeOpNode invert()
	{
		boolean allInvertable = false;
		CoffeeOpNode curr = null;
		if (this.isChainable() && this.first.isChainable())
		{
			allInvertable = true;
			curr = this;
			while (curr != null && curr.operator != null)
			{
				allInvertable = allInvertable && INVERSIONS.containsKey(curr.operator);
				if (curr.first instanceof CoffeeOpNode)
				{
					curr = (CoffeeOpNode) curr.first;
				}
				else
				{
					curr = null;
				}
			}
			if (!allInvertable)
			{
				return (CoffeeOpNode) new CoffeeParensNode(this).invert();
			}
			curr = this;
			while (curr != null && curr.operator != null)
			{
				curr.invert = !curr.invert;
				curr.operator = INVERSIONS.get(curr.operator);
				if (curr.first instanceof CoffeeOpNode)
				{
					curr = (CoffeeOpNode) curr.first;
				}
				else
				{
					curr = null;
				}
			}
			return this;
		}
		else if (INVERSIONS.containsKey(this.operator))
		{
			this.operator = INVERSIONS.get(this.operator);
			if (this.first.unwrap() instanceof CoffeeOpNode)
			{
				this.first.invert();
			}
			return this;
		}
		else if (this.second != null)
		{
			return (CoffeeOpNode) new CoffeeParensNode(this).invert();
		}
		else if ("!".equals(this.operator)
				&& this.first.unwrap() instanceof CoffeeOpNode
				&& ("!".equals(((CoffeeOpNode) this.first.unwrap()).operator)
						|| "in".equals(((CoffeeOpNode) this.first.unwrap()).operator) || "instanceof"
							.equals(((CoffeeOpNode) this.first.unwrap()).operator)))
		{
			return (CoffeeOpNode) this.first.unwrap();
		}
		else
		{
			return new CoffeeOpNode("!", this);
		}
	}

	@Override
	public String getText()
	{
		return MessageFormat.format("Op {0}", operator);
	}

	private boolean isUnary()
	{
		return getChildCount() == 1;
	}

	protected boolean isComplex()
	{
		return !(this.isUnary() && ("+".equals(this.operator) || "-".equals(this.operator))) || this.first.isComplex();
	}

	protected boolean isChainable()
	{
		return "<".equals(this.operator) || ">".equals(this.operator) || ">=".equals(this.operator)
				|| "<=".equals(this.operator) || "===".equals(this.operator) || "!==".equals(this.operator);
	}
}
