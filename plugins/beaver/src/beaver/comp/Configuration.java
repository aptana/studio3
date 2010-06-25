/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * This file is part of Beaver Parser Generator.                       *
 * Copyright (C) 2003,2004 Alexander Demenchuk <alder@softanvil.com>.  *
 * All rights reserved.                                                *
 * See the file "LICENSE" for the terms and conditions for copying,    *
 * distribution and modification of Beaver.                            *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package beaver.comp;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import beaver.comp.util.BitSet;
import beaver.spec.Grammar;
import beaver.spec.GrammarSymbol;
import beaver.spec.NonTerminal;
import beaver.spec.Production;
import beaver.spec.Terminal;


/**
 * This class represents an LALR item or a parser configuration. Each configuration consists of a
 * production, a "dot" that marks a position in a production and a set of lookahead symbols.
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
class Configuration implements Comparable
{
	static class Set
	{
		static class Factory
		{
			private Map configurations;
			private Configuration probe;
			private Grammar grammar;

			/** Start of a linked list of all configurations in a state */
			Configuration first_conf;

			/** Last configuration in a list. Place where we add new configurations. */
			Configuration last_conf;

			/** Number of configurations added so far */
			int num_conf;

			Factory(Grammar grammar)
			{
				this.configurations = new HashMap(89);
				this.probe = new Configuration();
				this.grammar = grammar;
			}

			void reset()
			{
				first_conf = last_conf = null;
				num_conf = 0;

				//configurations.clear();
				configurations = new HashMap(89);
			}

			Configuration addConfiguration(Production rule, int mark)
			{
				probe.rule = rule;
				probe.dot = mark;

				Configuration conf = (Configuration) configurations.get(probe);
				if (conf == null)
				{
					conf = new Configuration(probe, grammar);
					configurations.put(conf, conf);

					if (last_conf == null)
						last_conf = first_conf = conf;
					else
						last_conf = last_conf.next = conf;
					num_conf++;
				}
				return conf;
			}

			Set getCore()
			{
				Configuration[] core = new Configuration[num_conf];
				int j = 0;
				for (Configuration conf = first_conf; conf != null; conf = conf.next)
				{
					core[j++] = conf;
				}
				Arrays.sort(core);

				Configuration conf = first_conf = core[0];
				int core_hash_code = conf.hashCode();
				for (j = 1; j < num_conf; j++)
				{
					conf = conf.next = core[j];
					core_hash_code = core_hash_code * 571 + conf.hashCode();
				}
				last_conf = conf;
				conf.next = null;

				return new Set(this, core_hash_code);
			}
		}

		/** Producer of new configurations in this set */
		Configuration.Set.Factory conf_set_factory;

		/** Start of a linked list of all configurations in a state */
		Configuration first_conf;

		/** Last core configuration. */
		Configuration last_core_conf;

		/** Cached core size */
		int core_size;

		/** Cached core hash code */
		int core_hash_code;

		private Set(Configuration.Set.Factory conf_set_factory, int hash_code)
		{
			this.conf_set_factory = conf_set_factory;
			this.first_conf = conf_set_factory.first_conf;
			this.last_core_conf = conf_set_factory.last_conf;
			this.core_hash_code = hash_code;
			this.core_size = conf_set_factory.num_conf;
		}

		void appendReversePropagation(Set conf_set)
		{
			Configuration stop = last_core_conf.next;
			for (Configuration my_conf = first_conf, cp_conf = conf_set.first_conf; my_conf != stop; my_conf = my_conf.next, cp_conf = cp_conf.next)
			{
				my_conf.appendReversePropagation(cp_conf);
			}
		}

		void buildClosure()
		{
			for (Configuration conf = first_conf; conf != null; conf = conf.next)
			{
				if (conf.isDotAfterLastSymbol())
					continue;

				GrammarSymbol sym = conf.getSymbolAfterDot();
				if (sym instanceof NonTerminal)
				{
					NonTerminal nt = (NonTerminal) sym;
					for (Production rule = nt.definitions.start(); rule != null; rule = rule.next_definition)
					{
						Configuration new_conf = conf_set_factory.addConfiguration(rule, 0);
						if (new_conf.addLookaheads(conf))
						{
							conf.addForwardPropagation(new_conf);
						}
					}
				}
			}
		}
		
		void reverseReversePropagation()
		{
			for (Configuration conf = first_conf; conf != null; conf = conf.next)
			{
				conf.reverseReversePropagation();
			}
		}

		void resetContributionFlags()
		{
			for (Configuration conf = first_conf; conf != null; conf = conf.next)
			{
				conf.has_contributed = false;
			}
		}

		private boolean equals(Set conf_set)
		{
			if (conf_set == this)
				return true;

			if (conf_set.core_size != this.core_size)
				return false;

			Configuration my_conf = first_conf;
			Configuration cmp_conf = conf_set.first_conf;
			Configuration stop = last_core_conf.next;
			while (my_conf != stop && my_conf.equals(cmp_conf))
			{
				my_conf = my_conf.next;
				cmp_conf = cmp_conf.next;
			}
			return my_conf == stop;
		}

		public boolean equals(Object obj)
		{
			return obj instanceof Set && this.equals((Set) obj);
		}

		public int hashCode()
		{
			return core_hash_code;
		}

		public String toString()
		{
			StringBuffer str = new StringBuffer(1000);
			Configuration conf = first_conf;
			for (; conf != last_core_conf.next; conf = conf.next)
			{
				str.append('+').append(conf).append('\n');
			}
			for (; conf != null; conf = conf.next)
			{
				str.append(' ').append(conf).append('\n');
			}
			return str.toString();
		}
	}

	static class PropagationLink
	{
		/** Next element in a linked list */
		PropagationLink next;

		/** A configuration to propagate to/from */
		Configuration conf;

		PropagationLink(Configuration conf)
		{
			this.conf = conf;
		}
	}

	/** Next configuration in a linked list of state configurations */
	Configuration next;

	Production rule;
	int dot;
	BitSet lookaheads;

	// Configurations may initially be missing some symbols from their lookahead sets.
	// Links are maintained from each configuration to the set of configurations that
	// would need to be updated if symbols are added to the lookahead set.
	PropagationLink fwd_propagation;
	PropagationLink bck_propagation, last_bck_propagation;

	/**
	 * A flags that is used by state factory to mark configurations that have already
	 * contributed to a successor state.
	 */
	boolean has_contributed;

	/**
	 * Constructor for a Configuration.Factory probe
	 */
	private Configuration()
	{
	}

	Configuration(Configuration factory_probe, Grammar grammar)
	{
		this.rule = factory_probe.rule;
		this.dot = factory_probe.dot;
		this.lookaheads = new BitSet(grammar.terminals.length);
	}

	GrammarSymbol getSymbolAfterDot()
	{
		return rule.rhs.items[dot].symbol;
	}

	boolean isDotAfterLastSymbol()
	{
		return dot == rule.rhs.items.length;
	}

	void addLookahead(Terminal term)
	{
		lookaheads.add(term.id);
	}

	/**
	 * Adds lookahead symbols from a given configuration.
	 *
	 * @return true if all rhs parts were nullable nonterminals and hence the lookahead set
	 *         needs to be expanded by propagating terminals from configurations that contribute
	 *         lookaheadds to the current source of terminals.
	 */
	boolean addLookaheads(Configuration conf)
	{
		for (int i = conf.dot + 1; i < conf.rule.rhs.items.length; i++)
		{
			GrammarSymbol sym = conf.rule.rhs.items[i].symbol;
			if (sym instanceof Terminal)
			{
				lookaheads.add(sym.id);
				return false;
			}
			else
			{
				NonTerminal nt = (NonTerminal) sym;
				lookaheads.add(nt.first_set);
				if (!nt.is_nullable)
					return false;
			}
		}
		return true;
	}

	void addForwardPropagation(Configuration conf)
	{
		PropagationLink link = new PropagationLink(conf);
		link.next = fwd_propagation;
		fwd_propagation = link;
	}

	void addReversePropagation(Configuration conf)
	{
		PropagationLink link = new PropagationLink(conf);
		if (last_bck_propagation == null)
			last_bck_propagation = bck_propagation = link;
		else
			last_bck_propagation = last_bck_propagation.next = link;
	}

	void appendReversePropagation(Configuration conf)
	{
		if (last_bck_propagation == null)
			bck_propagation = conf.bck_propagation;
		else
			last_bck_propagation.next = conf.bck_propagation;
		last_bck_propagation = conf.last_bck_propagation;
	}

	void reverseReversePropagation()
	{
		PropagationLink link = bck_propagation;
		while (link != null)
		{
			PropagationLink next_link = link.next;
			Configuration conf = link.conf;

			link.conf = this;
			link.next = conf.fwd_propagation;
			conf.fwd_propagation = link;

			link = next_link;
		}
		bck_propagation = null;
	}

	boolean findLookaheads()
	{
		boolean more_found = false;
		for (PropagationLink link = fwd_propagation; link != null; link = link.next)
		{
			if (link.conf.lookaheads.add(this.lookaheads))
			{
				more_found = true;
				link.conf.has_contributed = false;
			}
		}
		return more_found;
	}

	boolean equals(Configuration conf)
	{
		return this.rule == conf.rule && this.dot == conf.dot;
	}

	public boolean equals(Object obj)
	{
		return obj instanceof Configuration && this.equals((Configuration) obj);
	}

	public int hashCode()
	{
		return rule.id * 37 + dot;
	}

	/**
	 * Defined ordering of two configurations in a set.
	 * <p/>
	 * Sets are kept ordered, so it's easy to match cores of configuration set
	 *
	 * @param o another Configuration
	 * @return int < 0, 0, or int > 0 if current Configuration <, ==, or > the one provided in the method's argument
	 */
	public int compareTo(Object o)
	{
		if (o == this)
			return 0;

		Configuration conf = (Configuration) o;
		int cmp = this.rule.id - conf.rule.id;
		if (cmp == 0)
		{
			cmp = this.dot - conf.dot;
		}
		return cmp;
	}

	public String toString()
	{
		StringBuffer str = new StringBuffer(100);
		str.append(rule.lhs).append(" =");
		for (int i = 0; i < rule.rhs.items.length; i++)
		{
			Production.RHS.Item rhs_item = rule.rhs.items[i];
			str.append(' ');
			if (i == dot)
				str.append('*');
			str.append(rhs_item);
		}
		if (dot == rule.rhs.items.length)
			str.append(" *");
		int line;
		if ((line = rule.getFirstLine()) > 0)
		{
			str.append(" @ ").append(line);
		}
		return str.toString();
	}
}
