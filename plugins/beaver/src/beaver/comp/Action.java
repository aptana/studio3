/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * This file is part of Beaver Parser Generator.                       *
 * Copyright (C) 2003,2004 Alexander Demenchuk <alder@softanvil.com>.  *
 * All rights reserved.                                                *
 * See the file "LICENSE" for the terms and conditions for copying,    *
 * distribution and modification of Beaver.                            *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package beaver.comp;

import java.util.Arrays;
import java.util.Comparator;

import beaver.comp.util.BitSet;
import beaver.comp.util.Log;
import beaver.spec.Grammar;
import beaver.spec.GrammarSymbol;
import beaver.spec.NonTerminal;
import beaver.spec.Production;
import beaver.spec.Terminal;

/**
 * This class abstracts an action that is performed by an automaton when it's in some state and
 * a specified symbol has arrived.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
class Action
{
	static final Comparator LOOKAHEAD_ID_COMPARATOR = new Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			return ((Action) o1).lookahead.id - ((Action) o2).lookahead.id;
		}
	};
	
	/**
	 * Action types
	 */
	static class Type
	{
		static class Conflict extends Type
		{
			static class ShiftReduce extends Conflict
			{
				ShiftReduce(Shift shift_act, Reduce reduce_act, State state, String reason)
				{
					super("shift-reduce", makeDescription(shift_act, reduce_act, state, reason));
				}
				
				static private String makeDescription(Shift shift_act, Reduce reduce_act, State state, String reason)
				{
					StringBuffer text = new StringBuffer(256)
						.append("\n\tshift ")
						.append(shift_act.lookahead.name)
						.append(" in:");
					for (Configuration conf = state.conf_set.first_conf; conf != null; conf = conf.next)
					{
						if (conf.dot < conf.rule.rhs.items.length && conf.rule.rhs.items[conf.dot].symbol == shift_act.lookahead)
						{
							text.append("\n\t\t").append(conf);
						}
					}
					text.append("\n\tor reduce:\n\t\t")
						.append(reduce_act.rule.toString());
					int line = reduce_act.rule.getFirstLine();
					if (line > 0)
					{
						text.append(" @ ").append(line);
					}
					text.append("\n\t- ")
						.append(reason);
					
					return text.toString();
				}
			}
			
			static class ReduceReduce extends Conflict
			{
				ReduceReduce(Reduce act1, Reduce act2, State state, String reason)
				{
					super("reduce-reduce", makeDescription(act1, act2, state, reason));
				}
				
				static private String makeDescription(Reduce act1, Reduce act2, State state, String reason)
				{
					StringBuffer text = new StringBuffer(256)
						.append("\n\treduce\t")
						.append(act1.rule);
					int line;
					if ((line = act1.rule.getFirstLine()) > 0)
						text.append(" @ ").append(line);
					text.append("\n\tor\t")
						.append(act2.rule);
					if ((line = act2.rule.getFirstLine()) > 0) 
						text.append(" @ ").append(line);
					text.append("\n\ton ")
						.append(act1.lookahead.name)
						.append(" - ")
						.append(reason);
					return text.toString();
				}
			}
			
			private String descr;
			
			Conflict(String type, String details)
			{
				super(0, type + " conflict");
				descr = details;
			}
			
			public String toString()
			{
				return new StringBuffer(super.toString().length() + 2 + descr.length())
					.append(super.toString())
					.append(": ")
					.append(descr)
					.toString();
			}
		}
		
		static final Type SHIFT    = new Type(1, "SHIFT");
		static final Type REDUCE   = new Type(2, "REDUCE");
		static final Type ACCEPT   = new Type(3, "ACCEPT");
		static final Type RESOLVED = new Type(-1, "RESOLVED");
		static final Type NOT_USED = new Type(-2, "NOT USED");

		private int id;
		private String name;

		Type(int id, String name)
		{
			this.id = id;
			this.name = name;
		}

		boolean isRemovable()
		{
			return id < 0;
		}

		boolean isResolved()
		{
			return id <= 0;
		}

		public String toString()
		{
			return name;
		}
	}

	/**
	 * SHIFT action
	 */
	static class Shift extends Action
	{
		State state;

		Shift(GrammarSymbol lookahead, State state)
		{
			super(Type.SHIFT, lookahead);
			this.state = state;
		}

		/**
		 * Tries to resolve a shift-reduce conflict using terminal/production precedence and associativity.
		 *
		 * @param act action that conflicts with this one
		 * @return true if conflict has been resolved
		 */
		boolean resolveConflict(Action act, State act_state, Log log)
		{
			if (!(act instanceof Reduce))
				throw new IllegalArgumentException("shift-reduce expected, \"" + act + "\" found");
			
			Reduce reduce_act = (Reduce) act;
			Terminal reduce_prec_sym = reduce_act.rule.prec_sym;

			if (this.lookahead instanceof NonTerminal)
			{
				act.type = new Type.Conflict.ShiftReduce(this, reduce_act, act_state, lookahead.name +  " is a non-terminal"); 
				return false;
			}
			Terminal shift_prec_sym = (Terminal) this.lookahead;

			if (shift_prec_sym.prec > reduce_prec_sym.prec)
			{
				if (reduce_prec_sym.prec < 0)
				{
					log.warning("Resolved Shift-Reduce conflict by selecting (" + this.toString() + ") over (" + act.toString() + ") using precedence.");
				}
				act.type = Type.RESOLVED;
				return true;
			}
			if (shift_prec_sym.prec < reduce_prec_sym.prec)
			{
				if (shift_prec_sym.prec < 0)
				{
					log.warning("Resolved Shift-Reduce conflict by selecting (" + act.toString() + ") over (" + this.toString() + ") using precedence.");
				}
				this.type = Type.RESOLVED;
				return true;
			}
			//
			// here shift_prec_sym.prec == reduce_prec_sym.prec
			//
			if (shift_prec_sym.assoc == Terminal.Associativity.RIGHT)
			{
				act.type = Type.RESOLVED;
				return true;
			}
			if (shift_prec_sym.assoc == Terminal.Associativity.LEFT)
			{
				this.type = Type.RESOLVED;
				return true;
			}

			act.type = new Type.Conflict.ShiftReduce(this, reduce_act, act_state, shift_prec_sym.prec > 0 ? lookahead.name +  " is nonassociative" : "insufficient precedence information");
			return false;
		}

		/**
		 * @return state ID - always a positive number in the [1..N_states] range
		 */
		short getId()
		{
			return (short) state.id;
		}

		public String toString()
		{
			return lookahead + ": " + type + "; goto " + state.id;
		}
	}

	/**
	 * REDUCE actions
	 */
	static class Reduce extends Action
	{
		/**
		 * An instance of this class creates REDUCE actions for configurations with the "dot"
		 * after the last RHS symbol.
		 */
		static class Maker extends BitSet.Processor
		{
			Terminal[] terminals;
			State state;
			Production rule;

			Maker(Terminal[] terminals, State first_state)
			{
				this.terminals = terminals;
				this.state = first_state;
			}

			/**
			 * Adds all of the reduce actions.
			 * <p/>
			 * A reduce action is added for each element of the lookaheads set of a configuration
			 * which has its "dot" at the extreme right.
			 */
			void buildReduceActions()
			{
				for (; state != null; state = state.next)
				{
					for (Configuration conf = state.conf_set.first_conf; conf != null; conf = conf.next)
					{
						if (conf.dot == conf.rule.rhs.items.length)
						{
							rule = conf.rule;
							conf.lookaheads.forEachElementRun(this);
						}
					}
				}
			}

			/**
			 * Creates REDUCE actions for each lookahead symbol of the "complete" production configuration.
			 *
			 * @param index index of the terminal
			 */
			protected void process(int index)
			{
				state.actions.add(new Reduce(terminals[index], rule));
			}
		}

		Production rule;

		Reduce(Terminal lookahead, Production rule)
		{
			super(Type.REDUCE, lookahead);
			this.rule = rule;
		}

		/**
		 * Tries to resolve a reduce-reduce conflict using production precedence and associativity.
		 *
		 * @param act action that conflicts with this one
		 * @return true if conflict has been resolved
		 */
		boolean resolveConflict(Action act, State act_state, Log log)
		{
			if (!(act instanceof Reduce))
				throw new IllegalArgumentException("reduce-reduce expected");

			Terminal my_prec_sym = rule.prec_sym;
			Reduce reduce_act = (Reduce) act;
			Terminal act_prec_sym = reduce_act.rule.prec_sym;

			if (my_prec_sym.prec > act_prec_sym.prec)
			{
				act.type = Type.RESOLVED;
				return true;
			}
			if (my_prec_sym.prec < act_prec_sym.prec)
			{
				this.type = Type.RESOLVED;
				return true;
			}
			// else my_prec_sym.prec == act_prec_sym.prec
			act.type = new Type.Conflict.ReduceReduce(this, reduce_act, act_state, "equal precedence");
			return false;
		}

		/**
		 * @return a negative number, which complement is a rule ID
		 */
		short getId()
		{
			return (short) ~rule.id;
		}

		public String toString()
		{
			return lookahead != null ? lookahead + ": " + type + " " + rule : "[any]: REDUCE " + rule;
		}
	}

	/**
	 * ACCEPT action
	 */
	static class Accept extends Action
	{
		private short id;

		Accept(Grammar grammar)
		{
			super(Type.ACCEPT, grammar.goal_symbol);
			id = (short) ~ grammar.rules.length;
		}

		/**
		 * @return a negative number which does not match any REDUCE action
		 */
		short getId()
		{
			return id;
		}
	}

	/**
	 * Instances of this class represent linked lists of actions in a state.
	 */
	static class List
	{
		static final Comparator NUM_ACTIONS_CMP = new Comparator()
		{
			public int compare(Object o1, Object o2)
			{
				return ((Action.List) o2).num_actions - ((Action.List) o1).num_actions;
			}
		};
		
		State state;
		Action first;
		Action last;
		int num_actions;

		List(State state)
		{
			this.state = state;
		}

		void add(Action act)
		{
			if (last == null)
				last = first = act;
			else
				last = last.next = act;
			num_actions++;
		}

		/**
		 * Tries to resolve shift-reduce and reduce-reduce conflicts by delegating actual resolution to
		 * either Shift or Reduce actions. If the conflict is resolved one of the two conflicting actions
		 * will be marked as RESOLVED and removed at the end.
		 * <p/>
		 * Note: that the process that build this list initially guarantees that SHIFTs always go before
		 * REDUCEs.
		 * 
		 * @param log Logger that will accept conflict reports
		 *
		 * @return number of unresolved conflicts
		 */
		int resolveConflicts(Log log)
		{
			int num_conflicts = 0;
			if (first != null && num_actions > 1)
			{
				for (Action act = first; act != last; act = act.next)
				{
					if (!act.type.isResolved())
					{
						for (Action cmp = act.next; cmp != null; cmp = cmp.next)
						{
							if (cmp.lookahead == act.lookahead && !act.resolveConflict(cmp, state, log))
							{
								num_conflicts++;
							}
						}
					}
				}
				removeResolvedActions();
			}
			return num_conflicts;
		}
		
		void reportConflicts(Log log)
		{
			if (first != null && num_actions > 1)
			{
				for (Action act = first; act != null; act = act.next)
				{
					if (act.type instanceof Type.Conflict)
					{
						log.error(act.type.toString());
					}
				}				
			}
		}

		private void removeResolvedActions()
		{
			while (first != null && first.type.isRemovable())
			{
				first = first.next;
				num_actions--;
			}
			last = first;
			for (Action next = first.next; next != null; next = next.next)
			{
				if (next.type.isRemovable())
				{
					num_actions--;
				}
				else
				{
					last = last.next = next;
				}
			}
			last.next = null;
		}

		/**
		 * Marked productions attached to REDUCE rules as "reducible". Prepares assertion check that
		 * all productions in the grammar are reducible (it's an error if some are not).
		 */
		void markReducibleProductions()
		{
			for (Action act = first; act != null; act = act.next)
			{
				if (act.type == Type.REDUCE)
				{
					((Reduce) act).rule.is_reducible = true;
				}
			}
		}

		/**
		 * Checks whether the list of actions contains several REDUCE actions with the same production.
		 * Find which production is used the most often (if more then one) and replaces those multiple
		 * REDUCE actions with a single DEFAULT action.
		 * <p/>
		 * Replaced actions are not removed. They will be used to check for possible collisions when
		 * automaton action tables are built.
		 *
		 * @param default_symbol synthetic symbol that is placed instead of the action lookahead
		 */
		void compress()
		{
			Production maxrule = null;
			int maxcnt = 0;

			for (Action act = first; act != null; act = act.next)
			{
				if (act.type == Type.REDUCE)
				{
					Production rule = ((Reduce) act).rule;
					if (rule == maxrule) continue;
					int cnt = 1;
					for (Action cmp = act.next; cmp != null; cmp = cmp.next)
					{
						if (cmp.type == Type.REDUCE && ((Reduce) cmp).rule == rule)
						{
							cnt++;
						}
					}
					if (cnt > maxcnt)
					{
						maxrule = rule;
						maxcnt = cnt;
					}
				}
			}

			if (maxcnt > 1)
			{
				Action act = first;
				while (act.type != Type.REDUCE || ((Reduce) act).rule != maxrule)
				{
					act = act.next;
				}
				act.lookahead = null;
				for (act = act.next; act != null; act = act.next)
				{
					if (act.type == Type.REDUCE && ((Reduce) act).rule == maxrule)
					{
						act.type = Type.NOT_USED;
					}
				}
			}
		}

		/**
		 * Split list of actions into list of actions with terminal lookahead symbol and another
		 * list with nonterminal lookahead actions. In the end current list keeps only actions
		 * "removed" by compression.
		 *
		 * @param terminal_actions    initially empty list for actions with Terminal lookahead
		 * @param nonterminal_actions initially empty list for actions with NonTerminal lookahead
		 * @return default action
		 */
		Action split(List terminal_actions, List nonterminal_actions)
		{
			Action default_act = null, first_not_used = null, last_not_used = null;
			this.num_actions = 0;
			for (Action act = first; act != null; act = act.next)
			{
				if (act.type.isRemovable())
				{
					if (last_not_used == null)
						last_not_used = first_not_used = act;
					else
						last_not_used = last_not_used.next = act;
					num_actions++;
				}
				else
				{
					if (act.lookahead instanceof NonTerminal)
						nonterminal_actions.add(act);
					else if (act.lookahead instanceof Terminal)
						terminal_actions.add(act);
					else // default
					{
						if (default_act != null)
							throw new IllegalStateException("multiple default actions in state " + state.id + " actions list");
						default_act = act;
					}
				}
			}
			first = first_not_used;
			last = last_not_used;

			if (last_not_used != null)
				last_not_used.next = null;

			if (default_act != null)
				default_act.next = null;

			if (terminal_actions.last != null)
				terminal_actions.last.next = null;

			if (nonterminal_actions.last != null)
				nonterminal_actions.last.next = null;
			
			terminal_actions.sort();
			nonterminal_actions.sort();

			return default_act;
		}

		/**
		 * Sorts the list of actions, so that they are ordered by a lookahead symbol ID,
		 */
		void sort()
		{
			if (num_actions > 1)
			{
				Action[] actions = new Action[num_actions];
				int i = 0;
				for (Action act = first; act != null; act = act.next)
				{
					actions[i++] = act;
				}
				Arrays.sort(actions, LOOKAHEAD_ID_COMPARATOR);
				
				Action act = first = actions[i = 0];
				while (++i < num_actions)
				{
					act = act.next = actions[i];
				}
				(last = act).next = null;
			}
		}
	}

	/** Next action in a state */
	Action next;

	/** Action type */
	Type type;

	/** The lookahead symbol */
	GrammarSymbol lookahead;

	Action(Type type, GrammarSymbol lookahead)
	{
		this.type = type;
		this.lookahead = lookahead;
	}

	short getId()
	{
		return 0;
	}

	boolean resolveConflict(Action act, State state, Log log)
	{
		throw new IllegalStateException("only shift-reduce or reduce-reduce conflicts are expected");
	}

	public String toString()
	{
		return lookahead + ": " + type;
	}
}
