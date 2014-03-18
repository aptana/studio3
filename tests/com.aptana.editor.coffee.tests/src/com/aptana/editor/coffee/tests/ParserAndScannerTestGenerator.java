package com.aptana.editor.coffee.tests;

import java.io.File;
import java.io.FileOutputStream;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;

import com.aptana.core.util.IOUtil;
import com.aptana.core.util.ProcessUtil;

public class ParserAndScannerTestGenerator
{

	@SuppressWarnings("nls")
	private static final String HEADER = "package com.aptana.editor.coffee.parsing.lexer;\n" + //
			"\n" + //
			"import java.io.IOException;\n" + //
			"\n" + //
			"import junit.framework.TestCase;\n" + //
			"\n" + //
			"import com.aptana.editor.coffee.parsing.Terminals;\n" + //
			"\n" + //
			"@SuppressWarnings(\"nls\")\n" + //
			"public class CoffeeScannerTest extends TestCase\n" + //
			"{\n" + //
			"\n" + //
			"	private CoffeeScanner scanner;\n" + //
			"\n" + //
			"	protected void setUp() throws Exception\n" + //
			"	{\n" + //
			"		super.setUp();\n" + //
			"		scanner = new CoffeeScanner();\n" + //
			"	}\n" + //
			"\n" + //
			"	protected void tearDown() throws Exception\n" + //
			"	{\n" + //
			"		scanner = null;\n" + //
			"		super.tearDown();\n" + //
			"	}\n";

	@SuppressWarnings("nls")
	private static final String FOOTER = "	/**\n"
			+ //
			"	 * Used to compare the token list we have versus the output of running \"coffee -t\" on the same code.\n"
			+ //
			"	 * \n"
			+ //
			"	 * @param source\n"
			+ //
			"	 * @param expected\n"
			+ //
			"	 * @throws IOException\n"
			+ //
			"	 * @throws beaver.Scanner.Exception\n"
			+ //
			"	 */\n"
			+ //
			"	protected void assertTokenList(String source, String expected) throws IOException, beaver.Scanner.Exception\n"
			+ //
			"	{\n"
			+ //
			"		scanner.setSource(source);\n"
			+ //
			"		StringBuilder builder = new StringBuilder();\n"
			+ //
			"		while (true)\n"
			+ //
			"		{\n"
			+ //
			"			CoffeeSymbol symbol = scanner.nextToken();\n"
			+ //
			"			if (Terminals.EOF == symbol.getId())\n"
			+ //
			"			{\n"
			+ //
			"				break;\n"
			+ //
			"			}\n"
			+ //
			"\n"
			+ //
			"			builder.append(symbol.toString()).append(' ');\n"
			+ //
			"		}\n"
			+ //
			"		// delete last extra space\n"
			+ //
			"		builder.deleteCharAt(builder.length() - 1);\n"
			+ //
			"		assertEquals(expected, builder.toString());\n"
			+ //
			"	}\n"
			+ //
			"\n"
			+ //
			"	protected void assertToken(short type, Object value, int start, int end) throws IOException,\n"
			+ //
			"			beaver.Scanner.Exception\n"
			+ //
			"	{\n"
			+ //
			"		CoffeeSymbol token = scanner.nextToken();\n"
			+ //
			"		assertEquals(\"Token type doesn't match\", type, token.getId());\n"
			+ //
			"		assertEquals(\"Start offset doesn't match\", start, token.getStart());\n"
			+ //
			"		assertEquals(\"End offset doesn't match\", end, token.getEnd());\n"
			+ //
			"		if (value != null)\n"
			+ //
			"		{\n"
			+ //
			"			assertEquals(\"Token value doesn't match\", value, token.getValue());\n"
			+ //
			"		}\n"
			+ //
			"	}\n"
			+ //
			"\n"
			+ //
			"	protected void assertToken(short type, int start, int end) throws IOException, beaver.Scanner.Exception\n"
			+ //
			"	{\n" + //
			"		assertToken(type, null, start, end);\n" + //
			"	}\n" + //
			"}\n";

	public static void main(String[] args) throws Exception
	{
		// Run through the input coffee files and generate expected test output!

		File dir = new File("/Users/cwilliams/repos/studio3/tests/com.aptana.editor.coffee.tests/files");
		for (File file : dir.listFiles())
		{
			IStatus status = ProcessUtil.runInBackground("coffee", Path.fromOSString(dir.getAbsolutePath()), "-t",
					file.getName());
			if (status.isOK())
			{
				IOUtil.write(new FileOutputStream(new File(dir, file.getName() + ".tokens")), status.getMessage());
			}
			status = ProcessUtil.runInBackground("coffee", Path.fromOSString(dir.getAbsolutePath()), "-n",
					file.getName());
			if (status.isOK())
			{
				IOUtil.write(new FileOutputStream(new File(dir, file.getName() + ".ast")), status.getMessage());
			}
		}
	}
}
