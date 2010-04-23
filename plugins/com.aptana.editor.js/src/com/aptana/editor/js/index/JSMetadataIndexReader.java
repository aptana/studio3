package com.aptana.editor.js.index;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

import com.aptana.editor.js.model.FunctionElement;
import com.aptana.editor.js.model.PropertyElement;
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
				String[] columns = types.get(0).getWord().split(IndexConstants.DELIMITER);
				String retrievedName = columns[0];
				
				result = new TypeElement();
				result.setName(retrievedName);

				// create pattern for doing member queries
				String memberPattern = MessageFormat.format(
					"^[^{0}]+{0}{1}(?:{0}|$)",
					new Object[] { IndexConstants.DELIMITER, typeName }
				);
				
				// read properties
				List<QueryResult> properties = index.query(new String[] { IndexConstants.PROPERTY }, memberPattern, SearchPattern.REGEX_MATCH);
				
				if (properties != null)
				{
					for (QueryResult property : properties)
					{
						String[] propertyColumns = property.getWord().split(IndexConstants.DELIMITER);
						String propertyName = propertyColumns[0];
						
						PropertyElement p = new PropertyElement();
						p.setName(propertyName);
						
						result.addProperty(p);
					}
				}
				
				// read methods
				List<QueryResult> methods = index.query(new String[] { IndexConstants.FUNCTION }, memberPattern, SearchPattern.REGEX_MATCH);
				
				if (methods != null)
				{
					for (QueryResult method : methods)
					{
						String[] methodColumns = method.getWord().split(IndexConstants.DELIMITER);
						String methodName = methodColumns[0];
						
						FunctionElement f = new FunctionElement();
						f.setName(methodName);
						
						result.addProperty(f);
					}
				}
				
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
