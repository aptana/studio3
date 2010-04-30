package com.aptana.editor.css.contentassist.index;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.aptana.editor.css.contentassist.model.ElementElement;
import com.aptana.editor.css.contentassist.model.PropertyElement;
import com.aptana.editor.css.contentassist.model.UserAgentElement;
import com.aptana.index.core.Index;
import com.aptana.index.core.QueryResult;
import com.aptana.index.core.SearchPattern;

public class CSSIndexReader
{
	/**
	 * CSSIndexReader
	 */
	public CSSIndexReader()
	{
	}
	
	/**
	 * getElements
	 * 
	 * @param index
	 * @return
	 * @throws IOException
	 */
	public List<ElementElement> getElements(Index index) throws IOException
	{
		List<QueryResult> items = index.query(new String[] { CSSIndexConstants.ELEMENT }, "*", SearchPattern.PATTERN_MATCH);
		List<ElementElement> result = new LinkedList<ElementElement>();

		if (items != null)
		{
			for (QueryResult queryResult : items)
			{
				String key = queryResult.getWord();
				String[] columns = key.split(CSSIndexConstants.DELIMITER);
				ElementElement element = new ElementElement();
				int column = 0;

				element.setName(columns[column++]);
				element.setDisplayName(columns[column++]);

				for (String userAgentKey : columns[column++].split(CSSIndexConstants.SUB_DELIMITER))
				{
					// get user agent and add to element
					element.addUserAgent(this.getUserAgent(index, userAgentKey));
				}

				element.setDescription(columns[column++]);
				element.setExample(columns[column++]);

				for (String property : columns[column++].split(CSSIndexConstants.SUB_DELIMITER))
				{
					element.addProperty(property);
				}

				element.setRemark(columns[column++]);

				result.add(element);
			}
		}

		return result;
	}

	/**
	 * getProperties
	 * 
	 * @param index
	 * @return
	 * @throws IOException 
	 */
	public List<PropertyElement> getProperties(Index index) throws IOException
	{
		List<QueryResult> items = index.query(new String[] { CSSIndexConstants.PROPERTY }, "*", SearchPattern.PATTERN_MATCH);
		List<PropertyElement> result = new LinkedList<PropertyElement>();
		
		if (items != null)
		{
			for (QueryResult queryResult : items)
			{
				String key = queryResult.getWord();
				String columns[] = key.split(CSSIndexConstants.DELIMITER);
				int column = 0;
				PropertyElement property = new PropertyElement();
				
				property.setName(columns[column++]);
				property.setAllowMultipleValues(Boolean.valueOf(columns[column++]));
				property.setType(columns[column++]);
				// TODO: specifications
				
				for (String userAgentKey : columns[column++].split(CSSIndexConstants.SUB_DELIMITER))
				{
					// get user agent and add to element
					property.addUserAgent(this.getUserAgent(index, userAgentKey));
				}
				
				property.setDescription(columns[column++]);
				property.setExample(columns[column++]);
				property.setHint(columns[column++]);
				property.setRemark(columns[column++]);
				// TODO: values
				
				result.add(property);
			}
		}
		
		return result;
	}
	
	/**
	 * getProperties
	 * 
	 * @param index
	 * @param names
	 * @return
	 */
	public List<PropertyElement> getProperties(Index index, String[] names)
	{
		List<PropertyElement> result = new LinkedList<PropertyElement>();
		
		// TODO
		
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
		String searchKey = userAgentKey + CSSIndexConstants.DELIMITER;
		List<QueryResult> items = index.query(new String[] { CSSIndexConstants.USER_AGENT }, searchKey, SearchPattern.PREFIX_MATCH);
		UserAgentElement result = null;
		
		if (items != null && items.size() > 0)
		{
			String key = items.get(0).getWord();
			String[] columns = key.split(CSSIndexConstants.DELIMITER);
			int column = 1; // skip index
			
			result = new UserAgentElement();
			result.setDescription(columns[column++]);
			result.setOS(columns[column++]);
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
}
