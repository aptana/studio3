package com.aptana.scope;

import java.util.Stack;
import java.util.regex.Pattern;

public class ScopeSelector
{
	private static final Pattern or_split = Pattern.compile("\\s*,\\s*");
	private static final Pattern and_split = Pattern.compile("\\s+");
	
	private ISelectorNode _root;
	
	/**
	 * parse
	 * 
	 * @param selector
	 * @return
	 */
	public static ScopeSelector parse(String selector)
	{
		Stack<ISelectorNode> stack = new Stack<ISelectorNode>();
		ScopeSelector result = null;
		
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
		
		if (stack.size() > 0)
		{
			result = new ScopeSelector(stack.pop());
		}
		
		return result;
	}
	
	/**
	 * ScopeSelector
	 * 
	 * @param root
	 */
	public ScopeSelector(ISelectorNode root)
	{
		this._root = root;
	}
	
	/**
	 * getRoot
	 * 
	 * @return
	 */
	public ISelectorNode getRoot()
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
		
		if (scope != null)
		{
			result = this._root.matches(scope);
		}
		
		return result;
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
