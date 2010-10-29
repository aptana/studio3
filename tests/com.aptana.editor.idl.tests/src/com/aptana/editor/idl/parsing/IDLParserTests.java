package com.aptana.editor.idl.parsing;

import java.io.IOException;

import junit.framework.TestCase;
import beaver.Parser.Exception;

import com.aptana.editor.idl.parsing.ast.IDLNodeType;
import com.aptana.parsing.ast.IParseNode;

public class IDLParserTests extends TestCase
{
	/**
	 * parse
	 * 
	 * @param source
	 * @param types
	 * @throws IOException
	 * @throws Exception
	 */
	protected IParseNode parse(String source, IDLNodeType... types)
	{
		// create parser
		IDLParser parser = new IDLParser();

		// create scanner and associate source
		IDLScanner scanner = new IDLScanner();
		scanner.setSource(source);

		// parse it
		IParseNode result = null;

		try
		{
			result = (IParseNode) parser.parse(scanner);
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
		catch (IOException e)
		{
			fail(e.getMessage());
		}

		// make sure we got something
		assertNotNull(result);

		IParseNode current = result;

		// check node types
		for (IDLNodeType type : types)
		{
			current = current.getNextNode();

			assertNotNull(current);
			assertEquals(type.getIndex(), current.getNodeType());
		}

		return result;
	}
}
