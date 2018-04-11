/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * This file is part of Beaver Parser Generator.                       *
 * Copyright (C) 2003,2004 Alexander Demenchuk <alder@softanvil.com>.  *
 * All rights reserved.                                                *
 * See the file "LICENSE" for the terms and conditions for copying,    *
 * distribution and modification of Beaver.                            *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package beaver.comp.util;

/**
 * A set for elements 0..N
 */
public class BitSet
{
	private int[] bit_bags;
	private boolean has_bits;

	public BitSet(int capacity_in_bits)
	{
		bit_bags = new int[(capacity_in_bits + 31) >> 5];
	}

	public BitSet()
	{
		this(256);
	}

	/**
	 * Adds a new element to the set.
	 *
	 * @param i element to add to the set
	 * @return true if element was added and false if it was already there
	 */
	public boolean add(int i)
	{
		int bag_index = i >> 5;
		ensureIndexWithinRange(bag_index);
		int bit_index = i & 31;
		int bit_mask = 1 << bit_index;
		boolean bit_not_set = (bit_bags[bag_index] & bit_mask) == 0;
		if (bit_not_set)
		{
			bit_bags[bag_index] |= bit_mask;
			has_bits = true;
		}
		return bit_not_set;
	}

	/**
	 * Adds every element of another set to this set.
	 *
	 * @param another_set set of elements to be added to this set
	 * @return true if this set has new bits added
	 */
	public boolean add(BitSet another_set)
	{
		boolean new_bits_added = false;
		if (another_set.has_bits)
		{
			int cmp_len = another_set.bit_bags.length;
			if (cmp_len > bit_bags.length)
				expandCapacity(cmp_len);
			for (int i = 0; i < cmp_len; i++)
			{
				int diff = another_set.bit_bags[i] & ~bit_bags[i];
				if (diff != 0)
				{
					bit_bags[i] |= diff;
					has_bits = new_bits_added = true;
				}
			}
		}
		return new_bits_added;
	}

	/**
	 * Checks whether the element is in the set
	 *
	 * @param i element to check
	 * @return true if the element is present in the set
	 */
	public boolean isSet(int i)
	{
		return has_bits && (bit_bags[i >> 5] & (1 << (i & 31))) != 0;
	}

	/**
	 * Checks whether the set has no set bits.
	 *
	 * @return true if all the bits of the set are cleared
	 */
	public boolean isEmpty()
	{
		return !has_bits;
	}

	/**
	 * An API that a "bit processor" has to implement.
	 */
	public static abstract class Processor
	{
		/**
		 * A callback. BitSet calls this method for each bit that meets a selection criteria.
		 *
		 * @param bit_index index of s set bit
		 */
		protected abstract void process(int bit_index);
	}

	/**
	 * Invokes a bit processor for each set bit in the set.
	 *
	 * @param proc an action implmentation to be called
	 */
	public void forEachElementRun(Processor proc)
	{
		if (has_bits)
		{
			for (int bag_index = 0; bag_index < bit_bags.length; bag_index++)
			{
				for (int bit_index = bag_index << 5, bag = bit_bags[bag_index]; bag != 0; bag >>>= 1, bit_index++)
				{
					if ((bag & 0x0001) == 0)
					{
						if ((bag & 0xFFFF) == 0)
						{
							bit_index += 16;
							bag >>>= 16;
						}
						if ((bag & 0x00FF) == 0)
						{
							bit_index += 8;
							bag >>>= 8;
						}
						if ((bag & 0x000F) == 0)
						{
							bit_index += 4;
							bag >>>= 4;
						}
						if ((bag & 0x0003) == 0)
						{
							bit_index += 2;
							bag >>>= 2;
						}
						if ((bag & 0x0001) == 0)
						{
							bit_index += 1;
							bag >>>= 1;
						}
					}
					proc.process(bit_index);
				}
			}
		}
	}

	/**
	 * Checks that a bag index points within the allocated array of bit bags.
	 *
	 * @param bag_index index of the bit bag
	 */
	private void ensureIndexWithinRange(int bag_index)
	{
		if (bag_index >= bit_bags.length)
		{
			if (bag_index > 0xFFFF)
				throw new IllegalArgumentException("huge bit sets (more than 2M bits) are not supported");
			// they can easily be supported (with one extra shift below) though, but in the context of
			// a parser generator it does not make a lot of sence

			// calculate a new size for the bit bags array as the next pow(2) after a bag_index
			int new_length = bag_index | bag_index >> 1;

			new_length |= new_length >> 2;
			new_length |= new_length >> 4;
			new_length |= new_length >> 8;

			expandCapacity(new_length + 1);
		}
	}

	/**
	 * Expands an array of bags to the new size.
	 *
	 * @param new_length new number of bit bags to use
	 */
	private void expandCapacity(int new_length)
	{
		int[] new_bags = new int[new_length];
		System.arraycopy(bit_bags, 0, new_bags, 0, bit_bags.length);
		bit_bags = new_bags;
	}
}
