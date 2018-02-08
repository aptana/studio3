/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jruby.RubyRegexp;

import com.aptana.scope.ScopeSelector;

public class BundleEntry
{
	private abstract class ChildVisibilityContext<T extends AbstractBundleElement>
	{
		private List<T> preVisibleItems;
		private Set<T> becameHidden;
		private Set<T> becameVisible;

		/**
		 * Note that the context update and event firing are divided into separate steps synchronization purposes. It is
		 * expected that a lock will be made prior to creating this context, children will be added/removed, the context
		 * will be updated to record the visibility changes (via updateContext), the lock will be released, and then the
		 * visibility events will fire. Having the events fire outside of the lock prevents potential deadlocks that the
		 * listeners could cause
		 */
		public ChildVisibilityContext()
		{
			this.preVisibleItems = this.getElements();
		}

		/**
		 * Fire events for each element that has become visible or has become hidden. It is assumed that updateContext
		 * has been called prior to this method
		 */
		public void fireVisibilityEvents()
		{
			BundleManager manager = BundleManager.getInstance();

			// fire hidden events
			if (becameHidden != null && becameHidden.size() > 0)
			{
				// set visibility flag
				for (T element : becameHidden)
				{
					// fire hidden event
					manager.fireElementBecameHiddenEvent(element);
				}
			}

			// fire visible events
			if (becameVisible != null && becameVisible.size() > 0)
			{
				// set visibility flag
				for (T element : becameVisible)
				{
					// fire visible event
					manager.fireElementBecameVisibleEvent(element);
				}
			}
		}

		/**
		 * Return a list of abstract element items
		 * 
		 * @return
		 */
		public abstract List<T> getElements();

		/**
		 * Update the context to determine which elements have become visible and which elements have become hidden.
		 * This should be called before fireVisibilityEvents.
		 */
		public void updateContext()
		{
			becameVisible = new HashSet<T>(this.getElements());
			becameHidden = new HashSet<T>(preVisibleItems);

			becameHidden.removeAll(becameVisible);
			becameVisible.removeAll(preVisibleItems);
		}
	}

	private abstract class NameBasedProcessor<T extends AbstractElement> implements BundleProcessor
	{
		private Set<String> names = new HashSet<String>();
		private List<T> result = new ArrayList<T>();

		/**
		 * Return a list of abstract element items from the specified bundle
		 * 
		 * @param bundle
		 * @return
		 */
		protected abstract List<T> getElements(BundleElement bundle);

		/**
		 * Get the list of items that are visible
		 * 
		 * @return
		 */
		public List<T> getResult()
		{
			return result;
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.scripting.model.BundleProcessor#processBundle(com.aptana.scripting.model.BundleEntry,
		 * com.aptana.scripting.model.BundleElement)
		 */
		public boolean processBundle(BundleEntry entry, BundleElement bundle)
		{
			for (T command : getElements(bundle))
			{
				String name = command.getDisplayName();

				if (!names.contains(name))
				{
					names.add(name);
					result.add(command);
				}
			}

			return true;
		}
	}

	public class VisibilityContext
	{
		private List<BundleElement> preVisibleBundles;
		private Set<BundleElement> becameVisible;
		private Set<BundleElement> becameHidden;

		private ChildVisibilityContext<CommandElement> commands;
		private ChildVisibilityContext<EnvironmentElement> envs;
		private ChildVisibilityContext<MenuElement> menus;
		private ChildVisibilityContext<SmartTypingPairsElement> pairs;
		private ChildVisibilityContext<ProjectTemplateElement> projectTemplates;
		private ChildVisibilityContext<ProjectSampleElement> projectSamples;
		private ChildVisibilityContext<SnippetElement> snippets;
		private ChildVisibilityContext<SnippetCategoryElement> snippetCategories;
		private ChildVisibilityContext<BuildPathElement> buildPaths;

		/**
		 * VisibilityContext
		 */
		public VisibilityContext()
		{
			this(null);
		}

		/**
		 * Note that the context update and event firing are divided into separate steps synchronization purposes. It is
		 * expected that a lock will be made prior to creating this context, children will be added/removed, the context
		 * will be updated to record the visibility changes (via updateContext), the lock will be released, and then the
		 * visibility events will fire. Having the events fire outside of the lock prevents potential deadlocks that the
		 * listeners could cause
		 * 
		 * @param elementClass
		 *            The abstract bundle element type to track for visibility changes. If this is null, then all
		 *            element types will be tracked along with bundle elements (which are not descendants of
		 *            AbstractBundleElement)
		 */
		public VisibilityContext(Class<? extends AbstractBundleElement> elementClass)
		{
			if (elementClass == null)
			{
				preVisibleBundles = getContributingBundles();
			}

			if (elementClass == null || elementClass == CommandElement.class)
			{
				commands = new ChildVisibilityContext<CommandElement>()
				{
					public List<CommandElement> getElements()
					{
						return getCommands();
					}
				};
			}

			if (elementClass == null || elementClass == EnvironmentElement.class)
			{
				envs = new ChildVisibilityContext<EnvironmentElement>()
				{
					public List<EnvironmentElement> getElements()
					{
						return getEnvs();
					}
				};
			}

			if (elementClass == null || elementClass == MenuElement.class)
			{
				menus = new ChildVisibilityContext<MenuElement>()
				{
					public List<MenuElement> getElements()
					{
						return getMenus();
					}
				};
			}

			if (elementClass == null || elementClass == SmartTypingPairsElement.class)
			{
				pairs = new ChildVisibilityContext<SmartTypingPairsElement>()
				{
					public List<SmartTypingPairsElement> getElements()
					{
						return getPairs();
					}
				};
			}

			if (elementClass == null || elementClass == ProjectTemplateElement.class)
			{
				projectTemplates = new ChildVisibilityContext<ProjectTemplateElement>()
				{
					public List<ProjectTemplateElement> getElements()
					{
						return getProjectTemplates();
					}
				};
			}

			if (elementClass == null || elementClass == ProjectSampleElement.class)
			{
				projectSamples = new ChildVisibilityContext<ProjectSampleElement>()
				{
					public List<ProjectSampleElement> getElements()
					{
						return getProjectSamples();
					}
				};
			}

			if (elementClass == null || elementClass == SnippetElement.class)
			{
				snippets = new ChildVisibilityContext<SnippetElement>()
				{
					public List<SnippetElement> getElements()
					{
						return getSnippets();
					}
				};
			}

			if (elementClass == null || elementClass == SnippetCategoryElement.class)
			{
				snippetCategories = new ChildVisibilityContext<SnippetCategoryElement>()
				{
					public List<SnippetCategoryElement> getElements()
					{
						return getSnippetCategories();
					}
				};
			}

			if (elementClass == null || elementClass == BuildPathElement.class)
			{
				buildPaths = new ChildVisibilityContext<BuildPathElement>()
				{
					public List<BuildPathElement> getElements()
					{
						return getBuildPaths();
					}
				};
			}
		}

		/**
		 * fireBundleVisibilityEvents
		 */
		private void fireBundleVisibilityEvents()
		{
			BundleManager manager = BundleManager.getInstance();

			// fire hidden events
			if (becameHidden != null && becameHidden.size() > 0)
			{
				List<BundleElement> hiddenList = new ArrayList<BundleElement>(becameHidden);

				// set visibility flag
				for (BundleElement bundle : hiddenList)
				{
					bundle.setVisible(false);
				}

				// create new entry with these bundle elements. This is needed so the precedence
				// rules can be applied to this collection
				BundleEntry hiddenEntry = new BundleEntry(getName(), hiddenList);

				// fire hidden event
				manager.fireBundleBecameHiddenEvent(hiddenEntry);
			}

			// fire visible events
			if (becameVisible != null && becameVisible.size() > 0)
			{
				List<BundleElement> visibleList = new ArrayList<BundleElement>(becameVisible);

				// set visibility flag
				for (BundleElement bundle : visibleList)
				{
					bundle.setVisible(true);
				}

				// create new entry with these bundle elements. This is needed so the precedence
				// rules can be applied to this collection
				BundleEntry visibleEntry = new BundleEntry(getName(), visibleList);

				// fire visible event
				manager.fireBundleBecameVisibleEvent(visibleEntry);
			}
		}

		/**
		 * fireElementVisibilityEvents
		 */
		public void fireElementVisibilityEvents()
		{
			if (preVisibleBundles != null)
			{
				fireBundleVisibilityEvents();
			}

			this.fireElementVisibilityEvents(commands);
			this.fireElementVisibilityEvents(envs);
			this.fireElementVisibilityEvents(menus);
			this.fireElementVisibilityEvents(pairs);
			this.fireElementVisibilityEvents(projectTemplates);
			this.fireElementVisibilityEvents(projectSamples);
			this.fireElementVisibilityEvents(snippets);
			this.fireElementVisibilityEvents(snippetCategories);
			this.fireElementVisibilityEvents(buildPaths);
		}

		/**
		 * fireElementVisibilityEvents
		 * 
		 * @param context
		 */
		private void fireElementVisibilityEvents(ChildVisibilityContext<? extends AbstractBundleElement> context)
		{
			if (context != null)
			{
				context.fireVisibilityEvents();
			}
		}

		/**
		 * updateBundleContext
		 */
		private void updateBundleContext()
		{
			becameVisible = new HashSet<BundleElement>(getContributingBundles());
			becameHidden = new HashSet<BundleElement>(preVisibleBundles);

			becameHidden.removeAll(becameVisible);
			becameVisible.removeAll(preVisibleBundles);
		}

		/**
		 * updateElementContext
		 */
		public void updateElementContext()
		{
			if (preVisibleBundles != null)
			{
				updateBundleContext();
			}

			this.updateElementContext(commands);
			this.updateElementContext(envs);
			this.updateElementContext(menus);
			this.updateElementContext(pairs);
			this.updateElementContext(projectTemplates);
			this.updateElementContext(projectSamples);
			this.updateElementContext(snippets);
			this.updateElementContext(snippetCategories);
			this.updateElementContext(buildPaths);
		}

		/**
		 * updateElementContext
		 * 
		 * @param context
		 */
		private void updateElementContext(ChildVisibilityContext<? extends AbstractBundleElement> context)
		{
			if (context != null)
			{
				context.updateContext();
			}
		}
	}

	private String _name;
	private List<BundleElement> _bundles;
	private Comparator<BundleElement> _comparator = new Comparator<BundleElement>()
	{
		public int compare(BundleElement o1, BundleElement o2)
		{
			int result = o1.getBundlePrecedence().compareTo(o2.getBundlePrecedence());

			if (result == 0)
			{
				if (o1.isReference() == o2.isReference())
				{
					result = o1.getPath().compareTo(o2.getPath());
				}
				else
				{
					result = (o1.isReference()) ? 1 : -1;
				}
			}

			return result;
		}
	};

	/**
	 * BundleEntry
	 * 
	 * @param name
	 */
	public BundleEntry(String name)
	{
		if (name == null || name.length() == 0)
		{
			throw new IllegalArgumentException(Messages.BundleEntry_Name_Not_Defined);
		}

		this._name = name;
		this._bundles = Collections.synchronizedList(new ArrayList<BundleElement>());
	}

	/**
	 * BundleEntry
	 * 
	 * @param name
	 * @param bundles
	 */
	private BundleEntry(String name, List<BundleElement> bundles)
	{
		this._name = name;
		this._bundles = Collections.synchronizedList(bundles);
		synchronized (this._bundles)
		{
			Collections.sort(this._bundles, this._comparator);
		}
	}

	/**
	 * add
	 * 
	 * @param bundle
	 */
	public void addBundle(BundleElement bundle)
	{
		// make sure we have a bundle
		if (bundle != null)
		{
			// only go through the add process and its side-effects if we don't have this bundle already
			if (!_bundles.contains(bundle))
			{
				VisibilityContext context = getVisibilityContext();

				// add the bundle
				_bundles.add(bundle);

				// keep bundles in canonical order
				synchronized (_bundles)
				{
					Collections.sort(_bundles, this._comparator);
				}

				context.updateElementContext();
				// fire visibility change events

				context.fireElementVisibilityEvents();
			}
		}
	}

	/**
	 * getBuildPaths
	 * 
	 * @return
	 */
	public List<BuildPathElement> getBuildPaths()
	{
		NameBasedProcessor<BuildPathElement> processor = new NameBasedProcessor<BuildPathElement>()
		{
			protected List<BuildPathElement> getElements(BundleElement bundle)
			{
				return bundle.getBuildPaths();
			}
		};

		this.processBundles(processor);

		return processor.getResult();
	}

	/**
	 * Returns an unmodifiable copy of the bundle list. Callers shouldn't need to worry about synchronizing access to
	 * the list since it is a copy.
	 * 
	 * @return
	 */
	public List<BundleElement> getBundles()
	{
		synchronized (_bundles)
		{
			return Collections.unmodifiableList(new ArrayList<BundleElement>(_bundles));
		}
	}

	/**
	 * getCommands
	 * 
	 * @return
	 */
	public List<CommandElement> getCommands()
	{
		NameBasedProcessor<CommandElement> processor = new NameBasedProcessor<CommandElement>()
		{
			protected List<CommandElement> getElements(BundleElement bundle)
			{
				return bundle.getCommands();
			}
		};

		this.processBundles(processor);

		return processor.getResult();
	}

	/**
	 * getContentAssists
	 * 
	 * @return
	 */
	public List<ContentAssistElement> getContentAssists()
	{
		NameBasedProcessor<ContentAssistElement> processor = new NameBasedProcessor<ContentAssistElement>()
		{
			protected List<ContentAssistElement> getElements(BundleElement bundle)
			{
				return bundle.getContentAssists();
			}
		};

		this.processBundles(processor);

		return processor.getResult();
	}

	/**
	 * getContributingBundles
	 * 
	 * @return
	 */
	public List<BundleElement> getContributingBundles()
	{
		final List<BundleElement> result = new ArrayList<BundleElement>();

		this.processBundles(new BundleProcessor()
		{
			public boolean processBundle(BundleEntry entry, BundleElement bundle)
			{
				result.add(bundle);
				return true;
			}
		});

		return result;
	}

	/**
	 * getDecreaseIndentMarkers
	 * 
	 * @return
	 */
	public Map<ScopeSelector, RubyRegexp> getDecreaseIndentMarkers()
	{
		final Map<ScopeSelector, RubyRegexp> result = new HashMap<ScopeSelector, RubyRegexp>();

		this.processBundles(new BundleProcessor()
		{
			public boolean processBundle(BundleEntry entry, BundleElement bundle)
			{
				Map<ScopeSelector, RubyRegexp> registry = bundle.getDecreaseIndentMarkers();

				if (registry != null)
				{
					result.putAll(registry);
				}

				return true;
			}
		});

		return result;
	}

	/**
	 * getEnvs
	 * 
	 * @return
	 */
	public List<EnvironmentElement> getEnvs()
	{
		NameBasedProcessor<EnvironmentElement> processor = new NameBasedProcessor<EnvironmentElement>()
		{
			protected List<EnvironmentElement> getElements(BundleElement bundle)
			{
				return bundle.getEnvs();
			}
		};

		this.processBundles(processor);

		return processor.getResult();
	}

	/**
	 * getFileTypeRegistry
	 * 
	 * @return
	 */
	public Map<String, String> getFileTypeRegistry()
	{
		final Map<String, String> result = new HashMap<String, String>();

		this.processBundles(new BundleProcessor()
		{
			public boolean processBundle(BundleEntry entry, BundleElement bundle)
			{
				Map<String, String> registry = bundle.getFileTypeRegistry();

				if (registry != null)
				{
					result.putAll(registry);
					return false;
				}

				return true;
			}
		});

		return result;
	}

	/**
	 * getFileTypes
	 * 
	 * @return
	 */
	public List<String> getFileTypes()
	{
		final List<String> result = new ArrayList<String>();

		this.processBundles(new BundleProcessor()
		{
			public boolean processBundle(BundleEntry entry, BundleElement bundle)
			{
				List<String> registry = bundle.getFileTypes();

				if (registry != null)
				{
					result.addAll(registry);
				}

				return true;
			}
		});

		return result;
	}

	/**
	 * getFoldingStartMarkers
	 * 
	 * @return
	 */
	public Map<ScopeSelector, RubyRegexp> getFoldingStartMarkers()
	{
		final Map<ScopeSelector, RubyRegexp> result = new HashMap<ScopeSelector, RubyRegexp>();

		this.processBundles(new BundleProcessor()
		{
			public boolean processBundle(BundleEntry entry, BundleElement bundle)
			{
				Map<ScopeSelector, RubyRegexp> registry = bundle.getFoldingStartMarkers();

				if (registry != null)
				{
					result.putAll(registry);
				}

				return true;
			}
		});

		return result;
	}

	/**
	 * getFoldingStopMarkers
	 * 
	 * @return
	 */
	public Map<ScopeSelector, RubyRegexp> getFoldingStopMarkers()
	{
		final Map<ScopeSelector, RubyRegexp> result = new HashMap<ScopeSelector, RubyRegexp>();

		this.processBundles(new BundleProcessor()
		{
			public boolean processBundle(BundleEntry entry, BundleElement bundle)
			{
				Map<ScopeSelector, RubyRegexp> registry = bundle.getFoldingStopMarkers();

				if (registry != null)
				{
					result.putAll(registry);
				}

				return true;
			}
		});

		return result;
	}

	/**
	 * getIncreaseIndentMarkers
	 * 
	 * @return
	 */
	public Map<ScopeSelector, RubyRegexp> getIncreaseIndentMarkers()
	{
		final Map<ScopeSelector, RubyRegexp> result = new HashMap<ScopeSelector, RubyRegexp>();

		this.processBundles(new BundleProcessor()
		{
			public boolean processBundle(BundleEntry entry, BundleElement bundle)
			{
				Map<ScopeSelector, RubyRegexp> registry = bundle.getIncreaseIndentMarkers();

				if (registry != null)
				{
					result.putAll(registry);
				}

				return true;
			}
		});

		return result;
	}

	/**
	 * getLoadPaths
	 * 
	 * @return
	 */
	public List<String> getLoadPaths()
	{
		final List<String> result = new LinkedList<String>();

		this.processBundles(new BundleProcessor()
		{
			public boolean processBundle(BundleEntry entry, BundleElement bundle)
			{
				result.addAll(bundle.getLoadPaths());

				return true;
			}
		});

		return result;
	}

	/**
	 * geMenus
	 * 
	 * @return
	 */
	public List<MenuElement> getMenus()
	{
		NameBasedProcessor<MenuElement> processor = new NameBasedProcessor<MenuElement>()
		{
			protected List<MenuElement> getElements(BundleElement bundle)
			{
				return bundle.getMenus();
			}
		};

		this.processBundles(processor);

		return processor.getResult();
	}

	/**
	 * getName
	 * 
	 * @return
	 */
	public String getName()
	{
		return this._name;
	}

	/**
	 * getPairs
	 * 
	 * @return
	 */
	public List<SmartTypingPairsElement> getPairs()
	{
		NameBasedProcessor<SmartTypingPairsElement> processor = new NameBasedProcessor<SmartTypingPairsElement>()
		{
			protected List<SmartTypingPairsElement> getElements(BundleElement bundle)
			{
				return bundle.getPairs();
			}
		};

		this.processBundles(processor);

		return processor.getResult();
	}

	/**
	 * getProjectTemplates
	 * 
	 * @return
	 */
	public List<ProjectTemplateElement> getProjectTemplates()
	{
		NameBasedProcessor<ProjectTemplateElement> processor = new NameBasedProcessor<ProjectTemplateElement>()
		{
			protected List<ProjectTemplateElement> getElements(BundleElement bundle)
			{
				return bundle.getProjectTemplates();
			}
		};

		this.processBundles(processor);

		return processor.getResult();
	}

	/**
	 * @return the list of project samples contributed
	 */
	public List<ProjectSampleElement> getProjectSamples()
	{
		NameBasedProcessor<ProjectSampleElement> processor = new NameBasedProcessor<ProjectSampleElement>()
		{
			protected List<ProjectSampleElement> getElements(BundleElement bundle)
			{
				return bundle.getProjectSamples();
			}
		};

		this.processBundles(processor);

		return processor.getResult();
	}

	/**
	 * @return the list of snippets contributed
	 */
	public List<SnippetElement> getSnippets()
	{
		NameBasedProcessor<SnippetElement> processor = new NameBasedProcessor<SnippetElement>()
		{
			protected List<SnippetElement> getElements(BundleElement bundle)
			{
				return bundle.getSnippets();
			}
		};

		this.processBundles(processor);

		return processor.getResult();
	}

	/**
	 * @return the list of categories contributed
	 */
	public List<SnippetCategoryElement> getSnippetCategories()
	{
		NameBasedProcessor<SnippetCategoryElement> processor = new NameBasedProcessor<SnippetCategoryElement>()
		{
			protected List<SnippetCategoryElement> getElements(BundleElement bundle)
			{
				return bundle.getSnippetCategories();
			}
		};

		this.processBundles(processor);

		return processor.getResult();
	}

	/**
	 * getVisibilityContext
	 * 
	 * @return
	 */
	public VisibilityContext getVisibilityContext()
	{
		return new VisibilityContext();
	}

	/**
	 * getVisibilityContext
	 * 
	 * @return
	 */
	public VisibilityContext getVisibilityContext(Class<? extends AbstractBundleElement> elementClass)
	{
		return new VisibilityContext(elementClass);
	}

	/**
	 * processBundles
	 * 
	 * @param processor
	 */
	protected void processBundles(BundleProcessor processor)
	{
		List<BundleElement> bundles = this.getBundles();

		// walk list of bundles in decreasing bundle scope precedence, processing
		// references before declarations
		for (int i = bundles.size() - 1; i >= 0; i--)
		{
			BundleElement bundle = bundles.get(i);

			// we're done processing if we've processed all bundle references and
			// one bundle declaration OR if our BundleProcessor tells us to stop

			// NOTE: the order of this conditional is important. We need to run
			// the processor on the current bundle before we decide to exit when
			// we hit a non-reference bundle
			if (!processor.processBundle(this, bundle) || !bundle.isReference())
			{
				break;
			}
		}
	}

	/**
	 * reload
	 */
	public void reload()
	{
		BundleManager manager = BundleManager.getInstance();

		for (BundleElement bundle : this.getBundles())
		{
			manager.reloadBundle(bundle);
		}
	}

	/**
	 * removeBundle
	 * 
	 * @param bundle
	 * @return
	 */
	public boolean removeBundle(BundleElement bundle)
	{
		VisibilityContext context = getVisibilityContext();
		boolean removed = _bundles.remove(bundle);
		if (removed)
		{
			context.updateElementContext();
			// fire visibility change events
			context.fireElementVisibilityEvents();
		}

		return removed;
	}

	/**
	 * size
	 * 
	 * @return
	 */
	public int size()
	{
		return _bundles.size();
	}
}
