/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.ruby;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.eclipse.jface.text.rules.IToken;

/**
 * @author Chris
 */
public class RubySourcePartitionScannerTest extends TestCase
{

	private void assertContentType(String contentType, String code, int offset)
	{
		assertEquals("Content type doesn't match expectations for: " + code.charAt(offset), contentType,
				getContentType(code, offset));
	}

	private String getContentType(String content, int offset)
	{
		IDocument doc = new Document(content);
		FastPartitioner partitioner = new FastPartitioner(
				new MergingPartitionScanner(new RubySourcePartitionScanner()), RubySourceConfiguration.CONTENT_TYPES);
		partitioner.connect(doc);
		return partitioner.getContentType(offset);
	}

	public void testUnclosedInterpolationDoesntInfinitelyLoop()
	{
		getContentType("%[\"#{\"]", 0);
		assert (true);
	}

	/**
	 * http://www.aptana.com/trac/ticket/5730
	 */
	public void testBug5730()
	{
		getContentType("# Comment\n" + "=begin\n" + "puts 'hi'\n" + "=ne", 0);
		assert (true);
	}

	/**
	 * http://www.aptana.com/trac/ticket/6052
	 */
	public void testBug6052()
	{
		getContentType("# Use this class to maintain the decision process\n" + "# To choose a next aprt of text etc.\n"
				+ "class Logic\n" + "=begin\n" + "  def initialize\n" + "  end\n"
				+ "############################################################################################\n"
				+ "  private\n"
				+ "############################################################################################ \n"
				+ "end", 0);
		assert (true);
	}

	public void testDivideAndRegexInHeredocInterpolation()
	{
		getContentType("test.execute <<END\n" + "#{/[0-9]+/ / 5}\n" + "END", 0);
		assert (true);
	}

	public void testPartitioningOfSingleLineComment()
	{
		String source = "# This is a comment\n";

		assertContentType(RubySourceConfiguration.SINGLE_LINE_COMMENT, source, 0);
		assertContentType(RubySourceConfiguration.SINGLE_LINE_COMMENT, source, 1);
		assertContentType(RubySourceConfiguration.SINGLE_LINE_COMMENT, source, 18);
	}

	public void testRecognizeSpecialCase()
	{
		String source = "a,b=?#,'This is not a comment!'\n";

		assertContentType(RubySourceConfiguration.DEFAULT, source, 5);
		assertContentType(RubySourceConfiguration.DEFAULT, source, 6);
	}

	public void testMultilineComment()
	{
		String source = "=begin\nComment\n=end";

		assertContentType(RubySourceConfiguration.MULTI_LINE_COMMENT, source, 0);
		assertContentType(RubySourceConfiguration.MULTI_LINE_COMMENT, source, 10);

		source = "=begin\n" + "  for multiline comments, the =begin and =end must\n" + "  appear in the first column\n"
				+ "=end";
		assertContentType(RubySourceConfiguration.MULTI_LINE_COMMENT, source, 0);
		assertContentType(RubySourceConfiguration.MULTI_LINE_COMMENT, source, source.length() / 2);
		assertContentType(RubySourceConfiguration.MULTI_LINE_COMMENT, source, source.length() - 2);
	}

	public void testMultilineCommentNotOnFirstColumn()
	{
		String source = " =begin\nComment\n=end";

		assertContentType(RubySourceConfiguration.DEFAULT, source, 0);
		assertContentType(RubySourceConfiguration.DEFAULT, source, 1);
		assertContentType(RubySourceConfiguration.DEFAULT, source, 2);
		assertContentType(RubySourceConfiguration.DEFAULT, source, 10);
	}

	public void testRecognizeDivision()
	{
		String source = "1/3 #This is a comment\n";

		assertContentType(RubySourceConfiguration.DEFAULT, source, 0);
		assertContentType(RubySourceConfiguration.DEFAULT, source, 3);
		assertContentType(RubySourceConfiguration.SINGLE_LINE_COMMENT, source, 5);
	}

	public void testRecognizeOddballCharacters()
	{
		String source = "?\" #comment\n";

		assertContentType(RubySourceConfiguration.DEFAULT, source, 0);
		assertContentType(RubySourceConfiguration.DEFAULT, source, 2);
		assertContentType(RubySourceConfiguration.SINGLE_LINE_COMMENT, source, 5);

		source = "?' #comment\n";

		assertContentType(RubySourceConfiguration.DEFAULT, source, 0);
		assertContentType(RubySourceConfiguration.DEFAULT, source, 2);
		assertContentType(RubySourceConfiguration.SINGLE_LINE_COMMENT, source, 5);

		source = "?/ #comment\n";

		assertContentType(RubySourceConfiguration.DEFAULT, source, 0);
		assertContentType(RubySourceConfiguration.DEFAULT, source, 2);
		assertContentType(RubySourceConfiguration.SINGLE_LINE_COMMENT, source, 5);
	}

	public void testPoundCharacterIsntAComment()
	{
		String source = "?#";
		assertContentType(RubySourceConfiguration.DEFAULT, source, 1);
	}

	public void testSinglelineCommentJustAfterMultilineComment()
	{
		String source = "=begin\nComment\n=end\n# this is a singleline comment\n";

		assertContentType(RubySourceConfiguration.MULTI_LINE_COMMENT, source, 0);
		assertContentType(RubySourceConfiguration.MULTI_LINE_COMMENT, source, 10);
		assertContentType(RubySourceConfiguration.SINGLE_LINE_COMMENT, source, source.length() - 5);
	}

	public void testMultipleCommentsInARow()
	{
		String code = "# comment 1\n# comment 2\nclass Chris\nend\n";

		assertContentType(RubySourceConfiguration.SINGLE_LINE_COMMENT, code, 6);
		assertContentType(RubySourceConfiguration.SINGLE_LINE_COMMENT, code, 17);
		assertContentType(RubySourceConfiguration.DEFAULT, code, 26);
		assertContentType(RubySourceConfiguration.DEFAULT, code, 29);
	}

	public void testCommentAfterEnd()
	{
		String code = "class Chris\nend # comment\n";
		assertContentType(RubySourceConfiguration.DEFAULT, code, 12);
		assertContentType(RubySourceConfiguration.SINGLE_LINE_COMMENT, code, 17);
	}

	public void testCommentAfterEndWhileEditing()
	{
		String code = "=begin\r\n" + "c\r\n" + "=end\r\n" + "#hmm\r\n" + "#comment here why is ths\r\n"
				+ "class Chris\r\n" + "  def thing\r\n" + "  end  #ocmm \r\n" + "end";
		assertContentType(RubySourceConfiguration.DEFAULT, code, 76);
		assertContentType(RubySourceConfiguration.SINGLE_LINE_COMMENT, code, 83);
	}

	public void testCommentAtEndOfLineWithStringAtBeginning()
	{
		String code = "hash = {\n" + "  \"string\" => { # comment\n" + "    123\n" + "  }\n" + "}";
		assertContentType(RubySourceConfiguration.DEFAULT, code, 0);
		assertContentType(RubySourceConfiguration.DEFAULT, code, 4);
		assertContentType(RubySourceConfiguration.DEFAULT, code, 6);

		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 11);
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 12);
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 18);

		assertContentType(RubySourceConfiguration.DEFAULT, code, 19);
		assertContentType(RubySourceConfiguration.DEFAULT, code, 22);

		assertContentType(RubySourceConfiguration.SINGLE_LINE_COMMENT, code, 25);
	}

	public void testLinesWithJustSpaceBeforeComment()
	{
		String code = "  \n" + "  # comment\n" + "  def method\n" + "    \n" + "  end";
		assertContentType(RubySourceConfiguration.SINGLE_LINE_COMMENT, code, 5);
		assertContentType(RubySourceConfiguration.DEFAULT, code, 17);
		assertContentType(RubySourceConfiguration.DEFAULT, code, 20);
	}

	public void testCommentsWithAlotOfPrecedingSpaces()
	{
		String code = "                # We \n" + "                # caller-requested until.\n" + "return self\n";
		assertContentType(RubySourceConfiguration.SINGLE_LINE_COMMENT, code, 16);
		assertContentType(RubySourceConfiguration.DEFAULT, code, 64);
		assertContentType(RubySourceConfiguration.DEFAULT, code, 70);
	}

	public void testCodeWithinString()
	{
		String code = "string = \"here's some code: #{1} there\"";
		assertContentType(RubySourceConfiguration.DEFAULT, code, 2); // st'r'...
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 10); // "'h'er...
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 28); // '#'{1...
		assertContentType(RubySourceConfiguration.DEFAULT, code, 30); // '1'} t...
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 31); // '}' th...
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 35); // th'e're..
	}

	public void testCodeWithinSingleQuoteString()
	{
		String code = "string = 'here s some code: #{1} there'";
		assertContentType(RubySourceConfiguration.DEFAULT, code, 2); // st'r'...
		assertContentType(RubySourceConfiguration.STRING_SINGLE, code, 10); // "'h'er...
		assertContentType(RubySourceConfiguration.STRING_SINGLE, code, 28); // '#'{1...
		assertContentType(RubySourceConfiguration.STRING_SINGLE, code, 30); // '1'} t...
		assertContentType(RubySourceConfiguration.STRING_SINGLE, code, 31); // '}' th...
		assertContentType(RubySourceConfiguration.STRING_SINGLE, code, 35); // th'e're..
	}

	public void testVariableSubstitutionWithinString()
	{
		String code = "string = \"here's some code: #$global there\"";
		assertContentType(RubySourceConfiguration.DEFAULT, code, 2); // st'r'...
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 10); // "'h'er...
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 28); // '#'$glo...
		assertContentType(RubySourceConfiguration.DEFAULT, code, 29); // '$'global
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 36);// ' 'there...
	}

	public void testStringWithinCodeWithinString()
	{
		String code = "string = \"here's some code: #{var = 'string'} there\"";
		assertContentType(RubySourceConfiguration.DEFAULT, code, 2); // st'r'...
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 10); // "'h'er...
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 28); // '#'{var
		assertContentType(RubySourceConfiguration.DEFAULT, code, 30); // 'v'ar =
		assertContentType(RubySourceConfiguration.STRING_SINGLE, code, 36); // '''string
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 46); // 't'here
	}

	public void testStringWithEndBraceWithinCodeWithinString()
	{
		String code = "string = \"here's some code: #{var = '}'; 1} there\"";
		assertContentType(RubySourceConfiguration.DEFAULT, code, 2); // st'r'...
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 10); // "'h'er...
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 28); // '#'{var
		assertContentType(RubySourceConfiguration.DEFAULT, code, 30); // 'v'ar =
		assertContentType(RubySourceConfiguration.STRING_SINGLE, code, 37); // '}';
		assertContentType(RubySourceConfiguration.DEFAULT, code, 39); // ';' 1}
		assertContentType(RubySourceConfiguration.DEFAULT, code, 41); // ; '1'}
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 42); // 1'}' t
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 44); // 't'here
	}

	public void testRegex()
	{
		String code = "regex = /hi there/";
		assertContentType(RubySourceConfiguration.DEFAULT, code, 2); // re'g'ex
		assertContentType(RubySourceConfiguration.REGULAR_EXPRESSION, code, 9); // '/'hi the
		assertContentType(RubySourceConfiguration.REGULAR_EXPRESSION, code, 11); // /h'i' the
	}

	public void testRegexWithDynamicCode()
	{
		String code = "/\\.#{Regexp.escape(extension.to_s)}$/ # comment";
		assertContentType(RubySourceConfiguration.REGULAR_EXPRESSION, code, 3);
		assertContentType(RubySourceConfiguration.SINGLE_LINE_COMMENT, code, 38); // '#' co
		assertContentType(RubySourceConfiguration.SINGLE_LINE_COMMENT, code, 40); // # 'c'ommen
	}

	public void testEscapedCharactersAndSingleQuoteInsideDoubleQuote()
	{
		String code = "quoted_value = \"'#{quoted_value[1..-2].gsub(/\\'/, \"\\\\\\\\'\")}'\" if quoted_value.include?(\"\\\\\\'\") # (for ruby mode) \"\n"
				+ "quoted_value";
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 16); //
		assertContentType(RubySourceConfiguration.DEFAULT, code, 19); // #{'q'uoted
		assertContentType(RubySourceConfiguration.REGULAR_EXPRESSION, code, 44); // /
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 51); // "\
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 59); // '" if
		assertContentType(RubySourceConfiguration.DEFAULT, code, 62); // 'i'f quoted_
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 87); // include?('"'
		assertContentType(RubySourceConfiguration.SINGLE_LINE_COMMENT, code, 95); // '#' (for ruby mode)
		assertContentType(RubySourceConfiguration.DEFAULT, code, code.length() - 3);
	}

	public void testSingleQuotedString()
	{
		String code = "require 'commands/server'";
		assertContentType(RubySourceConfiguration.DEFAULT, code, 1);
		assertContentType(RubySourceConfiguration.STRING_SINGLE, code, 8);
		assertContentType(RubySourceConfiguration.STRING_SINGLE, code, 9);
		assertContentType(RubySourceConfiguration.STRING_SINGLE, code, 17);
		assertContentType(RubySourceConfiguration.STRING_SINGLE, code, 18);
		assertContentType(RubySourceConfiguration.STRING_SINGLE, code, 24);
	}

	public void testCommands()
	{
		String code = "if OPTIONS[:detach]\n" + "  `mongrel_rails #{parameters.join(\" \")} -d`\n" + "else\n"
				+ "  ENV[\"RAILS_ENV\"] = OPTIONS[:environment]";
		assertContentType(RubySourceConfiguration.DEFAULT, code, 1);
		assertContentType(RubySourceConfiguration.COMMAND, code, 22);
		assertContentType(RubySourceConfiguration.COMMAND, code, 23);
		assertContentType(RubySourceConfiguration.COMMAND, code, 38);
		assertContentType(RubySourceConfiguration.DEFAULT, code, 50);
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 55);
		assertContentType(RubySourceConfiguration.DEFAULT, code, 58);
		assertContentType(RubySourceConfiguration.COMMAND, code, 59);
		assertContentType(RubySourceConfiguration.COMMAND, code, 63);
		assertContentType(RubySourceConfiguration.DEFAULT, code, 65);
	}

	public void testPercentXCommand()
	{
		String code = "if (@options.do_it)\n" + "  %x{#{cmd}}\n" + "end\n";
		assertContentType(RubySourceConfiguration.DEFAULT, code, 1); // i'f'
		assertContentType(RubySourceConfiguration.COMMAND, code, 22); // '%'x
		assertContentType(RubySourceConfiguration.COMMAND, code, 24); // %x'{'
		assertContentType(RubySourceConfiguration.DEFAULT, code, 27); // 'c'md
		assertContentType(RubySourceConfiguration.COMMAND, code, 30); // cmd'}'
		assertContentType(RubySourceConfiguration.COMMAND, code, 31); // cmd}'}'
		assertContentType(RubySourceConfiguration.DEFAULT, code, 33); // 'e'nd
	}

	public void testHeredocInArgumentList()
	{
		String code = "connection.delete <<-end_sql, \"#{self.class.name} Destroy\"\n"
				+ "  DELETE FROM #{self.class.table_name}\n"
				+ "  WHERE #{connection.quote_column_name(self.class.primary_key)} = #{quoted_id}\n" + "end_sql\n";
		assertContentType(RubySourceConfiguration.DEFAULT, code, 1); // c'o'nnection
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 18); // '<'<-end_sql
		assertContentType(RubySourceConfiguration.DEFAULT, code, 33); // 's'elf.class
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 48); // '}' Destroy
		assertContentType(RubySourceConfiguration.DEFAULT, code, 75); // 's'elf.class
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 96); // name'}'\n
		assertContentType(RubySourceConfiguration.DEFAULT, code, 108); // {'c'onnection
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 160); // '}' =
		assertContentType(RubySourceConfiguration.DEFAULT, code, 166); // {'q'uoted
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 175); // _id'}'\n
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 177); // 'e'nd_sql
	}

	public void testScaryString()
	{
		String code = "puts \"match|#{$`}<<#{$&}>>#{$'}|\"\n" + "pp $~";
		assertContentType(RubySourceConfiguration.DEFAULT, code, 1); // p'u'ts
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 5); // '"'match
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 13); // #'{'$`
		assertContentType(RubySourceConfiguration.DEFAULT, code, 14); // $
		assertContentType(RubySourceConfiguration.DEFAULT, code, 15); // `
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 16); // }
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 20); // {
		assertContentType(RubySourceConfiguration.DEFAULT, code, 21); // $
		assertContentType(RubySourceConfiguration.DEFAULT, code, 22); // &
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 23); // }
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 27); // {
		assertContentType(RubySourceConfiguration.DEFAULT, code, 28); // $
		assertContentType(RubySourceConfiguration.DEFAULT, code, 29); // '
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 30); // }
		assertContentType(RubySourceConfiguration.DEFAULT, code, 34); // 'p'p $~
	}

	// TODO Handle yet even wackier heredoc syntax:
	// http://blog.jayfields.com/2006/12/ruby-multiline-strings-here-doc-or.html

	public void testBraceFinderHandlesWeirdGlobal()
	{
		RubySourcePartitionScanner.EndBraceFinder finder = new RubySourcePartitionScanner.EndBraceFinder(
				"$'}|\"\npp $~");
		assertEquals(2, finder.find());
	}

	public void testNestedHeredocs()
	{
		String code = "methods += <<-BEGIN + nn_element_def(element) + <<-END\n"
				+ "  def #{element.downcase}(attributes = {})\n" + "BEGIN\n" + "  end\n" + "END\n" + "\n"
				+ "puts :symbol\n";
		assertContentType(RubySourceConfiguration.DEFAULT, code, 1);
		// _<_<-BEGIN
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 11);
		// <<-BEGI_N_
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 18);
		assertContentType(RubySourceConfiguration.DEFAULT, code, 20);
		assertContentType(RubySourceConfiguration.DEFAULT, code, 46);
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 48);
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 62); // #'{'
		assertContentType(RubySourceConfiguration.DEFAULT, code, 63); // #{'e'lem
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 79); // case'}'

		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 80); // }'('att
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 96); // )
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 102); // BEGI'N'
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 108); // en'd'

		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 112); // EN'D'
		assertContentType(RubySourceConfiguration.DEFAULT, code, 115); // 'p'uts
		assertContentType(RubySourceConfiguration.DEFAULT, code, 120); // ':'sym
	}

	public void testBug5448()
	{
		String code = "m.class_collisions controller_class_path,       \"#{controller_class_name}Controller\", # Sessions Controller\r\n"
				+ "    \"#{controller_class_name}Helper\"";
		assertContentType(RubySourceConfiguration.DEFAULT, code, 1);
		assertContentType(RubySourceConfiguration.DEFAULT, code, 40);
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 48);
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 50);
		assertContentType(RubySourceConfiguration.DEFAULT, code, 51);
		assertContentType(RubySourceConfiguration.DEFAULT, code, 71);
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 72);
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 83);
		assertContentType(RubySourceConfiguration.DEFAULT, code, 84);
		assertContentType(RubySourceConfiguration.SINGLE_LINE_COMMENT, code, 86);
	}

	public void testBug5208()
	{
		String code = "=begin\r\n" + "  This is a comment\r\n" + "=end\r\n" + "require 'gosu'";
		assertContentType(RubySourceConfiguration.MULTI_LINE_COMMENT, code, 0);
		assertContentType(RubySourceConfiguration.MULTI_LINE_COMMENT, code, 32); // =en'd'
		assertContentType(RubySourceConfiguration.DEFAULT, code, 36); // 'r'equire
		assertContentType(RubySourceConfiguration.STRING_SINGLE, code, 44);
	}

	public void testROR255()
	{
		String code = "\"all_of(#{@matchers.map { |matcher| matcher.mocha_inspect }.join(\", \") })\"";
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 1); // "'a'll_of
		assertContentType(RubySourceConfiguration.DEFAULT, code, 10); // #{'@'match
		assertContentType(RubySourceConfiguration.DEFAULT, code, 60); // }.'j'oin
		assertContentType(RubySourceConfiguration.DEFAULT, code, 70);
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 71); // ) '}')"
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 72); // ) }')'"
	}

	public void testROR950()
	{
		String code = "config.load_paths += [\"#{RAILS_ROOT}/vendor/plugins/sql_session_store/lib\"]";
		assertContentType(RubySourceConfiguration.DEFAULT, code, 0); // 'c'onfig
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 22); // ['"'#{
		assertContentType(RubySourceConfiguration.DEFAULT, code, 25); // #{'R'
		assertContentType(RubySourceConfiguration.DEFAULT, code, 34);
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 36); // '/'vendor

		code = "config.load_paths += %W(#{RAILS_ROOT}/vendor/plugins/sql_session_store/lib)";
		assertContentType(RubySourceConfiguration.DEFAULT, code, 0); // 'c'onfig
		assertContentType(RubySourceConfiguration.DEFAULT, code, 26); // #{'R'
		assertContentType(RubySourceConfiguration.DEFAULT, code, 35); // OO'T'}
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 37); // '/'vendor
	}

	public void testSmallQString()
	{
		String code = "%q(string)";
		for (int i = 0; i < code.length(); i++)
		{
			assertContentType(RubySourceConfiguration.STRING_SINGLE, code, i);
		}
	}

	public void testLargeQString()
	{
		String code = "%Q(string)";
		for (int i = 0; i < code.length(); i++)
		{
			assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, i);
		}
	}

	public void testPercentXCommand2()
	{
		String code = "%x(command)";
		for (int i = 0; i < code.length(); i++)
		{
			assertContentType(RubySourceConfiguration.COMMAND, code, i);
		}
	}

	public void testPercentSyntax()
	{
		String code = "%(unknown)";
		for (int i = 0; i < code.length(); i++)
		{
			assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, i);
		}
	}

	public void testPercentSSymbol()
	{
		String code = "%s(symbol)";
		assertContentType(RubySourceConfiguration.DEFAULT, code, 0);
		assertContentType(RubySourceConfiguration.DEFAULT, code, 1);
		assertContentType(RubySourceConfiguration.DEFAULT, code, 2);
		for (int i = 3; i < code.length(); i++)
		{
			assertContentType(RubySourceConfiguration.STRING_SINGLE, code, i);
		}
	}

	public void testSmallWString()
	{
		String code = "%w(string two)";
		for (int i = 0; i < code.length(); i++)
		{
			assertContentType(RubySourceConfiguration.STRING_SINGLE, code, i);
		}
	}

	public void testLargeWString()
	{
		String code = "%W(string two)";
		for (int i = 0; i < code.length(); i++)
		{
			assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, i);
		}
	}

	public void testSingleQuotedHeredoc()
	{
		String code = "heredoc =<<'END'\n  hello world!\nEND\n";
		assertContentType(RubySourceConfiguration.DEFAULT, code, 0);
		assertContentType(RubySourceConfiguration.DEFAULT, code, 8);
		assertContentType(RubySourceConfiguration.STRING_SINGLE, code, 9);
		assertContentType(RubySourceConfiguration.STRING_SINGLE, code, 32);
	}

	// TODO Write tests for nested heredocs and heredocs in middle of line with the heredoc being single-quoted (or
	// mixture)

	public void testROR975()
	{
		String code = "exist_sym = :\"#{row['PROVVPI']}.#{row['vpi']}.#{row['vci']}.#{row['seq_num']}\"";
		assertContentType(RubySourceConfiguration.DEFAULT, code, 0); // 'e'xist
		assertContentType(RubySourceConfiguration.DEFAULT, code, 12); // ':'
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 13); // '"'#
	}

	public void testROR1278()
	{
		String code = "hash = {:user=>{:emailaddr=>'4mydemo@4mypasswords.com', :login=>'4MyDemo',\n"
				+ ":password=>'password', :password_confirmation=>'password'},\n"
				+ ":userpin => {:pin =>'test', :pin_confirmation=>'test'}\n" + "}";

		RubySourcePartitionScanner scanner = new RubySourcePartitionScanner();
		IDocument document = new Document(code);
		scanner.setPartialRange(document, 136, 19, RubySourceConfiguration.DEFAULT, 132);
		IToken token = scanner.nextToken();
		assertEquals(RubySourceConfiguration.DEFAULT, token.getData());
	}

	public void testCGILib()
	{
		String code = "warn \"Warning:#{caller[0].sub(/'/, '')}: cgi-lib is deprecated after Ruby 1.8.1; use cgi instead\"";
		// warn
		assertContentType(RubySourceConfiguration.DEFAULT, code, 0);
		// "Warning
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 5);
		// caller
		assertContentType(RubySourceConfiguration.DEFAULT, code, 16);
		// /'/
		assertContentType(RubySourceConfiguration.REGULAR_EXPRESSION, code, 30);
		assertContentType(RubySourceConfiguration.REGULAR_EXPRESSION, code, 32);
		// ,
		assertContentType(RubySourceConfiguration.DEFAULT, code, 33);
		// ''
		assertContentType(RubySourceConfiguration.STRING_SINGLE, code, 35);
		assertContentType(RubySourceConfiguration.STRING_SINGLE, code, 36);
		// )
		assertContentType(RubySourceConfiguration.DEFAULT, code, 37);
		// }: cgi-lib
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 38);
	}

	public void testROR1307StringSymbols()
	{
		String code = "class AbiBuilderTest\n"
				+ "  def method\n"
				+ "    assert_equal array, Abi.send(:\"to_#{type}\", number), \"#{type} failed on Ruby number -> array\"\n\n"
				+ "    assert_equal [255], Abi.signed_to_udword(-1), 'Failed on signed_to_udword'\n" + "  end\n\n"
				+ "  def test_packed_number_encoding\n" + "    packed = { :p => 0x123, :q => 0xABCDEF, :n => 5 }\n"
				+ "    gold_packed = [0x02, 0x00, 0x03, 0x00, 0x01, 0x00, 0x23, 0x01, 0xEF, 0xCD, 0xAB, 0x05]\n"
				+ "    assert_equal gold_packed, Abi.to_packed(packed), 'packed'\n" + "  end\n" + "end";
		// class
		assertContentType(RubySourceConfiguration.DEFAULT, code, 0);
		// send'('
		assertContentType(RubySourceConfiguration.DEFAULT, code, 66);
		// send(':'
		assertContentType(RubySourceConfiguration.DEFAULT, code, 67);
		// "to_#{
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 68);
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 73);
		// type
		assertContentType(RubySourceConfiguration.DEFAULT, code, 74);
		assertContentType(RubySourceConfiguration.DEFAULT, code, 77);
		// }"
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 78);
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 79);
		// , number)
		assertContentType(RubySourceConfiguration.DEFAULT, code, 80);
		// "#{
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 91);
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 93);
		// type
		assertContentType(RubySourceConfiguration.DEFAULT, code, 94);
		assertContentType(RubySourceConfiguration.DEFAULT, code, 97);
		// } failed on Ruby number -> array"
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 98);
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 130);
		// assert_equal
		assertContentType(RubySourceConfiguration.DEFAULT, code, 137);
		// 'Failed on signed_to_udword'
		assertContentType(RubySourceConfiguration.STRING_SINGLE, code, 183);
		assertContentType(RubySourceConfiguration.STRING_SINGLE, code, 210);
	}

	public void testROR1248()
	{
		String code = "`wc -l \"#{ f.gsub('\"','\\\"') }\"`\n\n" + "puts \"syntax hilighting broken here\"";
		// `wc -l "#{
		assertContentType(RubySourceConfiguration.COMMAND, code, 0); // `
		assertContentType(RubySourceConfiguration.COMMAND, code, 7); // "
		assertContentType(RubySourceConfiguration.COMMAND, code, 9); // {
		// f.gsub(
		assertContentType(RubySourceConfiguration.DEFAULT, code, 11); // f
		assertContentType(RubySourceConfiguration.DEFAULT, code, 17); // (
		// '"'
		assertContentType(RubySourceConfiguration.STRING_SINGLE, code, 18); // '
		assertContentType(RubySourceConfiguration.STRING_SINGLE, code, 20); // '
		// ,
		assertContentType(RubySourceConfiguration.DEFAULT, code, 21); // ,
		// '\"'
		assertContentType(RubySourceConfiguration.STRING_SINGLE, code, 22); // '
		assertContentType(RubySourceConfiguration.STRING_SINGLE, code, 25); // '
		// )
		assertContentType(RubySourceConfiguration.DEFAULT, code, 26);
		// }"`
		assertContentType(RubySourceConfiguration.COMMAND, code, 28);
		assertContentType(RubySourceConfiguration.COMMAND, code, 30);
		// puts
		assertContentType(RubySourceConfiguration.DEFAULT, code, 33);
		assertContentType(RubySourceConfiguration.DEFAULT, code, 36);
		// "syntax hilighting broken here"
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 38);
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 68);
	}

	public void testCGI()
	{
		String src = "module TagMaker # :nodoc:\n" + "		\n"
				+ "    # Generate code for an element with required start and end tags.\n" + "    #\n"
				+ "    #   - -\n" + "    def nn_element_def(element)\n" + "      nOE_element_def(element, <<-END)\n"
				+ "          if block_given?\n" + "            yield.to_s\n" + "          else\n"
				+ "            \"\"\n" + "          end +\n" + "          \"</#{element.upcase}>\"\n" + "      END\n"
				+ "    end\n" + "    \n" + "    # Generate code for an empty element.\n" + "    #\n"
				+ "    #   - O EMPTY\n" + "    def nOE_element_def(element, append = nil)\n" + "   end\n" + "end";
		// module TagMaker # :nodoc:
		assertContentType(RubySourceConfiguration.DEFAULT, src, 0); // m
		assertContentType(RubySourceConfiguration.SINGLE_LINE_COMMENT, src, 16); // #

		// nOE_element_def(element, <<-END)
		assertContentType(RubySourceConfiguration.DEFAULT, src, 177); // ,
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, src, 179); // <
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, src, 184); // D
		assertContentType(RubySourceConfiguration.DEFAULT, src, 185); // )

		// </#{element.upcase}>
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, src, 296); // {
		assertContentType(RubySourceConfiguration.DEFAULT, src, 297); // e
		assertContentType(RubySourceConfiguration.DEFAULT, src, 310); // e
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, src, 311); // }

		// END
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, src, 321); // E
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, src, 323); // D

		// end
		assertContentType(RubySourceConfiguration.DEFAULT, src, 329); // e
	}

	public void testBlockComment()
	{
		String code = "=begin\n=end";
		assertContentType(RubySourceConfiguration.MULTI_LINE_COMMENT, code, 0);
		assertContentType(RubySourceConfiguration.MULTI_LINE_COMMENT, code, code.length() - 1);
	}

	public void testSymbolBeginningWithS()
	{
		String code = "hash[:symbol]";
		for (int i = 0; i < code.length(); i++)
			assertContentType(RubySourceConfiguration.DEFAULT, code, i);
	}

	public void testSymbolWithString()
	{
		String code = "hash[:\"symbol\"]";
		assertContentType(RubySourceConfiguration.DEFAULT, code, 0);
		assertContentType(RubySourceConfiguration.DEFAULT, code, 5);
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 6);
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 13);
		assertContentType(RubySourceConfiguration.DEFAULT, code, 14);
	}

	public void testSymbolHitsEndOfFile()
	{
		String code = "hash[:";
		for (int i = 0; i < code.length(); i++)
			assertContentType(RubySourceConfiguration.DEFAULT, code, i);
	}

	public void testPercentSSymbolHitsEndOfFile()
	{
		String code = "hash[%s";
		for (int i = 0; i < code.length(); i++)
			assertContentType(RubySourceConfiguration.DEFAULT, code, i);
	}

	public void testSymbolStringHitsEndOfFile()
	{
		String code = "hash[:\"";
		for (int i = 0; i < code.length() - 1; i++)
			assertContentType(RubySourceConfiguration.DEFAULT, code, i);
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, code.length() - 1);
	}

	public void testHeredoc()
	{
		String code = "def index\n    heredoc =<<-END\n" + "  This is a heredoc, I think\n" + "END\nend\n";
		assertContentType(RubySourceConfiguration.DEFAULT, code, 0);
		assertContentType(RubySourceConfiguration.DEFAULT, code, 22);
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 23);
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 35);
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 61);
		assertContentType(RubySourceConfiguration.DEFAULT, code, 63);
	}

	public void testReturnsRubyDefaultContentTypeNotDocumentDefaultContentType()
	{
		String src = "  config.parameters << :password";
		IDocument document = new Document(src);
		RubySourcePartitionScanner scanner = new RubySourcePartitionScanner();
		scanner.setPartialRange(document, 0, src.length(), IDocument.DEFAULT_CONTENT_TYPE, 0);

		assertToken(scanner, RubySourceConfiguration.DEFAULT, 0, 2);
		assertToken(scanner, RubySourceConfiguration.DEFAULT, 2, 6);
		assertToken(scanner, RubySourceConfiguration.DEFAULT, 8, 1);
		assertToken(scanner, RubySourceConfiguration.DEFAULT, 9, 10);
		assertToken(scanner, RubySourceConfiguration.DEFAULT, 19, 1);
		assertToken(scanner, RubySourceConfiguration.DEFAULT, 20, 2);
		assertToken(scanner, RubySourceConfiguration.DEFAULT, 22, 1);
		assertToken(scanner, RubySourceConfiguration.DEFAULT, 23, 1);
		assertToken(scanner, RubySourceConfiguration.DEFAULT, 24, 8);
	}
	
	/*
	 * https://aptana.lighthouseapp.com/projects/45260/tickets/372-color-syntax-when-dividing-inline-ruby
	 */
	public void testBug372()
	{
		String code = "\"#{@mem / 100.0}\", @test_object\n\"#{@mem / 100.0}\", @test_object";
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 0);
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 2);
		assertContentType(RubySourceConfiguration.DEFAULT, code, 3);
		assertContentType(RubySourceConfiguration.DEFAULT, code, 14);
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 15);
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 16);
		assertContentType(RubySourceConfiguration.DEFAULT, code, 17);
		assertContentType(RubySourceConfiguration.DEFAULT, code, 31);
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 32);
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 34);
		assertContentType(RubySourceConfiguration.DEFAULT, code, 35);
		assertContentType(RubySourceConfiguration.DEFAULT, code, 46);
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 47);
		assertContentType(RubySourceConfiguration.STRING_DOUBLE, code, 48);
		assertContentType(RubySourceConfiguration.DEFAULT, code, 49);
		assertContentType(RubySourceConfiguration.DEFAULT, code, 62);
	}

	private void assertToken(IPartitionTokenScanner scanner, String contentType, int offset, int length)
	{
		IToken token = scanner.nextToken();
		assertEquals(contentType, token.getData());
		assertEquals(offset, scanner.getTokenOffset());
		assertEquals(length, scanner.getTokenLength());
	}
}
