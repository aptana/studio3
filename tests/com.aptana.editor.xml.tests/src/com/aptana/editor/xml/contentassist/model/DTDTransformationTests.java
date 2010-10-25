package com.aptana.editor.xml.contentassist.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.aptana.core.util.IOUtil;
import com.aptana.core.util.ResourceUtil;
import com.aptana.editor.xml.XMLPlugin;

public class DTDTransformationTests extends TestCase
{
	/**
	 * getContent
	 * 
	 * @param file
	 * @return
	 */
	protected String getContent(File file)
	{
		String result = "";

		try
		{
			FileInputStream input = new FileInputStream(file);

			result = IOUtil.read(input);
		}
		catch (IOException e)
		{
			fail(e.getMessage());
		}

		return result;
	}
	
	/**
	 * getContent
	 * 
	 * @param resource
	 * @return
	 */
	protected String getContent(String resource)
	{
		File file = this.getFile(new Path(resource));
		
		return this.getContent(file);
	}
	
	/**
	 * getFile
	 * 
	 * @param path
	 * @return
	 */
	protected File getFile(IPath path)
	{
		File result = null;

		try
		{
			URL url = FileLocator.find(XMLPlugin.getDefault().getBundle(), path, null);
			URL fileURL = FileLocator.toFileURL(url);
			URI fileURI = ResourceUtil.toURI(fileURL);

			result = new File(fileURI);
		}
		catch (IOException e)
		{
			fail(e.getMessage());
		}
		catch (URISyntaxException e)
		{
			fail(e.getMessage());
		}

		assertNotNull(result);
		assertTrue(result.exists());

		return result;
	}
	
	/**
	 * attributesTest
	 * 
	 * @param source
	 * @param elements
	 */
	protected void attributesTest(String source, String element, String... attributes)
	{
		DTDTransformer transformer = new DTDTransformer();
		
		transformer.transform(source);
		
		// find target element 
		ElementElement targetElement = null;
		
		for (ElementElement e : transformer.getElements())
		{
			if (e.getName().equals(element))
			{
				targetElement = e;
				break;
			}
		}
		
		assertNotNull(targetElement);
		//assertEquals(attributes.length, targetElement.getAttributes().size());
		
		// generate set of attribute name
		Set<String> names = new HashSet<String>();
		
		for (String name : targetElement.getAttributes())
		{
			names.add(name);
		}
		
		// assert
		for (String name : attributes)
		{
			assertTrue("Did not find attribute: " + name, names.contains(name));
		}
	}
	
	/**
	 * elementsTest
	 * 
	 * @param source
	 * @param elements
	 */
	protected void elementsTest(String source, String... elements)
	{
		DTDTransformer transformer = new DTDTransformer();
		
		transformer.transform(source);
		
		// gather element names for easy lookup
		Set<String> names = new HashSet<String>();
		
		for (ElementElement element : transformer.getElements())
		{
			names.add(element.getName());
		}
		
		// assert the element name list
		for (String name : elements)
		{
			assertTrue("Did not find element: " + name, names.contains(name));
		}
	}
	
	/**
	 * testSingleElement
	 */
	public void testSingleElement()
	{
		String source = this.getContent("DTD/singleElement.dtd");
		
		this.elementsTest(
			source,
			"svg"
		);
	}
	
	/**
	 * testMultipleElements
	 */
	public void testMultipleElements()
	{
		String source = this.getContent("DTD/multipleElements.dtd");
		
		this.elementsTest(
			source,
			"svg",
			"circle",
			"ellipse",
			"rectangle",
			"path"
		);
	}
	
	/**
	 * testSingleAttribute
	 */
	public void testSingleAttribute()
	{
		String source = this.getContent("DTD/elementAttribute.dtd");
		
		this.attributesTest(
			source,
			"svg",
			"x"
		);
	}
	
	/**
	 * testMultipleAttributes
	 */
	public void testMultipleAttributes()
	{
		String source = this.getContent("DTD/multipleElementAttributes.dtd");
		
		this.attributesTest(
			source,
			"svg",
			"x",
			"y",
			"width",
			"height"
		);
	}
	
	/**
	 * testSVGDTD
	 */
	public void testSVGDTD()
	{
		String source = this.getContent("DTD/svg11-flat.dtd");
		
		this.elementsTest(
			source,
			"svg"
		);
	}
}
