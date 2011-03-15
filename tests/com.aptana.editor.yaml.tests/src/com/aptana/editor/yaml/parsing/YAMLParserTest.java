package com.aptana.editor.yaml.parsing;

import junit.framework.TestCase;

import com.aptana.parsing.IParseState;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.IParseRootNode;

public class YAMLParserTest extends TestCase
{

	protected void setUp() throws Exception
	{
		super.setUp();
	}

	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	public void testParseSimpleYAML() throws Exception
	{
		YAMLParser parser = new YAMLParser();
		String src = "development:\n  adapter: mysql\n  pool: 5\n  timeout: 5000\n\ntest:\n  adapter: sqlite3\n\n"; //$NON-NLS-1$
		IParseState parseState = new ParseState();
		parseState.setEditState(src, null, 0, 0);
		IParseRootNode rootNode = parser.parse(parseState);

		// Check the structure and offsets!
		YAMLParseRootNode yprn = (YAMLParseRootNode) rootNode;
		assertEquals(0, yprn.getStartingOffset());
		assertEquals(src.length(), yprn.getEndingOffset());
		assertEquals(1, yprn.getChildCount());

		IParseNode fullSrcMap = yprn.getChild(0);
		assertEquals(0, fullSrcMap.getStartingOffset());
		assertEquals(80, fullSrcMap.getEndingOffset());
		assertEquals(2, fullSrcMap.getChildCount());

		IParseNode development = fullSrcMap.getChild(0);
		assertEquals(0, development.getStartingOffset());
		assertEquals(54, development.getEndingOffset());
		assertEquals(1, development.getChildCount());
		
		IParseNode developmentMap = development.getChild(0);
		assertEquals(15, developmentMap.getStartingOffset());
		assertEquals(54, developmentMap.getEndingOffset());
		assertEquals(3, developmentMap.getChildCount());
		
		IParseNode adapter = developmentMap.getChild(0);
		assertEquals(15, adapter.getStartingOffset());
		assertEquals(28, adapter.getEndingOffset());
		assertEquals(1, adapter.getChildCount());

		IParseNode mysql = adapter.getChild(0);
		assertEquals(24, mysql.getStartingOffset());
		assertEquals(28, mysql.getEndingOffset());
		assertEquals(0, mysql.getChildCount());

		IParseNode test = fullSrcMap.getChild(1);
		assertEquals(57, test.getStartingOffset());
		assertEquals(80, test.getEndingOffset());
		assertEquals(1, test.getChildCount());
	}

}
