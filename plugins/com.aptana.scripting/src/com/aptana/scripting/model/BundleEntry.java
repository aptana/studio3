package com.aptana.scripting.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BundleEntry
{
	private String _name;
	private List<BundleElement> _bundles;

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
	 * add
	 * 
	 * @param bundle
	 */
	public void addBundle(BundleElement bundle)
	{
		if (bundle != null)
		{
			synchronized (this._bundles)
			{
				this._bundles.add(bundle);

				// keep bundles in canonical order
				Collections.sort(this._bundles, new Comparator<BundleElement>()
				{
					public int compare(BundleElement o1, BundleElement o2)
					{
						int result = o1.getBundleScope().compareTo(o2.getBundleScope());

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
				});
			}
		}
	}

	/**
	 * getActiveScope
	 * 
	 * @return
	 */
	public BundleScope getActiveScope()
	{
		BundleScope result = BundleScope.UNKNOWN;

		synchronized (this._bundles)
		{
			int size = this._bundles.size();

			if (size > 0)
			{
				result = this._bundles.get(size - 1).getBundleScope();
			}
		}

		return result;
	}

	/**
	 * getBundles
	 * 
	 * @return
	 */
	public BundleElement[] getBundles()
	{
		BundleElement[] result;

		synchronized (this._bundles)
		{
			result = this._bundles.toArray(new BundleElement[this._bundles.size()]);
		}

		return result;
	}

	/**
	 * getCommands
	 * 
	 * @return
	 */
	public CommandElement[] getCommands()
	{
		final Set<String> names = new HashSet<String>();
		final List<CommandElement> result = new ArrayList<CommandElement>();

		this.processBundles(new BundleProcessor()
		{
			public boolean processBundle(BundleEntry entry, BundleElement bundle)
			{
				for (CommandElement command : bundle.getCommands())
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
		});

		return result.toArray(new CommandElement[result.size()]);
	}

	/**
	 * getCommands
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
	 * geMenus
	 * 
	 * @return
	 */
	public MenuElement[] getMenus()
	{
		final Set<String> names = new HashSet<String>();
		final List<MenuElement> result = new ArrayList<MenuElement>();

		this.processBundles(new BundleProcessor()
		{
			public boolean processBundle(BundleEntry entry, BundleElement bundle)
			{
				for (MenuElement menu : bundle.getMenus())
				{
					String name = menu.getDisplayName();

					if (names.contains(name) == false)
					{
						names.add(name);
						result.add(menu);
					}
				}

				return true;
			}
		});

		return result.toArray(new MenuElement[result.size()]);
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
	 * @param bundle
	 * @return
	 */
	public boolean removeBundle(BundleElement bundle)
	{
		boolean result;

		synchronized (this._bundles)
		{
			result = this._bundles.remove(bundle);
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
