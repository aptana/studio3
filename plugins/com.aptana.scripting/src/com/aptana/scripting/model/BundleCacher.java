package com.aptana.scripting.model;

import java.beans.IntrospectionException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.jruby.Ruby;
import org.jruby.RubyProc;
import org.jruby.RubyRegexp;
import org.jruby.util.KCode;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.representer.Representer;

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

	public void cache(File bundleDirectory, IProgressMonitor monitor)
	{
		// Force bundle manager to load the bundle...
		if (!Platform.isRunning())
		{
			BundleManager.getInstance().loadBundle(bundleDirectory);
		}
		// grab the bundle model
		BundleElement be = BundleManager.getInstance().getBundleFromPath(bundleDirectory);
		if (be == null)
		{
			return;
		}

		// Now write the config file out...
		FileWriter writer = null;
		try
		{
			File configFile = new File(bundleDirectory, CACHE_FILE);
			writer = new FileWriter(configFile);

			Yaml yaml = createYAML();
			yaml.dump(be, writer);
		}
		catch (IOException e)
		{
			ScriptingActivator.logError(e.getMessage(), e);
		}
		finally
		{
			if (writer != null)
			{
				try
				{
					writer.close();
				}
				catch (IOException e)
				{
					// ignore
				}
			}
		}
	}

	public BundleElement load(File bundleDirectory, List<File> bundleFiles)
	{
		File cacheFile = new File(bundleDirectory, CACHE_FILE);
		if (!cacheFile.exists())
		{
			return null;
		}
		// Compare lastMod versus all the bundleFiles.
		long lastMod = cacheFile.lastModified();
		for (File file : bundleFiles)
		{
			// TODO Just update the cache with the updated files/diff!
			if (file.lastModified() > lastMod)
			{
				// One of the files is newer, don't load cache!
				return null;
			}
		}

		// Load up the bundle contents from the cache
		BundleElement be = null;
		FileReader reader = null;
		try
		{
			Yaml yaml = createYAML();
			reader = new FileReader(cacheFile);
			be = (BundleElement) yaml.load(reader);
			// FIXME Handle if a file referenced by one of the model elements has been deleted! Remove from the model
			// and rewrite the cache?
		}
		catch (Exception e)
		{
			ScriptingActivator.logError(e.getMessage(), e);
		}
		finally
		{
			if (reader != null)
			{
				try
				{
					reader.close();
				}
				catch (IOException e)
				{
					// ignore
				}
			}
		}
		return be;
	}

	private Yaml createYAML()
	{
		return new Yaml(new RubyRegexpConstructor(), new MyRepresenter());
	}

	private class MyRepresenter extends Representer
	{
		public MyRepresenter()
		{
			this.representers.put(RubyRegexp.class, new RepresentRubyRegexp());
			this.representers.put(ScopeSelector.class, new RepresentScopeSelector());
		}

		@Override
		protected Set<Property> getProperties(Class<? extends Object> type) throws IntrospectionException
		{
			if (type.equals(Ruby.class) || type.equals(KCode.class) || type.equals(RubyProc.class))
			{
				return Collections.emptySet();
			}
			Set<Property> set = super.getProperties(type);
			if (CommandElement.class.isAssignableFrom(type) || type.equals(EnvironmentElement.class))
			{
				// drop runtime, invoke, and invoke block properties
				Set<Property> toRemove = new HashSet<Property>();
				for (Property prop : set)
				{
					if (prop.getName().equals("invokeBlock") || prop.getName().equals("runtime") //$NON-NLS-1$ //$NON-NLS-2$
							|| prop.getName().equals("invoke")) //$NON-NLS-1$
					{
						toRemove.add(prop);
					}
				}
				for (Property prop : toRemove)
				{
					set.remove(prop);
				}
			}
			return set;
		}

		/**
		 * Store ruby regexps as strings.
		 * 
		 * @author cwilliams
		 */
		private class RepresentRubyRegexp implements Represent
		{
			public Node representData(Object data)
			{
				RubyRegexp dice = (RubyRegexp) data;
				String value = dice.toString();
				return representScalar(new Tag(REGEXP_TAG), value);
			}
		}

		/**
		 * Store scope selectors as strings.
		 * 
		 * @author cwilliams
		 */
		private class RepresentScopeSelector implements Represent
		{
			public Node representData(Object data)
			{
				ScopeSelector dice = (ScopeSelector) data;
				String value = dice.toString();
				return representScalar(new Tag("!scope"), value);
			}
		}
	}

	class RubyRegexpConstructor extends Constructor
	{
		public RubyRegexpConstructor()
		{
			this.yamlConstructors.put(new Tag("!scope"), new ConstructScopeSelector());
			this.yamlConstructors.put(new Tag(REGEXP_TAG), new ConstructRubyRegexp());
			this.yamlConstructors.put(new Tag(BundleElement.class), new ConstructBundleElement());
			this.yamlConstructors.put(new Tag(MenuElement.class), new ConstructMenuElement());
			this.yamlConstructors.put(new Tag(SnippetElement.class), new ConstructSnippetElement());
			this.yamlConstructors.put(new Tag(ContentAssistElement.class), new ConstructContentAssistElement());
			this.yamlConstructors.put(new Tag(CommandElement.class), new ConstructCommandElement());
			this.yamlConstructors.put(new Tag(TemplateElement.class), new ConstructTemplateElement());
			this.yamlConstructors.put(new Tag(SmartTypingPairsElement.class), new ConstructSmartTypingPairsElement());
			this.yamlConstructors.put(new Tag(ProjectTemplateElement.class), new ConstructProjectTemplateElement());
			this.yamlConstructors.put(new Tag(EnvironmentElement.class), new ConstructEnvironmentElement());

			// Tell it that "children" field for MenuElement is a list of MenuElements
			TypeDescription menuDescription = new TypeDescription(MenuElement.class);
			menuDescription.putListPropertyType("children", MenuElement.class); //$NON-NLS-1$
			addTypeDescription(menuDescription);
		}

		private class ConstructScopeSelector extends AbstractConstruct
		{
			public Object construct(Node node)
			{
				String val = (String) constructScalar((ScalarNode) node);
				return new ScopeSelector(val);
			}
		}

		private class ConstructRubyRegexp extends AbstractConstruct
		{
			public Object construct(Node node)
			{
				String val = (String) constructScalar((ScalarNode) node);
				return RubyRegexp.newRegexp(ScriptingEngine.getInstance().getScriptingContainer().getProvider()
						.getRuntime(), val, 0);
			}
		}

		private abstract class AbstractBundleElementConstruct extends ConstructMapping
		{
			/**
			 * Grab the path from the mapping node and grab it's value!
			 * 
			 * @param node
			 * @return
			 */
			protected String getPath(Node node)
			{
				String path = null;
				MappingNode map = (MappingNode) node;
				List<NodeTuple> nodes = map.getValue();
				for (NodeTuple tuple : nodes)
				{
					Node keyNode = tuple.getKeyNode();
					if (keyNode instanceof ScalarNode)
					{
						ScalarNode scalar = (ScalarNode) keyNode;
						String valueOfKey = scalar.getValue();
						if ("path".equals(valueOfKey)) //$NON-NLS-1$
						{
							Node valueNode = tuple.getValueNode();
							if (valueNode instanceof ScalarNode)
							{
								ScalarNode scalarValue = (ScalarNode) valueNode;
								path = scalarValue.getValue();
								break;
							}
						}
					}
				}
				return path;
			}
		}

		private class ConstructBundleElement extends AbstractBundleElementConstruct
		{
			public Object construct(Node node)
			{
				node.setType(BundleElement.class);
				BundleElement be = new BundleElement(getPath(node));
				construct2ndStep(node, be);
				return be;
			}
		}

		private class ConstructCommandElement extends AbstractBundleElementConstruct
		{
			public Object construct(Node node)
			{
				node.setType(CommandElement.class);
				CommandElement be = new CommandElement(getPath(node))
				{
					private CommandElement real;

					@Override
					public boolean isExecutable()
					{
						// HACK
						return true;
					}

					@Override
					public String getInvoke()
					{
						lazyLoad();
						return real.getInvoke();
					}

					@Override
					public RubyProc getInvokeBlock()
					{
						lazyLoad();
						return real.getInvokeBlock();
					}

					@Override
					public CommandResult execute(CommandContext context)
					{
						lazyLoad();
						return real.execute(context);
					}

					@Override
					public Ruby getRuntime()
					{
						lazyLoad();
						return real.getRuntime();
					}

					@Override
					public CommandContext createCommandContext()
					{
						lazyLoad();
						return new CommandContext(real);
					}

					private synchronized void lazyLoad()
					{
						if (real == null)
						{
							BundleElement owning = getOwningBundle();
							// remove all elements that are declared in the same file, since they'll end up getting
							// loaded below.
							List<AbstractElement> elements = BundleElement.getElementsByPath(getPath());
							for (AbstractElement element : elements)
							{
								if (element instanceof AbstractBundleElement)
								{
									AbstractBundleElement abe = (AbstractBundleElement) element;
									owning.removeChild(abe);
								}
							}

							// Now load up the file so it really loads into the BundleManager
							BundleManager.getInstance().loadScript(new File(getPath()));

							// Now for whatever code is holding a reference to this, redirect method calls to the
							// real command
							real = owning.getCommandByName(getDisplayName());
						}
					}
				};
				construct2ndStep(node, be);
				return be;
			}
		}

		private class ConstructSnippetElement extends AbstractBundleElementConstruct
		{
			public Object construct(Node node)
			{
				node.setType(SnippetElement.class);
				SnippetElement be = new SnippetElement(getPath(node));
				construct2ndStep(node, be);
				return be;
			}
		}

		private class ConstructMenuElement extends AbstractBundleElementConstruct
		{
			public Object construct(Node node)
			{
				node.setType(MenuElement.class);
				MenuElement be = new MenuElement(getPath(node));
				construct2ndStep(node, be);
				return be;
			}
		}

		private class ConstructProjectTemplateElement extends AbstractBundleElementConstruct
		{
			public Object construct(Node node)
			{
				node.setType(ProjectTemplateElement.class);
				ProjectTemplateElement be = new ProjectTemplateElement(getPath(node));
				construct2ndStep(node, be);
				return be;
			}
		}

		private class ConstructEnvironmentElement extends AbstractBundleElementConstruct
		{
			public Object construct(Node node)
			{
				node.setType(EnvironmentElement.class);
				EnvironmentElement be = new EnvironmentElement(getPath(node))
				{
					private EnvironmentElement real;

					@Override
					public RubyProc getInvokeBlock()
					{
						lazyLoad();
						return real.getInvokeBlock();
					}

					private synchronized void lazyLoad()
					{
						if (real == null)
						{
							BundleElement owning = getOwningBundle();
							// remove all elements that are declared in the same file, since they'll end up getting
							// loaded below.
							List<AbstractElement> elements = BundleElement.getElementsByPath(getPath());
							for (AbstractElement element : elements)
							{
								if (element instanceof AbstractBundleElement)
								{
									AbstractBundleElement abe = (AbstractBundleElement) element;
									owning.removeChild(abe);
								}
							}

							// Now load up the file so it really loads into the BundleManager
							BundleManager.getInstance().loadScript(new File(getPath()));

							// Now for whatever code is holding a reference to this, redirect method calls to the
							// real command
							List<EnvironmentElement> envs = owning.getEnvs();
							for (EnvironmentElement env : envs)
							{
								if (env.getPath().equals(getPath()))
								{
									real = env;
									break;
								}
							}
						}
					}
				};
				construct2ndStep(node, be);
				return be;
			}
		}

		private class ConstructTemplateElement extends AbstractBundleElementConstruct
		{
			public Object construct(Node node)
			{
				node.setType(TemplateElement.class);
				TemplateElement be = new TemplateElement(getPath(node));
				construct2ndStep(node, be);
				return be;
			}
		}

		private class ConstructContentAssistElement extends AbstractBundleElementConstruct
		{
			public Object construct(Node node)
			{
				node.setType(ContentAssistElement.class);
				ContentAssistElement be = new ContentAssistElement(getPath(node));
				construct2ndStep(node, be);
				return be;
			}
		}

		private class ConstructSmartTypingPairsElement extends AbstractBundleElementConstruct
		{
			public Object construct(Node node)
			{
				node.setType(SmartTypingPairsElement.class);
				SmartTypingPairsElement be = new SmartTypingPairsElement(getPath(node));
				construct2ndStep(node, be);
				return be;
			}
		}
	}
}
