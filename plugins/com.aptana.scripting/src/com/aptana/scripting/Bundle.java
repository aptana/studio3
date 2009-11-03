package com.aptana.scripting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bundle
{
	private static List<Bundle> BUNDLES;
	private static Map<String,Bundle> BUNDLES_BY_NAME;
	
	private String _name;
	private List<Snippet> _snippets;
	private List<Command> _commands;
	
	/**
	 * Bundle
	 * 
	 * @param name
	 */
	public Bundle(String name)
	{
		this._name = name;
	}
	
	/**
	 * addBundle
	 * 
	 * @param bundle
	 */
	public static void addBundle(Bundle bundle)
	{
		if (bundle != null)
		{
			if (BUNDLES == null)
			{
				BUNDLES = new ArrayList<Bundle>();
			}
			
			BUNDLES.add(bundle);
			
			if (BUNDLES_BY_NAME == null)
			{
				BUNDLES_BY_NAME = new HashMap<String, Bundle>();
			}
			
			BUNDLES_BY_NAME.put(bundle.getName(), bundle);
		}
	}
	
	/**
	 * addCommand
	 * 
	 * @param command
	 */
	public void addCommand(Command command)
	{
		if (command != null)
		{
			if (this._commands == null)
			{
				this._commands = new ArrayList<Command>();
			}
			
			this._commands.add(command);
		}
	}
	
	/**
	 * addSnippet
	 * 
	 * @param snippet
	 */
	public void addSnippet(Snippet snippet)
	{
		if (snippet != null)
		{
			if (this._snippets == null)
			{
				this._snippets = new ArrayList<Snippet>();
			}
			
			this._snippets.add(snippet);
		}
	}
	
	/**
	 * getBundlesByName
	 * 
	 * @param name
	 * @return
	 */
	public static Bundle getBundleByName(String name)
	{
		Bundle result = null;
		
		if (BUNDLES_BY_NAME != null)
		{
			result = BUNDLES_BY_NAME.get(name);
		}
		
		return result;
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
	 * removeBundle
	 * 
	 * @param bundle
	 */
	public static void removeBundle(Bundle bundle)
	{
		if (bundle != null)
		{
			if (BUNDLES != null)
			{
				BUNDLES.remove(bundle);
			}
		}
	}
}
