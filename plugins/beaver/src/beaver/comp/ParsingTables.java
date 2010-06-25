/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * This file is part of Beaver Parser Generator.                       *
 * Copyright (C) 2003,2004 Alexander Demenchuk <alder@softanvil.com>.  *
 * All rights reserved.                                                *
 * See the file "LICENSE" for the terms and conditions for copying,    *
 * distribution and modification of Beaver.                            *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package beaver.comp;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import beaver.spec.Grammar;
import beaver.spec.GrammarSymbol;
import beaver.spec.Terminal;

/**
 * Action tables of the automaton.
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
class ParsingTables
{
	/** Start of the list of all states. */
	public final State first_state;

	/** Number of terminals in a grammar */
	final int n_term;

	/** Actions lookup "table" to be used with terminal lookahead symbols */
	short[] actions;

	/** A table with "actions" lookaheads. Is used to detect "collisions". */
	short[] lookaheads;

	/**
	 * For each state, the offset into "actions" table that is used to find action for a terminal that has been fetched
	 * from the scanner.
	 */
	int[] terminal_offsets;

	/**
	 * For each state, the offset into "actions" table that is used to find action for a nonterminal that has been
	 * created by a reduced production.
	 */
	int[] nonterminal_offsets;

	/** An index of the last action in the table, i.e. the rest (tail) of the table is unused. */
	int last_action_index;

	/** Default action for each state */
	short[] default_actions;
	
	/** Indicates whether tables are compressed - states may have default action */
	boolean compressed;

	ParsingTables(Grammar grammar, State first_state)
	{
		int num_states = countStates(first_state);

		this.first_state = first_state;
		this.n_term = grammar.terminals.length;

		default_actions = new short[num_states + 1];
		terminal_offsets = new int[num_states + 1];
		nonterminal_offsets = new int[num_states + 1];

		Arrays.fill(terminal_offsets, UNUSED_OFFSET);
		Arrays.fill(nonterminal_offsets, UNUSED_OFFSET);

		actions = new short[16384]; // compressed Java 1.2 grammar uses less than 10k
		lookaheads = new short[actions.length];

		Arrays.fill(lookaheads, (short) -1);

		ArrayList list_of_action_lists = new ArrayList(num_states * 2);
		for (State state = first_state; state != null; state = state.next)
		{
			if (state.default_action != null)
			{
				default_actions[state.id] = state.default_action.getId();
				compressed = true;
			}

			if (state.terminal_lookahead_actions.num_actions > 0)
			{
				list_of_action_lists.add(state.terminal_lookahead_actions);
			}

			if (state.nonterminal_lookahead_actions.num_actions > 0)
			{
				list_of_action_lists.add(state.nonterminal_lookahead_actions);
			}
		}
		Action.List[] action_lists = (Action.List[]) list_of_action_lists.toArray(new Action.List[list_of_action_lists.size()]);
		Arrays.sort(action_lists, Action.List.NUM_ACTIONS_CMP);

		renumberSymbols(grammar, action_lists);

		int start_index = 0;
		for (int i = 0; i < action_lists.length; i++)
		{
			Action.List list = action_lists[i];
			int offset = findOffset(list, start_index);
			if (list.first.lookahead instanceof Terminal)
			{
				if (terminal_offsets[list.state.id] != UNUSED_OFFSET)
					throw new IllegalStateException("terminal offset " + list.state.id + " is used");
				terminal_offsets[list.state.id] = offset;
			}
			else
			{
				if (nonterminal_offsets[list.state.id] != UNUSED_OFFSET)
					throw new IllegalStateException("nonterminal offset " + list.state.id + " is used");
				nonterminal_offsets[list.state.id] = offset;
			}
			last_action_index = Math.max(last_action_index, offset + list.last.lookahead.id);
			start_index = advanceStartIndex(start_index);
		}
	}

	private void renumberSymbols(Grammar grammar, Action.List[] action_lists)
	{
		for (int i = 0; i < action_lists.length; i++)
		{
			for (Action act = action_lists[i].first; act != null; act = act.next)
			{
				act.lookahead.nrefs++;
			}
		}
		Arrays.sort(grammar.terminals, 1, grammar.terminals.length, GrammarSymbol.NUMBER_OF_REFERENCES_COMPARATOR);
		Arrays.sort(grammar.nonterminals, GrammarSymbol.NUMBER_OF_REFERENCES_COMPARATOR);

		for (int i = 1 /* leave EOF alone */; i < grammar.terminals.length; i++)
		{
			grammar.terminals[i].id = (short) i;
		}
		for (int i = 0; i < grammar.nonterminals.length; i++)
		{
			grammar.nonterminals[i].id = (short) (i + grammar.terminals.length);
		}
		for (int i = 0; i < action_lists.length; i++)
		{
			action_lists[i].sort();
		}
	}

	private int advanceStartIndex(int start_index)
	{
		while (start_index < actions.length && actions[start_index] != 0)
			start_index++;
		return start_index;
	}

	private int findOffset(Action.List action_list, int start_index)
	{
		int min_lookahead_id = action_list.first.lookahead.id;
		int max_lookahead_id = action_list.last.lookahead.id;
		int range = max_lookahead_id - min_lookahead_id + 1;
		
		while (true) // typically loops once, but may do it several time if initial tables are too small and need expansion  
		{
			int last_index = actions.length - range;
	
			for (int index = start_index; index <= last_index; index++)
			{
				if (actions[index] != 0)
					continue;
	
				int offset = index - min_lookahead_id;
				if (tryInsertActions(action_list, offset))
				{
					insertActions(action_list, offset);
					return offset;
				}
			}
			
			if (actions.length >= 1024 * 1024) // the end of table grows has a very arbitrary limit - need to stop somewhere, and... 1M should be enough for everyone ;-)
				throw new IllegalStateException("cannot find place for some actions in parsing tables");

			actions = expand(actions);
			int len = lookaheads.length; 
			lookaheads = expand(lookaheads);
			Arrays.fill(lookaheads, len, lookaheads.length, (short) -1);
		}		
	}

	private void insertActions(Action.List action_list, int offset)
	{
		for (Action act = action_list.first; act != null; act = act.next)
		{
			int index = offset + act.lookahead.id;
			if (actions[index] != 0)
				throw new IllegalStateException("inserting action in occupied slot");
			actions[index] = act.getId();
		}
	}

	private boolean tryInsertActions(Action.List action_list, int offset)
	{
		if (canInsertActions(action_list, offset))
		{
			insertLookaheads(action_list, offset);

			if (action_list.first.lookahead.id >= n_term || !hasCollisions())
				return true;

			removeLookaheads(action_list, offset);
		}
		return false;
	}

	private boolean canInsertActions(Action.List action_list, int offset)
	{
		for (Action act = action_list.first; act != null; act = act.next)
		{
			if (actions[offset + act.lookahead.id] != 0)
				return false;
		}
		return true;
	}

	private void insertLookaheads(Action.List action_list, int offset)
	{
		for (Action act = action_list.first; act != null; act = act.next)
		{
			int index = offset + act.lookahead.id;
			if (lookaheads[index] >= 0)
				throw new IllegalStateException("lookahead collision during initial insert");
			lookaheads[index] = act.lookahead.id;
		}
	}

	private void removeLookaheads(Action.List action_list, int offset)
	{
		for (Action act = action_list.first; act != null; act = act.next)
		{
			lookaheads[offset + act.lookahead.id] = -1;
		}
	}

	private boolean hasCollisions()
	{
		for (State state = first_state; state != null; state = state.next)
		{
			int offset = terminal_offsets[state.id];
			if (offset == UNUSED_OFFSET)
				continue;

			Action act = state.terminal_lookahead_actions.first;
			for (int la = 0; la < n_term; la++)
			{
				if (act != null && act.lookahead.id == la)
				{
					act = act.next;
					continue;
				}
				int index = offset + la;
				if (0 <= index && index < lookaheads.length && lookaheads[index] == la)
					return true;
			}
		}
		return false;
	}

	void writeTo(DataOutputStream data_stream) throws IOException
	{
		int len;
		
		data_stream.writeInt(len = last_action_index + 1);
		for (int i = 0; i < len; i++)
		{
			data_stream.writeShort(actions[i]);
		}
		for (int i = 0; i < len; i++)
		{
			data_stream.writeShort(lookaheads[i]);
		}
		
		data_stream.writeInt(len = terminal_offsets.length);
		for (int i = 0; i < len; i++)
		{
			data_stream.writeInt(terminal_offsets[i]);
		}
		for (int i = 0; i < len; i++)
		{
			data_stream.writeInt(nonterminal_offsets[i]);
		}
		
		data_stream.writeInt(compressed ? len : (len = 0));
		for (int i = 0; i < len; i++)
		{
			data_stream.writeShort(default_actions[i]);
		}
	}
	
	static final int UNUSED_OFFSET = Integer.MIN_VALUE;
	
	static int countStates(State state)
	{
		while (state.next != null)
			state = state.next;
		return state.id;
	}
	
	static short[] expand(short[] array)
	{
		short[] temp = new short[array.length * 2];
		System.arraycopy(array, 0, temp, 0, array.length);
		return temp;
	}
}