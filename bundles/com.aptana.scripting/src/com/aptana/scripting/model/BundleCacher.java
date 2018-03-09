/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.model;

import java.beans.IntrospectionException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubMonitor;
import org.jruby.Ruby;
import org.jruby.RubyProc;
import org.jruby.RubyRegexp;
import org.jruby.util.KCode;
import org.jruby.util.RegexpOptions;
//import org.yaml.snakeyaml.TypeDescription;
//import org.yaml.snakeyaml.Yaml;
//import org.yaml.snakeyaml.constructor.AbstractConstruct;
//import org.yaml.snakeyaml.constructor.Construct;
//import org.yaml.snakeyaml.constructor.Constructor;
//import org.yaml.snakeyaml.introspector.Property;
//import org.yaml.snakeyaml.nodes.MappingNode;
//import org.yaml.snakeyaml.nodes.Node;
//import org.yaml.snakeyaml.nodes.NodeId;
//import org.yaml.snakeyaml.nodes.NodeTuple;
//import org.yaml.snakeyaml.nodes.ScalarNode;
//import org.yaml.snakeyaml.nodes.SequenceNode;
//import org.yaml.snakeyaml.nodes.Tag;
//import org.yaml.snakeyaml.representer.Represent;
//import org.yaml.snakeyaml.representer.Representer;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.IOUtil;
import com.aptana.scope.ScopeSelector;
import com.aptana.scripting.ScriptingActivator;
import com.aptana.scripting.ScriptingEngine;

/**
 * This class serializes and deserializes the scripting model for a given bundle.
 * 
 * @author cwilliams
 */
public class BundleCacher
{

	/**
	 * The file where we store our serialized model.
	 */
	private static final String CACHE_FILE = "cache.yml"; //$NON-NLS-1$

	private static final String REGEXP_TAG = "!regexp"; //$NON-NLS-1$
	private static final String SCOPE_SELECTOR_TAG = "!scope"; //$NON-NLS-1$
	private static final String ENVIRONMENT_TAG = "!environment"; //$NON-NLS-1$
	private static final String COMMAND_TAG = "!command"; //$NON-NLS-1$
	private static final String CONTENT_ASSIST_TAG = "!content_assist"; //$NON-NLS-1$
	private static final String TEMPLATE_TAG = "!template"; //$NON-NLS-1$

	private LoadCycleListener listener;

	private class BundleCacheInvalidatingLoadCycleListener implements LoadCycleListener
	{
		public void scriptUnloaded(File script)
		{
			// if file has been deleted, update the cache!
			if (!script.exists())
			{
				File bundleDir = getBundleDir(script);
				// Update the cache
				cache(bundleDir, new NullProgressMonitor());
			}
		}

		public void scriptReloaded(File script)
		{
			// if file is newer than cache, update the cache!
			updateCacheIfNecessary(script);
		}

		public void scriptLoaded(File script)
		{
			updateCacheIfNecessary(script);
		}

		private void updateCacheIfNecessary(File script)
		{
			File bundleDir = getBundleDir(script);
			File cacheFile = getCacheFile(bundleDir);

			List<File> bundleFiles = new ArrayList<File>();
			bundleFiles.add(script);
			if (anyFilesNewer(cacheFile, bundleFiles, new NullProgressMonitor()))
			{
				// Update the cache
				cache(bundleDir, new NullProgressMonitor());
			}
		}
	}

	public BundleCacher()
	{
		listener = new BundleCacheInvalidatingLoadCycleListener();
		getBundleManager().addLoadCycleListener(listener);
	}

	protected File getBundleDir(File script)
	{
		return getBundleManager().getBundleDirectory(script);
	}

	public void dispose()
	{
		if (listener != null)
		{
			getBundleManager().removeLoadCycleListener(listener);
		}
		listener = null;
	}

	public void cache(File bundleDirectory, IProgressMonitor monitor)
	{
		// grab the bundle model
		cache(getBundleManager().getBundleFromPath(bundleDirectory));
	}

	protected BundleManager getBundleManager()
	{
		return BundleManager.getInstance();
	}

	protected boolean cache(BundleElement be)
	{
//		if (be == null)
//		{
			return false;
//		}
//
//		// Now write the config file out...
//		OutputStreamWriter writer = null;
//		File cacheFile = null;
//		Yaml yaml = null;
//
//		try
//		{
//			if (be.getBundleDirectory().canWrite())
//			{
//				yaml = createYAML(be.getBundleDirectory());
//
//				Locale locale = Locale.getDefault();
//				cacheFile = new File(be.getBundleDirectory(), MessageFormat.format(
//						"cache.{0}_{1}.yml", locale.getLanguage(), locale.getCountry())); //$NON-NLS-1$
//				writer = new OutputStreamWriter(new FileOutputStream(cacheFile), IOUtil.UTF_8);
//
//				yaml.dump(be, writer);
//			}
//		}
//		catch (IOException e)
//		{
//			IdeLog.logError(ScriptingActivator.getDefault(), e);
//		}
//		finally
//		{
//			if (writer != null)
//			{
//				try
//				{
//					writer.close();
//				}
//				catch (IOException e)
//				{
//					// ignore
//				}
//			}
//		}
//
//		InputStreamReader reader = null;
//		boolean serializationSucceeded = false;
//		try
//		{
//			if (be.getBundleDirectory().canRead())
//			{
//				if (cacheFile == null || !cacheFile.exists())
//				{
//					return false;
//				}
//				reader = new InputStreamReader(new FileInputStream(cacheFile), IOUtil.UTF_8);
//				BundleElement be2 = (BundleElement) yaml.load(reader);
//				if (be2 == null)
//				{
//					return false;
//				}
//				// invoke blocks don't serialize correctly, so the comparison gets screwy.
//				String beString1 = be.toSource(false);
//				String beString2 = be2.toSource(false);
//
//				// It's not the ideal way to test equality, but seems to work correctly. This is the mechanism
//				// currently in use by the unit tests
//				serializationSucceeded = beString2.equals(beString1);
//				return true;
//			}
//		}
//		catch (IOException e)
//		{
//			IdeLog.logError(ScriptingActivator.getDefault(), e);
//		}
//		finally
//		{
//			if (reader != null)
//			{
//				try
//				{
//					reader.close();
//				}
//				catch (IOException e)
//				{
//					// ignore
//				}
//			}
//
//			if (!serializationSucceeded && cacheFile != null)
//			{
//				IdeLog.logWarning(ScriptingActivator.getDefault(),
//						MessageFormat.format(Messages.BundleCacher_SerializationExceptionDeletingCacheFile, cacheFile));
//				cacheFile.delete();
//			}
//		}
//
//		return false;
	}

	/**
	 * Tries to return the cache file that matches the current locale.
	 * 
	 * @param bundleDirectory
	 * @return
	 */
	private File getCacheFile(File bundleDirectory)
	{
		File file = null;
		Locale current = Locale.getDefault();
		// Try "cache.lang_country.yml", like "cache.en_US.yml"
		file = new File(bundleDirectory, MessageFormat.format("cache.{0}_{1}.yml", current.getLanguage(), //$NON-NLS-1$
				current.getCountry()));
		if (file.isFile())
		{
			return file;
		}
		// Then try just language: cache.en.yml
		file = new File(bundleDirectory, MessageFormat.format("cache.{0}.yml", current.getLanguage())); //$NON-NLS-1$
		if (file.isFile())
		{
			return file;
		}
		// Fall back to cache.yml
		return new File(bundleDirectory, CACHE_FILE);
	}

	/**
	 * Load the cached file from disk
	 * 
	 * @param bundleDirectory
	 * @param bundleFiles
	 * @param monitor
	 * @return
	 */
	public BundleElement load(final File bundleDirectory, List<File> bundleFiles, IProgressMonitor monitor)
	{
		return load(bundleDirectory, bundleFiles, monitor, false);
	}

	/**
	 * Load the cached file from disk
	 * 
	 * @param bundleDirectory
	 * @param bundleFiles
	 * @param monitor
	 * @param ignoreFileStatus
	 * @return
	 */
	public BundleElement load(final File bundleDirectory, List<File> bundleFiles, IProgressMonitor monitor,
			boolean ignoreFileStatus)
	{
		return null;
//		SubMonitor sub = SubMonitor.convert(monitor, 120);
//		BundleElement be = null;
//		try
//		{
//			File cacheFile = getCacheFile(bundleDirectory);
//			if (!cacheFile.exists())
//			{
//				return null;
//			}
//
//			// IF any file is newer, ignore the cache, it'll get rewritten
//			if (!ignoreFileStatus && anyFilesNewer(cacheFile, bundleFiles, sub.newChild(10)))
//			{
//				return null;
//			}
//
//			// Load up the bundle contents from the cache
//			InputStreamReader reader = null;
//			try
//			{
//				Yaml yaml = createYAML(bundleDirectory);
//				reader = new InputStreamReader(new FileInputStream(cacheFile), IOUtil.UTF_8);
//				sub.subTask(MessageFormat.format(Messages.BundleCacher_LoadCacheTaskName,
//						bundleDirectory.getAbsolutePath()));
//
//				synchronized (this)
//				{
//					be = (BundleElement) yaml.load(reader);
//				}
//
//				sub.worked(80);
//
//				// If any file has been deleted, ignore the cache, it'll get rewritten
//				if (!ignoreFileStatus && anyFileDeleted(be, sub.newChild(10)))
//				{
//					return null;
//				}
//				fireScriptLoadedEvents(be, sub.newChild(20));
//			}
//			catch (Exception e)
//			{
//				IdeLog.logError(ScriptingActivator.getDefault(),
//						MessageFormat.format("Failed to load bundle {0}", bundleDirectory.getAbsolutePath()), e); //$NON-NLS-1$
//				IdeLog.logInfo(ScriptingActivator.getDefault(),
//						MessageFormat.format(Messages.BundleCacher_LoadingYAMLError, bundleDirectory.getAbsolutePath()));
//			}
//			finally
//			{
//				if (reader != null)
//				{
//					try
//					{
//						reader.close();
//					}
//					catch (IOException e)
//					{
//						// ignore
//					}
//				}
//			}
//		}
//		finally
//		{
//			sub.done();
//		}
//		return be;
	}

	private void fireScriptLoadedEvents(BundleElement be, IProgressMonitor monitor)
	{
		SubMonitor sub = SubMonitor.convert(monitor, 100);
		// Fire off the events that normally would get fired
		Set<File> files = getFiles(be, sub.newChild(30));
		for (File file : files)
		{
			getBundleManager().fireScriptLoadedEvent(file);
			sub.worked(100 / files.size());
		}
		sub.done();
	}

	private Set<File> getFiles(BundleElement be, IProgressMonitor monitor)
	{
		Set<File> files = new HashSet<File>();
		if (be == null)
		{
			return files;
		}

		String path = be.getPath();
		files.add(new File(path));

		List<AbstractBundleElement> children = be.getChildren();
		if (children == null)
		{
			return files;
		}
		SubMonitor sub = SubMonitor.convert(monitor, children.size());
		try
		{
			for (AbstractBundleElement abe : be.getChildren())
			{
				path = abe.getPath();
				files.add(new File(path));
				sub.worked(1);
				if (abe instanceof MenuElement)
				{
					MenuElement menu = (MenuElement) abe;
					files.addAll(getFiles(menu));
				}
			}
		}
		finally
		{
			sub.done();
		}
		return files;
	}

	private Set<File> getFiles(MenuElement parent)
	{
		if (parent == null || !parent.hasChildren())
		{
			return Collections.emptySet();
		}
		Set<File> files = new HashSet<File>();
		for (MenuElement child : parent.getChildren())
		{
			String path = child.getPath();
			files.add(new File(path));
			files.addAll(getFiles(child));
		}
		return files;
	}

	private boolean anyFilesNewer(File cacheFile, List<File> bundleFiles, IProgressMonitor monitor)
	{
		SubMonitor sub = SubMonitor.convert(monitor, bundleFiles.size());
		try
		{
			// Compare lastMod versus all the bundleFiles.
			long lastMod = cacheFile.lastModified();
			for (File file : bundleFiles)
			{
				sub.subTask(MessageFormat.format(Messages.BundleCacher_ComparingTimestampSubTaskName,
						file.getAbsolutePath()));
				// assume if there's exactly the same value then we should be safe and reload
				if (file.lastModified() >= lastMod)
				{
					// One of the files is newer, don't load cache! This will reload everything from disk and rewrite
					// the cache
					IdeLog.logInfo(ScriptingActivator.getDefault(),
							MessageFormat.format(Messages.BundleCacher_OutOfDateCacheMsg, file.getPath()));
					return true;
				}
			}
		}
		finally
		{
			sub.done();
		}
		return false;
	}

	private boolean anyFileDeleted(BundleElement be, IProgressMonitor monitor)
	{
		if (be == null)
		{
			return false;
		}
		// FIXME If a file is deleted, remove all the elements contributed from that path and rewrite the cache!
		// (i.e. just update the diff!)
		List<AbstractBundleElement> children = be.getChildren();
		if (children == null)
		{
			return false;
		}
		SubMonitor sub = SubMonitor.convert(monitor, children.size());
		try
		{
			for (AbstractBundleElement abe : be.getChildren())
			{
				String path = abe.getPath();
				if (!new File(path).exists())
				{
					IdeLog.logInfo(
							ScriptingActivator.getDefault(),
							MessageFormat.format(Messages.BundleCacher_FileReferencedInCacheMissingMsg, path,
									abe.toString()));
					return true;
				}
				if (abe instanceof MenuElement)
				{
					MenuElement menu = (MenuElement) abe;
					return anyFileDeleted(menu);
				}
				sub.worked(1);
			}
		}
		finally
		{
			sub.done();
		}
		return false;
	}

	private boolean anyFileDeleted(MenuElement parent)
	{
		if (parent == null || !parent.hasChildren())
		{
			return false;
		}
		for (MenuElement child : parent.getChildren())
		{
			String path = child.getPath();
			if (!new File(path).exists())
			{
				IdeLog.logInfo(
						ScriptingActivator.getDefault(),
						MessageFormat.format(Messages.BundleCacher_FileReferencedInCacheMissingMsg, path,
								child.toString()));
				return true;
			}
			return anyFileDeleted(child);
		}
		return false;
	}

//	private Yaml createYAML(File bundleDirectory)
//	{
//		return new Yaml(new BundleElementsConstructor(bundleDirectory), new MyRepresenter(bundleDirectory));
//	}
//
//	private class MyRepresenter extends Representer
//	{
//		private File bundleDirectory;
//
//		public MyRepresenter(File bundleDirectory)
//		{
//			this.bundleDirectory = bundleDirectory;
//			this.representers.put(RubyRegexp.class, new RepresentRubyRegexp());
//			this.representers.put(ScopeSelector.class, new RepresentScopeSelector());
//			this.addClassTag(LazyCommandElement.class, new Tag(COMMAND_TAG));
//			this.addClassTag(LazyEnvironmentElement.class, new Tag(ENVIRONMENT_TAG));
//			this.addClassTag(LazyTemplateElement.class, new Tag(TEMPLATE_TAG));
//			this.addClassTag(LazyContentAssistElement.class, new Tag(CONTENT_ASSIST_TAG));
//			this.addClassTag(BundleElement.class, new Tag("!ruby/object:Ruble::Bundle"));
//			this.addClassTag(SmartTypingPairsElement.class, new Tag("!ruby/object:Ruble::SmartTypingPair"));
//			this.addClassTag(MenuElement.class, new Tag("!ruby/object:Ruble::Menu"));
//			this.addClassTag(CommandElement.class, new Tag("!ruby/object:Ruble::Command"));
//			this.addClassTag(SnippetElement.class, new Tag("!ruby/object:Ruble::Snippet"));
//			this.addClassTag(EnvironmentElement.class, new Tag("!ruby/object:Ruble::Env"));
//			this.addClassTag(TemplateElement.class, new Tag("!ruby/object:Ruble::Template"));
//			this.addClassTag(Platform.class, new Tag("!ruby/object:Ruble::Platform"));
//		}
//
//		@Override
//		protected NodeTuple representJavaBeanProperty(Object javaBean, Property property, Object propertyValue,
//				Tag customTag)
//		{
//			if (javaBean instanceof AbstractElement)
//			{
//				if ("path".equals(property.getName()) || "buildPath".equals(property.getName())) //$NON-NLS-1$ //$NON-NLS-2$
//				{
//					String path = (String) propertyValue;
//					IPath relative = Path.fromOSString(path).makeRelativeTo(
//							Path.fromOSString(bundleDirectory.getAbsolutePath()));
//					propertyValue = relative.toOSString();
//				}
//			}
//			return super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);
//		}
//
//		@Override
//		protected Set<Property> getProperties(Class<? extends Object> type) throws IntrospectionException
//		{
//			if (type.equals(Ruby.class) || type.equals(KCode.class) || type.equals(RubyProc.class))
//			{
//				return Collections.emptySet();
//			}
//			Set<Property> set = super.getProperties(type);
//			if (CommandElement.class.isAssignableFrom(type) || type.equals(EnvironmentElement.class))
//			{
//				// drop runtime, invoke, and invoke block properties
//				Set<Property> toRemove = new HashSet<Property>();
//				for (Property prop : set)
//				{
//					if ("invokeBlock".equals(prop.getName()) || "runtime".equals(prop.getName()) //$NON-NLS-1$ //$NON-NLS-2$
//							|| "invoke".equals(prop.getName())) //$NON-NLS-1$
//					{
//						toRemove.add(prop);
//					}
//				}
//
//				set.removeAll(toRemove);
//			}
//			return set;
//		}
//
//		/**
//		 * Store ruby regexps as strings.
//		 * 
//		 * @author cwilliams
//		 */
//		private class RepresentRubyRegexp implements Represent
//		{
//			public Node representData(Object data)
//			{
//				RubyRegexp dice = (RubyRegexp) data;
//				String value = dice.toString();
//				return representScalar(new Tag(REGEXP_TAG), value);
//			}
//		}
//
//		/**
//		 * Store scope selectors as strings.
//		 * 
//		 * @author cwilliams
//		 */
//		private class RepresentScopeSelector implements Represent
//		{
//			public Node representData(Object data)
//			{
//				ScopeSelector dice = (ScopeSelector) data;
//				String value = dice.toString();
//				return representScalar(new Tag(SCOPE_SELECTOR_TAG), value);
//			}
//		}
//	}
//
//	class BundleElementsConstructor extends Constructor
//	{
//
//		private File bundleDirectory;
//
//		public BundleElementsConstructor(File bundleDirectory)
//		{
//			this.bundleDirectory = bundleDirectory;
//			this.yamlConstructors.put(new Tag(SCOPE_SELECTOR_TAG), new ConstructScopeSelector());
//			this.yamlConstructors.put(new Tag("!ruby/regexp"), new ConstructRubyRegexp()); //$NON-NLS-1$
//			this.yamlConstructors.put(new Tag(REGEXP_TAG), new ConstructRubyRegexp());
//			this.yamlConstructors.put(new Tag(COMMAND_TAG), new ConstructCommandElement());
//			this.yamlConstructors.put(new Tag(ENVIRONMENT_TAG), new ConstructEnvironmentElement());
//			this.yamlConstructors.put(new Tag(CONTENT_ASSIST_TAG), new ConstructContentAssistElement());
//			this.yamlConstructors.put(new Tag(TEMPLATE_TAG), new ConstructTemplateElement());
//			this.yamlConstructors.put(new Tag(BundleElement.class), new ConstructBundleElement());
//			this.yamlConstructors.put(new Tag(MenuElement.class), new ConstructMenuElement());
//			this.yamlConstructors.put(new Tag(SnippetElement.class), new ConstructSnippetElement());
//			this.yamlConstructors.put(new Tag(SnippetCategoryElement.class), new ConstructSnippetCategoryElement());
//			this.yamlConstructors.put(new Tag(ContentAssistElement.class), new ConstructContentAssistElement());
//			this.yamlConstructors.put(new Tag(CommandElement.class), new ConstructCommandElement());
//			this.yamlConstructors.put(new Tag(TemplateElement.class), new ConstructTemplateElement());
//			this.yamlConstructors.put(new Tag(SmartTypingPairsElement.class), new ConstructSmartTypingPairsElement());
//			this.yamlConstructors.put(new Tag(ProjectTemplateElement.class), new ConstructProjectTemplateElement());
//			this.yamlConstructors.put(new Tag(ProjectSampleElement.class), new ConstructProjectSampleElement());
//			this.yamlConstructors.put(new Tag(EnvironmentElement.class), new ConstructEnvironmentElement());
//			this.yamlConstructors.put(new Tag(BuildPathElement.class), new ConstructBuildPathElement());
//
//			// Tell it that "children" field for MenuElement is a list of MenuElements
//			TypeDescription menuDescription = new TypeDescription(MenuElement.class);
//			menuDescription.putListPropertyType("children", MenuElement.class); //$NON-NLS-1$
//			addTypeDescription(menuDescription);
//		}
//
//		private class ConstructScopeSelector extends AbstractConstruct
//		{
//			public Object construct(Node node)
//			{
//				String val = (String) constructScalar((ScalarNode) node);
//				return new ScopeSelector(val);
//			}
//		}
//
//		private class ConstructRubyRegexp extends AbstractConstruct
//		{
//			public Object construct(Node node)
//			{
//				String val = (String) constructScalar((ScalarNode) node);
//				// Handle when regexp is using // syntax. Lop the slashes off the ends.
//				if (val != null && val.length() > 2 && val.charAt(0) == '/')
//				{
//					val = val.substring(1, val.length() - 1);
//				}
//				return RubyRegexp.newRegexp(ScriptingEngine.getInstance().getScriptingContainer().getProvider()
//						.getRuntime(), val, RegexpOptions.NULL_OPTIONS);
//			}
//		}
//
//		// TODO All these subclasses are pretty much the same. Pass in a Class type to constructor and use reflection to
//		// reduce duplication!
//		private abstract class AbstractBundleElementConstruct extends AbstractConstruct
//		{
//			/**
//			 * Grab the path from the mapping node and grab its value!
//			 * 
//			 * @param node
//			 * @return
//			 */
//			protected String getPath(Node node)
//			{
//				return getPath(node, "path"); //$NON-NLS-1$
//			}
//
//			/**
//			 * Grab the property value, assume it's a relative path and prepend the bundle's directory to make it an
//			 * absolute path.
//			 * 
//			 * @param node
//			 * @return
//			 */
//			protected String getPath(Node node, String propertyName)
//			{
//				String relativePath = null;
//				if (node instanceof MappingNode)
//				{
//					MappingNode map = (MappingNode) node;
//					List<NodeTuple> nodes = map.getValue();
//					for (NodeTuple tuple : nodes)
//					{
//						Node keyNode = tuple.getKeyNode();
//						if (keyNode instanceof ScalarNode)
//						{
//							ScalarNode scalar = (ScalarNode) keyNode;
//							String valueOfKey = scalar.getValue();
//							if (propertyName.equals(valueOfKey))
//							{
//								Node valueNode = tuple.getValueNode();
//								if (valueNode instanceof ScalarNode)
//								{
//									ScalarNode scalarValue = (ScalarNode) valueNode;
//									relativePath = scalarValue.getValue();
//									break;
//								}
//							}
//						}
//					}
//				}
//				if (relativePath != null)
//				{
//					IPath pathObj = Path.fromOSString(relativePath);
//					if (!pathObj.isAbsolute())
//					{
//						// Prepend the bundle directory.
//						relativePath = new File(bundleDirectory, relativePath).getAbsolutePath();
//					}
//				}
//				return relativePath;
//			}
//
//			/**
//			 * Fix for https://aptana.lighthouseapp.com/projects/35272/tickets/1658 Sets the prefix triggers for
//			 * CommandElements and subclasses. Fixes an issue where "[def]" isn't treated as an array of strings with
//			 * "def" as an item in it (and would instead think it's a string of "[def]").
//			 */
//			protected void setPrefixTriggers(Node node, CommandElement be)
//			{
//				MappingNode mapNode = (MappingNode) node;
//				List<NodeTuple> tuples = mapNode.getValue();
//				for (NodeTuple tuple : tuples)
//				{
//					ScalarNode keyNode = (ScalarNode) tuple.getKeyNode();
//					String key = keyNode.getValue();
//					if ("customProperties".equals(key)) //$NON-NLS-1$
//					{
//						Node customPropertiesValueNode = tuple.getValueNode();
//						if (customPropertiesValueNode instanceof MappingNode)
//						{
//							MappingNode custompropertiesNode = (MappingNode) customPropertiesValueNode;
//							for (NodeTuple propTuple : custompropertiesNode.getValue())
//							{
//								ScalarNode propKeyNode = (ScalarNode) propTuple.getKeyNode();
//								if ("prefix_values".equals(propKeyNode.getValue())) //$NON-NLS-1$
//								{
//									SequenceNode prefixValuesNode = (SequenceNode) propTuple.getValueNode();
//									List<String> values = new ArrayList<String>();
//									for (Node prefixValue : prefixValuesNode.getValue())
//									{
//										if (prefixValue instanceof ScalarNode)
//										{
//											ScalarNode blah = (ScalarNode) prefixValue;
//											values.add(blah.getValue());
//										}
//										else
//										{
//											IdeLog.logWarning(ScriptingActivator.getDefault(),
//													"Expected a flattened array for trigger, but got nested arrays."); //$NON-NLS-1$
//										}
//									}
//									be.setTrigger(TriggerType.PREFIX.getName(),
//											values.toArray(new String[values.size()]));
//								}
//							}
//						}
//					}
//				}
//			}
//		}
//
//		private class ConstructBuildPathElement extends AbstractBundleElementConstruct
//		{
//			public Object construct(Node node)
//			{
//				node.setType(BuildPathElement.class);
//
//				String path = getPath(node);
//				String buildPath = getPath(node, "buildPath"); //$NON-NLS-1$
//				BuildPathElement bpe = new BuildPathElement(path);
//				Construct mappingConstruct = yamlClassConstructors.get(NodeId.mapping);
//				mappingConstruct.construct2ndStep(node, bpe);
//				bpe.setPath(path);
//				bpe.setBuildPath(buildPath);
//				return bpe;
//			}
//		}
//
//		private class ConstructBundleElement extends AbstractBundleElementConstruct
//		{
//			public Object construct(Node node)
//			{
//				node.setType(BundleElement.class);
//				String path = getPath(node);
//				BundleElement be = new BundleElement(path);
//				Construct mappingConstruct = yamlClassConstructors.get(NodeId.mapping);
//				mappingConstruct.construct2ndStep(node, be);
//				be.setPath(path);
//				return be;
//			}
//		}
//
//		private class ConstructCommandElement extends AbstractBundleElementConstruct
//		{
//			public Object construct(Node node)
//			{
//				node.setType(CommandElement.class);
//				String path = getPath(node);
//				CommandElement be = new LazyCommandElement(path);
//				Construct mappingConstruct = yamlClassConstructors.get(NodeId.mapping);
//				mappingConstruct.construct2ndStep(node, be);
//				be.setPath(path);
//				setPrefixTriggers(node, be);
//				return be;
//			}
//		}
//
//		private class ConstructSnippetElement extends AbstractBundleElementConstruct
//		{
//			public Object construct(Node node)
//			{
//				node.setType(SnippetElement.class);
//				String path = getPath(node);
//				SnippetElement be = new SnippetElement(path);
//				Construct mappingConstruct = yamlClassConstructors.get(NodeId.mapping);
//				mappingConstruct.construct2ndStep(node, be);
//				be.setPath(path);
//				setPrefixTriggers(node, be);
//				return be;
//			}
//		}
//
//		private class ConstructSnippetCategoryElement extends AbstractBundleElementConstruct
//		{
//			public Object construct(Node node)
//			{
//				node.setType(SnippetCategoryElement.class);
//				String path = getPath(node);
//				SnippetCategoryElement sce = new SnippetCategoryElement(path);
//				Construct mappingConstruct = yamlClassConstructors.get(NodeId.mapping);
//				mappingConstruct.construct2ndStep(node, sce);
//				sce.setPath(path);
//				return sce;
//			}
//		}
//
//		private class ConstructMenuElement extends AbstractBundleElementConstruct
//		{
//			public Object construct(Node node)
//			{
//				node.setType(MenuElement.class);
//				String path = getPath(node);
//				MenuElement be = new MenuElement(path);
//				Construct mappingConstruct = yamlClassConstructors.get(NodeId.mapping);
//				mappingConstruct.construct2ndStep(node, be);
//				be.setPath(path);
//				forcePathsOfChildren(be.getChildren());
//				return be;
//			}
//
//			private void forcePathsOfChildren(List<MenuElement> children)
//			{
//				if (children != null)
//				{
//					for (MenuElement child : children)
//					{
//						String childPath = child.getPath();
//						IPath pathObj = Path.fromOSString(childPath);
//						if (!pathObj.isAbsolute())
//						{
//							// Prepend the bundle directory.
//							child.setPath(bundleDirectory.getAbsolutePath() + File.separator + childPath);
//						}
//						forcePathsOfChildren(child.getChildren());
//					}
//				}
//			}
//		}
//
//		private class ConstructProjectTemplateElement extends AbstractBundleElementConstruct
//		{
//			public Object construct(Node node)
//			{
//				node.setType(ProjectTemplateElement.class);
//				String path = getPath(node);
//				ProjectTemplateElement be = new ProjectTemplateElement(path);
//				Construct mappingConstruct = yamlClassConstructors.get(NodeId.mapping);
//				mappingConstruct.construct2ndStep(node, be);
//				be.setPath(path);
//				return be;
//			}
//		}
//
//		private class ConstructProjectSampleElement extends AbstractBundleElementConstruct
//		{
//			public Object construct(Node node)
//			{
//				node.setType(ProjectSampleElement.class);
//				String path = getPath(node);
//				ProjectSampleElement be = new ProjectSampleElement(path);
//				Construct mappingConstruct = yamlClassConstructors.get(NodeId.mapping);
//				mappingConstruct.construct2ndStep(node, be);
//				be.setPath(path);
//				return be;
//			}
//		}
//
//		private class ConstructEnvironmentElement extends AbstractBundleElementConstruct
//		{
//			public Object construct(Node node)
//			{
//				node.setType(EnvironmentElement.class);
//				String path = getPath(node);
//				EnvironmentElement be = new LazyEnvironmentElement(path);
//				Construct mappingConstruct = yamlClassConstructors.get(NodeId.mapping);
//				mappingConstruct.construct2ndStep(node, be);
//				be.setPath(path);
//				return be;
//			}
//		}
//
//		private class ConstructTemplateElement extends AbstractBundleElementConstruct
//		{
//			public Object construct(Node node)
//			{
//				node.setType(TemplateElement.class);
//				String path = getPath(node);
//				TemplateElement be = new LazyTemplateElement(path);
//				Construct mappingConstruct = yamlClassConstructors.get(NodeId.mapping);
//				mappingConstruct.construct2ndStep(node, be);
//				be.setPath(path);
//				setPrefixTriggers(node, be);
//				return be;
//			}
//		}
//
//		private class ConstructContentAssistElement extends AbstractBundleElementConstruct
//		{
//			public Object construct(Node node)
//			{
//				node.setType(ContentAssistElement.class);
//				String path = getPath(node);
//				ContentAssistElement be = new LazyContentAssistElement(path);
//				Construct mappingConstruct = yamlClassConstructors.get(NodeId.mapping);
//				mappingConstruct.construct2ndStep(node, be);
//				be.setPath(path);
//				setPrefixTriggers(node, be);
//				return be;
//			}
//		}
//
//		private class ConstructSmartTypingPairsElement extends AbstractBundleElementConstruct
//		{
//			public Object construct(Node node)
//			{
//				node.setType(SmartTypingPairsElement.class);
//				String path = getPath(node);
//				SmartTypingPairsElement be = new SmartTypingPairsElement(path);
//				MappingNode mapNode = (MappingNode) node;
//				List<NodeTuple> tuples = mapNode.getValue();
//				for (NodeTuple tuple : tuples)
//				{
//					ScalarNode keyNode = (ScalarNode) tuple.getKeyNode();
//					String key = keyNode.getValue();
//					// "pairs", "scope", "displayName" are required
//					if ("pairs".equals(key)) //$NON-NLS-1$
//					{
//						SequenceNode pairsValueNode = (SequenceNode) tuple.getValueNode();
//						List<Character> pairs = new ArrayList<Character>();
//						List<Node> pairsValues = pairsValueNode.getValue();
//						for (Node pairValue : pairsValues)
//						{
//							ScalarNode blah = (ScalarNode) pairValue;
//							String pairCharacter = blah.getValue();
//							pairs.add(Character.valueOf(pairCharacter.charAt(0)));
//						}
//						be.setPairs(pairs);
//					}
//					else if ("scope".equals(key)) //$NON-NLS-1$
//					{
//						ScalarNode scopeValueNode = (ScalarNode) tuple.getValueNode();
//						be.setScope(scopeValueNode.getValue());
//					}
//					else if ("displayName".equals(key)) //$NON-NLS-1$
//					{
//						ScalarNode displayNameNode = (ScalarNode) tuple.getValueNode();
//						be.setDisplayName(displayNameNode.getValue());
//					}
//				}
//				return be;
//			}
//		}
//	}
//
//	/**
//	 * Lazily loads the real command element from disk when we try to access the invoke string/block or try to execute
//	 * it, since that stuff doesn't get serialized.
//	 * 
//	 * @author cwilliams
//	 */
//	private class LazyCommandElement extends CommandElement
//	{
//		private CommandElement real;
//
//		public LazyCommandElement(String path)
//		{
//			super(path);
//		}
//
//		@Override
//		public boolean isExecutable()
//		{
//			// FIXME Should really serialize some value that records what OSes the command has an invoke for so we can
//			// tell better if this is executable on this os!
//			return true;
//		}
//
//		@Override
//		public String getInvoke()
//		{
//			lazyLoad();
//			if (real == null)
//			{
//				return null;
//			}
//			return real.getInvoke();
//		}
//
//		@Override
//		public RubyProc getInvokeBlock()
//		{
//			lazyLoad();
//			if (real == null)
//			{
//				return null;
//			}
//			return real.getInvokeBlock();
//		}
//
//		@Override
//		public CommandResult execute(CommandContext context)
//		{
//			lazyLoad();
//			if (real == null)
//			{
//				return null;
//			}
//			return real.execute(context);
//		}
//
//		@Override
//		public Ruby getRuntime()
//		{
//			lazyLoad();
//			if (real == null)
//			{
//				return null;
//			}
//			return real.getRuntime();
//		}
//
//		@Override
//		public CommandContext createCommandContext()
//		{
//			lazyLoad();
//			return new CommandContext(real);
//		}
//
//		private synchronized void lazyLoad()
//		{
//			if (real == null)
//			{
//				BundleElement owning = getOwningBundle();
//				if (owning == null) // we haven't even been attached yet!
//				{
//					return;
//				}
//				// remove all elements that are declared in the same file, since they'll end up getting
//				// loaded below.
//				List<AbstractElement> elements = BundleElement.getElementsByPath(getPath());
//				for (AbstractElement element : elements)
//				{
//					if (element instanceof AbstractBundleElement)
//					{
//						AbstractBundleElement abe = (AbstractBundleElement) element;
//						owning.removeChild(abe);
//					}
//				}
//
//				// Now load up the file so it really loads into the BundleManager
//				BundleManager.getInstance().loadScript(new File(getPath()));
//				// If bundle.rb itself was reloaded we need to update the bundle element we're attached to as owner!
//				if (getPath().equals(owning.getPath()))
//				{
//					owning = BundleManager.getInstance().getBundleFromPath(new File(getPath()).getParent());
//					this.setOwningBundle(owning);
//				}
//
//				// Now for whatever code is holding a reference to this, redirect method calls to the
//				// real command
//				real = owning.getCommandByName(getDisplayName());
//			}
//		}
//	}
//
//	/**
//	 * Lazily loads the real CA element from disk when we try to access the invoke string/block or try to execute it,
//	 * since that stuff doesn't get serialized.
//	 * 
//	 * @author cwilliams
//	 */
//	private class LazyContentAssistElement extends ContentAssistElement
//	{
//		private ContentAssistElement real;
//
//		public LazyContentAssistElement(String path)
//		{
//			super(path);
//		}
//
//		@Override
//		public boolean isExecutable()
//		{
//			// FIXME Should really serialize some value that records what OSes the command has an invoke for so we can
//			// tell better if this is executable on this os!
//			return true;
//		}
//
//		@Override
//		public String getInvoke()
//		{
//			lazyLoad();
//			if (real == null)
//			{
//				return null;
//			}
//			return real.getInvoke();
//		}
//
//		@Override
//		public RubyProc getInvokeBlock()
//		{
//			lazyLoad();
//			if (real == null)
//			{
//				return null;
//			}
//			return real.getInvokeBlock();
//		}
//
//		@Override
//		public CommandResult execute(CommandContext context)
//		{
//			lazyLoad();
//			if (real == null)
//			{
//				return null;
//			}
//			return real.execute(context);
//		}
//
//		@Override
//		public Ruby getRuntime()
//		{
//			lazyLoad();
//			if (real == null)
//			{
//				return null;
//			}
//			return real.getRuntime();
//		}
//
//		@Override
//		public CommandContext createCommandContext()
//		{
//			lazyLoad();
//			return new CommandContext(real);
//		}
//
//		private synchronized void lazyLoad()
//		{
//			if (real == null)
//			{
//				BundleElement owning = getOwningBundle();
//				if (owning == null) // we haven't even been attached yet!
//				{
//					return;
//				}
//				// remove all elements that are declared in the same file, since they'll end up getting
//				// loaded below.
//				List<AbstractElement> elements = BundleElement.getElementsByPath(getPath());
//				for (AbstractElement element : elements)
//				{
//					if (element instanceof AbstractBundleElement)
//					{
//						AbstractBundleElement abe = (AbstractBundleElement) element;
//						owning.removeChild(abe);
//					}
//				}
//
//				// Now load up the file so it really loads into the BundleManager
//				BundleManager.getInstance().loadScript(new File(getPath()));
//				// If bundle.rb itself was reloaded we need to update the bundle element we're attached to as owner!
//				if (getPath().equals(owning.getPath()))
//				{
//					owning = BundleManager.getInstance().getBundleFromPath(new File(getPath()).getParent());
//					this.setOwningBundle(owning);
//				}
//
//				// Now for whatever code is holding a reference to this, redirect method calls to the
//				// real command
//				for (ContentAssistElement ca : owning.getContentAssists())
//				{
//					if (ca.getDisplayName().equals(getDisplayName()))
//					{
//						real = ca;
//						break;
//					}
//				}
//			}
//		}
//	}
//
//	/**
//	 * Lazily loads the real environment element from disk when we try to access the invoke block, since that doesn't
//	 * get serialized.
//	 * 
//	 * @author cwilliams
//	 */
//	private class LazyEnvironmentElement extends EnvironmentElement
//	{
//		private EnvironmentElement real;
//
//		public LazyEnvironmentElement(String path)
//		{
//			super(path);
//		}
//
//		@Override
//		public RubyProc getInvokeBlock()
//		{
//			lazyLoad();
//			if (real != null)
//			{
//				return real.getInvokeBlock();
//			}
//			// TODO Log an error!
//			return null;
//		}
//
//		private synchronized void lazyLoad()
//		{
//			if (real == null)
//			{
//				BundleElement owning = getOwningBundle();
//				if (owning == null)
//				{
//					return;
//				}
//				// remove all elements that are declared in the same file, since they'll end up getting
//				// loaded below.
//				List<AbstractElement> elements = BundleElement.getElementsByPath(getPath());
//				for (AbstractElement element : elements)
//				{
//					if (element instanceof AbstractBundleElement)
//					{
//						AbstractBundleElement abe = (AbstractBundleElement) element;
//						owning.removeChild(abe);
//					}
//				}
//
//				// Now load up the file so it really loads into the BundleManager
//				BundleManager.getInstance().loadScript(new File(getPath()));
//				// If bundle.rb itself was reloaded we need to update the bundle element we're attached to as owner!
//				if (getPath().equals(owning.getPath()))
//				{
//					owning = BundleManager.getInstance().getBundleFromPath(new File(getPath()).getParent());
//					this.setOwningBundle(owning);
//				}
//
//				// Now for whatever code is holding a reference to this, redirect method calls to the
//				// real command
//				List<EnvironmentElement> envs = owning.getEnvs();
//				for (EnvironmentElement env : envs)
//				{
//					// This is pretty messed up. The display name is a random guid, so our best equals check is same
//					// file and same scope
//					if (env.getPath().equals(getPath()) && env.getScope().equals(getScope()))
//					{
//						real = env;
//						break;
//					}
//				}
//			}
//		}
//	}
//
//	private class LazyTemplateElement extends TemplateElement
//	{
//		private TemplateElement real;
//
//		public LazyTemplateElement(String path)
//		{
//			super(path);
//		}
//
//		@Override
//		public String getInvoke()
//		{
//			lazyLoad();
//			if (real == null)
//			{
//				return null;
//			}
//			return real.getInvoke();
//		}
//
//		@Override
//		public RubyProc getInvokeBlock()
//		{
//			lazyLoad();
//			if (real == null)
//			{
//				return null;
//			}
//			return real.getInvokeBlock();
//		}
//
//		@Override
//		public CommandResult execute(CommandContext context)
//		{
//			lazyLoad();
//			if (real == null)
//			{
//				return null;
//			}
//			return real.execute(context);
//		}
//
//		@Override
//		public Ruby getRuntime()
//		{
//			lazyLoad();
//			if (real == null)
//			{
//				return null;
//			}
//			return real.getRuntime();
//		}
//
//		@Override
//		public CommandContext createCommandContext()
//		{
//			lazyLoad();
//			return new CommandContext(real);
//		}
//
//		private synchronized void lazyLoad()
//		{
//			if (real == null)
//			{
//				BundleElement owning = getOwningBundle();
//				if (owning == null) // we haven't even been attached yet!
//				{
//					return;
//				}
//				// FIXME We should be updating "real" pointer for every "lazy" element, not just templates!
//				Set<LazyTemplateElement> lazyTemplates = new HashSet<BundleCacher.LazyTemplateElement>();
//				lazyTemplates.add(this);
//				// remove all elements that are declared in the same file, since they'll end up getting
//				// loaded below.
//				List<AbstractElement> elements = BundleElement.getElementsByPath(getPath());
//				for (AbstractElement element : elements)
//				{
//					if (element instanceof AbstractBundleElement)
//					{
//						AbstractBundleElement abe = (AbstractBundleElement) element;
//						owning.removeChild(abe);
//
//						if (element instanceof LazyTemplateElement)
//						{
//							lazyTemplates.add((LazyTemplateElement) abe);
//						}
//					}
//				}
//
//				// Now load up the file so it really loads into the BundleManager
//				BundleManager.getInstance().loadScript(new File(getPath()));
//				// If bundle.rb itself was reloaded we need to update the bundle element we're attached to as owner!
//				if (getPath().equals(owning.getPath()))
//				{
//					owning = BundleManager.getInstance().getBundleFromPath(new File(getPath()).getParent());
//					this.setOwningBundle(owning);
//				}
//
//				// Now for whatever code is holding a reference to the lazy templates, redirect method calls to the
//				// real template
//				for (TemplateElement template : owning.getFileTemplates())
//				{
//					if (template instanceof LazyTemplateElement)
//					{
//						continue;
//					}
//					for (LazyTemplateElement lazy : lazyTemplates)
//					{
//						if (template.getDisplayName().equals(lazy.getDisplayName()))
//						{
//							lazy.real = template;
//							break;
//						}
//					}
//				}
//			}
//		}
//	}
}
