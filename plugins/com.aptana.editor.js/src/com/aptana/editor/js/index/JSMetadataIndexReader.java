package com.aptana.editor.js.index;

import java.io.IOException;
import java.util.List;

import com.aptana.editor.js.model.TypeElement;
import com.aptana.index.core.Index;
import com.aptana.index.core.QueryResult;
import com.aptana.index.core.SearchPattern;

public class JSMetadataIndexReader
{
	public TypeElement readType(Index index, String typeName)
	{
		TypeElement result = null;
		
		try
		{
			String pattern = typeName + IndexConstants.DELIMITER;
			List<QueryResult> types = index.query(new String[] { IndexConstants.TYPE }, pattern, SearchPattern.PREFIX_MATCH);
			
			if (types != null && types.size() > 0)
			{
				String value = types.get(0).getWord();
				String retrievedName = value.substring(0, value.indexOf(IndexConstants.DELIMITER));
				
				result = new TypeElement();
				result.setName(retrievedName);
				
				// read properties
				
				// read methods
				
				// read description
				
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return result;
	}
}
