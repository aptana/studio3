package com.aptana.editor.html.contentassist.index;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.aptana.editor.html.contentassist.model.ElementElement;
import com.aptana.index.core.Index;
import com.aptana.index.core.QueryResult;
import com.aptana.index.core.SearchPattern;

public class HTMLIndexReader
{
	/**
	 * getElements
	 * 
	 * @return
	 * @throws IOException 
	 */
	public List<ElementElement> getElements(Index index) throws IOException
	{
		List<QueryResult> items = index.query(new String[] { HTMLIndexConstants.ELEMENT }, "*", SearchPattern.PATTERN_MATCH);
		List<ElementElement> result = new LinkedList<ElementElement>();
		
		if (items != null)
		{
			for (QueryResult queryResult : items)
			{
				String key = queryResult.getWord();
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

				result.add(element);
			}
		}
		
		return result;
	}
}
