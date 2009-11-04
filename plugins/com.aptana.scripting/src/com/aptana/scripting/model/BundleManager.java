package com.aptana.scripting.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jruby.anno.JRubyMethod;

public class BundleManager
{
	private static BundleManager INSTANCE;
	
	private List<Bundle> _bundles;
	private Map<String,Bundle> _bundelsByPath;
	
	/**
	 * BundleManager
	 */
	private BundleManager()
	{
	}
	
	/**
	 * getInstance
	 * 
	 * @return
	 */
	@JRubyMethod(name="instance")
	public static BundleManager getInstance()
	{
		if (INSTANCE == null)
		{
			INSTANCE = new BundleManager();
		}
		
		return INSTANCE;
	}
	
	/**
	 * addBundle
	 * 
	 * @param bundle
	 */
	@JRubyMethod(name="add_bundle")
	public void addBundle(Bundle bundle)
	{
		if (bundle != null)
		{
			if (this._bundles == null)
			{
				this._bundles = new ArrayList<Bundle>();
			}
			
			this._bundles.add(bundle);
			
			if (this._bundelsByPath == null)
			{
				this._bundelsByPath = new HashMap<String, Bundle>();
			}
			
			this._bundelsByPath.put(bundle.getPath(), bundle);
		}
	}
	
	/**
	 * getBundleFromPath
	 * 
	 * @param path
	 * @return
	 */
	@JRubyMethod(name="bundle_from_path")
	public Bundle getBundleFromPath(String path)
	{
		Bundle result = null;
		
		if (this._bundelsByPath != null)
		{
			result = this._bundelsByPath.get(path);
		}
		
		return result;
	}

	/**
	 * removeBundle
	 * 
	 * @param bundle
	 */
	public void removeBundle(Bundle bundle)
	{
		if (bundle != null)
		{
			if (this._bundles != null)
			{
				this._bundles.remove(bundle);
			}
		}
	}
}
