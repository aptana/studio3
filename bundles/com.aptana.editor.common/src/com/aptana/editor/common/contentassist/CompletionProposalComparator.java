/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.contentassist;

import java.util.Comparator;

import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.templates.TemplateProposal;

public enum CompletionProposalComparator implements Comparator<ICompletionProposal>
{
	RelevanceSort
	{
		public int compare(ICompletionProposal o1, ICompletionProposal o2)
		{
			// sort by "relevance," determined by the individual completion proposal processors
			if (o1 instanceof ICommonCompletionProposal && o2 instanceof ICommonCompletionProposal)
			{
				int o1Relevance = ((ICommonCompletionProposal) o1).getRelevance();
				int o2Relevance = ((ICommonCompletionProposal) o2).getRelevance();
				return Integer.valueOf(o1Relevance).compareTo(o2Relevance);
			}
			return 0;
		}
	},
	TemplateSort
	{
		public int compare(ICompletionProposal o1, ICompletionProposal o2)
		{
			// Templates appear after other items. Note reversal of terms
			if (o1 instanceof TemplateProposal && !(o2 instanceof TemplateProposal))
			{
				return -1;
			}
			else if (!(o1 instanceof TemplateProposal) && o2 instanceof TemplateProposal)
			{
				return 1;
			}
			else
			{
				return 0;
			}
		}
	},
	NameSort
	{
		// sort alphabetically, case sensitive, descending
		public int compare(ICompletionProposal o1, ICompletionProposal o2)
		{
			// note reversal of parameters

			int diff = o2.getDisplayString().compareToIgnoreCase(o1.getDisplayString());
			if (diff == 0)
			{
				diff = o1.getDisplayString().compareTo(o2.getDisplayString());
			}
			return diff;
		}
	};

	/**
	 * Sort items in descending order
	 * 
	 * @param other
	 * @return
	 */
	public static Comparator<ICompletionProposal> descending(final Comparator<ICompletionProposal> other)
	{
		return new Comparator<ICompletionProposal>()
		{
			public int compare(ICompletionProposal o1, ICompletionProposal o2)
			{
				// note reversal of parameters
				return other.compare(o2, o1);
			}
		};
	}

	/**
	 * Sort items in ascending order
	 * 
	 * @param other
	 * @return
	 */
	public static Comparator<ICompletionProposal> ascending(final Comparator<ICompletionProposal> other)
	{
		return new Comparator<ICompletionProposal>()
		{
			public int compare(ICompletionProposal o1, ICompletionProposal o2)
			{
				return other.compare(o1, o2);
			}
		};
	}

	/**
	 * Iterates though each of the comparison operators, returning the result if the items are not equal
	 * 
	 * @param multipleOptions
	 * @return
	 */
	public static Comparator<ICompletionProposal> getComparator(final CompletionProposalComparator... multipleOptions)
	{
		return new Comparator<ICompletionProposal>()
		{
			public int compare(ICompletionProposal o1, ICompletionProposal o2)
			{
				for (CompletionProposalComparator option : multipleOptions)
				{
					int result = option.compare(o1, o2);
					if (result != 0)
					{
						return result;
					}
				}
				return 0;
			}
		};
	}
}
