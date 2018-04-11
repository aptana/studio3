/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scope;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import beaver.Symbol;

import com.aptana.core.epl.util.LRUCache;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.scope.parsing.ScopeParser;
import com.aptana.scripting.ScriptingActivator;
import com.aptana.scripting.model.AbstractBundleElement;

public class ScopeSelector extends Symbol implements IScopeSelector
{
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
		IScopeSelector bestMatch = null;

		if (!CollectionsUtil.isEmpty(selectors))
		{
			List<IScopeSelector> reversed = new ArrayList<IScopeSelector>(selectors);

			Collections.reverse(reversed);

			for (IScopeSelector selector : reversed)
			{
				if (selector != null && selector.matches(scope))
				{
					if (bestMatch == null)
					{
						bestMatch = selector;
					}
					else if (selector.compareTo(bestMatch) > 0)
					{
						bestMatch = selector;
					}
				}
			}
		}

		return bestMatch;
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

	/**
	 * Sorts the matching bundle elements from "worst" match to "best" match.
	 * 
	 * @param bundleElements
	 */
	public static void sort(List<? extends AbstractBundleElement> bundleElements)
	{
		if (!CollectionsUtil.isEmpty(bundleElements))
		{
			Collections.sort(bundleElements, new Comparator<AbstractBundleElement>()
			{
				public int compare(AbstractBundleElement o1, AbstractBundleElement o2)
				{
					return o1.getScopeSelector().compareTo(o2.getScopeSelector());
				}
			});
		}
	}

	private final ISelectorNode _root;
	private List<Integer> matchResults;

	/**
	 * Lazily cache the toString() value solely for performance reasons. We call toString() in equals(), hashCode(),
	 * some other locations - so this value is computed by concatenating children nodes repeatedly.
	 */
	private String fString;

	/**
	 * Cache used to hold the results for a selector.
	 */
	private static final LRUCache<String, ISelectorNode> cacheParse = new LRUCache<String, ISelectorNode>(200);

	/**
	 * Lock to access cacheParse.
	 */
	private static final Object lock = new Object();

	/**
	 * Entry to signal null in the cache when a parse returns null.
	 */
	private static final SelectorNode NULL_SELECTOR = new SelectorNode();

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
		synchronized (lock)
		{
			ISelectorNode node = cacheParse.get(selector);
			if (node == null)
			{
				node = parse(selector);
				if (node == null)
				{
					// Handle the case for parse() returning null (because get() may return null as a default value but
					// we want to cache that the return was null too).
					node = NULL_SELECTOR;
				}
				cacheParse.put(selector, node);
			}
			if (node == NULL_SELECTOR) // Check if it's the same instance.
			{
				node = null;
			}
			this._root = node;
		}
	}

	public int compareTo(IScopeSelector o)
	{
		return compare(matchResults, o.getMatchResults());
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

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scope.IScopeSelector#getMatchResults()
	 */
	public List<Integer> getMatchResults()
	{
		if (matchResults == null)
		{
			return Collections.emptyList();
		}

		return matchResults;
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
					Collection<Integer> tmpResults = this._root.getMatchResults();
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

	private static ISelectorNode parse(String selector)
	{
		ScopeParser parser = new ScopeParser();

		// clear current root
		ISelectorNode root = null;

		if (!StringUtil.isEmpty(selector))
		{
			try
			{
				root = parser.parse(selector);
			}
			catch (Exception e)
			{
				// @formatter:off
				String message = MessageFormat.format(
					"An error occurred while parsing scope ''{0}'' : {1}",
					selector,
					e.getMessage()
				);
				// @formatter:on

				IdeLog.logError(ScriptingActivator.getDefault(), message);
			}
		}
		return root;
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
}
