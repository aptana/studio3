package com.aptana.editor.html.contentassist;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;

import com.aptana.editor.css.contentassist.index.CSSIndexConstants;
import com.aptana.editor.html.Activator;
import com.aptana.editor.html.contentassist.index.HTMLIndexReader;
import com.aptana.editor.html.contentassist.index.HTMLMetadataReader;
import com.aptana.editor.html.contentassist.model.ElementElement;
import com.aptana.editor.html.contentassist.model.EntityElement;
import com.aptana.index.core.Index;

public class HTMLIndexQueryHelper
{
	private HTMLIndexReader _reader;
	private HTMLMetadataReader _metadata;
	
	/**
	 * HTMLContentAssistHelper
	 */
	public HTMLIndexQueryHelper()
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
	 * getElement
	 * 
	 * @param name
	 * @return
	 */
	public ElementElement getElement(String name)
	{
		ElementElement result = null;
		
		if (name != null && name.length() > 0)
		{
			// TODO: optimize with a name->element hash
			for (ElementElement element : this.getElements())
			{
				if (name.equals(element.getName()))
				{
					result = element;
					break;
				}
			}
		}
		
		return result;
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
	
//	/**
//	 * getIndex
//	 * 
//	 * @return
//	 */
//	private Index getIndex()
//	{
//		return IndexManager.getInstance().getIndex(HTMLIndexConstants.METADATA);
//	}
	
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
	 * getEntities
	 * 
	 * @return
	 */
	public List<EntityElement> getEntities()
	{
		return this.getMetadata().getEntities();
	}
	
	/**
	 * getMetadata
	 * 
	 * @return
	 */
	private HTMLMetadataReader getMetadata()
	{
		if (this._metadata == null)
		{
			this._metadata = new HTMLMetadataReader();
			String[] resources = this.getMetadataResources();
			
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
	
	/**
	 * getMetadataResources
	 * 
	 * @return
	 */
	protected String[] getMetadataResources()
	{
		return new String[] { "/metadata/html_metadata.xml" };
	}
	
	/**
	 * getReader
	 * 
	 * @return
	 */
	protected HTMLIndexReader getReader()
	{
		if (this._reader == null)
		{
			this._reader = new HTMLIndexReader();
		}
		
		return this._reader;
	}
}
