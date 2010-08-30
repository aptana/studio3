package com.aptana.scope;

public interface ISelectorNode
{
	boolean matches(MatchContext context);
	
	int matchLength();

	int matchFragments();
}
