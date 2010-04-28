package com.aptana.editor.css.contentassist.index;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.aptana.editor.css.contentassist.model.ElementElement;
import com.aptana.index.core.Index;
import com.aptana.index.core.QueryResult;
import com.aptana.index.core.SearchPattern;

public class CSSIndexReader
{
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
				
				element.setName(columns[0]);
				element.setDisplayName(columns[1]);
				element.setDescription(columns[2]);
				element.setExample(columns[3]);
				element.setRemark(columns[4]);
				
				result.add(element);
			}
		}
		
		return result;
	}
}
