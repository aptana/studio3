package com.aptana.scripting.model;

import com.aptana.scope.ScopeSelector;

/**
 * This filter should be used to filter the bundle elements whose scope selector is
 * within the specified scope.
 * <p>
 * For example:
 *
 * A bundle element with scope selector:
 *
 * scope selector = "b c"
 *
 * will match the following scopes:
 *
 * "a b c d"
 * "a b c"
 * "b c"
 * "b c d"
 *
 * will not match:
 *
 * "a b"
 * "a b d c"
 * "c d"
 * "b d c"
 *
 * @author schitale
 *
 */
public class ScopeContainsFilter extends ScopeFilter
{
	/**
	 * This filter should be used to filter the bundle elements whose scope selector is
	 * within the specified scope.
	 *
	 * @param scope
	 */
	public ScopeContainsFilter(String scope)
	{
		super(ScopeSelector.splitScope(scope));
	}
}
