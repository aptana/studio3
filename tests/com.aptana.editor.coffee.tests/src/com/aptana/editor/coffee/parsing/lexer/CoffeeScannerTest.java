package com.aptana.editor.coffee.parsing.lexer;

import org.junit.After;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.io.IOException;

import junit.framework.TestCase;

import com.aptana.editor.coffee.parsing.Terminals;

@SuppressWarnings("nls")
public class CoffeeScannerTest
{

	private CoffeeScanner scanner;

	@Before
	public void setUp() throws Exception
	{
//		super.setUp();
		scanner = new CoffeeScanner();
	}

	@After
	public void tearDown() throws Exception
	{
		scanner = null;
//		super.tearDown();
	}

	@Test
	public void testNumberAssignment() throws Exception
	{
		String source = "number = 42\n";
		scanner.setSource(source);
		assertToken(Terminals.IDENTIFIER, 0, 6); // number
		assertToken(Terminals.EQUAL, 7, 8); // =
		assertToken(Terminals.NUMBER, 9, 11); // 42
		assertToken(Terminals.TERMINATOR, 11, 12); // \n
	}

	@Test
	public void testBooleanAssignment() throws Exception
	{
		String source = "opposite = true\n";
		scanner.setSource(source);
		assertToken(Terminals.IDENTIFIER, 0, 8); // opposite
		assertToken(Terminals.EQUAL, 9, 10); // =
		assertToken(Terminals.BOOL, 11, 15); // true
		assertToken(Terminals.TERMINATOR, 15, 16); // \n
	}

	@Test
	public void testMathAssignment() throws Exception
	{
		String source = "math =\n" + //
				"  root:   Math.sqrt\n" + //
				"  square: square\n" + //
				"  cube:   (x) -> x * square x"; //
		scanner.setSource(source);
		assertToken(Terminals.IDENTIFIER, "math", 0, 4);
		assertToken(Terminals.EQUAL, "=", 5, 6);
		assertToken(Terminals.INDENT, 2, 6, 6);
		assertToken(Terminals.LCURLY, "{", 6, 6); // FIXME Fix offsets for implicit curlies
		assertToken(Terminals.IDENTIFIER, "root", 9, 13);
		assertToken(Terminals.COLON, ":", 13, 14);
		assertToken(Terminals.IDENTIFIER, "Math", 17, 21);
		assertToken(Terminals.DOT, ".", 21, 22);
		assertToken(Terminals.IDENTIFIER, "sqrt", 22, 26);
		assertToken(Terminals.TERMINATOR, "\n", 26, 27);
		assertToken(Terminals.IDENTIFIER, "square", 29, 35);
		assertToken(Terminals.COLON, ":", 35, 36);
		assertToken(Terminals.IDENTIFIER, "square", 37, 43);
		assertToken(Terminals.TERMINATOR, "\n", 43, 44);
		assertToken(Terminals.IDENTIFIER, "cube", 46, 50);
		assertToken(Terminals.COLON, ":", 50, 51);
		assertToken(Terminals.PARAM_START, "(", 54, 55);
		assertToken(Terminals.IDENTIFIER, "x", 55, 56);
		assertToken(Terminals.PARAM_END, ")", 56, 57);
		assertToken(Terminals.FUNC_ARROW, "->", 58, 60);
		assertToken(Terminals.INDENT, 2, 60, 60);
		assertToken(Terminals.IDENTIFIER, "x", 61, 62);
		assertToken(Terminals.MATH, "*", 63, 64);
		assertToken(Terminals.IDENTIFIER, "square", 65, 71);
		assertToken(Terminals.CALL_START, "(", 72, 72);
		assertToken(Terminals.IDENTIFIER, "x", 72, 73);
		assertToken(Terminals.CALL_END, ")", 73, 73);
		assertToken(Terminals.OUTDENT, 2, 73, 73);
		assertToken(Terminals.RCURLY, "}", 73, 73); // FIXME Fix offsets for implicit curlies
		assertToken(Terminals.OUTDENT, 2, 73, 73);
		assertToken(Terminals.TERMINATOR, "\n", 73, 74);
	}

	@Test
	public void testWebsiteExample() throws Exception
	{
		String source = "# Assignment:\n" + //
				"number   = 42\n" + //
				"opposite = true\n" + //
				"\n" + //
				"# Conditions:\n" + //
				"number = -42 if opposite\n" + //
				"\n" + //
				"# Functions:\n" + //
				"square = (x) -> x * x\n" + //
				"\n" + //
				"# Arrays:\n" + //
				"list = [1, 2, 3, 4, 5]\n" + //
				"\n" + //
				"# Objects:\n" + //
				"math =\n" + //
				"  root:   Math.sqrt\n" + //
				"  square: square\n" + //
				"  cube:   (x) -> x * square x\n" + //
				"\n" + //
				"# Splats:\n" + //
				"race = (winner, runners...) ->\n" + //
				"  print winner, runners\n" + //
				"\n" + //
				"# Existence:\n" + //
				"alert \"I knew it!\" if elvis?\n" + //
				"\n" + //
				"# Array comprehensions:\n" + //
				"cubes = (math.cube num for num in list)\n"; //

		String expected = "[IDENTIFIER number] [= =] [NUMBER 42] [TERMINATOR \\n] [IDENTIFIER opposite] [= =] [BOOL true] [TERMINATOR \\n] [IDENTIFIER number] [= =] [- -] [NUMBER 42] [POST_IF if] [IDENTIFIER opposite] [TERMINATOR \\n] [IDENTIFIER square] [= =] [PARAM_START (] [IDENTIFIER x] [PARAM_END )] [-> ->] [INDENT 2] [IDENTIFIER x] [MATH *] [IDENTIFIER x] [OUTDENT 2] [TERMINATOR \\n] [IDENTIFIER list] [= =] [[ [] [NUMBER 1] [, ,] [NUMBER 2] [, ,] [NUMBER 3] [, ,] [NUMBER 4] [, ,] [NUMBER 5] [] ]] [TERMINATOR \\n] [IDENTIFIER math] [= =] [INDENT 2] [{ {] [IDENTIFIER root] [: :] [IDENTIFIER Math] [. .] [IDENTIFIER sqrt] [TERMINATOR \\n] [IDENTIFIER square] [: :] [IDENTIFIER square] [TERMINATOR \\n] [IDENTIFIER cube] [: :] [PARAM_START (] [IDENTIFIER x] [PARAM_END )] [-> ->] [INDENT 2] [IDENTIFIER x] [MATH *] [IDENTIFIER square] [CALL_START (] [IDENTIFIER x] [CALL_END )] [OUTDENT 2] [} }] [OUTDENT 2] [TERMINATOR \\n] [IDENTIFIER race] [= =] [PARAM_START (] [IDENTIFIER winner] [, ,] [IDENTIFIER runners] [... ...] [PARAM_END )] [-> ->] [INDENT 2] [IDENTIFIER print] [CALL_START (] [IDENTIFIER winner] [, ,] [IDENTIFIER runners] [CALL_END )] [OUTDENT 2] [TERMINATOR \\n] [IDENTIFIER alert] [CALL_START (] [STRING \"I knew it!\"] [CALL_END )] [POST_IF if] [IDENTIFIER elvis] [? ?] [TERMINATOR \\n] [IDENTIFIER cubes] [= =] [( (] [IDENTIFIER math] [. .] [IDENTIFIER cube] [CALL_START (] [IDENTIFIER num] [CALL_END )] [FOR for] [IDENTIFIER num] [FORIN in] [IDENTIFIER list] [) )] [TERMINATOR \\n]";
		assertTokenList(source, expected);
	}

	@Test
	public void testFunctionsExample1() throws Exception
	{
		String source = "square = (x) -> x * x\n" + //
				"cube   = (x) -> square(x) * x\n"; //

		String expected = "[IDENTIFIER square] [= =] [PARAM_START (] [IDENTIFIER x] [PARAM_END )] [-> ->] [INDENT 2] [IDENTIFIER x] [MATH *] [IDENTIFIER x] [OUTDENT 2] [TERMINATOR \\n] [IDENTIFIER cube] [= =] [PARAM_START (] [IDENTIFIER x] [PARAM_END )] [-> ->] [INDENT 2] [IDENTIFIER square] [CALL_START (] [IDENTIFIER x] [CALL_END )] [MATH *] [IDENTIFIER x] [OUTDENT 2] [TERMINATOR \\n]";
		assertTokenList(source, expected);
	}

	@Test
	public void testFunctionsExample2() throws Exception
	{
		String source = "fill = (container, liquid = \"coffee\") ->\n" + //
				"  \"Filling the #{container} with #{liquid}...\"\n"; //

		String expected = "[IDENTIFIER fill] [= =] [PARAM_START (] [IDENTIFIER container] [, ,] [IDENTIFIER liquid] [= =] [STRING \"coffee\"] [PARAM_END )] [-> ->] [INDENT 2] [( (] [STRING \"Filling the \"] [+ +] [IDENTIFIER container] [+ +] [STRING \" with \"] [+ +] [IDENTIFIER liquid] [+ +] [STRING \"...\"] [) )] [OUTDENT 2] [TERMINATOR \\n]";
		assertTokenList(source, expected);
	}

	@Test
	public void testObjectsAndArraysExample1() throws Exception
	{
		String source = "song = [\"do\", \"re\", \"mi\", \"fa\", \"so\"]\n" + //
				"\n" + //
				"singers = {Jagger: \"Rock\", Elvis: \"Roll\"}\n" + //
				"\n" + //
				"bitlist = [\n" + //
				"  1, 0, 1\n" + //
				"  0, 0, 1\n" + //
				"  1, 1, 0\n" + //
				"]\n" + //
				"\n" + //
				"kids =\n" + //
				"  brother:\n" + //
				"    name: \"Max\"\n" + //
				"    age:  11\n" + //
				"  sister:\n" + //
				"    name: \"Ida\"\n" + //
				"    age:  9\n"; //

		String expected = "[IDENTIFIER song] [= =] [[ [] [STRING \"do\"] [, ,] [STRING \"re\"] [, ,] [STRING \"mi\"] [, ,] [STRING \"fa\"] [, ,] [STRING \"so\"] [] ]] [TERMINATOR \\n] [IDENTIFIER singers] [= =] [{ {] [IDENTIFIER Jagger] [: :] [STRING \"Rock\"] [, ,] [IDENTIFIER Elvis] [: :] [STRING \"Roll\"] [} }] [TERMINATOR \\n] [IDENTIFIER bitlist] [= =] [[ [] [INDENT 2] [NUMBER 1] [, ,] [NUMBER 0] [, ,] [NUMBER 1] [TERMINATOR \\n] [NUMBER 0] [, ,] [NUMBER 0] [, ,] [NUMBER 1] [TERMINATOR \\n] [NUMBER 1] [, ,] [NUMBER 1] [, ,] [NUMBER 0] [OUTDENT 2] [] ]] [TERMINATOR \\n] [IDENTIFIER kids] [= =] [INDENT 2] [{ {] [IDENTIFIER brother] [: :] [INDENT 2] [{ {] [IDENTIFIER name] [: :] [STRING \"Max\"] [TERMINATOR \\n] [IDENTIFIER age] [: :] [NUMBER 11] [} }] [OUTDENT 2] [TERMINATOR \\n] [IDENTIFIER sister] [: :] [INDENT 2] [{ {] [IDENTIFIER name] [: :] [STRING \"Ida\"] [TERMINATOR \\n] [IDENTIFIER age] [: :] [NUMBER 9] [} }] [OUTDENT 2] [} }] [OUTDENT 2] [TERMINATOR \\n]";
		assertTokenList(source, expected);
	}

	@Test
	public void testObjectsAndArraysExample2() throws Exception
	{
		String source = "$('.account').attr class: 'active'\n" + //
				"\n" + //
				"log object.class\n"; //

		String expected = "[IDENTIFIER $] [CALL_START (] [STRING '.account'] [CALL_END )] [. .] [IDENTIFIER attr] [CALL_START (] [{ {] [IDENTIFIER class] [: :] [STRING 'active'] [} }] [CALL_END )] [TERMINATOR \\n] [IDENTIFIER log] [CALL_START (] [IDENTIFIER object] [. .] [IDENTIFIER class] [CALL_END )] [TERMINATOR \\n]";
		assertTokenList(source, expected);
	}

	@Test
	public void testLexicalScopingExample1() throws Exception
	{
		String source = "outer = 1\n" + //
				"changeNumbers = ->\n" + //
				"  inner = -1\n" + //
				"  outer = 10\n" + //
				"inner = changeNumbers()\n"; //

		String expected = "[IDENTIFIER outer] [= =] [NUMBER 1] [TERMINATOR \\n] [IDENTIFIER changeNumbers] [= =] [-> ->] [INDENT 2] [IDENTIFIER inner] [= =] [- -] [NUMBER 1] [TERMINATOR \\n] [IDENTIFIER outer] [= =] [NUMBER 10] [OUTDENT 2] [TERMINATOR \\n] [IDENTIFIER inner] [= =] [IDENTIFIER changeNumbers] [CALL_START (] [CALL_END )] [TERMINATOR \\n]";
		assertTokenList(source, expected);
	}

	@Test
	public void testConditionalsExample1() throws Exception
	{
		String source = "mood = greatlyImproved if singing\n" + //
				"\n" + //
				"if happy and knowsIt\n" + //
				"  clapsHands()\n" + //
				"  chaChaCha()\n" + //
				"else\n" + //
				"  showIt()\n" + //
				"\n" + //
				"date = if friday then sue else jill\n" + //
				"\n" + //
				"options or= defaults\n"; //

		String expected = "[IDENTIFIER mood] [= =] [IDENTIFIER greatlyImproved] [POST_IF if] [IDENTIFIER singing] [TERMINATOR \\n] [IF if] [IDENTIFIER happy] [LOGIC &&] [IDENTIFIER knowsIt] [INDENT 2] [IDENTIFIER clapsHands] [CALL_START (] [CALL_END )] [TERMINATOR \\n] [IDENTIFIER chaChaCha] [CALL_START (] [CALL_END )] [OUTDENT 2] [ELSE else] [INDENT 2] [IDENTIFIER showIt] [CALL_START (] [CALL_END )] [OUTDENT 2] [TERMINATOR \\n] [IDENTIFIER date] [= =] [IF if] [IDENTIFIER friday] [INDENT 2] [IDENTIFIER sue] [OUTDENT 2] [ELSE else] [INDENT 2] [IDENTIFIER jill] [OUTDENT 2] [TERMINATOR \\n] [IDENTIFIER options] [COMPOUND_ASSIGN ||=] [IDENTIFIER defaults] [TERMINATOR \\n]";
		assertTokenList(source, expected);
	}

	@Test
	public void testSplatsExample1() throws Exception
	{
		String source = "gold = silver = rest = \"unknown\"\n" + //
				"\n" + //
				"awardMedals = (first, second, others...) ->\n" + //
				"  gold   = first\n" + //
				"  silver = second\n" + //
				"  rest   = others\n" + //
				"\n" + //
				"contenders = [\n" + //
				"  \"Michael Phelps\"\n" + //
				"  \"Liu Xiang\"\n" + //
				"  \"Yao Ming\"\n" + //
				"  \"Allyson Felix\"\n" + //
				"  \"Shawn Johnson\"\n" + //
				"  \"Roman Sebrle\"\n" + //
				"  \"Guo Jingjing\"\n" + //
				"  \"Tyson Gay\"\n" + //
				"  \"Asafa Powell\"\n" + //
				"  \"Usain Bolt\"\n" + //
				"]\n" + //
				"\n" + //
				"awardMedals contenders...\n" + //
				"\n" + //
				"alert \"Gold: \" + gold\n" + //
				"alert \"Silver: \" + silver\n" + //
				"alert \"The Field: \" + rest\n"; //

		String expected = "[IDENTIFIER gold] [= =] [IDENTIFIER silver] [= =] [IDENTIFIER rest] [= =] [STRING \"unknown\"] [TERMINATOR \\n] [IDENTIFIER awardMedals] [= =] [PARAM_START (] [IDENTIFIER first] [, ,] [IDENTIFIER second] [, ,] [IDENTIFIER others] [... ...] [PARAM_END )] [-> ->] [INDENT 2] [IDENTIFIER gold] [= =] [IDENTIFIER first] [TERMINATOR \\n] [IDENTIFIER silver] [= =] [IDENTIFIER second] [TERMINATOR \\n] [IDENTIFIER rest] [= =] [IDENTIFIER others] [OUTDENT 2] [TERMINATOR \\n] [IDENTIFIER contenders] [= =] [[ [] [INDENT 2] [STRING \"Michael Phelps\"] [TERMINATOR \\n] [STRING \"Liu Xiang\"] [TERMINATOR \\n] [STRING \"Yao Ming\"] [TERMINATOR \\n] [STRING \"Allyson Felix\"] [TERMINATOR \\n] [STRING \"Shawn Johnson\"] [TERMINATOR \\n] [STRING \"Roman Sebrle\"] [TERMINATOR \\n] [STRING \"Guo Jingjing\"] [TERMINATOR \\n] [STRING \"Tyson Gay\"] [TERMINATOR \\n] [STRING \"Asafa Powell\"] [TERMINATOR \\n] [STRING \"Usain Bolt\"] [OUTDENT 2] [] ]] [TERMINATOR \\n] [IDENTIFIER awardMedals] [CALL_START (] [IDENTIFIER contenders] [... ...] [CALL_END )] [TERMINATOR \\n] [IDENTIFIER alert] [CALL_START (] [STRING \"Gold: \"] [+ +] [IDENTIFIER gold] [CALL_END )] [TERMINATOR \\n] [IDENTIFIER alert] [CALL_START (] [STRING \"Silver: \"] [+ +] [IDENTIFIER silver] [CALL_END )] [TERMINATOR \\n] [IDENTIFIER alert] [CALL_START (] [STRING \"The Field: \"] [+ +] [IDENTIFIER rest] [CALL_END )] [TERMINATOR \\n]";
		assertTokenList(source, expected);
	}

	@Test
	public void testLoopsExample1() throws Exception
	{
		String source = "# Eat lunch.\n" + //
				"eat food for food in ['toast', 'cheese', 'wine']\n"; //

		String expected = "[IDENTIFIER eat] [CALL_START (] [IDENTIFIER food] [CALL_END )] [FOR for] [IDENTIFIER food] [FORIN in] [[ [] [STRING 'toast'] [, ,] [STRING 'cheese'] [, ,] [STRING 'wine'] [] ]] [TERMINATOR \\n]";
		assertTokenList(source, expected);
	}

	@Test
	public void testLoopsExample2() throws Exception
	{
		String source = "countdown = (num for num in [10..1])"; //

		String expected = "[IDENTIFIER countdown] [= =] [( (] [IDENTIFIER num] [FOR for] [IDENTIFIER num] [FORIN in] [[ [] [NUMBER 10] [.. ..] [NUMBER 1] [] ]] [) )] [TERMINATOR \\n]";
		assertTokenList(source, expected);
	}

	@Test
	public void testLoopsExample3() throws Exception
	{
		String source = "yearsOld = max: 10, ida: 9, tim: 11\n" + //
				"\n" + //
				"ages = for child, age of yearsOld\n" + //
				"  child + \" is \" + age\n"; //

		String expected = "[IDENTIFIER yearsOld] [= =] [{ {] [IDENTIFIER max] [: :] [NUMBER 10] [, ,] [IDENTIFIER ida] [: :] [NUMBER 9] [, ,] [IDENTIFIER tim] [: :] [NUMBER 11] [} }] [TERMINATOR \\n] [IDENTIFIER ages] [= =] [FOR for] [IDENTIFIER child] [, ,] [IDENTIFIER age] [FOROF of] [IDENTIFIER yearsOld] [INDENT 2] [IDENTIFIER child] [+ +] [STRING \" is \"] [+ +] [IDENTIFIER age] [OUTDENT 2] [TERMINATOR \\n]";
		assertTokenList(source, expected);
	}

	@Test
	public void testLoopsExample4() throws Exception
	{
		String source = "# Econ 101\n" + //
				"if this.studyingEconomics\n" + //
				"  buy()  while supply > demand\n" + //
				"  sell() until supply > demand\n" + //
				"\n" + //
				"# Nursery Rhyme\n" + //
				"num = 6\n" + //
				"lyrics = while num -= 1\n" + //
				"  num + \" little monkeys, jumping on the bed.\n" + //
				"    One fell out and bumped his head.\"\n"; //

		String expected = "[IF if] [THIS this] [. .] [IDENTIFIER studyingEconomics] [INDENT 2] [IDENTIFIER buy] [CALL_START (] [CALL_END )] [WHILE while] [IDENTIFIER supply] [COMPARE >] [IDENTIFIER demand] [TERMINATOR \\n] [IDENTIFIER sell] [CALL_START (] [CALL_END )] [UNTIL until] [IDENTIFIER supply] [COMPARE >] [IDENTIFIER demand] [OUTDENT 2] [TERMINATOR \\n] [IDENTIFIER num] [= =] [NUMBER 6] [TERMINATOR \\n] [IDENTIFIER lyrics] [= =] [WHILE while] [IDENTIFIER num] [COMPOUND_ASSIGN -=] [NUMBER 1] [INDENT 2] [IDENTIFIER num] [+ +] [STRING \" little monkeys, jumping on the bed.    One fell out and bumped his head.\"] [OUTDENT 2] [TERMINATOR \\n]";
		assertTokenList(source, expected);
	}

	@Test
	public void testLoopsExample5() throws Exception
	{
		String source = "for filename in list\n" + //
				"  do (filename) ->\n" + //
				"    fs.readFile filename, (err, contents) ->\n" + //
				"      compile filename, contents.toString()\n"; //

		// FIXME I had to change the output a little bit since in some rare cases they return "[CALL_END CALL_END]"
		// rather than "[CALL_END )]"
		String expected = "[FOR for] [IDENTIFIER filename] [FORIN in] [IDENTIFIER list] [INDENT 2] [UNARY do] [PARAM_START (] [IDENTIFIER filename] [PARAM_END )] [-> ->] [INDENT 2] [IDENTIFIER fs] [. .] [IDENTIFIER readFile] [CALL_START (] [IDENTIFIER filename] [, ,] [PARAM_START (] [IDENTIFIER err] [, ,] [IDENTIFIER contents] [PARAM_END )] [-> ->] [INDENT 2] [IDENTIFIER compile] [CALL_START (] [IDENTIFIER filename] [, ,] [IDENTIFIER contents] [. .] [IDENTIFIER toString] [CALL_START (] [CALL_END )] [CALL_END )] [OUTDENT 2] [CALL_END )] [OUTDENT 2] [OUTDENT 2] [TERMINATOR \\n]";
		assertTokenList(source, expected);
	}

	@Test
	public void testSliceAndSpliceExample1() throws Exception
	{
		String source = "numbers = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9]\n" + //
				"\n" + //
				"copy    = numbers[0...numbers.length]\n" + //
				"\n" + //
				"middle  = copy[3..6]\n"; //

		String expected = "[IDENTIFIER numbers] [= =] [[ [] [NUMBER 0] [, ,] [NUMBER 1] [, ,] [NUMBER 2] [, ,] [NUMBER 3] [, ,] [NUMBER 4] [, ,] [NUMBER 5] [, ,] [NUMBER 6] [, ,] [NUMBER 7] [, ,] [NUMBER 8] [, ,] [NUMBER 9] [] ]] [TERMINATOR \\n] [IDENTIFIER copy] [= =] [IDENTIFIER numbers] [INDEX_START [] [NUMBER 0] [... ...] [IDENTIFIER numbers] [. .] [IDENTIFIER length] [INDEX_END ]] [TERMINATOR \\n] [IDENTIFIER middle] [= =] [IDENTIFIER copy] [INDEX_START [] [NUMBER 3] [.. ..] [NUMBER 6] [INDEX_END ]] [TERMINATOR \\n]";
		assertTokenList(source, expected);
	}

	@Test
	public void testSliceAndSpliceExample2() throws Exception
	{
		String source = "numbers = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9]\n" + //
				"\n" + //
				"numbers[3..6] = [-3, -4, -5, -6]\n"; //

		String expected = "[IDENTIFIER numbers] [= =] [[ [] [NUMBER 0] [, ,] [NUMBER 1] [, ,] [NUMBER 2] [, ,] [NUMBER 3] [, ,] [NUMBER 4] [, ,] [NUMBER 5] [, ,] [NUMBER 6] [, ,] [NUMBER 7] [, ,] [NUMBER 8] [, ,] [NUMBER 9] [] ]] [TERMINATOR \\n] [IDENTIFIER numbers] [INDEX_START [] [NUMBER 3] [.. ..] [NUMBER 6] [INDEX_END ]] [= =] [[ [] [- -] [NUMBER 3] [, ,] [- -] [NUMBER 4] [, ,] [- -] [NUMBER 5] [, ,] [- -] [NUMBER 6] [] ]] [TERMINATOR \\n]";
		assertTokenList(source, expected);
	}

	@Test
	public void testEverythingIsAnExpressionExample1() throws Exception
	{
		String source = "grade = (student) ->\n" + //
				"  if student.excellentWork\n" + //
				"    \"A+\"\n" + //
				"  else if student.okayStuff\n" + //
				"    if student.triedHard then \"B\" else \"B-\"\n" + //
				"  else\n" + //
				"    \"C\"\n" + //
				"\n" + //
				"eldest = if 24 > 21 then \"Liz\" else \"Ike\"\n"; //

		String expected = "[IDENTIFIER grade] [= =] [PARAM_START (] [IDENTIFIER student] [PARAM_END )] [-> ->] [INDENT 2] [IF if] [IDENTIFIER student] [. .] [IDENTIFIER excellentWork] [INDENT 2] [STRING \"A+\"] [OUTDENT 2] [ELSE else] [IF if] [IDENTIFIER student] [. .] [IDENTIFIER okayStuff] [INDENT 2] [IF if] [IDENTIFIER student] [. .] [IDENTIFIER triedHard] [INDENT 2] [STRING \"B\"] [OUTDENT 2] [ELSE else] [INDENT 2] [STRING \"B-\"] [OUTDENT 2] [OUTDENT 2] [ELSE else] [INDENT 2] [STRING \"C\"] [OUTDENT 2] [OUTDENT 2] [TERMINATOR \\n] [IDENTIFIER eldest] [= =] [IF if] [NUMBER 24] [COMPARE >] [NUMBER 21] [INDENT 2] [STRING \"Liz\"] [OUTDENT 2] [ELSE else] [INDENT 2] [STRING \"Ike\"] [OUTDENT 2] [TERMINATOR \\n]";
		assertTokenList(source, expected);
	}

	@Test
	public void testEverythingIsAnExpressionExample2() throws Exception
	{
		String source = "six = (one = 1) + (two = 2) + (three = 3)\n"; //

		String expected = "[IDENTIFIER six] [= =] [( (] [IDENTIFIER one] [= =] [NUMBER 1] [) )] [+ +] [( (] [IDENTIFIER two] [= =] [NUMBER 2] [) )] [+ +] [( (] [IDENTIFIER three] [= =] [NUMBER 3] [) )] [TERMINATOR \\n]";
		assertTokenList(source, expected);
	}

	@Test
	public void testEverythingIsAnExpressionExample3() throws Exception
	{
		String source = "# The first ten global properties.\n" + //
				"\n" + //
				"globals = (name for name of window)[0...10]\n"; //

		String expected = "[IDENTIFIER globals] [= =] [( (] [IDENTIFIER name] [FOR for] [IDENTIFIER name] [FOROF of] [IDENTIFIER window] [) )] [INDEX_START [] [NUMBER 0] [... ...] [NUMBER 10] [INDEX_END ]] [TERMINATOR \\n]";
		assertTokenList(source, expected);
	}

	@Test
	public void testEverythingIsAnExpressionExample4() throws Exception
	{
		String source = "alert(\n" + //
				"  try\n" + //
				"    nonexistent / undefined\n" + //
				"  catch error\n" + //
				"    \"And the error is ... \" + error\n" + //
				")\n"; //

		String expected = "[IDENTIFIER alert] [CALL_START (] [INDENT 2] [TRY try] [INDENT 2] [IDENTIFIER nonexistent] [MATH /] [BOOL undefined] [OUTDENT 2] [CATCH catch] [IDENTIFIER error] [INDENT 2] [STRING \"And the error is ... \"] [+ +] [IDENTIFIER error] [OUTDENT 2] [OUTDENT 2] [CALL_END )] [TERMINATOR \\n]";
		assertTokenList(source, expected);
	}

	@Test
	public void testOperatorsAndAliasesExample1() throws Exception
	{
		String source = "launch() if ignition is on\n" + //
				"\n" + //
				"volume = 10 if band isnt SpinalTap\n" + //
				"\n" + //
				"letTheWildRumpusBegin() unless answer is no\n" + //
				"\n" + //
				"if car.speed < limit then accelerate()\n" + //
				"\n" + //
				"winner = yes if pick in [47, 92, 13]\n" + //
				"\n" + //
				"print inspect \"My name is \" + @name\n"; //

		String expected = "[IDENTIFIER launch] [CALL_START (] [CALL_END )] [POST_IF if] [IDENTIFIER ignition] [COMPARE ==] [BOOL true] [TERMINATOR \\n] [IDENTIFIER volume] [= =] [NUMBER 10] [POST_IF if] [IDENTIFIER band] [COMPARE !=] [IDENTIFIER SpinalTap] [TERMINATOR \\n] [IDENTIFIER letTheWildRumpusBegin] [CALL_START (] [CALL_END )] [POST_IF unless] [IDENTIFIER answer] [COMPARE ==] [BOOL false] [TERMINATOR \\n] [IF if] [IDENTIFIER car] [. .] [IDENTIFIER speed] [COMPARE <] [IDENTIFIER limit] [INDENT 2] [IDENTIFIER accelerate] [CALL_START (] [CALL_END )] [OUTDENT 2] [TERMINATOR \\n] [IDENTIFIER winner] [= =] [BOOL true] [POST_IF if] [IDENTIFIER pick] [RELATION in] [[ [] [NUMBER 47] [, ,] [NUMBER 92] [, ,] [NUMBER 13] [] ]] [TERMINATOR \\n] [IDENTIFIER print] [CALL_START (] [IDENTIFIER inspect] [CALL_START (] [STRING \"My name is \"] [+ +] [@ @] [IDENTIFIER name] [CALL_END )] [CALL_END )] [TERMINATOR \\n]";
		assertTokenList(source, expected);
	}

	@Test
	public void testExistentialExample1() throws Exception
	{
		String source = "solipsism = true if mind? and not world?\n" + //
				"\n" + //
				"speed ?= 75\n" + //
				"\n" + //
				"footprints = yeti ? \"bear\"\n"; //

		String expected = "[IDENTIFIER solipsism] [= =] [BOOL true] [POST_IF if] [IDENTIFIER mind] [? ?] [LOGIC &&] [UNARY !] [IDENTIFIER world] [? ?] [TERMINATOR \\n] [IDENTIFIER speed] [COMPOUND_ASSIGN ?=] [NUMBER 75] [TERMINATOR \\n] [IDENTIFIER footprints] [= =] [IDENTIFIER yeti] [LOGIC ?] [STRING \"bear\"] [TERMINATOR \\n]";
		assertTokenList(source, expected);
	}

	@Test
	public void testExistentialExample2() throws Exception
	{
		String source = "zip = lottery.drawWinner?().address?.zipcode\n"; //

		String expected = "[IDENTIFIER zip] [= =] [IDENTIFIER lottery] [. .] [IDENTIFIER drawWinner] [FUNC_EXIST ?] [CALL_START (] [CALL_END )] [. .] [IDENTIFIER address] [?. ?.] [IDENTIFIER zipcode] [TERMINATOR \\n]";
		assertTokenList(source, expected);
	}

	@Test
	public void testClassesExample1() throws Exception
	{
		String source = "class Animal\n" + //
				"  constructor: (@name) ->\n" + //
				"\n" + //
				"  move: (meters) ->\n" + //
				"    alert @name + \" moved \" + meters + \"m.\"\n" + //
				"\n" + //
				"class Snake extends Animal\n" + //
				"  move: ->\n" + //
				"    alert \"Slithering...\"\n" + //
				"    super 5\n" + //
				"\n" + //
				"class Horse extends Animal\n" + //
				"  move: ->\n" + //
				"    alert \"Galloping...\"\n" + //
				"    super 45\n" + //
				"\n" + //
				"sam = new Snake \"Sammy the Python\"\n" + //
				"tom = new Horse \"Tommy the Palomino\"\n" + //
				"\n" + //
				"sam.move()\n" + //
				"tom.move()\n"; //

		String expected = "[CLASS class] [IDENTIFIER Animal] [INDENT 2] [{ {] [IDENTIFIER constructor] [: :] [PARAM_START (] [@ @] [IDENTIFIER name] [PARAM_END )] [-> ->] [INDENT 2] [OUTDENT 2] [TERMINATOR \\n] [IDENTIFIER move] [: :] [PARAM_START (] [IDENTIFIER meters] [PARAM_END )] [-> ->] [INDENT 2] [IDENTIFIER alert] [CALL_START (] [@ @] [IDENTIFIER name] [+ +] [STRING \" moved \"] [+ +] [IDENTIFIER meters] [+ +] [STRING \"m.\"] [CALL_END )] [OUTDENT 2] [} }] [OUTDENT 2] [TERMINATOR \\n] [CLASS class] [IDENTIFIER Snake] [EXTENDS extends] [IDENTIFIER Animal] [INDENT 2] [{ {] [IDENTIFIER move] [: :] [-> ->] [INDENT 2] [IDENTIFIER alert] [CALL_START (] [STRING \"Slithering...\"] [CALL_END )] [TERMINATOR \\n] [SUPER super] [CALL_START (] [NUMBER 5] [CALL_END )] [OUTDENT 2] [} }] [OUTDENT 2] [TERMINATOR \\n] [CLASS class] [IDENTIFIER Horse] [EXTENDS extends] [IDENTIFIER Animal] [INDENT 2] [{ {] [IDENTIFIER move] [: :] [-> ->] [INDENT 2] [IDENTIFIER alert] [CALL_START (] [STRING \"Galloping...\"] [CALL_END )] [TERMINATOR \\n] [SUPER super] [CALL_START (] [NUMBER 45] [CALL_END )] [OUTDENT 2] [} }] [OUTDENT 2] [TERMINATOR \\n] [IDENTIFIER sam] [= =] [UNARY new] [IDENTIFIER Snake] [CALL_START (] [STRING \"Sammy the Python\"] [CALL_END )] [TERMINATOR \\n] [IDENTIFIER tom] [= =] [UNARY new] [IDENTIFIER Horse] [CALL_START (] [STRING \"Tommy the Palomino\"] [CALL_END )] [TERMINATOR \\n] [IDENTIFIER sam] [. .] [IDENTIFIER move] [CALL_START (] [CALL_END )] [TERMINATOR \\n] [IDENTIFIER tom] [. .] [IDENTIFIER move] [CALL_START (] [CALL_END )] [TERMINATOR \\n]";
		assertTokenList(source, expected);
	}

	@Test
	public void testClassesExample2() throws Exception
	{
		String source = "String::dasherize = ->\n" + //
				"  this.replace /_/g, \"-\"\n"; //

		String expected = "[IDENTIFIER String] [:: ::] [IDENTIFIER dasherize] [= =] [-> ->] [INDENT 2] [THIS this] [. .] [IDENTIFIER replace] [CALL_START (] [REGEX /_/g] [, ,] [STRING \"-\"] [CALL_END )] [OUTDENT 2] [TERMINATOR \\n]";
		assertTokenList(source, expected);
	}

	@Test
	public void testDestructuringAssignmentExample1() throws Exception
	{
		String source = "theBait   = 1000\n" + //
				"theSwitch = 0\n" + //
				"\n" + //
				"[theBait, theSwitch] = [theSwitch, theBait]\n"; //

		String expected = "[IDENTIFIER theBait] [= =] [NUMBER 1000] [TERMINATOR \\n] [IDENTIFIER theSwitch] [= =] [NUMBER 0] [TERMINATOR \\n] [[ [] [IDENTIFIER theBait] [, ,] [IDENTIFIER theSwitch] [] ]] [= =] [[ [] [IDENTIFIER theSwitch] [, ,] [IDENTIFIER theBait] [] ]] [TERMINATOR \\n]";
		assertTokenList(source, expected);
	}

	@Test
	public void testDestructuringAssignmentExample2() throws Exception
	{
		String source = "weatherReport = (location) ->\n" + //
				"  # Make an Ajax request to fetch the weather...\n" + //
				"  [location, 72, \"Mostly Sunny\"]\n" + //
				"\n" + //
				"[city, temp, forecast] = weatherReport \"Berkeley, CA\"\n"; //

		String expected = "[IDENTIFIER weatherReport] [= =] [PARAM_START (] [IDENTIFIER location] [PARAM_END )] [-> ->] [INDENT 2] [[ [] [IDENTIFIER location] [, ,] [NUMBER 72] [, ,] [STRING \"Mostly Sunny\"] [] ]] [OUTDENT 2] [TERMINATOR \\n] [[ [] [IDENTIFIER city] [, ,] [IDENTIFIER temp] [, ,] [IDENTIFIER forecast] [] ]] [= =] [IDENTIFIER weatherReport] [CALL_START (] [STRING \"Berkeley, CA\"] [CALL_END )] [TERMINATOR \\n]";
		assertTokenList(source, expected);
	}

	@Test
	public void testDestructuringAssignmentExample3() throws Exception
	{
		String source = "futurists =\n" + //
				"  sculptor: \"Umberto Boccioni\"\n" + //
				"  painter:  \"Vladimir Burliuk\"\n" + //
				"  poet:\n" + //
				"    name:   \"F.T. Marinetti\"\n" + //
				"    address: [\n" + //
				"      \"Via Roma 42R\"\n" + //
				"      \"Bellagio, Italy 22021\"\n" + //
				"    ]\n" + //
				"\n" + //
				"{poet: {name, address: [street, city]}} = futurists\n"; //

		String expected = "[IDENTIFIER futurists] [= =] [INDENT 2] [{ {] [IDENTIFIER sculptor] [: :] [STRING \"Umberto Boccioni\"] [TERMINATOR \\n] [IDENTIFIER painter] [: :] [STRING \"Vladimir Burliuk\"] [TERMINATOR \\n] [IDENTIFIER poet] [: :] [INDENT 2] [{ {] [IDENTIFIER name] [: :] [STRING \"F.T. Marinetti\"] [TERMINATOR \\n] [IDENTIFIER address] [: :] [[ [] [INDENT 2] [STRING \"Via Roma 42R\"] [TERMINATOR \\n] [STRING \"Bellagio, Italy 22021\"] [OUTDENT 2] [] ]] [} }] [OUTDENT 2] [} }] [OUTDENT 2] [TERMINATOR \\n] [{ {] [IDENTIFIER poet] [: :] [{ {] [IDENTIFIER name] [, ,] [IDENTIFIER address] [: :] [[ [] [IDENTIFIER street] [, ,] [IDENTIFIER city] [] ]] [} }] [} }] [= =] [IDENTIFIER futurists] [TERMINATOR \\n]";
		assertTokenList(source, expected);
	}

	@Test
	public void testDestructuringAssignmentExample4() throws Exception
	{
		String source = "tag = \"<impossible>\"\n" + //
				"\n" + //
				"[open, contents..., close] = tag.split(\"\")\n"; //

		String expected = "[IDENTIFIER tag] [= =] [STRING \"<impossible>\"] [TERMINATOR \\n] [[ [] [IDENTIFIER open] [, ,] [IDENTIFIER contents] [... ...] [, ,] [IDENTIFIER close] [] ]] [= =] [IDENTIFIER tag] [. .] [IDENTIFIER split] [CALL_START (] [STRING \"\"] [CALL_END )] [TERMINATOR \\n]";
		assertTokenList(source, expected);
	}

	@Test
	public void testFunctionBindingExample1() throws Exception
	{
		String source = "Account = (customer, cart) ->\n" + //
				"  @customer = customer\n" + //
				"  @cart = cart\n" + //
				"\n" + //
				"  $('.shopping_cart').bind 'click', (event) =>\n" + //
				"    @customer.purchase @cart\n"; //

		String expected = "[IDENTIFIER Account] [= =] [PARAM_START (] [IDENTIFIER customer] [, ,] [IDENTIFIER cart] [PARAM_END )] [-> ->] [INDENT 2] [@ @] [IDENTIFIER customer] [= =] [IDENTIFIER customer] [TERMINATOR \\n] [@ @] [IDENTIFIER cart] [= =] [IDENTIFIER cart] [TERMINATOR \\n] [IDENTIFIER $] [CALL_START (] [STRING '.shopping_cart'] [CALL_END )] [. .] [IDENTIFIER bind] [CALL_START (] [STRING 'click'] [, ,] [PARAM_START (] [IDENTIFIER event] [PARAM_END )] [=> =>] [INDENT 2] [@ @] [IDENTIFIER customer] [. .] [IDENTIFIER purchase] [CALL_START (] [@ @] [IDENTIFIER cart] [CALL_END )] [OUTDENT 2] [CALL_END )] [OUTDENT 2] [TERMINATOR \\n]";
		assertTokenList(source, expected);
	}

	@Test
	public void testEmbeddedJSExample1() throws Exception
	{
		String source = "hi = `function() {\n" + //
				"  return [document.title, \"Hello JavaScript\"].join(\": \");\n" + //
				"}`\n"; //

		String expected = "[IDENTIFIER hi] [= =] [JS function() {\n  return [document.title, \"Hello JavaScript\"].join(\": \");\n}] [TERMINATOR \\n]";
		assertTokenList(source, expected);
	}

	@Test
	public void testSwitchWhenElseExample1() throws Exception
	{
		String source = "switch day\n" + //
				"  when \"Mon\" then go work\n" + //
				"  when \"Tue\" then go relax\n" + //
				"  when \"Thu\" then go iceFishing\n" + //
				"  when \"Fri\", \"Sat\"\n" + //
				"    if day is bingoDay\n" + //
				"      go bingo\n" + //
				"      go dancing\n" + //
				"  when \"Sun\" then go church\n" + //
				"  else go work\n"; //

		// FIXME I had to change the output a little bit since in some rare cases they return "[CALL_END CALL_END]"
		// rather than "[CALL_END )]"
		String expected = "[SWITCH switch] [IDENTIFIER day] [INDENT 2] [LEADING_WHEN when] [STRING \"Mon\"] [INDENT 2] [IDENTIFIER go] [CALL_START (] [IDENTIFIER work] [CALL_END )] [OUTDENT 2] [TERMINATOR \\n] [LEADING_WHEN when] [STRING \"Tue\"] [INDENT 2] [IDENTIFIER go] [CALL_START (] [IDENTIFIER relax] [CALL_END )] [OUTDENT 2] [TERMINATOR \\n] [LEADING_WHEN when] [STRING \"Thu\"] [INDENT 2] [IDENTIFIER go] [CALL_START (] [IDENTIFIER iceFishing] [CALL_END )] [OUTDENT 2] [TERMINATOR \\n] [LEADING_WHEN when] [STRING \"Fri\"] [, ,] [STRING \"Sat\"] [INDENT 2] [IF if] [IDENTIFIER day] [COMPARE ==] [IDENTIFIER bingoDay] [INDENT 2] [IDENTIFIER go] [CALL_START (] [IDENTIFIER bingo] [CALL_END )] [TERMINATOR \\n] [IDENTIFIER go] [CALL_START (] [IDENTIFIER dancing] [CALL_END )] [OUTDENT 2] [OUTDENT 2] [TERMINATOR \\n] [LEADING_WHEN when] [STRING \"Sun\"] [INDENT 2] [IDENTIFIER go] [CALL_START (] [IDENTIFIER church] [CALL_END )] [OUTDENT 2] [ELSE else] [INDENT 2] [IDENTIFIER go] [CALL_START (] [IDENTIFIER work] [CALL_END )] [OUTDENT 2] [OUTDENT 2] [TERMINATOR \\n]";
		assertTokenList(source, expected);
	}

	@Test
	public void testTryCatchFinallyExample1() throws Exception
	{
		String source = "try\n" + //
				"  allHellBreaksLoose()\n" + //
				"  catsAndDogsLivingTogether()\n" + //
				"catch error\n" + //
				"  print error\n" + //
				"finally\n" + //
				"  cleanUp()\n"; //

		String expected = "[TRY try] [INDENT 2] [IDENTIFIER allHellBreaksLoose] [CALL_START (] [CALL_END )] [TERMINATOR \\n] [IDENTIFIER catsAndDogsLivingTogether] [CALL_START (] [CALL_END )] [OUTDENT 2] [CATCH catch] [IDENTIFIER error] [INDENT 2] [IDENTIFIER print] [CALL_START (] [IDENTIFIER error] [CALL_END )] [OUTDENT 2] [FINALLY finally] [INDENT 2] [IDENTIFIER cleanUp] [CALL_START (] [CALL_END )] [OUTDENT 2] [TERMINATOR \\n]";
		assertTokenList(source, expected);
	}

	@Test
	public void testChainedComparisonsExample1() throws Exception
	{
		String source = "cholesterol = 127\n" + //
				"\n" + //
				"healthy = 200 > cholesterol > 60\n"; //

		String expected = "[IDENTIFIER cholesterol] [= =] [NUMBER 127] [TERMINATOR \\n] [IDENTIFIER healthy] [= =] [NUMBER 200] [COMPARE >] [IDENTIFIER cholesterol] [COMPARE >] [NUMBER 60] [TERMINATOR \\n]";
		assertTokenList(source, expected);
	}

	// TODO Add tests for String Interpolation examples

	@Test
	public void testExtendedRegexExample1() throws Exception
	{
		String source = "OPERATOR = /// ^ (\n" + //
				"  ?: [-=]>             # function\n" + //
				"   | [-+*/%<>&|^!?=]=  # compound assign / compare\n" + //
				"   | >>>=?             # zero-fill right shift\n" + //
				"   | ([-+:])\\1         # doubles\n" + //
				"   | ([&|<>])\\2=?      # logic / shift\n" + //
				"   | \\?\\.              # soak access\n" + //
				"   | \\.{2,3}           # range or splat\n" + //
				") ///\n"; //

		String expected = "[IDENTIFIER OPERATOR] [= =] [REGEX /^(?:[-=]>|[-+*\\/%<>&|^!?=]=|>>>=?|([-+:])\\1|([&|<>])\\2=?|\\?\\.|\\.{2,3})/] [TERMINATOR \\n]";
		assertTokenList(source, expected);
	}

	@Test
	public void testCakeExample1() throws Exception
	{
		String source = "fs = require 'fs'\n" + //
				"\n" + //
				"option '-o', '--output [DIR]', 'directory for compiled code'\n" + //
				"\n" + //
				"task 'build:parser', 'rebuild the Jison parser', (options) ->\n" + //
				"  require 'jison'\n" + //
				"  code = require('./lib/grammar').parser.generate()\n" + //
				"  dir  = options.output or 'lib'\n" + //
				"  fs.writeFile \"#{dir}/parser.js\", code\n"; //

		String expected = "[IDENTIFIER fs] [= =] [IDENTIFIER require] [CALL_START (] [STRING 'fs'] [CALL_END )] [TERMINATOR \\n] [IDENTIFIER option] [CALL_START (] [STRING '-o'] [, ,] [STRING '--output [DIR]'] [, ,] [STRING 'directory for compiled code'] [CALL_END )] [TERMINATOR \\n] [IDENTIFIER task] [CALL_START (] [STRING 'build:parser'] [, ,] [STRING 'rebuild the Jison parser'] [, ,] [PARAM_START (] [IDENTIFIER options] [PARAM_END )] [-> ->] [INDENT 2] [IDENTIFIER require] [CALL_START (] [STRING 'jison'] [CALL_END )] [TERMINATOR \\n] [IDENTIFIER code] [= =] [IDENTIFIER require] [CALL_START (] [STRING './lib/grammar'] [CALL_END )] [. .] [IDENTIFIER parser] [. .] [IDENTIFIER generate] [CALL_START (] [CALL_END )] [TERMINATOR \\n] [IDENTIFIER dir] [= =] [IDENTIFIER options] [. .] [IDENTIFIER output] [LOGIC ||] [STRING 'lib'] [TERMINATOR \\n] [IDENTIFIER fs] [. .] [IDENTIFIER writeFile] [CALL_START (] [( (] [STRING \"\"] [+ +] [IDENTIFIER dir] [+ +] [STRING \"/parser.js\"] [) )] [, ,] [IDENTIFIER code] [CALL_END )] [OUTDENT 2] [CALL_END )] [TERMINATOR \\n]";
		assertTokenList(source, expected);
	}

	@Test
	public void testSlashNewlineContinuation() throws Exception
	{
		// @formatter:off
		String source = 
		  "isLeech: (card) ->\n" +
	      "  _no = card.noCount\n" +
	      "  fmax = @getInt(\"leechFails\")\n" +
	      "  return false  unless fmax\n" +
	      "  not card.successive and _no >= fmax and \\\n" +
	      "    (fmax - _no) % (Math.max(fmax / 2, 1)) == 0";
        // @formatter:on

		String expected = "[{ {] [IDENTIFIER isLeech] [: :] [PARAM_START (] [IDENTIFIER card] [PARAM_END )] [-> ->] [INDENT 2] [IDENTIFIER _no] [= =] [IDENTIFIER card] [. .] [IDENTIFIER noCount] [TERMINATOR \\n] [IDENTIFIER fmax] [= =] [@ @] [IDENTIFIER getInt] [CALL_START (] [STRING \"leechFails\"] [CALL_END )] [TERMINATOR \\n] [RETURN return] [BOOL false] [POST_IF unless] [IDENTIFIER fmax] [TERMINATOR \\n] [UNARY !] [IDENTIFIER card] [. .] [IDENTIFIER successive] [LOGIC &&] [IDENTIFIER _no] [COMPARE >=] [IDENTIFIER fmax] [LOGIC &&] [( (] [IDENTIFIER fmax] [- -] [IDENTIFIER _no] [) )] [MATH %] [( (] [IDENTIFIER Math] [. .] [IDENTIFIER max] [CALL_START (] [IDENTIFIER fmax] [MATH /] [NUMBER 2] [, ,] [NUMBER 1] [CALL_END )] [) )] [COMPARE ==] [NUMBER 0] [OUTDENT 2] [} }] [TERMINATOR \\n]";
		assertTokenList(source, expected);
	}

	/**
	 * Used to compare the token list we have versus the output of running "coffee -t" on the same code.
	 * 
	 * @param source
	 * @param expected
	 * @throws IOException
	 * @throws beaver.Scanner.Exception
	 */
	protected void assertTokenList(String source, String expected) throws IOException, beaver.Scanner.Exception
	{
		scanner.setSource(source);
		StringBuilder builder = new StringBuilder();
		while (true)
		{
			CoffeeSymbol symbol = scanner.nextToken();
			if (Terminals.EOF == symbol.getId())
			{
				break;
			}

			builder.append(symbol.toString()).append(' ');
		}
		// delete last extra space
		builder.deleteCharAt(builder.length() - 1);
		assertEquals(expected, builder.toString());
	}

	protected void assertToken(short type, Object value, int start, int end) throws IOException,
			beaver.Scanner.Exception
	{
		CoffeeSymbol token = scanner.nextToken();
		assertEquals("Token type doesn't match", type, token.getId());
		assertEquals("Start offset doesn't match", start, token.getStart());
		assertEquals("End offset doesn't match", end, token.getEnd());
		if (value != null)
		{
			assertEquals("Token value doesn't match", value, token.getValue());
		}
	}

	protected void assertToken(short type, int start, int end) throws IOException, beaver.Scanner.Exception
	{
		assertToken(type, null, start, end);
	}
}
