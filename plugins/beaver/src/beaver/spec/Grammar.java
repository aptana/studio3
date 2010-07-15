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
 * Parsed specification
 */
public class Grammar
{
	@SuppressWarnings("serial")
	static public class Exception extends java.lang.Exception
	{
		public Exception(String msg)
		{
			super(msg);
		}
	}

	static public final String EBNF_LIST_TYPE = "java.util.ArrayList";
	static public final String EBNF_LIST_TYPE_NAME = EBNF_LIST_TYPE.substring(EBNF_LIST_TYPE.lastIndexOf('.') + 1);
	
	/** Content of the "prolog" section to be copied verbatim at the beginning of the generated parser source file. */
	public String prolog;

	/** Java package of the parser */
	public String package_name;
	
	/** List of java interfaces that will be declared in the class header */
	public String[] impls;
	
	/** List of Java types and/or packages that wil be declared in the "import" section of a generated source file. */
	public String[] imports;
	
	/** New parser class name */
	public String class_name;

	/** Declarations edded to the parser class through %embed */
	public String class_code;

	/** Part of parser instance initialization code added though %init */
	public String init_code;

	/** Terminal symbols used in the grammar. */
	public Terminal[] terminals;

	/** NonTerminal symbols used in the grammar. */
	public NonTerminal[] nonterminals;
	
	/** The list of production rules */
	public Production[] rules;
	
	/** grammar's goal */
	public NonTerminal goal_symbol;

	/** "$" terminal symbol */
	public Terminal eof;

	/** Cached "error" nonterminal */
	public NonTerminal error;

	Grammar()
	{
		eof = new Terminal("EOF");
		error = new NonTerminal("error");
	}

	public void markNullableProductions()
	{
		boolean changed;
		do
		{
			changed = false;

			for (int i = 0; i < nonterminals.length; i++)
			{
				if (nonterminals[i].checkNullability())
				{
					changed = true;
				}
			}
		}
		while (changed);
	}
	
	/**
	 * Find first sets of every nonterminal.
	 * <p/>
	 * The first set is the set of all terminal symbols which can begin a string generated
	 * by that nonterminal.
	 */
	public void buildFirstSets()
	{
		for (int i = 0; i < nonterminals.length; i++)
		{
			nonterminals[i].first_set = new BitSet(terminals.length);
		}
		for (int i = 0; i < rules.length; i++)
		{
			rules[i].startFirstSet();
		}
		boolean modified;
		do
		{
			modified = false;

			for (int i = 0; i < rules.length; i++)
			{
				if (rules[i].extendFirstSet())
				{
					modified = true;
				}
			}
		}
		while (modified);
	}
}
