package com.aptana.explorer.internal.ui;

import java.io.IOException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.ui.internal.misc.StringMatcher;

import com.aptana.editor.html.contentassist.index.HTMLIndexConstants;
import com.aptana.editor.ruby.index.IRubyIndexConstants;
import com.aptana.explorer.ExplorerPlugin;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;
import com.aptana.index.core.QueryResult;

@SuppressWarnings("restriction")
class PathFilter extends ViewerFilter
{

	private static Object[] EMPTY = new Object[0];
	
	private StringMatcher singularMatcher;
	private StringMatcher pluralMatcher;
	
	/*
	 * Cache of filtered elements in the tree
	 */
	private Map<Object, Object[]> cache = new HashMap<Object, Object[]>();

	/*
	 * Maps parent elements to TRUE or FALSE
	 */
	private Map<Object, Boolean> foundAnyCache = new HashMap<Object, Boolean>();

	private List<QueryResult> queryResults;

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element)
	{
		IResource resource = null;
		if (element instanceof IResource)
		{
			resource = (IResource) element;
		}
		else if (element instanceof IAdaptable)
		{
			IAdaptable adaptable = (IAdaptable) element;
			resource = (IResource) adaptable.getAdapter(IResource.class);
		}
		if (resource != null)
		{
			return isEnclosed(viewer, resource);
		}
		// TODO Try matching on the label like PatternFilter does
		return true;
	}

	private boolean isEnclosed(Viewer viewer, IResource resource)
	{
		IPath path = resource.getProjectRelativePath();
		String rawPath = path.toPortableString();

		if (match(rawPath))
			return true;

		// Otherwise check if any of the words of the text matches
		String[] words = getWords(rawPath);
		for (int i = 0; i < words.length; i++)
		{
			String word = words[i];
			if (match(word))
			{
				return true;
			}
		}

		if (resource instanceof IContainer)
		{
			IContainer container = (IContainer) resource;
			try
			{
				Object[] visible = filter(viewer, container, container.members());
				if (visible != null && visible.length > 0)
					return true;
			}
			catch (CoreException e)
			{
				ExplorerPlugin.logError(e);
			}
		}

		// check if the resource is included by the filtered file or vise versa
		if (queryResults == null)
		{
			// FIXME We should have a search API layer over the top of this and shouldn't be hitting indices directly. Pass a scope object to the search API and it calculates what indices to search within!
			queryResults = new ArrayList<QueryResult>();
			Index index = IndexManager.getInstance().getIndex(resource.getProject().getLocationURI());
			try
			{
				queryResults = index.query(new String[] { HTMLIndexConstants.RESOURCE_CSS,
						HTMLIndexConstants.RESOURCE_JS, IRubyIndexConstants.REQUIRE }, null, 0);
			}
			catch (IOException e)
			{
				return false;
			}
		}
		if (queryResults != null)
		{
			for (QueryResult result : queryResults)
			{
				String[] documents = result.getDocuments();
				for (String document : documents)
				{
					if ((match(document) && resource.getLocationURI().toString().equals(result.getWord()))
							|| (match(result.getWord()) && resource.getLocation().toPortableString().equals(document)))
					{
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Answers whether the given String matches the pattern.
	 * 
	 * @param string
	 *            the String to test
	 * @return whether the string matches the pattern
	 */
	private boolean match(String string)
	{
		if (singularMatcher == null)
		{
			return true;
		}
		return singularMatcher.match(string) || (pluralMatcher != null && pluralMatcher.match(string));
	}

	/**
	 * The pattern string for which this filter should select elements in the viewer.
	 * 
	 * @param patternString
	 */
	public void setPattern(String patternString)
	{
		clearCaches();
		if (patternString == null || patternString.equals("")) { //$NON-NLS-1$
			singularMatcher = null;
			pluralMatcher = null;
		}
		else
		{
			singularMatcher = new StringMatcher("*" + patternString + "*", true, false); //$NON-NLS-1$ //$NON-NLS-2$
			if (patternString.contains("?") || patternString.contains("*")) //$NON-NLS-1$ //$NON-NLS-2$
				return;
			// TODO Generate a matcher for the singular and plural forms of the pattern unless singular is prefix of
			// plural!
			// i.e. people/person vs user/users
			// For now let's assume user always inputs singular and we always generate singular.
			pluralMatcher = new StringMatcher("*" + Inflector.pluralize(patternString) + "*", true, false); //$NON-NLS-1$ //$NON-NLS-2$
		}
		if (queryResults != null)
		{
			queryResults.clear();
			queryResults = null;
		}
	}

	@Override
	public Object[] filter(Viewer viewer, Object parent, Object[] elements)
	{
		Object[] filtered = cache.get(parent);
		if (filtered == null)
		{
			Boolean foundAny = foundAnyCache.get(parent);
			if (foundAny != null && !foundAny.booleanValue())
			{
				filtered = EMPTY;
			}
			else
			{
				filtered = super.filter(viewer, parent, elements);
			}
			cache.put(parent, filtered);
		}
		return filtered;
	}

	/**
	 * Clears the caches used for optimizing this filter. Needs to be called whenever the tree content changes.
	 */
	private void clearCaches()
	{
		cache.clear();
		foundAnyCache.clear();
	}

	/**
	 * Take the given filter text and break it down into words using a BreakIterator.
	 * 
	 * @param text
	 * @return an array of words
	 */
	private String[] getWords(String text)
	{
		List<String> words = new ArrayList<String>();
		// Break the text up into words, separating based on whitespace and
		// common punctuation.
		// Previously used String.split(..., "\\W"), where "\W" is a regular
		// expression (see the Javadoc for class Pattern).
		// Need to avoid both String.split and regular expressions, in order to
		// compile against JCL Foundation (bug 80053).
		// Also need to do this in an NL-sensitive way. The use of BreakIterator
		// was suggested in bug 90579.
		BreakIterator iter = BreakIterator.getWordInstance();
		iter.setText(text);
		int i = iter.first();
		while (i != java.text.BreakIterator.DONE && i < text.length())
		{
			int j = iter.following(i);
			if (j == java.text.BreakIterator.DONE)
			{
				j = text.length();
			}
			// match the word
			if (Character.isLetterOrDigit(text.charAt(i)))
			{
				String word = text.substring(i, j);
				words.add(word);
			}
			i = j;
		}
		return words.toArray(new String[words.size()]);
	}
}
