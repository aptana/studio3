/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scope;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;

import com.aptana.core.util.StringUtil;
import com.aptana.scripting.model.AbstractBundleElement;

public class ScopeSelector implements IScopeSelector
{
	private static final String NEGATIVE_LOOKAHEAD = "-"; //$NON-NLS-1$
	private static final Pattern or_split = Pattern.compile("\\s*,\\s*"); //$NON-NLS-1$
	private static final Pattern and_split = Pattern.compile("\\s+"); //$NON-NLS-1$

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
	public static IScopeSelector bestMatch(Collection<IScopeSelector> selectors, String scope)
	{
		if (selectors == null || selectors.isEmpty())
		{
			return null;
		}

		List<IScopeSelector> reversed = new ArrayList<IScopeSelector>(selectors);
		Collections.reverse(reversed);
		IScopeSelector bestMatch = null;
		for (IScopeSelector selector : reversed)
		{
			if (selector == null)
			{
				continue;
			}
			if (selector.matches(scope))
			{
				if (bestMatch == null)
				{
					bestMatch = selector;
					continue;
				}

				if (selector.compareTo(bestMatch) > 0)
				{
					bestMatch = selector;
				}
			}
		}
		return bestMatch;
	}

	/**
	 * Sorts the matching bundle elements from "worst" match to "best" match.
	 * 
	 * @param bundleElements
	 */
	public static void sort(List<? extends AbstractBundleElement> bundleElements)
	{
		if (bundleElements == null || bundleElements.isEmpty())
		{
			return;
		}

		Collections.sort(bundleElements, new Comparator<AbstractBundleElement>()
		{

			public int compare(AbstractBundleElement o1, AbstractBundleElement o2)
			{
				return o1.getScopeSelector().compareTo(o2.getScopeSelector());
			}

		});
	}

	private ISelectorNode _root;
	private List<Integer> matchResults;
	/**
	 * Lazily cache the toString() value solely for performance reasons. We call toString() in equals(), hashCode(),
	 * some other locations - so this value is computed by concatenating children nodes repeatedly.
	 */
	private String fString;

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

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof ScopeSelector)
		{
			return this.toString().equals(obj.toString());
		}
		return false;
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

	@Override
	public int hashCode()
	{
		return toString().hashCode();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scope.IScopeSelector#matches(java.lang.String)
	 */
	public boolean matches(String scope)
	{
		matchResults = new ArrayList<Integer>();
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
					// Add match results. If more than one value, we need to replace existing zeros in our list...
					Collection<Integer> tmpResults = this._root.matchResults();
					int toRemove = tmpResults.size() - 1;
					for (int x = 0; x < toRemove; x++)
					{
						matchResults.remove(0);
					}
					matchResults.addAll(0, tmpResults);

					// Fill with preceding zeros.
					while (matchResults.size() < context.getLength())
					{
						matchResults.add(0, 0);
					}

					// we matched, so report success and stop looking for a match
					result = true;
					break;
				}
				matchResults.add(0, 0); // Add a non-match

				// restore position where we started and move forward one
				context.popCurrentStep();
				context.backup();
			}
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scope.IScopeSelector#matches(java.lang.String[])
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
				ands = processNegativeLookaheads(ands);
				int startingSize = stack.size();
				int i = 0;

				for (; i < ands.length; i++)
				{
					String and = ands[i];

					// stop processing "and"s if we encounter a negative lookahead operator
					if (and != null && and.equals(NEGATIVE_LOOKAHEAD))
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

					if (operator != null && operator.equals(NEGATIVE_LOOKAHEAD))
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

	/**
	 * Handles when '-' negative lookahead is butted up against next NameSelector.
	 * 
	 * @param ands
	 * @return
	 */
	private String[] processNegativeLookaheads(String[] ands)
	{
		List<String> processed = new ArrayList<String>();
		for (String and : ands)
		{
			if (and.startsWith(NEGATIVE_LOOKAHEAD) && and.length() > 1)
			{
				processed.add(NEGATIVE_LOOKAHEAD);
				processed.add(and.substring(1));
			}
			else
			{
				processed.add(and);
			}
		}
		return processed.toArray(new String[processed.size()]);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		if (fString == null)
		{
			fString = (this._root == null) ? StringUtil.EMPTY : this._root.toString();
		}
		return fString;
	}

	public List<Integer> matchResults()
	{
		if (matchResults == null)
		{
			return Collections.emptyList();
		}
		return matchResults;
	}

	public int compareTo(IScopeSelector o)
	{
		return compare(matchResults, o.matchResults());
	}

	private static int compare(List<Integer> results, List<Integer> matchResults)
	{
		// offset in list is offset of space-delimited part
		// number at that offset is length of the match at that part
		// winner is the one with longest deepest match
		// so first look for highest offset with a non-zero value

		// if lists are not of same length, expand smaller one to match by filling with zeros
		while (results.size() > matchResults.size())
		{
			if (matchResults.isEmpty())
			{
				matchResults = new ArrayList<Integer>();
			}
			matchResults.add(0);
		}
		while (matchResults.size() > results.size())
		{
			if (results.isEmpty())
			{
				results = new ArrayList<Integer>();
			}

			results.add(0);
		}

		// So starting at the end of the lists, look for the highest match length, ties go back an offset to be broken
		for (int i = results.size() - 1; i >= 0; i--)
		{
			int firstVal = results.get(i);
			int secondVal = matchResults.get(i);
			// If one of the two has a longer match at the offset, it wins
			if (firstVal != secondVal)
			{
				return firstVal - secondVal;
			}
		}

		return 0;
	}
}
