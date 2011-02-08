package com.aptana.scripting.model;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.NullProgressMonitor;

public class BundleCacherTest extends TestCase
{
	private BundleElement nonCached;
	private BundleElement deserialized;
	private BundleCacher cacher;
	private File bundleDirectory;
	private BundleManager bundleManager;

	protected void setUp() throws Exception
	{
		super.setUp();
		bundleManager = BundleManager.getInstance();
		bundleManager.reset();
		cacher = new BundleCacher();
	}

	protected void tearDown() throws Exception
	{
		try
		{
			nonCached = null;
			deserialized = null;
			delete(bundleDirectory);
			bundleDirectory = null;
			cacher = null;
			bundleManager = null;
		}
		finally
		{
			super.tearDown();
		}
	}

	public void testSerializeAndDeserializeSnippet() throws Exception
	{
		String fileContents = "require 'ruble'\n" + "					\n" + "bundle do\n" + "  snippet 'def ... end ' do |s|\n"
				+ "    s.trigger = 'def'\n" + "    s.expansion = 'def ${1:method_name}\n" + "  $0\n" + "end'\n"
				+ "  end\n" + "end\n";
		assertDeserializedCacheEqualsFromDisk(fileContents);
	}

	public void testSerializeAndDeserializeSmartTypingPairs() throws Exception
	{
		String fileContents = "require 'ruble'\n\n"
				+ "bundle {|b| }\n"
				+ "smart_typing_pairs['text.html'] = ['<', '>']\n"
				+ "smart_typing_pairs['text.html meta.tag - punctuation.definition.tag.begin'] = ['\"', '\"', '(', ')', '{', '}', '[', ']', \"'\", \"'\"]\n";
		assertDeserializedCacheEqualsFromDisk(fileContents);
	}

	public void testSerializeAndDeserializeContentAssist() throws Exception
	{
		String fileContents = "require 'ruble'\n\n" + "bundle {|b| }\n"
				+ "content_assist 'Type Inference code assist' do |ca|\n" + "  ca.scope = 'source.ruby'\n"
				+ "  ca.input = :document\n" + "  ca.invoke do |context|\n" + "    require 'content_assist'\n"
				+ "    ContentAssistant.new($stdin, context.editor.caret_offset).assist\n" + "  end\n" + "end\n";
		assertDeserializedCacheEqualsFromDisk(fileContents);
	}

	public void testSerializeAndDeserializeCommand() throws Exception
	{
		String fileContents = "require 'ruble'\n\n" + "bundle {|b| }\n" + "command 'Hash Pointer - =>' do |cmd|\n"
				+ "  cmd.key_binding = 'Control+L'\n" + "  cmd.output = :insert_as_text\n" + "  cmd.input = :none\n"
				+ "  cmd.scope = 'source.ruby'\n" + "  cmd.invoke do |context|\n" + "    ' => '\n" + "  end\n"
				+ "end\n";
		assertDeserializedCacheEqualsFromDisk(fileContents);
	}

	public void testSerializeAndDeserializeEnvironmentElement() throws Exception
	{
		// FIXME This doesn't match because the invoke block isn't getting pulled up!
		String fileContents = "require 'ruble'\n\n"
				+ "bundle {|b| }\n"
				+ "env 'text.html' do |e|\n"
				+ "  e['TM_COMMENT_START'] = '<!-- '\n"
				+ "  e['TM_COMMENT_END'] = ' -->'\n"
				+ "  e['TM_HTML_EMPTY_TAGS'] = 'area|base|basefont|br|col|frame|hr|img|input|isindex|link|meta|param'\n"
				+ "  e.delete('TM_COMMENT_START_2')\n" + "  e.delete('TM_COMMENT_END_2')\n"
				+ "  e.delete('TM_COMMENT_DISABLE_INDENT')\n" + "end\n";
		assertDeserializedCacheEqualsFromDisk(fileContents);
	}

	public void testSerializeAndDeserializeMenus() throws Exception
	{
		String fileContents = "require 'ruble'\n\n" + "bundle do |b|\n" + "  b.menu 'HTML' do |m|\n"
				+ "    m.scope = ['text.html']\n" + "    m.command 'Documentation for Tag'\n" + "    m.separator\n"
				+ "    m.menu 'Entities' do |e|\n" + "      e.command 'Convert Character / Selection to Entities'\n"
				+ "    end\n" + "  end\n" + "end\n";
		assertDeserializedCacheEqualsFromDisk(fileContents);
	}

	public void testSerializeAndDeserializeFileTemplates() throws Exception
	{
		// FIXME This doesn't match because the invoke block isn't getting pulled up!
		String fileContents = "require 'ruble'\n\n" + "bundle {|b| }\n\n" + "template('SVG Template') do |t|\n"
				+ "  t.filetype = '*.svg'\n" + "  t.invoke do |context|\n"
				+ "    ENV['TM_DATE'] = Time.now.strftime('%Y-%m-%d')\n"
				+ "    raw_contents = IO.read(\"#{ENV['TM_BUNDLE_SUPPORT']}/../templates/template.svg\")\n"
				+ "    raw_contents.gsub(/\\$\\{([^}]*)\\}/) {|match| ENV[match[2..-2]] }\n" + "  end\n" + "end\n";
		assertDeserializedCacheEqualsFromDisk(fileContents);
	}

	public void testSerializeAndDeserializeProjectTemplate() throws Exception
	{
		String fileContents = "require 'ruble'\n\n" + "bundle {|b| }\n"
				+ "project_template 'Basic Web Template' do |t|\n" + "  t.type = :web\n"
				+ "  t.location = 'templates/basic_web_template.zip'\n"
				+ "  t.description = 'A basic template which includes only a default index.html file'\n" + "end\n";
		assertDeserializedCacheEqualsFromDisk(fileContents);
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
		String tmpDir = System.getProperty("java.io.tmpdir");
		bundleDirectory = new File(new File(tmpDir), "bundle_cache_test_" + System.currentTimeMillis());
		bundleDirectory.mkdirs();
		assertTrue("Failed to create test bundle directory", bundleDirectory.exists());

		// Now add a bundle.rb file and define smart typing pairs inside
		File bundleRB = new File(bundleDirectory, "bundle.rb");
		FileWriter writer = new FileWriter(bundleRB);
		writer.write(fileContents);
		writer.close();

		assertTrue("Failed to write test bundle.rb to disk", bundleRB.exists());

		bundleManager.loadScript(bundleRB, false);
		nonCached = bundleManager.getBundleFromPath(bundleDirectory);
		assertNotNull("Failed to load the test bundle into memory from file", nonCached);
		String nonCachedString = nonCached.toString();
		// FIXME Wipe the invoke block lines out, since we explicitly don't serialize/deserialize those?
		System.out.println(nonCachedString);

		// Now generate a cached YAML serialized version of this...
		cacher.cache(bundleDirectory, new NullProgressMonitor());

		bundleManager.reset();

		//  Now lets load it back in
		List<File> bundleFiles = new ArrayList<File>();
		bundleFiles.add(bundleRB);
		deserialized = cacher.load(bundleDirectory, bundleFiles, new NullProgressMonitor());
		assertNotNull("Failed to deserialize the test bundle from YAML", deserialized);
		String deserializedString = deserialized.toString();
		System.out.println(deserializedString);

		assertEquals(nonCachedString, deserializedString);
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
