package com.aptana.scope;

import java.util.Stack;
import java.util.regex.Pattern;

public class ScopeSelector
{
	private static final Pattern or_split = Pattern.compile("\\s*,\\s*");
	private static final Pattern and_split = Pattern.compile("\\s+");
	
	private ISelectorNode _root;
	
	/**
	 * ScopeSelector
	 * 
	 * @param root
	 */
	ScopeSelector(ISelectorNode root)
	{
		this._root = root;
	}
	
	/**
	 * ScopeSelector
	 * 
	 * @param selector
	 */
	public ScopeSelector(String selector)
	{
		this.parse(selector);
	}
	
	/**
	 * getRoot
	 * 
	 * @return
	 */
	ISelectorNode getRoot()
	{
		return this._root;
	}
	
	/**
	 * matches
	 * 
	 * @param scope
	 * @return
	 */
	public boolean matches(String scope)
	{
		boolean result = false;
		
		if (this._root != null && scope != null)
		{
			MatchContext context = new MatchContext(scope);
			
			result = this._root.matches(context);
		}
		
		return result;
	}
	
	/**
	 * parse
	 * 
	 * @param selector
	 * @return
	 */
	private void parse(String selector)
	{
		Stack<ISelectorNode> stack = new Stack<ISelectorNode>();
		
		if (selector != null)
		{
			// simple parser for "and" and "or"
			String[] ors = or_split.split(selector);
			
			for (String or : ors)
			{
				// process ands
				String[] ands = and_split.split(or);
				int currentSize = stack.size();
				
				for (String and : ands)
				{
					stack.push(new NameSelector(and));
					
					if (stack.size() > currentSize + 1)
					{
						ISelectorNode right = stack.pop();
						ISelectorNode left = stack.pop();
						
						stack.push(new AndSelector(left, right));
					}
				}
				
				if (stack.size() > 1)
				{
					ISelectorNode right = stack.pop();
					ISelectorNode left = stack.pop();
					
					stack.push(new OrSelector(left, right));
				}
			}
		}
		
		this._root = (stack.size() > 0) ? stack.pop() : null;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return this._root.toString();
	}
}
