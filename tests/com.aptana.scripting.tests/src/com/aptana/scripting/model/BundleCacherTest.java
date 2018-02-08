package com.aptana.scripting.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeFalse;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.jruby.RubyRegexp;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.FileUtil;
import com.aptana.scope.ScopeSelector;

public class BundleCacherTest
{
	private BundleElement nonCached;
	private BundleElement deserialized;
	private BundleCacher cacher;
	private File bundleDirectory;
	private BundleManager bundleManager;

	@Before
	public void setUp() throws Exception
	{
		bundleManager = BundleManager.getInstance();
		bundleManager.reset();
		cacher = new BundleCacher();
	}

	@After
	public void tearDown() throws Exception
	{
		nonCached = null;
		deserialized = null;
		delete(bundleDirectory);
		bundleDirectory = null;
		cacher = null;
		bundleManager = null;
	}

	@Test
	public void testAPSTUD4562() throws Exception
	{
		// @formatter:off
		String fileContents = "--- !!com.aptana.scripting.model.BundleElement \n" +
			"author: Christopher Williams\n" +
			"copyright: Copyright 2010 Aptana Inc. Distributed under the MIT license.\n" +
			"decreaseIndentMarkers: \n" +
			"  source.js: !regexp /^(.*\\*\\/)?\\s*(\\}|\\))([^{]*\\{)?([;,]?\\s*|\\.[^{]*|\\s*\\)[;\\s]*)$/\n" +
			"description: Javascript bundle for RadRails, ported from the TextMate bundle\n" +
			"displayName: \"JavaScript\"\n" +
			"increaseIndentMarkers: \n" +
			"  source.js: !regexp /^.*(\\{[^}\"'']*|\\([^)\"'']*)$/\n" +
			"path: bundle.rb\n" +
			"repository: git://github.com/aptana/js.ruble.git\n" +
			"visible: true\n";
		// @formatter:on

		createBundleDirectory();

		// Now write the contents as a cache file
		writeFile("cache.yml", fileContents);

		List<File> files = Collections.emptyList();
		deserialized = cacher.load(bundleDirectory, files, new NullProgressMonitor(), true);

		// Now make sure the bundle has OK increase indent regexp!
		Map<ScopeSelector, RubyRegexp> indents = deserialized.getIncreaseIndentMarkers();
		assertEquals(1, indents.size());
		RubyRegexp regexp = indents.values().iterator().next();
		assertEquals("(?-mix:^.*(\\{[^}\"'']*|\\([^)\"'']*)$)", regexp.toString());
	}

	@Test
	public void testSerializeAndDeserializeSnippet() throws Exception
	{
		assumeFalse("Tests currently fail under tycho due to framework load paths not being set properly.", EclipseUtil.isTycho());
		// @formatter:off
		String fileContents = "require 'ruble'\n" 
			+ "					\n" 
			+ "bundle do\n" 
			+ "  snippet 'def ... end ' do |s|\n"
			+ "    s.trigger = 'def'\n" 
			+ "    s.expansion = 'def ${1:method_name}\n" 
			+ "  $0\n" 
			+ "end'\n"
			+ "  end\n" 
			+ "end\n";
		assertDeserializedCacheEqualsFromDisk(fileContents);
		// @formatter:on
	}

	@Test
	public void testSerializeAndDeserializeSmartTypingPairs() throws Exception
	{
		assumeFalse("Tests currently fail under tycho due to framework load paths not being set properly.", EclipseUtil.isTycho());
		// @formatter:off
		String fileContents = "require 'ruble'\n\n"
			+ "bundle {|b| }\n"
			+ "smart_typing_pairs['text.html'] = ['<', '>']\n"
			+ "smart_typing_pairs['text.html meta.tag - punctuation.definition.tag.begin'] = ['\"', '\"', '(', ')', '{', '}', '[', ']', \"'\", \"'\"]\n";
		assertDeserializedCacheEqualsFromDisk(fileContents);
		// @formatter:on
	}

	@Test
	public void testSerializeAndDeserializeContentAssist() throws Exception
	{
		assumeFalse("Tests currently fail under tycho due to framework load paths not being set properly.", EclipseUtil.isTycho());
		// @formatter:off
		String fileContents = "require 'ruble'\n\n" 
			+ "bundle {|b| }\n"
			+ "content_assist 'Type Inference code assist' do |ca|\n" 
			+ "  ca.scope = 'source.ruby'\n"
			+ "  ca.input = :document\n" 
			+ "  ca.invoke do |context|\n" 
			+ "    require 'content_assist'\n"
			+ "    ContentAssistant.new($stdin, context.editor.caret_offset).assist\n" 
			+ "  end\n" + "end\n";
		assertDeserializedCacheEqualsFromDisk(fileContents);
		// @formatter:on
	}

	@Test
	public void testSerializeAndDeserializeCommand() throws Exception
	{
		assumeFalse("Tests currently fail under tycho due to framework load paths not being set properly.", EclipseUtil.isTycho());
		// @formatter:off
		String fileContents = "require 'ruble'\n\n" 
			+ "bundle {|b| }\n" 
			+ "command 'Hash Pointer - =>' do |cmd|\n"
			+ "  cmd.key_binding = 'Control+L'\n" 
			+ "  cmd.output = :insert_as_text\n" 
			+ "  cmd.input = :none\n"
			+ "  cmd.scope = 'source.ruby'\n" 
			+ "  cmd.invoke do |context|\n" 
			+ "    ' => '\n" 
			+ "  end\n"
			+ "end\n";
		assertDeserializedCacheEqualsFromDisk(fileContents);
		// @formatter:on
	}

	@Test
	public void testSerializeAndDeserializeEnvironmentElement() throws Exception
	{
		// FIXME This doesn't match because the invoke block isn't getting pulled up!
		assumeFalse("Tests currently fail under tycho due to framework load paths not being set properly.", EclipseUtil.isTycho());
		// @formatter:off
		String fileContents = "require 'ruble'\n\n"
			+ "bundle {|b| }\n"
			+ "env 'text.html' do |e|\n"
			+ "  e['TM_COMMENT_START'] = '<!-- '\n"
			+ "  e['TM_COMMENT_END'] = ' -->'\n"
			+ "  e['TM_HTML_EMPTY_TAGS'] = 'area|base|basefont|br|col|frame|hr|img|input|isindex|link|meta|param'\n"
			+ "  e.delete('TM_COMMENT_START_2')\n" + "  e.delete('TM_COMMENT_END_2')\n"
			+ "  e.delete('TM_COMMENT_DISABLE_INDENT')\n" + "end\n";
		assertDeserializedCacheEqualsFromDisk(fileContents);
		// @formatter:on
	}

	@Test
	public void testSerializeAndDeserializeMenus() throws Exception
	{
		assumeFalse("Tests currently fail under tycho due to framework load paths not being set properly.", EclipseUtil.isTycho());
		// @formatter:off
		String fileContents = "require 'ruble'\n\n" 
			+ "bundle do |b|\n" 
			+ "  b.menu 'HTML' do |m|\n"
			+ "    m.scope = ['text.html']\n" 
			+ "    m.command 'Documentation for Tag'\n" 
			+ "    m.separator\n"
			+ "    m.menu 'Entities' do |e|\n" 
			+ "      e.command 'Convert Character / Selection to Entities'\n"
			+ "    end\n" 
			+ "  end\n" 
			+ "end\n";
		assertDeserializedCacheEqualsFromDisk(fileContents);
		// @formatter:on
	}

	@Test
	public void testSerializeAndDeserializeFileTemplates() throws Exception
	{
		// FIXME This doesn't match because the invoke block isn't getting pulled up!
		assumeFalse("Tests currently fail under tycho due to framework load paths not being set properly.", EclipseUtil.isTycho());
		// @formatter:off
		String fileContents = "require 'ruble'\n\n" 
			+ "bundle {|b| }\n\n" 
			+ "template('SVG Template') do |t|\n"
			+ "  t.filetype = '*.svg'\n" 
			+ "  t.invoke do |context|\n"
			+ "    ENV['TM_DATE'] = Time.now.strftime('%Y-%m-%d')\n"
			+ "    raw_contents = IO.read(\"#{ENV['TM_BUNDLE_SUPPORT']}/../templates/template.svg\")\n"
			+ "    raw_contents.gsub(/\\$\\{([^}]*)\\}/) {|match| ENV[match[2..-2]] }\n" 
			+ "  end\n" 
			+ "end\n";
		assertDeserializedCacheEqualsFromDisk(fileContents);
		// @formatter:on
	}

	@Test
	public void testSerializeAndDeserializeProjectTemplate() throws Exception
	{
		assumeFalse("Tests currently fail under tycho due to framework load paths not being set properly.", EclipseUtil.isTycho());
		// @formatter:off
		String fileContents = "require 'ruble'\n\n" 
			+ "bundle {|b| }\n"
			+ "project_template 'Basic Web Template' do |t|\n" 
			+ "  t.type = :web\n"
			+ "  t.location = 'templates/basic_web_template.zip'\n"
			+ "  t.description = 'A basic template which includes only a default index.html file'\n" 
			+ "end\n";
		assertDeserializedCacheEqualsFromDisk(fileContents);
		// @formatter:on
	}

	@Test
	public void testSerializeAndDeserializeProjectBuildPath() throws Exception
	{
		assumeFalse("Tests currently fail under tycho due to framework load paths not being set properly.", EclipseUtil.isTycho());
		// @formatter:off
		String fileContents = "require 'ruble'\n\n"
			+ "bundle do |b|\n" 
			+ "  b.project_build_path[\"jQuery 1.6.2\"] = \"#{File.dirname($0)}/support/jquery.1.6.2.sdocml\"\n"
			+ "end\n";
		assertDeserializedCacheEqualsFromDisk(fileContents);
		// @formatter:on
	}

	@Test
	public void testNewerTranslationFileBlowsAwayCache() throws Exception
	{
		assumeFalse("Tests currently fail under tycho due to framework load paths not being set properly.", EclipseUtil.isTycho());
		// @formatter:off
		String fileContents = "require 'ruble'\n\n" 
			+ "bundle do |b|\n" 
			+ "  b.menu t(:bundle_name) do |m|\n"
			+ "    m.scope = ['text.html']\n" 
			+ "    m.command 'Documentation for Tag'\n" 
			+ "    m.separator\n"
			+ "    m.menu 'Entities' do |e|\n" 
			+ "      e.command 'Convert Character / Selection to Entities'\n"
			+ "    end\n" 
			+ "  end\n" 
			+ "end\n";
		// @formatter:on

		createBundleDirectory();

		// Write a translation file
		File enYML = writeFile("config/locales/en.yml", "en:\n  bundle_name: 'HTML'\n");
		assertTrue("Failed to write test en.yml to disk", enYML.exists());

		// Now add a bundle.rb file and have it use the translation
		File bundleRB = writeFile("bundle.rb", fileContents);
		assertTrue("Failed to write test bundle.rb to disk", bundleRB.exists());

		// Load up the bundle
		bundleManager.loadScript(bundleRB, false);
		nonCached = bundleManager.getBundleFromPath(bundleDirectory);
		assertNotNull("Failed to load the test bundle into memory from file", nonCached);
		String nonCachedString = nonCached.toSource(false); // Store it's representation

		// Now generate a cached YAML serialized version of this...
		cacher.cache(bundleDirectory, new NullProgressMonitor());

		bundleManager.reset();

		// Now lets load it back in
		List<File> bundleFiles = CollectionsUtil.newList(bundleRB, enYML);
		deserialized = cacher.load(bundleDirectory, bundleFiles, new NullProgressMonitor(), true);
		assertNotNull("Failed to deserialize the test bundle from YAML", deserialized);
		String deserializedString = deserialized.toSource(false);

		// verify that our raw and cached version match
		assertEquals("Desearialized bundle doesn't match original", nonCachedString, deserializedString);
		Thread.sleep(1000);
		// OK, all good, now let's update the translations
		enYML = writeFile("config/locales/en.yml", "en:\n  bundle_name: 'New HTML'\n");

		// Now lets load it back in and verify that the cache is blown because translation file is newer than cache
		bundleFiles = CollectionsUtil.newList(bundleRB, enYML);
		BundleElement loaded = cacher.load(bundleDirectory, bundleFiles, new NullProgressMonitor(), false);
		assertNull("Expected to not get anything from cache since we updated the translations.", loaded);
	}

	/**
	 * Compares the bundle generated from loading it from disk through JRuby versus loading from cache YAML file through
	 * SnakeYAML. The elements in-memory should have the same structure (tested by comparing toString()).
	 * 
	 * @param fileContents
	 * @throws Exception
	 */
	private void assertDeserializedCacheEqualsFromDisk(String fileContents) throws Exception
	{
		createBundleDirectory();

		// Now add a bundle.rb file and define smart typing pairs inside
		File bundleRB = writeFile("bundle.rb", fileContents);

		assertTrue("Failed to write test bundle.rb to disk", bundleRB.exists());

		bundleManager.loadScript(bundleRB, false);
		nonCached = bundleManager.getBundleFromPath(bundleDirectory);
		assertNotNull("Failed to load the test bundle into memory from file", nonCached);
		String nonCachedString = nonCached.toSource(false);
		// System.out.println(nonCachedString);

		// Now generate a cached YAML serialized version of this...
		cacher.cache(bundleDirectory, new NullProgressMonitor());

		bundleManager.reset();

		//  Now lets load it back in
		List<File> bundleFiles = new ArrayList<File>();
		bundleFiles.add(bundleRB);
		deserialized = cacher.load(bundleDirectory, bundleFiles, new NullProgressMonitor(), true);
		assertNotNull("Failed to deserialize the test bundle from YAML", deserialized);
		String deserializedString = deserialized.toSource(false);
		// System.out.println(deserializedString);

		assertEquals("Desearialized bundle doesn't match original", nonCachedString, deserializedString);
	}

	protected File writeFile(String fileName, String fileContents) throws IOException
	{
		File file = new File(bundleDirectory, fileName);
		file.getParentFile().mkdirs();
		FileWriter writer = new FileWriter(file);
		writer.write(fileContents);
		writer.close();
		assertTrue("Failed to write test " + fileName + " to disk", file.exists());
		return file;
	}

	protected void createBundleDirectory()
	{
		bundleDirectory = new File(FileUtil.getTempDirectory().toOSString(),
				"bundle_cache_test_" + System.currentTimeMillis());
		bundleDirectory.mkdirs();
		assertTrue("Failed to create test bundle directory", bundleDirectory.exists());
	}

	private void delete(File file)
	{
		if (file == null)
		{
			return;
		}
		// recursively delete
		File[] children = file.listFiles();
		if (children != null)
		{
			for (File child : children)
			{
				delete(child);
			}
		}
		if (!file.delete())
		{
			file.deleteOnExit();
		}
	}

}
