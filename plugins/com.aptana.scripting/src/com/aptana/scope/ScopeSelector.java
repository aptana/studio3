package com.aptana.scope;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;

public class ScopeSelector
{
	private static final Pattern or_split = Pattern.compile("\\s*,\\s*"); //$NON-NLS-1$
	private static final Pattern and_split = Pattern.compile("\\s+"); //$NON-NLS-1$
	private static final String[] EMPTY_SPLIT_SCOPE = new String[0];
	
	private ISelectorNode _root;
	
	/**
	 * splitScope
	 * 
	 * @param scope
	 * @return
	 */
	public static String[] splitScope(String scope)
	{
		if (scope != null)
		{
			String[] ands = and_split.split(scope);
			
			if (ands.length == 0)
			{
				return EMPTY_SPLIT_SCOPE;
			}
			else if (ands.length == 1)
			{
				return new String[] { scope };
			}
			else
			{
				List<String> splitScopeList = new LinkedList<String>();
				
				for (int i = 0; i < ands.length; i++)
				{
					StringBuilder sb = new StringBuilder();
					
					for (int j = 0; j <= i; j++)
					{
						if (j > 0)
						{
							sb.append(" "); //$NON-NLS-1$
						}
						
						sb.append(ands[j]);
					}
					
					splitScopeList.add(sb.toString());
				}
				
				// Most specific scope is first in the array
				Collections.reverse(splitScopeList);
				
				return splitScopeList.toArray(EMPTY_SPLIT_SCOPE);
			}
		}
		
		return null;
	}
	
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
	 * matches
	 * 
	 * @param scopes
	 * @return
	 */
	public boolean matches(String[] scopes)
	{
		boolean result = false;
		
		if (this._root != null && scopes != null)
		{
			for (String scope : scopes)
			{
				if (this.matches(scope))
				{
					result = true;
					break;
				}
			}
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
		Stack<ISelectorNode> stack = null;
		
		if (selector != null)
		{
			stack = new Stack<ISelectorNode>();
			
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
		
		this._root = (stack != null && stack.size() > 0) ? stack.pop() : null;
	}
	

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return (this._root == null) ? "null" : this._root.toString();
	}
}
