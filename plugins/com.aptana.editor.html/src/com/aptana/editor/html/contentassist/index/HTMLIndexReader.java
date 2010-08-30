package com.aptana.editor.html.contentassist.index;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.aptana.editor.css.contentassist.index.CSSIndexConstants;
import com.aptana.editor.html.contentassist.model.ElementElement;
import com.aptana.editor.html.contentassist.model.UserAgentElement;
import com.aptana.index.core.Index;
import com.aptana.index.core.QueryResult;
import com.aptana.index.core.SearchPattern;

public class HTMLIndexReader
{
	/**
	 * createElement
	 * 
	 * @param key
	 * @return
	 * @throws IOException
	 */
	private ElementElement createElementFromKey(Index index, String key) throws IOException
	{
		String[] columns = key.split(HTMLIndexConstants.DELIMITER);
		ElementElement element = new ElementElement();
		int column = 0;

		element.setName(columns[column++]);
		element.setDisplayName(columns[column++]);
		element.setRelatedClass(columns[column++]);

		for (String attribute : columns[column++].split(HTMLIndexConstants.SUB_DELIMITER))
		{
			element.addAttribute(attribute);
		}

		for (String userAgentKey : columns[column++].split(HTMLIndexConstants.SUB_DELIMITER))
		{
			element.addUserAgent(this.getUserAgent(index, userAgentKey));
		}

		element.setDeprecated(columns[column++]);
		element.setDescription(columns[column++]);

		for (String event : columns[column++].split(HTMLIndexConstants.SUB_DELIMITER))
		{
			element.addEvent(event);
		}

		element.setExample(columns[column++]);

		for (String reference : columns[column++].split(HTMLIndexConstants.SUB_DELIMITER))
		{
			element.addReference(reference);
		}

		element.setRemark(columns[column++]);

		return element;
	}

	/**
	 * getElement
	 * 
	 * @param index
	 * @param name
	 * @return
	 * @throws IOException
	 */
	public ElementElement getElement(Index index, String name) throws IOException
	{
		String searchKey = name + CSSIndexConstants.DELIMITER;
		List<QueryResult> items = index.query(new String[] { HTMLIndexConstants.ELEMENT }, searchKey,
				SearchPattern.PREFIX_MATCH);
		ElementElement result = null;

		if (items != null)
		{
			for (QueryResult item : items)
			{
				String key = item.getWord();

				result = this.createElementFromKey(index, key);

				break;
			}
		}

		return result;
	}

	/**
	 * getElements
	 * 
	 * @return
	 * @throws IOException
	 */
	public List<ElementElement> getElements(Index index) throws IOException
	{
		List<QueryResult> items = index.query(new String[] { HTMLIndexConstants.ELEMENT },
				"*", SearchPattern.PATTERN_MATCH); //$NON-NLS-1$
		List<ElementElement> result = new LinkedList<ElementElement>();

		if (items != null)
		{
			for (QueryResult item : items)
			{
				String key = item.getWord();
				ElementElement element = this.createElementFromKey(index, key);

				result.add(element);
			}
		}

		return result;
	}

	/**
	 * getUserAgent
	 * 
	 * @param userAgentKey
	 * @return
	 * @throws IOException
	 */
	protected UserAgentElement getUserAgent(Index index, String userAgentKey) throws IOException
	{
		String searchKey = userAgentKey + HTMLIndexConstants.DELIMITER;
		List<QueryResult> items = index.query(new String[] { HTMLIndexConstants.USER_AGENT }, searchKey,
				SearchPattern.PREFIX_MATCH);
		UserAgentElement result = null;

		if (items != null && items.size() > 0)
		{
			String key = items.get(0).getWord();
			String[] columns = key.split(HTMLIndexConstants.DELIMITER);
			int column = 1; // skip index

			result = new UserAgentElement();
			result.setPlatform(columns[column++]);

			// NOTE: split does not return a final empty element if the string being split
			// ends with the delimiter.
			if (column < columns.length)
			{
				result.setVersion(columns[column++]);
			}
		}

		return result;
	}

	/**
	 * getValues
	 * 
	 * @return
	 */
	public Map<String, String> getValues(Index index, String category)
	{
		Map<String, String> result = null;

		if (index != null)
		{
			String pattern = "*"; //$NON-NLS-1$

			try
			{
				List<QueryResult> items = index.query(new String[] { category }, pattern, SearchPattern.PATTERN_MATCH);

				if (items != null && items.size() > 0)
				{
					result = new HashMap<String, String>();

					for (QueryResult item : items)
					{
						String[] paths = item.getDocuments();
						String path = (paths != null && paths.length > 0) ? paths[0] : ""; //$NON-NLS-1$
						try
						{
							URI uri = index.getRelativeDocumentPath(new URI(path));
							result.put(item.getWord(), uri.toString());
						}
						catch (URISyntaxException e)
						{
							result.put(item.getWord(), path);
						}
					}
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		return result;
	}
}
