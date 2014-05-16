package com.aptana.editor.coffee.parsing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.aptana.editor.coffee.parsing.ast.CoffeeLiteralNode;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.IParseRootNode;

@SuppressWarnings("nls")
public class CoffeeParserTest
{

	@Test
	public void testNumberAssignment() throws Exception
	{
		String source = "number = 42\n";
		String expectedAST = //
		"Block\n" + //
				"  Assign\n" + //
				"    Value \"number\"\n" + //
				"    Value \"42\"\n";

		IParseRootNode root = parse(source);
		assertAST(expectedAST, root);
		// Make sure offsets are correct
		IParseNode rootBlock = root.getChild(0);

		// number = 42
		IParseNode assignNode = rootBlock.getChild(0);
		assertEquals(0, assignNode.getStartingOffset());
		assertEquals(11, assignNode.getEndingOffset());

		// number
		IParseNode lhNode = assignNode.getChild(0);
		assertEquals(0, lhNode.getStartingOffset());
		assertEquals(6, lhNode.getEndingOffset());
		// 42
		IParseNode rhNode = assignNode.getChild(1);
		assertEquals(9, rhNode.getStartingOffset());
		assertEquals(11, rhNode.getEndingOffset());
	}

	@Test
	public void testBooleanAssignment() throws Exception
	{
		String source = "opposite = true\n";

		String expectedAST = //
		"Block\n" + //
				"  Assign\n" + //
				"    Value \"opposite\"\n" + //
				"    Value \"true\"\n";

		IParseRootNode root = parse(source);
		assertAST(expectedAST, root);
		// Make sure offsets are correct
		IParseNode rootBlock = root.getChild(0);

		// opposite = true
		IParseNode assignNode = rootBlock.getChild(0);
		assertEquals(0, assignNode.getStartingOffset());
		assertEquals(15, assignNode.getEndingOffset());

		// opposite
		IParseNode lhNode = assignNode.getChild(0);
		assertEquals(0, lhNode.getStartingOffset());
		assertEquals(8, lhNode.getEndingOffset());
		// true
		IParseNode rhNode = assignNode.getChild(1);
		assertEquals(11, rhNode.getStartingOffset());
		assertEquals(15, rhNode.getEndingOffset());
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

		String expected = "Block\n" + //
				"  Assign\n" + //
				"    Value \"number\"\n" + //
				"    Value \"42\"\n" + //
				"  Assign\n" + //
				"    Value \"opposite\"\n" + //
				"    Value \"true\"\n" + //
				"  If\n" + //
				"    Value \"opposite\"\n" + //
				"    Block\n" + //
				"      Assign\n" + //
				"        Value \"number\"\n" + //
				"        Op -\n" + //
				"          Value \"42\"\n" + //
				"  Assign\n" + //
				"    Value \"square\"\n" + //
				"    Code\n" + //
				"      Param \"x\"\n" + //
				"      Block\n" + //
				"        Op *\n" + //
				"          Value \"x\"\n" + //
				"          Value \"x\"\n" + //
				"  Assign\n" + //
				"    Value \"list\"\n" + //
				"    Value\n" + //
				"      Arr\n" + //
				"        Value \"1\"\n" + //
				"        Value \"2\"\n" + //
				"        Value \"3\"\n" + //
				"        Value \"4\"\n" + //
				"        Value \"5\"\n" + //
				"  Assign\n" + //
				"    Value \"math\"\n" + //
				"    Value\n" + //
				"      Obj\n" + //
				"        Assign\n" + //
				"          Value \"root\"\n" + //
				"          Value \"Math\"\n" + //
				"            Access \"sqrt\"\n" + //
				"        Assign\n" + //
				"          Value \"square\"\n" + //
				"          Value \"square\"\n" + //
				"        Assign\n" + //
				"          Value \"cube\"\n" + //
				"          Code\n" + //
				"            Param \"x\"\n" + //
				"            Block\n" + //
				"              Op *\n" + //
				"                Value \"x\"\n" + //
				"                Call\n" + //
				"                  Value \"square\"\n" + //
				"                  Value \"x\"\n" + //
				"  Assign\n" + //
				"    Value \"race\"\n" + //
				"    Code\n" + //
				"      Param \"winner\"\n" + //
				"      Param \"runners\"\n" + //
				"      Block\n" + //
				"        Call\n" + //
				"          Value \"print\"\n" + //
				"          Value \"winner\"\n" + //
				"          Value \"runners\"\n" + //
				"  If\n" + //
				"    Existence\n" + //
				"      Value \"elvis\"\n" + //
				"    Block\n" + //
				"      Call\n" + //
				"        Value \"alert\"\n" + //
				"        Value \"\"I knew it!\"\"\n" + //
				"  Assign\n" + //
				"    Value \"cubes\"\n" + //
				"    Value\n" + //
				"      Parens\n" + //
				"        Block\n" + //
				"          For\n" + //
				"            Block\n" + //
				"              Call\n" + //
				"                Value \"math\"\n" + //
				"                  Access \"cube\"\n" + //
				"                Value \"num\"\n" + //
				"            Value \"list\"\n";

		IParseRootNode rootNode = parse(source);
		assertAST(expected, rootNode);
		IParseNode[] comments = rootNode.getCommentNodes();
		assertNotNull("Should have gotten an array of comment nodes", comments);
		assertEquals("Number of comments doesn't match expectations", 8, comments.length);
		assertCommentNode(comments[0], 0, 13, "# Assignment:");
		assertCommentNode(comments[1], 45, 58, "# Conditions:");
		assertCommentNode(comments[2], 85, 97, "# Functions:");
		assertCommentNode(comments[3], 121, 130, "# Arrays:");
		assertCommentNode(comments[4], 155, 165, "# Objects:");
		assertCommentNode(comments[5], 241, 250, "# Splats:");
		assertCommentNode(comments[6], 307, 319, "# Existence:");
		assertCommentNode(comments[7], 350, 373, "# Array comprehensions:");
	}

	@Test
	public void testFunctionsExample1() throws Exception
	{
		String source = "square = (x) -> x * x\n" + //
				"cube   = (x) -> square(x) * x\n"; //

		String expected = "Block\n" + //
				"  Assign\n" + //
				"    Value \"square\"\n" + //
				"    Code\n" + //
				"      Param \"x\"\n" + //
				"      Block\n" + //
				"        Op *\n" + //
				"          Value \"x\"\n" + //
				"          Value \"x\"\n" + //
				"  Assign\n" + //
				"    Value \"cube\"\n" + //
				"    Code\n" + //
				"      Param \"x\"\n" + //
				"      Block\n" + //
				"        Op *\n" + //
				"          Call\n" + //
				"            Value \"square\"\n" + //
				"            Value \"x\"\n" + //
				"          Value \"x\"\n";
		assertAST(expected, source);
	}

	@Test
	public void testFunctionsExample2() throws Exception
	{
		String source = "fill = (container, liquid = \"coffee\") ->\n" + //
				"  \"Filling the #{container} with #{liquid}...\"\n"; //

		String expected = "Block\n" + //
				"  Assign\n" + //
				"    Value \"fill\"\n" + //
				"    Code\n" + //
				"      Param \"container\"\n" + //
				"      Param \"liquid\"\n" + //
				"        Value \"\"coffee\"\"\n" + //
				"      Block\n" + //
				"        Value\n" + //
				"          Parens\n" + //
				"            Block\n" + //
				"              Op +\n" + //
				"                Op +\n" + //
				"                  Op +\n" + //
				"                    Op +\n" + //
				"                      Value \"\"Filling the \"\"\n" + //
				"                      Value \"container\"\n" + //
				"                    Value \"\" with \"\"\n" + //
				"                  Value \"liquid\"\n" + //
				"                Value \"\"...\"\"\n";
		assertAST(expected, source);
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

		String expected = "Block\n" + //
				"  Assign\n" + //
				"    Value \"song\"\n" + //
				"    Value\n" + //
				"      Arr\n" + //
				"        Value \"\"do\"\"\n" + //
				"        Value \"\"re\"\"\n" + //
				"        Value \"\"mi\"\"\n" + //
				"        Value \"\"fa\"\"\n" + //
				"        Value \"\"so\"\"\n" + //
				"  Assign\n" + //
				"    Value \"singers\"\n" + //
				"    Value\n" + //
				"      Obj\n" + //
				"        Assign\n" + //
				"          Value \"Jagger\"\n" + //
				"          Value \"\"Rock\"\"\n" + //
				"        Assign\n" + //
				"          Value \"Elvis\"\n" + //
				"          Value \"\"Roll\"\"\n" + //
				"  Assign\n" + //
				"    Value \"bitlist\"\n" + //
				"    Value\n" + //
				"      Arr\n" + //
				"        Value \"1\"\n" + //
				"        Value \"0\"\n" + //
				"        Value \"1\"\n" + //
				"        Value \"0\"\n" + //
				"        Value \"0\"\n" + //
				"        Value \"1\"\n" + //
				"        Value \"1\"\n" + //
				"        Value \"1\"\n" + //
				"        Value \"0\"\n" + //
				"  Assign\n" + //
				"    Value \"kids\"\n" + //
				"    Value\n" + //
				"      Obj\n" + //
				"        Assign\n" + //
				"          Value \"brother\"\n" + //
				"          Value\n" + //
				"            Obj\n" + //
				"              Assign\n" + //
				"                Value \"name\"\n" + //
				"                Value \"\"Max\"\"\n" + //
				"              Assign\n" + //
				"                Value \"age\"\n" + //
				"                Value \"11\"\n" + //
				"        Assign\n" + //
				"          Value \"sister\"\n" + //
				"          Value\n" + //
				"            Obj\n" + //
				"              Assign\n" + //
				"                Value \"name\"\n" + //
				"                Value \"\"Ida\"\"\n" + //
				"              Assign\n" + //
				"                Value \"age\"\n" + //
				"                Value \"9\"\n";
		assertAST(expected, source);
	}

	@Test
	public void testObjectsAndArraysExample2() throws Exception
	{
		String source = "$('.account').attr class: 'active'\n" + //
				"\n" + //
				"log object.class\n"; //

		String expected = "Block\n" + //
				"  Call\n" + //
				"    Value\n" + //
				"      Call\n" + //
				"        Value \"$\"\n" + //
				"        Value \"'.account'\"\n" + //
				"      Access \"attr\"\n" + //
				"    Value\n" + //
				"      Obj\n" + //
				"        Assign\n" + //
				"          Value \"class\"\n" + //
				"          Value \"'active'\"\n" + //
				"  Call\n" + //
				"    Value \"log\"\n" + //
				"    Value \"object\"\n" + //
				"      Access \"class\"\n";
		assertAST(expected, source);
	}

	@Test
	public void testLexicalScopingExample1() throws Exception
	{
		String source = "outer = 1\n" + //
				"changeNumbers = ->\n" + //
				"  inner = -1\n" + //
				"  outer = 10\n" + //
				"inner = changeNumbers()\n"; //

		String expected = "Block\n" + //
				"  Assign\n" + //
				"    Value \"outer\"\n" + //
				"    Value \"1\"\n" + //
				"  Assign\n" + //
				"    Value \"changeNumbers\"\n" + //
				"    Code\n" + //
				"      Block\n" + //
				"        Assign\n" + //
				"          Value \"inner\"\n" + //
				"          Op -\n" + //
				"            Value \"1\"\n" + //
				"        Assign\n" + //
				"          Value \"outer\"\n" + //
				"          Value \"10\"\n" + //
				"  Assign\n" + //
				"    Value \"inner\"\n" + //
				"    Call\n" + //
				"      Value \"changeNumbers\"\n";
		assertAST(expected, source);
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

		String expected = "Block\n" + //
				"  If\n" + //
				"    Value \"singing\"\n" + //
				"    Block\n" + //
				"      Assign\n" + //
				"        Value \"mood\"\n" + //
				"        Value \"greatlyImproved\"\n" + //
				"  If\n" + //
				"    Op &&\n" + //
				"      Value \"happy\"\n" + //
				"      Value \"knowsIt\"\n" + //
				"    Block\n" + //
				"      Call\n" + //
				"        Value \"clapsHands\"\n" + //
				"      Call\n" + //
				"        Value \"chaChaCha\"\n" + //
				"    Block\n" + //
				"      Call\n" + //
				"        Value \"showIt\"\n" + //
				"  Assign\n" + //
				"    Value \"date\"\n" + //
				"    If\n" + //
				"      Value \"friday\"\n" + //
				"      Block\n" + //
				"        Value \"sue\"\n" + //
				"      Block\n" + //
				"        Value \"jill\"\n" + //
				"  Assign\n" + //
				"    Value \"options\"\n" + //
				"    Value \"defaults\"\n";
		assertAST(expected, source);
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

		String expected = "Block\n" + //
				"  Assign\n" + //
				"    Value \"gold\"\n" + //
				"    Assign\n" + //
				"      Value \"silver\"\n" + //
				"      Assign\n" + //
				"        Value \"rest\"\n" + //
				"        Value \"\"unknown\"\"\n" + //
				"  Assign\n" + //
				"    Value \"awardMedals\"\n" + //
				"    Code\n" + //
				"      Param \"first\"\n" + //
				"      Param \"second\"\n" + //
				"      Param \"others\"\n" + //
				"      Block\n" + //
				"        Assign\n" + //
				"          Value \"gold\"\n" + //
				"          Value \"first\"\n" + //
				"        Assign\n" + //
				"          Value \"silver\"\n" + //
				"          Value \"second\"\n" + //
				"        Assign\n" + //
				"          Value \"rest\"\n" + //
				"          Value \"others\"\n" + //
				"  Assign\n" + //
				"    Value \"contenders\"\n" + //
				"    Value\n" + //
				"      Arr\n" + //
				"        Value \"\"Michael Phelps\"\"\n" + //
				"        Value \"\"Liu Xiang\"\"\n" + //
				"        Value \"\"Yao Ming\"\"\n" + //
				"        Value \"\"Allyson Felix\"\"\n" + //
				"        Value \"\"Shawn Johnson\"\"\n" + //
				"        Value \"\"Roman Sebrle\"\"\n" + //
				"        Value \"\"Guo Jingjing\"\"\n" + //
				"        Value \"\"Tyson Gay\"\"\n" + //
				"        Value \"\"Asafa Powell\"\"\n" + //
				"        Value \"\"Usain Bolt\"\"\n" + //
				"  Call\n" + //
				"    Value \"awardMedals\"\n" + //
				"    Splat\n" + //
				"      Value \"contenders\"\n" + //
				"  Call\n" + //
				"    Value \"alert\"\n" + //
				"    Op +\n" + //
				"      Value \"\"Gold: \"\"\n" + //
				"      Value \"gold\"\n" + //
				"  Call\n" + //
				"    Value \"alert\"\n" + //
				"    Op +\n" + //
				"      Value \"\"Silver: \"\"\n" + //
				"      Value \"silver\"\n" + //
				"  Call\n" + //
				"    Value \"alert\"\n" + //
				"    Op +\n" + //
				"      Value \"\"The Field: \"\"\n" + //
				"      Value \"rest\"\n";
		assertAST(expected, source);
	}

	@Test
	public void testLoopsExample1() throws Exception
	{
		String source = "# Eat lunch.\n" + //
				"eat food for food in ['toast', 'cheese', 'wine']\n"; //

		String expected = "Block\n" + //
				"  For\n" + //
				"    Block\n" + //
				"      Call\n" + //
				"        Value \"eat\"\n" + //
				"        Value \"food\"\n" + //
				"    Value\n" + //
				"      Arr\n" + //
				"        Value \"'toast'\"\n" + //
				"        Value \"'cheese'\"\n" + //
				"        Value \"'wine'\"\n";
		assertAST(expected, source);
	}

	@Test
	public void testLoopsExample2() throws Exception
	{
		String source = "countdown = (num for num in [10..1])"; //

		String expected = "Block\n" + //
				"  Assign\n" + //
				"    Value \"countdown\"\n" + //
				"    Value\n" + //
				"      Parens\n" + //
				"        Block\n" + //
				"          For\n" + //
				"            Block\n" + //
				"              Value \"num\"\n" + //
				"            Value\n" + //
				"              Range\n" + //
				"                Value \"10\"\n" + //
				"                Value \"1\"\n";
		assertAST(expected, source);
	}

	@Test
	public void testLoopsExample3() throws Exception
	{
		String source = "yearsOld = max: 10, ida: 9, tim: 11\n" + //
				"\n" + //
				"ages = for child, age of yearsOld\n" + //
				"  child + \" is \" + age\n"; //

		String expected = "Block\n" + //
				"  Assign\n" + //
				"    Value \"yearsOld\"\n" + //
				"    Value\n" + //
				"      Obj\n" + //
				"        Assign\n" + //
				"          Value \"max\"\n" + //
				"          Value \"10\"\n" + //
				"        Assign\n" + //
				"          Value \"ida\"\n" + //
				"          Value \"9\"\n" + //
				"        Assign\n" + //
				"          Value \"tim\"\n" + //
				"          Value \"11\"\n" + //
				"  Assign\n" + //
				"    Value \"ages\"\n" + //
				"    For\n" + //
				"      Block\n" + //
				"        Op +\n" + //
				"          Op +\n" + //
				"            Value \"child\"\n" + //
				"            Value \"\" is \"\"\n" + //
				"          Value \"age\"\n" + //
				"      Value \"yearsOld\"\n";
		assertAST(expected, source);
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

		String expected = "Block\n" + //
				"  If\n" + //
				"    Value \"this\"\n" + //
				"      Access \"studyingEconomics\"\n" + //
				"    Block\n" + //
				"      While\n" + //
				"        Op >\n" + //
				"          Value \"supply\"\n" + //
				"          Value \"demand\"\n" + //
				"        Block\n" + //
				"          Call\n" + //
				"            Value \"buy\"\n" + //
				"      While\n" + //
				"        Op !\n" + //
				"          Parens\n" + //
				"            Op >\n" + //
				"              Value \"supply\"\n" + //
				"              Value \"demand\"\n" + //
				"        Block\n" + //
				"          Call\n" + //
				"            Value \"sell\"\n" + //
				"  Assign\n" + //
				"    Value \"num\"\n" + //
				"    Value \"6\"\n" + //
				"  Assign\n" + //
				"    Value \"lyrics\"\n" + //
				"    While\n" + //
				"      Assign\n" + //
				"        Value \"num\"\n" + //
				"        Value \"1\"\n" + //
				"      Block\n" + //
				"        Op +\n" + //
				"          Value \"num\"\n" + //
				"          Value \"\" little monkeys, jumping on the bed.    One fell out and bumped his head.\"\"\n";
		assertAST(expected, source);
	}

	@Test
	public void testLoopsExample5() throws Exception
	{
		String source = "for filename in list\n" + //
				"  do (filename) ->\n" + //
				"    fs.readFile filename, (err, contents) ->\n" + //
				"      compile filename, contents.toString()\n"; //

		String expected = "Block\n" + //
				"  For\n" + //
				"    Block\n" + //
				"      Call\n" + //
				"        Code\n" + //
				"          Param \"filename\"\n" + //
				"          Block\n" + //
				"            Call\n" + //
				"              Value \"fs\"\n" + //
				"                Access \"readFile\"\n" + //
				"              Value \"filename\"\n" + //
				"              Code\n" + //
				"                Param \"err\"\n" + //
				"                Param \"contents\"\n" + //
				"                Block\n" + //
				"                  Call\n" + //
				"                    Value \"compile\"\n" + //
				"                    Value \"filename\"\n" + //
				"                    Call\n" + //
				"                      Value \"contents\"\n" + //
				"                        Access \"toString\"\n" + //
				"        Param \"filename\"\n" + //
				"    Value \"list\"\n";
		assertAST(expected, source);
	}

	@Test
	public void testSliceAndSpliceExample1() throws Exception
	{
		String source = "numbers = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9]\n" + //
				"\n" + //
				"copy    = numbers[0...numbers.length]\n" + //
				"\n" + //
				"middle  = copy[3..6]\n"; //

		String expected = "Block\n" + //
				"  Assign\n" + //
				"    Value \"numbers\"\n" + //
				"    Value\n" + //
				"      Arr\n" + //
				"        Value \"0\"\n" + //
				"        Value \"1\"\n" + //
				"        Value \"2\"\n" + //
				"        Value \"3\"\n" + //
				"        Value \"4\"\n" + //
				"        Value \"5\"\n" + //
				"        Value \"6\"\n" + //
				"        Value \"7\"\n" + //
				"        Value \"8\"\n" + //
				"        Value \"9\"\n" + //
				"  Assign\n" + //
				"    Value \"copy\"\n" + //
				"    Value \"numbers\"\n" + //
				"      Slice\n" + //
				"        Range\n" + //
				"          Value \"0\"\n" + //
				"          Value \"numbers\"\n" + //
				"            Access \"length\"\n" + //
				"  Assign\n" + //
				"    Value \"middle\"\n" + //
				"    Value \"copy\"\n" + //
				"      Slice\n" + //
				"        Range\n" + //
				"          Value \"3\"\n" + //
				"          Value \"6\"\n";
		assertAST(expected, source);
	}

	@Test
	public void testSliceAndSpliceExample2() throws Exception
	{
		String source = "numbers = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9]\n" + //
				"\n" + //
				"numbers[3..6] = [-3, -4, -5, -6]\n"; //

		String expected = "Block\n" + //
				"  Assign\n" + //
				"    Value \"numbers\"\n" + //
				"    Value\n" + //
				"      Arr\n" + //
				"        Value \"0\"\n" + //
				"        Value \"1\"\n" + //
				"        Value \"2\"\n" + //
				"        Value \"3\"\n" + //
				"        Value \"4\"\n" + //
				"        Value \"5\"\n" + //
				"        Value \"6\"\n" + //
				"        Value \"7\"\n" + //
				"        Value \"8\"\n" + //
				"        Value \"9\"\n" + //
				"  Assign\n" + //
				"    Value \"numbers\"\n" + //
				"      Slice\n" + //
				"        Range\n" + //
				"          Value \"3\"\n" + //
				"          Value \"6\"\n" + //
				"    Value\n" + //
				"      Arr\n" + //
				"        Op -\n" + //
				"          Value \"3\"\n" + //
				"        Op -\n" + //
				"          Value \"4\"\n" + //
				"        Op -\n" + //
				"          Value \"5\"\n" + //
				"        Op -\n" + //
				"          Value \"6\"\n";
		assertAST(expected, source);
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

		String expected = "Block\n" + //
				"  Assign\n" + //
				"    Value \"grade\"\n" + //
				"    Code\n" + //
				"      Param \"student\"\n" + //
				"      Block\n" + //
				"        If\n" + //
				"          Value \"student\"\n" + //
				"            Access \"excellentWork\"\n" + //
				"          Block\n" + //
				"            Value \"\"A+\"\"\n" + //
				"          Block\n" + //
				"            If\n" + //
				"              Value \"student\"\n" + //
				"                Access \"okayStuff\"\n" + //
				"              Block\n" + //
				"                If\n" + //
				"                  Value \"student\"\n" + //
				"                    Access \"triedHard\"\n" + //
				"                  Block\n" + //
				"                    Value \"\"B\"\"\n" + //
				"                  Block\n" + //
				"                    Value \"\"B-\"\"\n" + //
				"              Block\n" + //
				"                Value \"\"C\"\"\n" + //
				"  Assign\n" + //
				"    Value \"eldest\"\n" + //
				"    If\n" + //
				"      Op >\n" + //
				"        Value \"24\"\n" + //
				"        Value \"21\"\n" + //
				"      Block\n" + //
				"        Value \"\"Liz\"\"\n" + //
				"      Block\n" + //
				"        Value \"\"Ike\"\"\n";
		assertAST(expected, source);
	}

	@Test
	public void testEverythingIsAnExpressionExample2() throws Exception
	{
		String source = "six = (one = 1) + (two = 2) + (three = 3)\n"; //

		String expected = "Block\n" + //
				"  Assign\n" + //
				"    Value \"six\"\n" + //
				"    Op +\n" + //
				"      Op +\n" + //
				"        Value\n" + //
				"          Parens\n" + //
				"            Block\n" + //
				"              Assign\n" + //
				"                Value \"one\"\n" + //
				"                Value \"1\"\n" + //
				"        Value\n" + //
				"          Parens\n" + //
				"            Block\n" + //
				"              Assign\n" + //
				"                Value \"two\"\n" + //
				"                Value \"2\"\n" + //
				"      Value\n" + //
				"        Parens\n" + //
				"          Block\n" + //
				"            Assign\n" + //
				"              Value \"three\"\n" + //
				"              Value \"3\"\n";
		assertAST(expected, source);
	}

	@Test
	public void testEverythingIsAnExpressionExample3() throws Exception
	{
		String source = "# The first ten global properties.\n" + //
				"\n" + //
				"globals = (name for name of window)[0...10]\n"; //

		String expected = "Block\n" + //
				"  Assign\n" + //
				"    Value \"globals\"\n" + //
				"    Value\n" + //
				"      Parens\n" + //
				"        Block\n" + //
				"          For\n" + //
				"            Block\n" + //
				"              Value \"name\"\n" + //
				"            Value \"window\"\n" + //
				"      Slice\n" + //
				"        Range\n" + //
				"          Value \"0\"\n" + //
				"          Value \"10\"\n";
		assertAST(expected, source);
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

		String expected = "Block\n" + //
				"  Call\n" + //
				"    Value \"alert\"\n" + //
				"    Try\n" + //
				"      Block\n" + //
				"        Op /\n" + //
				"          Value \"nonexistent\"\n" + //
				"          Value \"undefined\"\n" + //
				"      Block\n" + //
				"        Op +\n" + //
				"          Value \"\"And the error is ... \"\"\n" + //
				"          Value \"error\"\n";
		assertAST(expected, source);
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

		String expected = "Block\n" + //
				"  If\n" + //
				"    Op ===\n" + //
				"      Value \"ignition\"\n" + //
				"      Value \"true\"\n" + //
				"    Block\n" + //
				"      Call\n" + //
				"        Value \"launch\"\n" + //
				"  If\n" + //
				"    Op !==\n" + //
				"      Value \"band\"\n" + //
				"      Value \"SpinalTap\"\n" + //
				"    Block\n" + //
				"      Assign\n" + //
				"        Value \"volume\"\n" + //
				"        Value \"10\"\n" + //
				"  If\n" + //
				"    Op !==\n" + //
				"      Value \"answer\"\n" + //
				"      Value \"false\"\n" + //
				"    Block\n" + //
				"      Call\n" + //
				"        Value \"letTheWildRumpusBegin\"\n" + //
				"  If\n" + //
				"    Op <\n" + //
				"      Value \"car\"\n" + //
				"        Access \"speed\"\n" + //
				"      Value \"limit\"\n" + //
				"    Block\n" + //
				"      Call\n" + //
				"        Value \"accelerate\"\n" + //
				"  If\n" + //
				"    In\n" + //
				"      Value \"pick\"\n" + //
				"      Value\n" + //
				"        Arr\n" + //
				"          Value \"47\"\n" + //
				"          Value \"92\"\n" + //
				"          Value \"13\"\n" + //
				"    Block\n" + //
				"      Assign\n" + //
				"        Value \"winner\"\n" + //
				"        Value \"true\"\n" + //
				"  Call\n" + //
				"    Value \"print\"\n" + //
				"    Call\n" + //
				"      Value \"inspect\"\n" + //
				"      Op +\n" + //
				"        Value \"\"My name is \"\"\n" + //
				"        Value \"this\"\n" + //
				"          Access \"name\"\n";
		assertAST(expected, source);
	}

	@Test
	public void testExistentialExample1() throws Exception
	{
		String source = "solipsism = true if mind? and not world?\n" + //
				"\n" + //
				"speed ?= 75\n" + //
				"\n" + //
				"footprints = yeti ? \"bear\"\n"; //

		String expected = "Block\n" + //
				"  If\n" + //
				"    Op &&\n" + //
				"      Existence\n" + //
				"        Value \"mind\"\n" + //
				"      Op !\n" + //
				"        Existence\n" + //
				"          Value \"world\"\n" + //
				"    Block\n" + //
				"      Assign\n" + //
				"        Value \"solipsism\"\n" + //
				"        Value \"true\"\n" + //
				"  Assign\n" + //
				"    Value \"speed\"\n" + //
				"    Value \"75\"\n" + //
				"  Assign\n" + //
				"    Value \"footprints\"\n" + //
				"    Op ?\n" + //
				"      Value \"yeti\"\n" + //
				"      Value \"\"bear\"\"\n";
		assertAST(expected, source);
	}

	@Test
	public void testExistentialExample2() throws Exception
	{
		String source = "zip = lottery.drawWinner?().address?.zipcode\n"; //

		String expected = "Block\n" + //
				"  Assign\n" + //
				"    Value \"zip\"\n" + //
				"    Value\n" + //
				"      Call?\n" + //
				"        Value \"lottery\"\n" + //
				"          Access \"drawWinner\"\n" + //
				"      Access \"address\"\n" + //
				"      Access? \"zipcode\"\n";
		assertAST(expected, source);
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

		String expected = "Block\n" + //
				"  Class\n" + //
				"    Value \"Animal\"\n" + //
				"    Block\n" + //
				"      Value\n" + //
				"        Obj\n" + //
				"          Assign\n" + //
				"            Value \"constructor\"\n" + //
				"            Code\n" + //
				"              Param\n" + //
				"                Value \"this\"\n" + //
				"                  Access \"name\"\n" + //
				"              Block\n" + //
				"          Assign\n" + //
				"            Value \"move\"\n" + //
				"            Code\n" + //
				"              Param \"meters\"\n" + //
				"              Block\n" + //
				"                Call\n" + //
				"                  Value \"alert\"\n" + //
				"                  Op +\n" + //
				"                    Op +\n" + //
				"                      Op +\n" + //
				"                        Value \"this\"\n" + //
				"                          Access \"name\"\n" + //
				"                        Value \"\" moved \"\"\n" + //
				"                      Value \"meters\"\n" + //
				"                    Value \"\"m.\"\"\n" + //
				"  Class\n" + //
				"    Value \"Snake\"\n" + //
				"    Value \"Animal\"\n" + //
				"    Block\n" + //
				"      Value\n" + //
				"        Obj\n" + //
				"          Assign\n" + //
				"            Value \"move\"\n" + //
				"            Code\n" + //
				"              Block\n" + //
				"                Call\n" + //
				"                  Value \"alert\"\n" + //
				"                  Value \"\"Slithering...\"\"\n" + //
				"                Call\n" + //
				"                  Value \"5\"\n" + //
				"  Class\n" + //
				"    Value \"Horse\"\n" + //
				"    Value \"Animal\"\n" + //
				"    Block\n" + //
				"      Value\n" + //
				"        Obj\n" + //
				"          Assign\n" + //
				"            Value \"move\"\n" + //
				"            Code\n" + //
				"              Block\n" + //
				"                Call\n" + //
				"                  Value \"alert\"\n" + //
				"                  Value \"\"Galloping...\"\"\n" + //
				"                Call\n" + //
				"                  Value \"45\"\n" + //
				"  Assign\n" + //
				"    Value \"sam\"\n" + //
				"    Call\n" + //
				"      Value \"Snake\"\n" + //
				"      Value \"\"Sammy the Python\"\"\n" + //
				"  Assign\n" + //
				"    Value \"tom\"\n" + //
				"    Call\n" + //
				"      Value \"Horse\"\n" + //
				"      Value \"\"Tommy the Palomino\"\"\n" + //
				"  Call\n" + //
				"    Value \"sam\"\n" + //
				"      Access \"move\"\n" + //
				"  Call\n" + //
				"    Value \"tom\"\n" + //
				"      Access \"move\"\n";

		IParseRootNode root = parse(source);
		assertAST(expected, root);
		IParseNode rootBlock = root.getChild(0);

		IParseNode animalClassNode = rootBlock.getChild(0);
		assertParseNode(animalClassNode, 0, 103);

		IParseNode animalBlockNode = animalClassNode.getChild(1);
		assertParseNode(animalBlockNode, 12, 103);

		IParseNode valueNode = animalBlockNode.getChild(0);
		assertParseNode(valueNode, 12, 103);

		IParseNode objNode = valueNode.getChild(0);
		assertParseNode(objNode, 12, 103);

		// the assignment of the function to "constructor"
		IParseNode assignNode = objNode.getChild(0);
		assertParseNode(assignNode, 15, 39);

		// the function, includes params and body
		IParseNode codeNode = assignNode.getChild(1);
		assertParseNode(codeNode, 28, 39);

		// block for the "constructor" function, which is empty
		IParseNode constructorBlockNode = codeNode.getChild(1);
		assertParseNode(constructorBlockNode, 38, 39);
	}

	@Test
	public void testClassesExample2() throws Exception
	{
		String source = "String::dasherize = ->\n" + //
				"  this.replace /_/g, \"-\"\n"; //

		String expected = "Block\n" + //
				"  Assign\n" + //
				"    Value \"String\"\n" + //
				"      Access \"dasherize\"\n" + //
				"    Code\n" + //
				"      Block\n" + //
				"        Call\n" + //
				"          Value \"this\"\n" + //
				"            Access \"replace\"\n" + //
				"          Value \"/_/g\"\n" + //
				"          Value \"\"-\"\"\n";
		assertAST(expected, source);
	}

	@Test
	public void testDestructuringAssignmentExample1() throws Exception
	{
		String source = "theBait   = 1000\n" + //
				"theSwitch = 0\n" + //
				"\n" + //
				"[theBait, theSwitch] = [theSwitch, theBait]\n"; //

		String expected = "Block\n" + //
				"  Assign\n" + //
				"    Value \"theBait\"\n" + //
				"    Value \"1000\"\n" + //
				"  Assign\n" + //
				"    Value \"theSwitch\"\n" + //
				"    Value \"0\"\n" + //
				"  Assign\n" + //
				"    Value\n" + //
				"      Arr\n" + //
				"        Value \"theBait\"\n" + //
				"        Value \"theSwitch\"\n" + //
				"    Value\n" + //
				"      Arr\n" + //
				"        Value \"theSwitch\"\n" + //
				"        Value \"theBait\"\n";
		assertAST(expected, source);
	}

	@Test
	public void testDestructuringAssignmentExample2() throws Exception
	{
		String source = "weatherReport = (location) ->\n" + //
				"  # Make an Ajax request to fetch the weather...\n" + //
				"  [location, 72, \"Mostly Sunny\"]\n" + //
				"\n" + //
				"[city, temp, forecast] = weatherReport \"Berkeley, CA\"\n"; //

		String expected = "Block\n" + //
				"  Assign\n" + //
				"    Value \"weatherReport\"\n" + //
				"    Code\n" + //
				"      Param \"location\"\n" + //
				"      Block\n" + //
				"        Value\n" + //
				"          Arr\n" + //
				"            Value \"location\"\n" + //
				"            Value \"72\"\n" + //
				"            Value \"\"Mostly Sunny\"\"\n" + //
				"  Assign\n" + //
				"    Value\n" + //
				"      Arr\n" + //
				"        Value \"city\"\n" + //
				"        Value \"temp\"\n" + //
				"        Value \"forecast\"\n" + //
				"    Call\n" + //
				"      Value \"weatherReport\"\n" + //
				"      Value \"\"Berkeley, CA\"\"\n";
		assertAST(expected, source);
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

		String expected = "Block\n" + //
				"  Assign\n" + //
				"    Value \"futurists\"\n" + //
				"    Value\n" + //
				"      Obj\n" + //
				"        Assign\n" + //
				"          Value \"sculptor\"\n" + //
				"          Value \"\"Umberto Boccioni\"\"\n" + //
				"        Assign\n" + //
				"          Value \"painter\"\n" + //
				"          Value \"\"Vladimir Burliuk\"\"\n" + //
				"        Assign\n" + //
				"          Value \"poet\"\n" + //
				"          Value\n" + //
				"            Obj\n" + //
				"              Assign\n" + //
				"                Value \"name\"\n" + //
				"                Value \"\"F.T. Marinetti\"\"\n" + //
				"              Assign\n" + //
				"                Value \"address\"\n" + //
				"                Value\n" + //
				"                  Arr\n" + //
				"                    Value \"\"Via Roma 42R\"\"\n" + //
				"                    Value \"\"Bellagio, Italy 22021\"\"\n" + //
				"  Assign\n" + //
				"    Value\n" + //
				"      Obj\n" + //
				"        Assign\n" + //
				"          Value \"poet\"\n" + //
				"          Value\n" + //
				"            Obj\n" + //
				"              Value \"name\"\n" + //
				"              Assign\n" + //
				"                Value \"address\"\n" + //
				"                Value\n" + //
				"                  Arr\n" + //
				"                    Value \"street\"\n" + //
				"                    Value \"city\"\n" + //
				"    Value \"futurists\"\n";
		assertAST(expected, source);
	}

	@Test
	public void testDestructuringAssignmentExample4() throws Exception
	{
		String source = "tag = \"<impossible>\"\n" + //
				"\n" + //
				"[open, contents..., close] = tag.split(\"\")\n"; //

		String expected = "Block\n" + //
				"  Assign\n" + //
				"    Value \"tag\"\n" + //
				"    Value \"\"<impossible>\"\"\n" + //
				"  Assign\n" + //
				"    Value\n" + //
				"      Arr\n" + //
				"        Value \"open\"\n" + //
				"        Splat\n" + //
				"          Value \"contents\"\n" + //
				"        Value \"close\"\n" + //
				"    Call\n" + //
				"      Value \"tag\"\n" + //
				"        Access \"split\"\n" + //
				"      Value \"\"\"\"\n";
		assertAST(expected, source);
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

		String expected = "Block\n" + //
				"  Assign\n" + //
				"    Value \"Account\"\n" + //
				"    Code\n" + //
				"      Param \"customer\"\n" + //
				"      Param \"cart\"\n" + //
				"      Block\n" + //
				"        Assign\n" + //
				"          Value \"this\"\n" + //
				"            Access \"customer\"\n" + //
				"          Value \"customer\"\n" + //
				"        Assign\n" + //
				"          Value \"this\"\n" + //
				"            Access \"cart\"\n" + //
				"          Value \"cart\"\n" + //
				"        Call\n" + //
				"          Value\n" + //
				"            Call\n" + //
				"              Value \"$\"\n" + //
				"              Value \"'.shopping_cart'\"\n" + //
				"            Access \"bind\"\n" + //
				"          Value \"'click'\"\n" + //
				"          Code\n" + //
				"            Param \"event\"\n" + //
				"            Block\n" + //
				"              Call\n" + //
				"                Value \"this\"\n" + //
				"                  Access \"customer\"\n" + //
				"                  Access \"purchase\"\n" + //
				"                Value \"this\"\n" + //
				"                  Access \"cart\"\n";
		assertAST(expected, source);
	}

	@Test
	public void testEmbeddedJSExample1() throws Exception
	{
		String source = "hi = `function() {\n" + //
				"  return [document.title, \"Hello JavaScript\"].join(\": \");\n" + //
				"}`\n"; //

		String expected = "Block\n" + //
				"  Assign\n" + //
				"    Value \"hi\"\n" + //
				"    Value \"function() {\n" + //
				"  return [document.title, \"Hello JavaScript\"].join(\": \");\n" + //
				"}\"\n";
		assertAST(expected, source);
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

		String expected = "Block\n" + //
				"  Switch\n" + //
				"    Value \"day\"\n" + //
				"    Value \"\"Mon\"\"\n" + //
				"    Block\n" + //
				"      Call\n" + //
				"        Value \"go\"\n" + //
				"        Value \"work\"\n" + //
				"    Value \"\"Tue\"\"\n" + //
				"    Block\n" + //
				"      Call\n" + //
				"        Value \"go\"\n" + //
				"        Value \"relax\"\n" + //
				"    Value \"\"Thu\"\"\n" + //
				"    Block\n" + //
				"      Call\n" + //
				"        Value \"go\"\n" + //
				"        Value \"iceFishing\"\n" + //
				"    Value \"\"Fri\"\"\n" + //
				"    Value \"\"Sat\"\"\n" + //
				"    Block\n" + //
				"      If\n" + //
				"        Op ===\n" + //
				"          Value \"day\"\n" + //
				"          Value \"bingoDay\"\n" + //
				"        Block\n" + //
				"          Call\n" + //
				"            Value \"go\"\n" + //
				"            Value \"bingo\"\n" + //
				"          Call\n" + //
				"            Value \"go\"\n" + //
				"            Value \"dancing\"\n" + //
				"    Value \"\"Sun\"\"\n" + //
				"    Block\n" + //
				"      Call\n" + //
				"        Value \"go\"\n" + //
				"        Value \"church\"\n" + //
				"    Block\n" + //
				"      Call\n" + //
				"        Value \"go\"\n" + //
				"        Value \"work\"\n";
		assertAST(expected, source);
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

		String expected = "Block\n" + //
				"  Try\n" + //
				"    Block\n" + //
				"      Call\n" + //
				"        Value \"allHellBreaksLoose\"\n" + //
				"      Call\n" + //
				"        Value \"catsAndDogsLivingTogether\"\n" + //
				"    Block\n" + //
				"      Call\n" + //
				"        Value \"print\"\n" + //
				"        Value \"error\"\n" + //
				"    Block\n" + //
				"      Call\n" + //
				"        Value \"cleanUp\"\n";
		assertAST(expected, source);
	}

	@Test
	public void testChainedComparisonsExample1() throws Exception
	{
		String source = "cholesterol = 127\n" + //
				"\n" + //
				"healthy = 200 > cholesterol > 60\n"; //

		String expected = "Block\n" + //
				"  Assign\n" + //
				"    Value \"cholesterol\"\n" + //
				"    Value \"127\"\n" + //
				"  Assign\n" + //
				"    Value \"healthy\"\n" + //
				"    Op >\n" + //
				"      Op >\n" + //
				"        Value \"200\"\n" + //
				"        Value \"cholesterol\"\n" + //
				"      Value \"60\"\n";
		assertAST(expected, source);
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

		String expected = "Block\n" + //
				"  Assign\n" + //
				"    Value \"OPERATOR\"\n" + //
				"    Value \"/^(?:[-=]>|[-+*\\/%<>&|^!?=]=|>>>=?|([-+:])\\1|([&|<>])\\2=?|\\?\\.|\\.{2,3})/\"\n";
		assertAST(expected, source);
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

		String expected = "Block\n" + //
				"  Assign\n" + //
				"    Value \"fs\"\n" + //
				"    Call\n" + //
				"      Value \"require\"\n" + //
				"      Value \"'fs'\"\n" + //
				"  Call\n" + //
				"    Value \"option\"\n" + //
				"    Value \"'-o'\"\n" + //
				"    Value \"'--output [DIR]'\"\n" + //
				"    Value \"'directory for compiled code'\"\n" + //
				"  Call\n" + //
				"    Value \"task\"\n" + //
				"    Value \"'build:parser'\"\n" + //
				"    Value \"'rebuild the Jison parser'\"\n" + //
				"    Code\n" + //
				"      Param \"options\"\n" + //
				"      Block\n" + //
				"        Call\n" + //
				"          Value \"require\"\n" + //
				"          Value \"'jison'\"\n" + //
				"        Assign\n" + //
				"          Value \"code\"\n" + //
				"          Call\n" + //
				"            Value\n" + //
				"              Call\n" + //
				"                Value \"require\"\n" + //
				"                Value \"'./lib/grammar'\"\n" + //
				"              Access \"parser\"\n" + //
				"              Access \"generate\"\n" + //
				"        Assign\n" + //
				"          Value \"dir\"\n" + //
				"          Op ||\n" + //
				"            Value \"options\"\n" + //
				"              Access \"output\"\n" + //
				"            Value \"'lib'\"\n" + //
				"        Call\n" + //
				"          Value \"fs\"\n" + //
				"            Access \"writeFile\"\n" + //
				"          Value\n" + //
				"            Parens\n" + //
				"              Block\n" + //
				"                Op +\n" + //
				"                  Op +\n" + //
				"                    Value \"\"\"\"\n" + //
				"                    Value \"dir\"\n" + //
				"                  Value \"\"/parser.js\"\"\n" + //
				"          Value \"code\"\n";
		assertAST(expected, source);
	}

	protected IParseRootNode parse(String source) throws Exception
	{
		CoffeeParser parser = new CoffeeParser();
		ParseState parseState = new ParseState(source);
		return parser.parse(parseState).getRootNode();
	}

	protected void assertCommentNode(IParseNode comment, int start, int end, String text)
	{
		assertParseNode(comment, start, end);
		assertEquals("Text of comment doesn't match expectations", text, comment.getText());
	}

	protected void assertParseNode(IParseNode node, int start, int end)
	{
		assertEquals("Start offset of node doesn't match expectations", start, node.getStartingOffset());
		assertEquals("End offset of node doesn't match expectations", end, node.getEndingOffset());
	}

	protected void assertAST(String expectedAST, String source) throws Exception
	{
		if (expectedAST == null || expectedAST.length() == 0)
		{
			fail("expected AST not yet filled in!");
		}
		IParseRootNode root = parse(source);
		assertAST(expectedAST, root);
	}

	protected void assertAST(String expectedAST, IParseRootNode root)
	{
		assertEquals(expectedAST, printChildrenAST(root.getChild(0), 0));
	}

	private String printChildrenAST(IParseNode parent, int indent)
	{
		if (parent instanceof CoffeeLiteralNode)
		{
			return "";
		}
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < indent; i++)
		{
			builder.append(' ');
		}
		builder.append(parent.getText()).append("\n");
		for (IParseNode child : parent.getChildren())
		{
			builder.append(printChildrenAST(child, indent + 2));
		}
		return builder.toString();
	}

}
