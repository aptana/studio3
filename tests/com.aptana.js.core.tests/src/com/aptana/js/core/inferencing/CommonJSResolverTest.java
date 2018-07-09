package com.aptana.js.core.inferencing;

import static org.junit.Assert.assertEquals;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aptana.core.util.FileUtil;
import com.aptana.js.core.parsing.GraalJSParser;
import com.aptana.js.core.parsing.JSParseState;
import com.aptana.js.core.parsing.ast.JSStringNode;
import com.aptana.parsing.IParser;
import com.aptana.parsing.ParseResult;
import com.aptana.parsing.ast.IParseNode;

public class CommonJSResolverTest
{

	private CommonJSResolver resolver;
	private IProject project = null;
	private IPath wd = FileUtil.getTempDirectory();
	private IPath indexRoot;

	@Before
	public void setup()
	{
		resolver = new CommonJSResolver();
		indexRoot = FileUtil.getTempDirectory().append("indexRoot" + System.currentTimeMillis());
		indexRoot.toFile().mkdir();
	}

	@After
	public void teardown()
	{
		resolver = null;
		FileUtil.deleteRecursively(indexRoot.toFile());
	}

	@Test
	public void testResolveAddsImplicitJSExtension()
	{
		assertEquals(wd.append("moduleId.js"), resolver.resolve("./moduleId", project, wd, indexRoot));
	}

	@Test
	public void testResolveRelativePath()
	{
		assertEquals(wd.append("moduleId.js"), resolver.resolve("./moduleId.js", project, wd, indexRoot));
	}

	@Test
	public void testResolveAbsolutePath()
	{
		assertEquals(indexRoot.append("moduleId.js"), resolver.resolve("moduleId.js", project, wd, indexRoot));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testResolveWithNonExistantCurrentLocationThrowsException()
	{
		wd = FileUtil.getTempDirectory().append("somethingThatDoesntExist");
		assertEquals(indexRoot.append("moduleId.js"), resolver.resolve("moduleId.js", project, wd, indexRoot));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testResolveWithNonExistantIndexRootThrowsException()
	{
		FileUtil.deleteRecursively(indexRoot.toFile());
		assertEquals(indexRoot.append("moduleId.js"), resolver.resolve("moduleId.js", project, wd, indexRoot));
	}

	@Test
	public void testGetModuleIdWithSimpleStringArgToRequire()
	{
		assertEquals("moduleId", CommonJSResolver.getModuleId(new JSStringNode("moduleId")));
	}

	@Test
	public void testGetModuleIdWithPathJoinInvokeInsideRequire() throws Exception
	{
		IParser parser = createParser();
		JSParseState parseState = new JSParseState("require(path.join('path', 'to', 'file.js'));");
		ParseResult pr = parser.parse(parseState);
		IParseNode node = pr.getRootNode().getChild(0).getChild(1).getChild(0);
		assertEquals("path/to/file.js", CommonJSResolver.getModuleId(node));
	}

	@Test
	public void testGetModuleIdWithPathJoinInvokeUsingDirNameReferenceInsideRequire() throws Exception
	{
		IParser parser = createParser();
		JSParseState parseState = new JSParseState("require(path.join(__dirname, 'path', 'to', 'file.js'));");
		ParseResult pr = parser.parse(parseState);
		IParseNode node = pr.getRootNode().getChild(0).getChild(1).getChild(0);
		assertEquals("./path/to/file.js", CommonJSResolver.getModuleId(node));
	}

	protected IParser createParser()
	{
		return new GraalJSParser();
	}
}
