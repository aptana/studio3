package com.aptana.editor.js.inferencing;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.aptana.core.util.IOUtil;
import com.aptana.core.util.ResourceUtil;
import com.aptana.editor.js.Activator;
import com.aptana.editor.js.JSTypeConstants;
import com.aptana.editor.js.contentassist.index.JSFileIndexingParticipant;
import com.aptana.editor.js.contentassist.index.JSIndexReader;
import com.aptana.editor.js.contentassist.model.ContentSelector;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.editor.js.contentassist.model.TypeElement;
import com.aptana.editor.js.parsing.JSParser;
import com.aptana.editor.js.parsing.ast.JSNode;
import com.aptana.editor.js.parsing.ast.JSParseRootNode;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.ast.IParseNode;

public abstract class InferencingTestsBase extends TestCase
{
	public class Indexer extends JSFileIndexingParticipant
	{
		public void indexTree(Index index, JSParseRootNode root, URI location)
		{
			this.processParseResults(index, root, location);
		}
	}

	private static final EnumSet<ContentSelector> PARENT_TYPES_AND_PROPERTIES = EnumSet.of(ContentSelector.PARENT_TYPES, ContentSelector.PROPERTIES);
	private JSIndexReader _reader;

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

	/**
	 * JSScope
	 * 
	 * @param ast
	 * @return
	 */
	protected JSScope getGlobals(JSParseRootNode ast)
	{
		return ast.getGlobals();
	}

	/**
	 * getGlobals
	 * 
	 * @param source
	 * @return
	 */
	protected JSScope getGlobals(String source)
	{
		IParseNode root = this.getParseRootNode(source);
		assertTrue(root instanceof JSParseRootNode);

		return this.getGlobals((JSParseRootNode) root);
	}

	/**
	 * getIndex
	 * 
	 * @return
	 */
	protected Index getIndex()
	{
		URI indexURI = this.getIndexURI();
		Index result = null;

		if (indexURI != null)
		{
			result = IndexManager.getInstance().getIndex(indexURI);
		}

		return result;
	}

	/**
	 * getIndexURI
	 * 
	 * @return
	 */
	protected URI getIndexURI()
	{
		return URI.create("inference.testing");
	}

	/**
	 * getLastStatementTypes
	 * 
	 * @param source
	 * @return
	 */
	public List<String> getLastStatementTypes(String source)
	{
		IParseNode root = this.getParseRootNode(source);
		assertNotNull(root);
		assertTrue(root instanceof JSParseRootNode);

		IParseNode statement = root.getLastChild();
		assertNotNull(statement);
		assertTrue(statement instanceof JSNode);

		JSScope globals = this.getGlobals((JSParseRootNode) root);
		assertNotNull(globals);

		Indexer indexer = new Indexer();
		indexer.indexTree(this.getIndex(), (JSParseRootNode) root, this.getLocation());

		return this.getTypes(globals, (JSNode) statement);
	}

	/**
	 * getURI
	 * 
	 * @return
	 */
	protected URI getLocation()
	{
		return URI.create("inference_file.js");
	}

	/**
	 * getParseRootNode
	 * 
	 * @param source
	 * @return
	 */
	protected IParseNode getParseRootNode(String source)
	{
		JSParser parser = new JSParser();
		ParseState parseState = new ParseState();

		parseState.setEditState(source, source, 0, 0);

		try
		{
			parser.parse(parseState);
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}

		return parseState.getParseResult();
	}

	/**
	 * getType
	 * 
	 * @param typeName
	 * @return
	 */
	protected TypeElement getType(String typeName)
	{
		return this._reader.getType(this.getIndex(), typeName, PARENT_TYPES_AND_PROPERTIES);
	}

	/**
	 * getTypes
	 * 
	 * @param node
	 * @return
	 */
	protected List<String> getTypes(JSScope globals, JSNode node)
	{
		JSNodeTypeInferrer walker = new JSNodeTypeInferrer(globals, this.getIndex(), this.getLocation());

		node.accept(walker);

		return walker.getTypes();
	}

	/**
	 * getTypes
	 * 
	 * @param nodes
	 * @return
	 */
	protected List<String> getTypes(JSScope globals, List<JSNode> nodes)
	{
		List<String> result = new LinkedList<String>();

		for (IParseNode node : nodes)
		{
			JSNodeTypeInferrer walker = new JSNodeTypeInferrer(globals, this.getIndex(), this.getLocation());

			assertTrue(node instanceof JSNode);

			((JSNode) node).accept(walker);

			result.addAll(walker.getTypes());
		}

		return result;
	}

	/**
	 * invocationTypeTests
	 * 
	 * @param source
	 * @param types
	 */
	public void lastStatementTypeTests(String source, String... types)
	{
		List<String> statementTypes = this.getLastStatementTypes(source);
		assertNotNull(statementTypes);

		assertEquals(types.length, statementTypes.size());

		for (String type : types)
		{
			String message = MessageFormat.format("Unable to locate {0} in {1}", type, statementTypes);

			if (JSTypeConstants.DYNAMIC_CLASS_PREFIX.equals(type))
			{
				boolean match = false;

				for (String returnedType : statementTypes)
				{
					if (returnedType.startsWith(JSTypeConstants.DYNAMIC_CLASS_PREFIX))
					{
						match = true;
						break;
					}
				}

				if (match == false)
				{
					fail(message);
				}
			}
			else
			{
				assertTrue(message, statementTypes.contains(type));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		// TODO Auto-generated method stub
		super.setUp();

		this._reader = new JSIndexReader();
	}

	/**
	 * structureTests
	 * 
	 * @param typeName
	 * @param propertyNames
	 */
	protected void structureTests(String typeName, String... propertyNames)
	{
		assertNotNull(typeName);

		this.structureTests(this.getType(typeName), propertyNames);
	}

	/**
	 * structureTests
	 * 
	 * @param type
	 * @param propertyNames
	 */
	protected void structureTests(TypeElement type, String... propertyNames)
	{
		List<String> parentTypes = type.getParentTypes();
		assertEquals(1, parentTypes.size());
		assertEquals(JSTypeConstants.OBJECT_TYPE, parentTypes.get(0));

		for (String propertyName : propertyNames)
		{
			PropertyElement property = type.getProperty(propertyName);
			assertNotNull(propertyName + " does not exist", property);

			List<String> propertyTypeNames = property.getTypeNames();
			assertNotNull(propertyName + " does not have a type", propertyTypeNames);

			assertEquals(1, propertyTypeNames.size());
			assertEquals(JSTypeConstants.BOOLEAN_TYPE, propertyTypeNames.get(0));
		}
	}

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception
	{
		this._reader = null;

		URI indexURI = this.getIndexURI();

		if (indexURI != null)
		{
			IndexManager.getInstance().removeIndex(indexURI);
		}

		super.tearDown();
	}

	/**
	 * varTypeTests
	 * 
	 * @param source
	 * @param symbol
	 * @param types
	 */
	public void varTypeTests(String source, String symbol, String... types)
	{
		JSScope globals = this.getGlobals(source);

		assertTrue(globals.hasLocalSymbol(symbol));
		JSPropertyCollection object = globals.getSymbol(symbol);
		assertNotNull(object);
		List<JSNode> values = object.getValues();
		assertNotNull(values);
		assertEquals(1, values.size());

		List<String> symbolTypes = this.getTypes(globals, values);
		assertNotNull(types);
		assertEquals(types.length, symbolTypes.size());

		for (String type : types)
		{
			assertTrue(symbolTypes.contains(type));
		}
	}
}
