/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * This file is part of Beaver Parser Generator.                       *
 * Copyright (C) 2003,2004 Alexander Demenchuk <alder@softanvil.com>.  *
 * All rights reserved.                                                *
 * See the file "LICENSE" for the terms and conditions for copying,    *
 * distribution and modification of Beaver.                            *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package beaver.spec;

import java.util.Comparator;

import beaver.Symbol;

/**
 * Represents production rules in the grammar.
 */
@SuppressWarnings("rawtypes")
public class Production
{
	static final Comparator NUM_TERM_CMP = new Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			return ((Production) o2).rhs.n_term - ((Production) o1).rhs.n_term;
		}
	};
	static final Comparator NUM_NONTERM_CMP = new Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			return ((Production) o2).rhs.n_nonterm - ((Production) o1).rhs.n_nonterm;
		}
	};
	
	static public class List
	{
		private Production first, last;
		private int size;
		
		public void add(Production rule)
		{
			if (last == null)
				last = first = rule;
			else
				last = last.next_definition = rule;
			size++;
		}
		
		public Production start()
		{
			return first;
		}
		
		public int size()
		{
			return size;
		}
	}
	
	static public class RHS
	{
		/**
		 * Components of the right-hand side of a production.
		 */
		static public class Item
		{
			/** Grammar symbol */
			public final GrammarSymbol symbol;
		
			/** Alias for the symbol (NULL if none) */
			public final String alias;
		
			Item(GrammarSymbol sym)
			{
				symbol = sym;
				alias = null;
			}
		
			Item(GrammarSymbol sym, String alias)
			{
				this.symbol = sym;
				this.alias = alias;
			}
			
			public String toString()
			{
				return alias == null
					? symbol.name
					: new StringBuffer(symbol.name.length() + 1 + alias.length())
						.append(symbol.name).append('.').append(alias)
						.toString();
			}
			
			void appendTo(StringBuffer str)
			{
				str.append(symbol.name);
				if (alias != null)
				{
					str.append('.').append(alias);
				}
			}
		}
		
		static public final Item[] NONE = {};

		public final Item[] items;
		Item first_term;
		int n_term, n_nonterm;
		
		RHS()
		{
			items = NONE;
		}
		
		RHS(Item[] items)
		{
			this.items = items;
			for (int i = 0; i < items.length; i++)
			{
				Item rhs_item = items[i];
				if (rhs_item.symbol instanceof Terminal)
				{
					if (first_term == null)
					{
						first_term = rhs_item;
					}
					n_term++;
				}
				else
				{
					n_nonterm++;
				}
			}
		}
		
		RHS(GrammarSymbol sym)
		{
			this(new Item[] { new Item(sym) });
		}
		
		RHS(GrammarSymbol symA, GrammarSymbol symB)
		{
			this(new Item[] { new Item(symA), new Item(symB) });
		}
		
		public Item start()
		{
			return items.length > 0 ? items[0] : null;
		}
        
        public Item end()
        {
            return items.length > 0 ? items[items.length - 1] : null;
        }
        
        public int size()
        {
            return items.length;
        }
		
		public String toString()
		{
			if (items.length == 0)
				return "";
			
			if (items.length == 1)
				return items[0].toString();
			
			int len = -1;
			for (int i = 0; i < items.length; i++)
			{
				len += 1 + items[i].symbol.name.length();
				if (items[i].alias != null)
				{
					len += 1 + items[i].alias.length();
				}
			}
			StringBuffer str = new StringBuffer(len);
			items[0].appendTo(str);
			for (int i = 1; i < items.length; i++)
			{
				str.append(' ');
				items[i].appendTo(str);
			}
			return str.toString();
		}
	}

	static private final Terminal DEFAULT_PRECEDENCE_SYMBOL = new Terminal("DEFAULT_PRECEDENCE", -1, Terminal.Associativity.NONE);

	/** Next definition of this rule LHS nonterminal */
	public Production next_definition;
	
	/** This rule ID */
	public final int id;

	/** Left-hand side of the rule */
	public final NonTerminal lhs;

	/** Right hand side of the production */
	public final RHS rhs;

	/** Precedence symbol for this rule */
	public final Terminal prec_sym;

	/** Code executed when this rule is reduced */
	public String code;

	/** Position of this rule in the source */
	public int start_pos, end_pos;

	/** True if this rule can be reduced. */
	public boolean is_reducible;


	Production(int id, NonTerminal lhs, RHS rhs, Terminal prec_sym)
	{
		this.id = id;
		this.lhs = lhs;
		this.rhs = rhs;
		if (prec_sym == null)
		{
			/* Set production precedence.
			 * 
			 * Unless a precedence symbol is provided explicitly in the input grammar rules take as their
			 * precedence symbol the rightmost RHS symbol with a defined precedence. The idea is that if
			 * the lookahead has higher precedence than the production currently used, we shift.
			 * 
			 * If there are no RHS symbols with a _defined_ precedence, a production will have the lowest
			 * precedence.
			 */
			prec_sym = DEFAULT_PRECEDENCE_SYMBOL;
			
			for (int i = rhs.items.length - 1; i >= 0; i--)
			{
				if (rhs.items[i].symbol instanceof Terminal)
				{
					Terminal term = (Terminal) rhs.items[i].symbol;
					if (term.prec > 0)
					{
						prec_sym = term;
						break;
					}
				}
			}
		}
		this.prec_sym = prec_sym;
		if (rhs.items.length == 0)
		{
			lhs.is_nullable = true;
		}
		lhs.definitions.add(this);
	}
	
	Production(int id, NonTerminal lhs, RHS rhs)
	{
		this(id, lhs, rhs, null);
	}
	
	/**
	 * Checks whether the production can derive an empty string.
	 *
	 * @return true if this production can match an empty string.
	 */
	boolean isNullable()
	{
		if (rhs.first_term != null)
			return false;

		for (int i = 0; i < rhs.items.length; i++)
		{
			if (!((NonTerminal) rhs.items[i].symbol).is_nullable)
				return false;
		}
		return true;
	}

	/**
	 * Runs the first iteration of first set construction.
	 */
	void startFirstSet()
	{
		for (int i = 0; i < rhs.items.length; i++)
		{
			if (rhs.items[i].symbol instanceof Terminal)
			{
				lhs.first_set.add(rhs.items[i].symbol.id);
				break;
			}
			NonTerminal rhs_nt = (NonTerminal) rhs.items[i].symbol;
			if (rhs_nt != lhs && rhs_nt.first_set != null)
			{
				lhs.first_set.add(rhs_nt.first_set);
			}
			if (!rhs_nt.is_nullable) break;
		}
	}

	/**
	 * Adds other terminals that may start this production.
	 *
	 * @return true if this cycle contributed more terminals to the first set
	 */
	boolean extendFirstSet()
	{
		boolean more_added = false;
		for (int i = 0; i < rhs.items.length; i++)
		{
			if (rhs.items[i].symbol instanceof Terminal)
				break;
			
			NonTerminal rhs_nt = (NonTerminal) rhs.items[i].symbol;
			if (rhs_nt != lhs && rhs_nt.first_set != null)
			{
				if (lhs.first_set.add(rhs_nt.first_set))
				{
					more_added = true;
				}
			}
			if (!rhs_nt.is_nullable) break;
		}
		return more_added;
	}
	
	public int getFirstLine()
	{
		return Symbol.getLine(start_pos);
	}

	public String toString()
	{
		return new StringBuffer(100)
			.append(lhs).append(" = ").append(rhs)
			.toString();
	}
}
