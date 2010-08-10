package com.aptana.scope;

import java.util.Collection;
import java.util.Stack;
import java.util.regex.Pattern;

public class ScopeSelector
{
	private static final Pattern or_split = Pattern.compile("\\s*,\\s*"); //$NON-NLS-1$
	private static final Pattern and_split = Pattern.compile("\\s+"); //$NON-NLS-1$

	private ISelectorNode _root;
	private int matchOffset;
	private int matchLength;

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

			for (int i = 0; i < context.getLength(); i++)
			{
				// save current position so we can advance later
				context.pushCurrentStep();

				// see if we match at this point within the context
				if (this._root.matches(context))
				{
					// we need to save what step matched (looking for highest),
					// plus how much of that step matched (looking for longest)
					matchOffset = i;
					matchLength = this._root.matchLength();

					// we matched, so report success and stop looking for a match
					result = true;
					break;
				}

				// restore position where we started and move forward one
				context.popCurrentStep();
				context.advance();
			}
		}

		return result;
	}

	/**
	 * http://manual.macromates.com/en/scope_selectors
	 * <ol>
	 * <li>1. Match the element deepest down in the scope e.g. string wins over source.php when the scope is source.php
	 * string.quoted.</li>
	 * <li>2. Match most of the deepest element e.g. string.quoted wins over string.</li>
	 * <li>3. Rules 1 and 2 applied again to the scope selector when removing the deepest element (in the case of a
	 * tie), e.g. text source string wins over source string.</li>
	 * </ol>
	 * 
	 * @param selectors
	 * @param scope
	 * @return
	 */
	public static ScopeSelector bestMatch(Collection<ScopeSelector> selectors, String scope)
	{
		int bestOffset = -1;
		int bestLength = 0;
		ScopeSelector bestMatch = null;
		for (ScopeSelector selector : selectors)
		{
			if (selector.matches(scope))
			{

				int offset = selector.matchOffset;
				if (offset > bestOffset)
				{
					bestOffset = offset;
					bestMatch = selector;
					bestLength = selector.matchLength;
				}
				else if (offset == bestOffset)
				{
					int length = selector.matchLength;
					if (length > bestLength)
					{
						bestMatch = selector;
					}
					else if (length == bestLength)
					{
						// FIXME Need to match higher up steps?
					}
				}
			}
		}
		return bestMatch;
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
				int startingSize = stack.size();
				int i = 0;

				for (; i < ands.length; i++)
				{
					String and = ands[i];

					// stop processing "and"s if we encounter a negative lookahead operator
					if (and != null && and.equals("-")) //$NON-NLS-1$
					{
						break;
					}

					stack.push(new NameSelector(and));

					if (stack.size() > startingSize + 1)
					{
						ISelectorNode right = stack.pop();
						ISelectorNode left = stack.pop();

						stack.push(new AndSelector(left, right));
					}
				}

				// process negative lookaheads
				if (i < ands.length && stack.size() > startingSize)
				{
					String operator = ands[i];

					if (operator != null && operator.equals("-")) //$NON-NLS-1$
					{
						if (i + 1 < ands.length)
						{
							// advance over '-'
							i++;

							// remember current position
							startingSize = stack.size();

							for (; i < ands.length; i++)
							{
								String simpleSelector = ands[i];

								stack.push(new NameSelector(simpleSelector));

								if (stack.size() > startingSize + 1)
								{
									ISelectorNode right = stack.pop();
									ISelectorNode left = stack.pop();

									stack.push(new AndSelector(left, right));
								}
							}

							ISelectorNode right = stack.pop();
							ISelectorNode left = stack.pop();

							stack.push(new NegativeLookaheadSelector(left, right));
						}
					}
					// else parse error
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
		return (this._root == null) ? "null" : this._root.toString(); //$NON-NLS-1$
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof ScopeSelector)
		{
			return this.toString().equals(obj.toString());
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return toString().hashCode();
	}
}
