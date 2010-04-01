/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
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
package com.aptana.xml;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.osgi.framework.Bundle;

/**
 * @author Kevin Lindsey based on code by Patrick Mueller
 */
public class BundleClassLoader extends ClassLoader
{
	private List<Bundle> _bundles;

	/**
	 * ScriptClassLoader
	 */
	public BundleClassLoader()
	{
		super(BundleClassLoader.class.getClassLoader());

		this._bundles = new ArrayList<Bundle>();
	}

	/**
	 * addBundle
	 *
	 * @param bundle
	 */
	public void addBundle(Bundle bundle)
	{
		if (bundle == null)
		{
			throw new IllegalArgumentException(Messages.BundleClassLoader_Bundled_Undefined);
		}
		
		if (this._bundles.contains(bundle) == false)
		{
			this._bundles.add(bundle);
		}
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
			this._bundles.remove(bundle);
		}
	}
	
	/**
	 * findClass
	 * 
	 * @param name
	 * @return Class
	 * @throws ClassNotFoundException
	 */
	protected Class<?> findClass(String name) throws ClassNotFoundException
	{
		Class<?> result = this.loadClassFromBundles(name);

		if (result == null)
		{
			throw new ClassNotFoundException(Messages.BundleClassLoader_Unable_To_Locate_Class+ name);
		}

		return result;
	}
	
	/**
	 * findResource
	 * 
	 * @param name
	 * @return URL
	 */
	protected URL findResource(String name)
	{
		URL result = super.findResource(name);

		if (result == null)
		{
			Iterator<Bundle> iterator = this._bundles.iterator();
			
			while (iterator.hasNext())
			{
				Bundle bundle = iterator.next();
				
				result = bundle.getResource(name);

				if (result != null)
				{
					break;
				}
			}
		}

		return result;
	}

	/**
	 * findResources
	 * 
	 * @param name
	 * @return Enumeration
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	protected Enumeration findResources(String name) throws IOException
	{
		Enumeration result = super.findResources(name);

		if (result == null)
		{
			Iterator<Bundle> iterator = this._bundles.iterator();
			
			while (iterator.hasNext())
			{
				Bundle bundle = iterator.next();
				
				result = bundle.getResources(name);

				if (result != null)
				{
					break;
				}
			}
		}

		if (result == null)
		{
			throw new IOException(Messages.BundleClassLoader_Unable_To_Locate_Resources + name);
		}

		return result;
	}

	/**
	 * loadClass
	 * 
	 * @param name
	 * @return Class
	 * @throws ClassNotFoundException
	 */
	public Class<?> loadClass(String name) throws ClassNotFoundException
	{
		Class<?> result = super.loadClass(name);

		if (result == null)
		{
			result = this.loadClassFromBundles(name);
		}

		if (result == null)
		{
			throw new ClassNotFoundException(Messages.BundleClassLoader_Unable_To_Load_Class + name);
		}

		return result;
	}

	/**
	 * loadClass
	 * 
	 * @param name
	 * @param resolve
	 * @return Class
	 * @throws ClassNotFoundException
	 */
	protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException
	{
		Class<?> result = super.loadClass(name, resolve);

		if (result == null)
		{
			result = this.loadClassFromBundles(name);
		}

		if (result == null)
		{
			throw new ClassNotFoundException(Messages.BundleClassLoader_Unable_To_Load_Class + name);
		}

		return result;
	}

	/**
	 * loadClassFromBundles
	 * 
	 * @param name
	 * @return Class
	 * @throws ClassNotFoundException
	 */
	private Class<?> loadClassFromBundles(String name) throws ClassNotFoundException
	{
		Class<?> result = null;
		Iterator<Bundle> iterator = this._bundles.iterator();

		while (iterator.hasNext())
		{
			Bundle bundle = iterator.next();

			try
			{
				result = bundle.loadClass(name);
			}
			catch (ClassNotFoundException e)
			{
				// do nothing
			}

			if (result != null)
			{
				break;
			}
		}

		return result;
	}
}
