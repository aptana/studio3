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

		/**
		 * VisibilityContext
		 */
		public ChildVisibilityContext()
		{
			this.preVisibleItems = this.getElements();
		}

		/**
		 * fireVisibilityEvents
		 */
		public void fireVisibilityEvents()
		{
			BundleManager manager = BundleManager.getInstance();

			Set<T> becameVisible = new HashSet<T>(this.getElements());
			Set<T> becameHidden = new HashSet<T>(preVisibleItems);

			becameHidden.removeAll(becameVisible);
			becameVisible.removeAll(preVisibleItems);

			// fire hidden events
			if (becameHidden.size() > 0)
			{
				// set visibility flag
				for (T element : becameHidden)
				{
					// fire hidden event
					manager.fireElementBecameHiddenEvent(element);
				}
			}

			// fire visible events
			if (becameVisible.size() > 0)
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

				if (names.contains(name) == false)
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
		private ChildVisibilityContext<CommandElement> commands;
		private ChildVisibilityContext<EnvironmentElement> envs;
		private ChildVisibilityContext<MenuElement> menus;
		private ChildVisibilityContext<SmartTypingPairsElement> pairs;
		private ChildVisibilityContext<ProjectTemplateElement> projectTemplates;

		/**
		 * VisibilityContext
		 */
		public VisibilityContext(Class<?> elementClass)
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
		}

		/**
		 * fireBundleVisibilityEvents
		 */
		private void fireBundleVisibilityEvents()
		{
			BundleManager manager = BundleManager.getInstance();

			Set<BundleElement> becameVisible = new HashSet<BundleElement>(getContributingBundles());
			Set<BundleElement> becameHidden = new HashSet<BundleElement>(preVisibleBundles);

			becameHidden.removeAll(becameVisible);
			becameVisible.removeAll(preVisibleBundles);

			// fire hidden events
			if (becameHidden.size() > 0)
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
			if (becameVisible.size() > 0)
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
		 * fireVisibilityEvents
		 */
		public void fireVisibilityEvents()
		{
			if (preVisibleBundles != null)
			{
				fireBundleVisibilityEvents();
			}

			this.fireVisibilityEvents(commands);
			this.fireVisibilityEvents(envs);
			this.fireVisibilityEvents(menus);
			this.fireVisibilityEvents(pairs);
			this.fireVisibilityEvents(projectTemplates);
		}

		/**
		 * fireVisibilityEvents
		 * 
		 * @param context
		 */
		private void fireVisibilityEvents(ChildVisibilityContext<? extends AbstractBundleElement> context)
		{
			if (context != null)
			{
				context.fireVisibilityEvents();
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
		this._bundles = new ArrayList<BundleElement>();
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
		this._bundles = bundles;

		Collections.sort(this._bundles, this._comparator);
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
			synchronized (this._bundles)
			{
				// only go through the add process and its side-effects if we don't have this bundle already
				if (this.hasBundle(bundle) == false)
				{
					VisibilityContext context = this.getVisibilityContext(null);

					// add the bundle
					this._bundles.add(bundle);

					// keep bundles in canonical order
					Collections.sort(this._bundles, this._comparator);

					// fire visibility change events
					context.fireVisibilityEvents();
				}
			}
		}
	}

	/**
	 * getBundles
	 * 
	 * @return
	 */
	public List<BundleElement> getBundles()
	{
		return Collections.unmodifiableList(this._bundles);
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
	 * getVisibilityContext
	 * 
	 * @return
	 */
	public VisibilityContext getVisibilityContext(Class<?> elementClass)
	{
		return new VisibilityContext(elementClass);
	}

	/**
	 * hasBundle
	 * 
	 * @param bundle
	 * @return
	 */
	public boolean hasBundle(BundleElement bundle)
	{
		return this._bundles.contains(bundle);
	}

	/**
	 * processBundles
	 * 
	 * @param processor
	 */
	protected void processBundles(BundleProcessor processor)
	{
		// NOTE: seems like a potentially long lock since we're running the processor
		// on each bundle instance
		synchronized (this._bundles)
		{
			// walk list of bundles in decreasing bundle scope precedence, processing
			// references before declarations
			for (int i = this._bundles.size() - 1; i >= 0; i--)
			{
				BundleElement bundle = this._bundles.get(i);

				// we're done processing if we've processed all bundle references and
				// one bundle declaration OR if our BundleProcessor tells us to stop

				// NOTE: the order of this conditional is important. We need to run
				// the processor on the current bundle before we decide to exit when
				// we hit a non-reference bundle
				if (processor.processBundle(this, bundle) == false || bundle.isReference() == false)
				{
					break;
				}
			}
		}
	}

	/**
	 * reload
	 */
	public void reload()
	{
		BundleManager manager = BundleManager.getInstance();

		synchronized (this._bundles)
		{
			for (BundleElement bundle : this._bundles)
			{
				manager.reloadBundle(bundle);
			}
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
		boolean result;

		synchronized (this._bundles)
		{
			VisibilityContext context = this.getVisibilityContext(null);

			result = this._bundles.remove(bundle);

			if (result)
			{
				// fire visibility change events
				context.fireVisibilityEvents();
			}
		}

		return result;
	}

	/**
	 * size
	 * 
	 * @return
	 */
	public int size()
	{
		int size = 0;

		if (this._bundles != null)
		{
			size = this._bundles.size();
		}

		return size;
	}
}
