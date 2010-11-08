/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.explorer.ui.filter;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.aptana.core.util.URLEncoder;
import com.aptana.editor.html.contentassist.index.HTMLIndexConstants;
import com.aptana.editor.ruby.index.IRubyIndexConstants;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;
import com.aptana.index.core.QueryResult;

public class PathFilter extends AbstractResourceBasedViewerFilter
{

	private static Object[] EMPTY = new Object[0];

	/*
	 * Cache of filtered elements in the tree
	 */
	private Map<Object, Object[]> cache = new HashMap<Object, Object[]>();

	/*
	 * Maps parent elements to TRUE or FALSE
	 */
	private Map<Object, Boolean> foundAnyCache = new HashMap<Object, Boolean>();

	private Map<Object, Boolean> leafCache = new HashMap<Object, Boolean>();

	private List<QueryResult> queryResults;
	private IResource filterResource;
	protected Pattern regexp;

	private String fFilterResourceURI;

	protected String patternString;

	/**
	 * Answers whether the given element in the given viewer matches the filter pattern. This is a default
	 * implementation that will show a leaf element in the tree based on whether the provided filter text matches the
	 * text of the given element's text, or that of it's children (if the element has any). Subclasses may override this
	 * method.
	 * 
	 * @param viewer
	 *            the tree viewer in which the element resides
	 * @param element
	 *            the element in the tree to check for a match
	 * @return true if the element matches the filter pattern
	 */
	protected boolean isElementVisible(Viewer viewer, Object element)
	{
		return isParentMatch(viewer, element) || isLeafMatch(viewer, element);
	}

	/**
	 * Returns true if any of the elements makes it through the filter. This method uses caching if enabled; the
	 * computation is done in computeAnyVisible.
	 * 
	 * @param viewer
	 * @param parent
	 * @param elements
	 *            the elements (must not be an empty array)
	 * @return true if any of the elements makes it through the filter.
	 */
	private boolean isAnyVisible(Viewer viewer, Object parent, Object[] elements)
	{
		Object[] filtered = cache.get(parent);
		if (filtered != null)
		{
			return filtered.length > 0;
		}
		Boolean foundAny = foundAnyCache.get(parent);
		if (foundAny == null)
		{
			foundAny = computeAnyVisible(viewer, elements) ? Boolean.TRUE : Boolean.FALSE;
			foundAnyCache.put(parent, foundAny);
		}
		return foundAny.booleanValue();
	}

	/**
	 * Check if the parent (category) is a match to the filter text. The default behavior returns true if the element
	 * has at least one child element that is a match with the filter text. Subclasses may override this method.
	 * 
	 * @param viewer
	 *            the viewer that contains the element
	 * @param element
	 *            the tree element to check
	 * @return true if the given element has children that matches the filter text
	 */
	private boolean isParentMatch(Viewer viewer, Object element)
	{
		// TODO Also check if name matches the text matchers!

		IResource resource = (IResource) element;
		if (wordMatches(resource.getName()))
		{
			return true;
		}

		Object[] children = ((ITreeContentProvider) ((AbstractTreeViewer) viewer).getContentProvider())
				.getChildren(element);

		if ((children != null) && (children.length > 0))
		{
			return isAnyVisible(viewer, element, children);
		}
		return false;
	}

	/**
	 * Returns true if any of the elements makes it through the filter.
	 * 
	 * @param viewer
	 *            the viewer
	 * @param elements
	 *            the elements to test
	 * @return <code>true</code> if any of the elements makes it through the filter
	 */
	private boolean computeAnyVisible(Viewer viewer, Object[] elements)
	{
		boolean elementFound = false;
		for (int i = 0; i < elements.length && !elementFound; i++)
		{
			Object element = elements[i];
			elementFound = isElementVisible(viewer, element);
		}
		return elementFound;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object,
	 * java.lang.Object)
	 */
	public final boolean select(Viewer viewer, Object parentElement, Object element)
	{
		return isElementVisible(viewer, element);
	}

	/**
	 * Check if the current (leaf) element is a match with the filter text. The default behavior checks that the label
	 * of the element is a match. Subclasses should override this method.
	 * 
	 * @param viewer
	 *            the viewer that contains the element
	 * @param element
	 *            the tree element to check
	 * @return true if the given element's label matches the filter text
	 */
	private boolean isLeafMatch(Viewer viewer, Object element)
	{
		Boolean result = leafCache.get(element);
		if (result == null)
		{
			result = doIsLeafMatch(viewer, element);
			leafCache.put(element, result);
		}
		return result;
	}

	private boolean doIsLeafMatch(Viewer viewer, Object element)
	{
		IResource resource = (IResource) element;
		if (resource.equals(filterResource))
		{
			return true;
		}

		IPath path = resource.getProjectRelativePath();
		String rawPath = path.toPortableString();

		// Check if any part of path matches regexp
		if (wordMatches(rawPath))
		{
			return true;
		}

		// check if the resource is included by the filtered file or vise versa
		if (queryResults == null)
		{
			// FIXME We should have a search API layer over the top of this and shouldn't be hitting indices directly.
			// Pass a scope object to the search API and it calculates what indices to search within!
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
			// Memoize the location/location URI because it's expensive to grab
			String resourceURI = null;
			String filterResourceLocation = filterResource.getLocation().toPortableString();
			String resourceLocation = resource.getLocation().toPortableString();
			for (QueryResult result : queryResults)
			{
				String includedFile = result.getWord();
				// check if the 'resource' includes 'filteredResource'
				if (includedFile.endsWith(URLEncoder.encode(filterResourceLocation, null, null)))
				{
					// OK, we've established that filteredResource is included by the result.documents
					// check if the documents contains 'resource'
					for (String document : result.getDocuments())
					{
						if (resourceURI == null)
						{
							resourceURI = resource.getLocationURI().toString();
						}
						if (document.equals(resourceURI))
						{
							return true;
						}
					}
				}
				else
				{
					// Check if the current 'resource' is included by 'filteredResource'
					// Something includes our current 'resource'
					if (includedFile.endsWith(URLEncoder.encode(resourceLocation, null, null)))
					{
						// See if one of the documents is the one we're filtering on, if so include this resource
						for (String document : result.getDocuments())
						{
							if (document.equals(getFilterResourceURI()))
							{
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	private String getFilterResourceURI()
	{
		if (fFilterResourceURI == null)
		{
			fFilterResourceURI = filterResource.getLocationURI().toString();
		}
		return fFilterResourceURI;
	}

	/**
	 * Return whether or not if any of the words in text satisfy the match critera.
	 * 
	 * @param text
	 *            the text to match
	 * @return boolean <code>true</code> if one of the words in text satisfies the match criteria.
	 */
	private boolean wordMatches(String text)
	{
		if (text == null)
		{
			return false;
		}

		return match(text);
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
		if (regexp == null)
		{
			return true;
		}
		return regexp.matcher(string).find();
	}

	/**
	 * The pattern string for which this filter should select elements in the viewer.
	 * 
	 * @param patternString
	 */
	protected void setPattern(String patternString)
	{
		this.patternString = patternString;
		if (patternString == null || patternString.equals("")) //$NON-NLS-1$
		{
			regexp = null;
		}
		else
		{
			regexp = Pattern.compile(MessageFormat.format("\\b({0})\\b", patternString)); //$NON-NLS-1$
		}
	}

	public void setResourceToFilterOn(IResource resource)
	{
		clearCaches();
		if (queryResults != null)
		{
			queryResults = null;
		}
		fFilterResourceURI = null;
		if (resource == null)
		{
			filterResource = null;
			setPattern(null);
			return;
		}
		this.filterResource = resource;
		setPattern(createPatternFromResource(resource));
	}

	protected String createPatternFromResource(IResource resource)
	{
		// TODO Strip off extension!
		return resource.getName();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerFilter#filter(org.eclipse.jface.viewers.Viewer, java.lang.Object,
	 * java.lang.Object[])
	 */
	public final Object[] filter(Viewer viewer, Object parent, Object[] elements)
	{
		// we don't want to optimize if we've extended the filter ... this
		// needs to be addressed in 3.4
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=186404
		if (regexp == null)
		{
			return elements;
		}

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
		else
		{
			// Sometimes the items in the cache are stale and it causes an ArrayIndexOutofBoundsException in
			// org.eclipse.jface.viewers.StructuredViewer.notifyFilteredOut(StructuredViewer.java:920)
			// Iterate over filtered and return the equivalent item in elements by testing equals to fix bug
			// mentioned above
			List<Object> copyOfCache = new ArrayList<Object>();
			for (Object cached : filtered)
			{
				for (Object element : elements)
				{
					if (cached.equals(element))
					{
						copyOfCache.add(element);
						break;
					}
				}
				if (copyOfCache.size() == filtered.length)
				{
					break;
				}
			}
			filtered = copyOfCache.toArray();
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
		leafCache.clear();
	}

	public String getPattern()
	{
		return this.patternString;
	}
}
