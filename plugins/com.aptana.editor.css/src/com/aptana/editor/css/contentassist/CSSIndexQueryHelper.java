package com.aptana.editor.css.contentassist;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;

import com.aptana.editor.css.Activator;
import com.aptana.editor.css.contentassist.index.CSSIndexConstants;
import com.aptana.editor.css.contentassist.index.CSSIndexReader;
import com.aptana.editor.css.contentassist.index.CSSMetadataReader;
import com.aptana.editor.css.contentassist.model.ElementElement;
import com.aptana.editor.css.contentassist.model.PropertyElement;
import com.aptana.index.core.Index;

public class CSSIndexQueryHelper
{
	private CSSIndexReader _reader;
	private CSSMetadataReader _metadata;

	/**
	 * CSSContentAssistHelper
	 */
	public CSSIndexQueryHelper()
	{
	}

	/**
	 * getClasses
	 * 
	 * @return
	 */
	public Map<String, String> getClasses(Index index)
	{
		return this.getReader().getValues(index, CSSIndexConstants.CLASS);
	}

	/**
	 * getElements
	 * 
	 * @return
	 */
	public List<ElementElement> getElements()
	{
		return this.getMetadata().getElements();
	}

	/**
	 * getIDs
	 * 
	 * @param index
	 * @return
	 */
	public Map<String, String> getIDs(Index index)
	{
		return this.getReader().getValues(index, CSSIndexConstants.IDENTIFIER);
	}

	/**
	 * getMetadata
	 */
	private CSSMetadataReader getMetadata()
	{
		if (this._metadata == null)
		{
			this._metadata = new CSSMetadataReader();
			String[] resources = new String[] { "/metadata/css_metadata.xml" };

			for (String resource : resources)
			{
				URL url = FileLocator.find(Activator.getDefault().getBundle(), new Path(resource), null);

				if (url != null)
				{
					InputStream stream = null;

					try
					{
						stream = url.openStream();

						this._metadata.loadXML(stream);
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
					catch (Throwable t)
					{
						t.printStackTrace();
					}
					finally
					{
						if (stream != null)
						{
							try
							{
								stream.close();
							}
							catch (IOException e)
							{
							}
						}
					}
				}
			}
		}

		return this._metadata;
	}

	// /**
	// * getIndex
	// *
	// * @return
	// */
	// private Index getIndex()
	// {
	// return IndexManager.getInstance().getIndex(CSSIndexConstants.METADATA);
	// }

	/**
	 * getProperties
	 * 
	 * @return
	 */
	public List<PropertyElement> getProperties()
	{
		return this.getMetadata().getProperties();
	}

	/**
	 * getProperty
	 * 
	 * @return
	 */
	public PropertyElement getProperty(String name)
	{
		PropertyElement result = null;

		if (name != null && name.length() > 0)
		{
			// TODO: optimize with name->property hash
			for (PropertyElement property : this.getProperties())
			{
				if (name.equals(property.getName()))
				{
					result = property;
					break;
				}
			}
		}

		return result;
	}

	/**
	 * getReader
	 * 
	 * @return
	 */
	private CSSIndexReader getReader()
	{
		if (this._reader == null)
		{
			this._reader = new CSSIndexReader();
		}

		return this._reader;
	}
}
