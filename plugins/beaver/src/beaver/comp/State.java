/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * This file is part of Beaver Parser Generator.                       *
 * Copyright (C) 2003,2004 Alexander Demenchuk <alder@softanvil.com>.  *
 * All rights reserved.                                                *
 * See the file "LICENSE" for the terms and conditions for copying,    *
 * distribution and modification of Beaver.                            *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package beaver.comp;

import java.util.HashMap;
import java.util.Map;

import beaver.spec.GrammarSymbol;

/**
 * This class represents LALR state.
 * A state consists of an LALR configuration set and a set of transitions to other states.
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
class State
{
	static class Factory
	{
		private State last_state;
		private int num_states;
		private Map states;
		private Configuration.Set.Factory conf_set_factory;

		Factory(Configuration.Set.Factory conf_set_factory)
		{
			states = new HashMap(89);
			this.conf_set_factory = conf_set_factory;
		}

		State getState(Configuration.Set core)
		{
			State state = (State) states.get(core);
			if (state == null)
			{
				core.buildClosure();
				states.put(core, state = new State(++num_states, core));
				if (last_state == null)
					last_state = state;
				else
					last_state = last_state.next = state;
				buildShiftsFrom(state);
			}
			else
			{
				state.conf_set.appendReversePropagation(core);
			}
			return state;
		}

		private void buildShiftsFrom(State state)
		{
			state.conf_set.resetContributionFlags();

			for (Configuration conf = state.conf_set.first_conf; conf != null; conf = conf.next)
			{
				if (conf.has_contributed || conf.isDotAfterLastSymbol())
					continue;

				conf_set_factory.reset();

				GrammarSymbol marked_symbol = conf.getSymbolAfterDot();

				// For every configuration in the "from" state which also has the "marked_symbol"
				// after the "dot" add the same configuration to the core set under construction
				// but with the mark ("dot") shifted one symbol to the right.
				for (Configuration nconf = conf; nconf != null; nconf = nconf.next)
				{
					if (nconf.has_contributed || nconf.isDotAfterLastSymbol() || nconf.getSymbolAfterDot() != marked_symbol)
						continue;

					Configuration new_core_conf = conf_set_factory.addConfiguration(nconf.rule, nconf.dot + 1);
					new_core_conf.addReversePropagation(nconf);

					nconf.has_contributed = true;
				}
				State new_state = getState(conf_set_factory.getCore());

				// The state "new_state" is reached from the state "state" by a shift action
				// on the symbol "marked_symbol"
				state.actions.add(new Action.Shift(marked_symbol, new_state));
			}
		}
	}

	State next;

	int id;
	Configuration.Set conf_set;
	Action.List actions;
	
	Action.List terminal_lookahead_actions;
	Action.List nonterminal_lookahead_actions;
	Action default_action;

	State(int num, Configuration.Set core)
	{
		id = num;
		conf_set = core;
		actions = new Action.List(this);
		terminal_lookahead_actions = new Action.List(this);
		nonterminal_lookahead_actions = new Action.List(this);
	}
	
	boolean findLookaheads()
	{
		boolean more_found = false;
		for (Configuration conf = conf_set.first_conf; conf != null; conf = conf.next)
		{
			if (!conf.has_contributed)
			{
				if (conf.findLookaheads())
				{
					more_found = true;
				}
				conf.has_contributed = true;
			}
		}
		return more_found;
	}
	
	void splitActions()
	{
		default_action = actions.split(terminal_lookahead_actions, nonterminal_lookahead_actions);
	}
}
