package com.aptana.editor.js.inferencing;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.aptana.core.util.IOUtil;
import com.aptana.core.util.ResourceUtil;
import com.aptana.editor.js.Activator;
import com.aptana.editor.js.contentassist.index.JSFileIndexingParticipant;
import com.aptana.editor.js.parsing.ast.JSNode;
import com.aptana.editor.js.parsing.ast.JSParseRootNode;
import com.aptana.index.core.Index;
import com.aptana.parsing.ast.IParseNode;

public class GeneralInferencingTests extends InferencingTestsBase
{
	public class Indexer extends JSFileIndexingParticipant
	{
		public void indexTree(Index index, JSParseRootNode root, URI location)
		{
			this.processParseResults(index, root, location);
		}
	}
	
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
	 * @param path
	 * @return
	 */
	protected String getContent(String path)
	{
		File file = this.getFile(new Path(path));
		
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
			URL url = FileLocator.find(Activator.getDefault().getBundle(), path, null);
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
	
	/* (non-Javadoc)
	 * @see com.aptana.editor.js.inferencing.InferencingTestsBase#getIndexURI()
	 */
	@Override
	protected URI getIndexURI()
	{
		return URI.create("inference.testing");
	}

	
	/* (non-Javadoc)
	 * @see com.aptana.editor.js.inferencing.InferencingTestsBase#getLocation()
	 */
	@Override
	protected URI getLocation()
	{
		return URI.create("inference_file.js");
	}

	/**
	 * invocationTypeTests
	 * 
	 * @param source
	 * @param types
	 */
	public void invocationTypeTests(String source, String... types)
	{
		IParseNode root = this.getParseRootNode(source);
		assertNotNull(root);
		assertTrue(root instanceof JSParseRootNode);
		
		IParseNode invocation = root.getLastChild();
		assertNotNull(invocation);
		assertTrue(invocation instanceof JSNode);
		
		JSScope globals = this.getGlobals((JSParseRootNode) root);
		assertNotNull(globals);
		
		Indexer indexer = new Indexer();
		indexer.indexTree(this.getIndex(), (JSParseRootNode) root, this.getLocation());
		
		List<String> invocationTypes = this.getTypes(globals, (JSNode) invocation);
		assertNotNull(invocationTypes);
		
		assertEquals(invocationTypes.size(), types.length);
		
		for (String type : types)
		{
			assertTrue(invocationTypes.contains(type));
		}
	}
	
	/**
	 * testIdentifierCycle
	 */
	public void testIdentifierCycle()
	{
		this.varTypeTests("var a = b, b = a;", "a");
		this.varTypeTests("var a = b, b = a;", "b");
	}
	/**
	 * testIdentifierCycle2
	 */
	public void testIdentifierCycle2()
	{
		this.varTypeTests("var a = b, b = c, c = a;", "a");
		this.varTypeTests("var a = b, b = c, c = a;", "b");
		this.varTypeTests("var a = b, b = c, c = a;", "c");
	}

	/**
	 * testObjectPropertyReturnsSelf
	 */
	public void testObjectPropertyReturnsSelf()
	{
		String source = this.getContent("inferencing/chaining.js");
		
		this.invocationTypeTests(source, "Utils.create.self");
	}
	
	/**
	 * testInvocationCycle
	 */
	public void testInvocationCycle()
	{
		String source = this.getContent("inferencing/invocation-cycle-1.js");
		
		this.invocationTypeTests(source, "Function");
	}
	
	/**
	 * testInvocationCycle2
	 */
	public void testInvocationCycle2()
	{
		String source = this.getContent("inferencing/invocation-cycle-2.js");
		
		this.invocationTypeTests(source, "Number");
	}
	
	/**
	 * testInvocationCycle3
	 */
	public void testInvocationCycle3()
	{
		String source = this.getContent("inferencing/invocation-cycle-3.js");
		
		this.invocationTypeTests(source, "Number");
	}
}
