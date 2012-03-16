/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.model;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.ObjectUtil;
import com.aptana.scope.IScopeSelector;
import com.aptana.scope.MatchAnyScopeSelector;
import com.aptana.scope.ScopeSelector;
import com.aptana.scripting.ScriptingActivator;

public abstract class AbstractBundleElement extends AbstractElement
{
	private static final String ALL_SCOPES = "all"; //$NON-NLS-1$

	private String _scope;
	private IScopeSelector _scopeSelector;
	protected BundleElement owningBundle;

	/**
	 * AbstractBundleElement
	 * 
	 * @param path
	 */
	public AbstractBundleElement(String path)
	{
		super(path);
	}

	/**
	 * getOwningBundle
	 * 
	 * @return
	 */
	public BundleElement getOwningBundle()
	{
		return this.owningBundle;
	}

	/**
	 * getScope
	 * 
	 * @return
	 */
	public String getScope()
	{
		return this._scope;
	}

	/**
	 * getScopeSelector
	 * 
	 * @return
	 */
	public IScopeSelector getScopeSelector()
	{
		if (this._scopeSelector == null)
		{
			if (this._scope == null || this._scope.length() == 0)
			{
				this._scopeSelector = new MatchAnyScopeSelector();
			}
			else
			{
				this._scopeSelector = new ScopeSelector(this._scope);
			}
		}

		return this._scopeSelector;
	}

	/**
	 * matches
	 * 
	 * @param scope
	 * @return
	 */
	public boolean matches(String scope)
	{
		IScopeSelector selector = this.getScopeSelector();
		boolean result = true;

		if (selector != null)
		{
			result = selector.matches(scope);
		}

		return result;
	}

	/**
	 * matches
	 * 
	 * @param scopes
	 * @return
	 */
	public boolean matches(String[] scopes)
	{
		IScopeSelector selector = this.getScopeSelector();
		boolean result = true;

		if (selector != null)
		{
			result = selector.matches(scopes);
		}

		return result;
	}

	/**
	 * setOwningBundle
	 * 
	 * @param bundle
	 */
	void setOwningBundle(BundleElement bundle)
	{
		this.owningBundle = bundle;
	}

	/**
	 * setScope
	 * 
	 * @param scope
	 */
	public void setScope(String scope)
	{
		// NOTE: If a scope selector is null, then we match
		// any scope. We convert "all" to null since they
		// are equivalent
		if (scope != null && scope.equals(ALL_SCOPES))
		{
			scope = null;
		}

		if (ObjectUtil.areNotEqual(this._scope, scope))
		{
			this._scope = BundleManager.getInstance().sharedString(scope);
			this._scopeSelector = null;
		}
	}

	protected URL getURLFromPath(String path)
	{
		if (path == null)
		{
			return null;
		}

		URL iconURL = null;

		try
		{
			// First try to convert path into a URL
			iconURL = new URL(path);
		}
		catch (MalformedURLException e1)
		{
			// If it fails, assume it's a project-relative local path
			IPath iconPath = new Path(getDirectory().getAbsolutePath()).append(path);
			try
			{
				iconURL = iconPath.toFile().toURI().toURL();
			}
			catch (Exception e)
			{
				IdeLog.logError(ScriptingActivator.getDefault(), MessageFormat.format(
						"Unable to convert {0} into an URL for bundle element {1}", path, getDisplayName())); //$NON-NLS-1$
			}
		}

		return iconURL;
	}

	/**
	 * getDirectory
	 * 
	 * @return
	 */
	private File getDirectory()
	{
		return getOwningBundle().getBundleDirectory();
	}
}
