package com.aptana.editor.ruby;

import junit.framework.TestCase;

import com.aptana.editor.ruby.core.IRubyElement;
import com.aptana.editor.ruby.core.IRubyField;
import com.aptana.editor.ruby.core.IRubyMethod;
import com.aptana.editor.ruby.core.IRubyType;
import com.aptana.editor.ruby.parsing.RubyParser;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.ast.IParseNode;

public class RubyParserTest extends TestCase
{

	private RubyParser fParser;
	private ParseState fParseState;

	@Override
	protected void setUp() throws Exception
	{
		fParser = new RubyParser();
		fParseState = new ParseState();
	}

	@Override
	protected void tearDown() throws Exception
	{
		fParser = null;
		fParseState.clearEditState();
	}

	public void testClass() throws Exception
	{
		String source = "class Person\n\tattr_reader :name, :age\n\tdef initialize(name, age)\n\t\t@name, @age = name, age\n\tend\nend";
		fParseState.setEditState(source, source, 0, 0);

		IParseNode result = fParser.parse(fParseState);
		IParseNode[] classes = result.getChildren();
		assertEquals(1, classes.length); // one class declaration
		assertEquals(IRubyElement.TYPE, classes[0].getType()); // class type
		assertEquals("Person", classes[0].toString()); // class name

		IRubyType rubyClass = (IRubyType) classes[0];
		// checks fields
		assertFields(rubyClass, new String[] { "@:name", "@:age", "@name", "@age" });
		// checks methods
		assertMethods(rubyClass, new String[] { "name()", "age()", "initialize(name, age)" });
	}

	private void assertFields(IRubyType rubyClass, String[] fieldNames)
	{
		IRubyField[] fields = rubyClass.getFields();
		assertEquals(fieldNames.length, fields.length);
		for (int i = 0; i < fields.length; ++i)
		{
			assertEquals(fieldNames[i], fields[i].toString());
		}
	}

	private void assertMethods(IRubyType rubyClass, String[] methodNames)
	{
		IRubyMethod[] methods = rubyClass.getMethods();
		assertEquals(methodNames.length, methods.length);
		for (int i = 0; i < methods.length; ++i)
		{
			assertEquals(methodNames[i], methods[i].toString());
		}
	}
}
