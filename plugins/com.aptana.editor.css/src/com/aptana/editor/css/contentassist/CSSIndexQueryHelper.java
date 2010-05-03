package com.aptana.editor.css.contentassist;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aptana.editor.css.contentassist.index.CSSIndexConstants;
import com.aptana.editor.css.contentassist.index.CSSIndexReader;
import com.aptana.editor.css.contentassist.model.ElementElement;
import com.aptana.editor.css.contentassist.model.PropertyElement;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;
import com.aptana.index.core.QueryResult;
import com.aptana.index.core.SearchPattern;

public class CSSIndexQueryHelper
{
	private CSSIndexReader _reader;

	/**
	 * CSSContentAssistHelper
	 */
	public CSSIndexQueryHelper()
	{
		this._reader = new CSSIndexReader();
	}

	/**
	 * getClasses
	 * 
	 * @return
	 */
	public Map<String, String> getClasses(Index index)
	{
		return this.getValues(index, CSSIndexConstants.CLASS);
	}

	/**
	 * getElements
	 * 
	 * @return
	 */
	public List<ElementElement> getElements()
	{
		List<ElementElement> result = null;

		try
		{
			result = this._reader.getElements(this.getIndex());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}

		return result;
	}

	/**
	 * getIDs
	 * 
	 * @param index
	 * @return
	 */
	public Map<String, String> getIDs(Index index)
	{
		return this.getValues(index, CSSIndexConstants.IDENTIFIER);
	}

	/**
	 * getIndex
	 * 
	 * @return
	 */
	private Index getIndex()
	{
		return IndexManager.getInstance().getIndex(CSSIndexConstants.METADATA);
	}

	/**
	 * getProperties
	 * 
	 * @return
	 */
	public List<PropertyElement> getProperties()
	{
		List<PropertyElement> result = null;

		try
		{
			result = this._reader.getProperties(this.getIndex());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (Throwable t)
		{
			t.printStackTrace();
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
			String pattern = "*";

			try
			{
				List<QueryResult> items = index.query(new String[] { category }, pattern, SearchPattern.PATTERN_MATCH);

				if (items != null && items.size() > 0)
				{
					result = new HashMap<String, String>();

					for (QueryResult item : items)
					{
						String[] paths = item.getDocuments();
						String path = (paths != null && paths.length > 0) ? paths[0] : "";

						result.put(item.getWord(), path);
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
