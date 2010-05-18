/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * This file is part of Beaver Parser Generator.                       *
 * Copyright (C) 2003,2004 Alexander Demenchuk <alder@softanvil.com>.  *
 * All rights reserved.                                                *
 * See the file "LICENSE" for the terms and conditions for copying,    *
 * distribution and modification of Beaver.                            *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package beaver.spec;

import beaver.comp.util.BitSet;

/**
 * Represents grammar nonterminals.
 */
public class NonTerminal extends GrammarSymbol
{
	/** List of productions where this non-terminal is a LHS */
	public Production.List definitions = new Production.List();

	/** Non-terminal can be nullable if any of its productions can derive an empty string. */
	public boolean is_nullable;

	/**
	 * The set of terminals that begin strings derived from production rules
	 * where this non-terminal is a LHS
	 */
	public BitSet first_set;

	NonTerminal(String name)
	{
		super(name);
	}

	NonTerminal(String name, String type)
	{
		super(name);
		super.type = type;
	}
	
	/**
	 * @return true if the nullability status has been changed from false to true
	 */
	boolean checkNullability()
	{
		if (is_nullable)
			return false;

		for (Production rule = definitions.start(); rule != null; rule = rule.next_definition)
		{
			if (rule.isNullable())
			{
				return is_nullable = true;
			}
		}
		return false;
	}
}
