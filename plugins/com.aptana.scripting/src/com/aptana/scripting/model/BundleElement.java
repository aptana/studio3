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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jruby.RubyRegexp;

import com.aptana.core.util.SourcePrinter;
import com.aptana.core.util.StringUtil;
import com.aptana.scope.ScopeSelector;
import com.aptana.scripting.model.ProjectTemplateElement.Type;

public class BundleElement extends AbstractElement
{
	private String _author;
	private String _copyright;
	private String _description;
	private String _license;
	private String _licenseUrl;
	private String _repository;

	private List<AbstractBundleElement> _children;
	private File _bundleDirectory;
	private BundlePrecedence _bundlePrecedence;
	private boolean _visible;

	private Map<String, String> _fileTypeRegistry;
	private List<String> _fileTypes;

	private Map<ScopeSelector, RubyRegexp> _foldingStartMarkers;
	private Map<ScopeSelector, RubyRegexp> _foldingStopMarkers;

	private Map<ScopeSelector, RubyRegexp> _increaseIndentMarkers;
	private Map<ScopeSelector, RubyRegexp> _decreaseIndentMarkers;

	/**
	 * Bundle
	 * 
	 * @param path
	 */
	public BundleElement(String path)
	{
		super(path);

		if (path != null)
		{
			// calculate bundle's root directory
			File pathFile = new File(path);
			File parentDirectory = (pathFile.isFile()) ? pathFile.getParentFile() : pathFile;
			String parentName = parentDirectory.getName();

			if (BundleManager.COMMANDS_DIRECTORY_NAME.equals(parentName) || BundleManager.SNIPPETS_DIRECTORY_NAME.equals(parentName))
			{
				parentDirectory = parentDirectory.getParentFile();
			}

			this._bundleDirectory = parentDirectory.getAbsoluteFile();
		}

		// it will be extremely rare to have no children, so go ahead and pre-allocate the child list. This will also
		// allow us to lock on the list instead of maintaining a separate lock object
		this._children = new ArrayList<AbstractBundleElement>();

		// calculate the bundle scope
		this._bundlePrecedence = BundleManager.getInstance().getBundlePrecedence(path);
	}

	/**
	 * addChild
	 * 
	 * @param element
	 */
	public void addChild(AbstractBundleElement element)
	{
		if (element != null)
		{
			synchronized (this._children)
			{
				if (this._children.contains(element) == false)
				{
					BundleEntry.VisibilityContext context = this.getVisibilityContext();

					this._children.add(element);

					if (context != null)
					{
						context.fireVisibilityEvents();
					}
				}
			}

			element.setOwningBundle(this);
		}
	}

	/**
	 * associateFileType
	 * 
	 * @param fileType
	 */
	public void associateFileType(String fileType)
	{
		if (_fileTypes == null)
		{
			_fileTypes = new ArrayList<String>();
		}

		_fileTypes.add(fileType);
	}

	/**
	 * associateScope
	 * 
	 * @param filePattern
	 * @param scope
	 */
	public void associateScope(String filePattern, String scope)
	{
		if (!StringUtil.isEmpty(filePattern) && !StringUtil.isEmpty(scope))
		{
			// Store the filetype -> scope mapping for later lookup when we need to set up the scope in the editor
			if (this._fileTypeRegistry == null)
			{
				this._fileTypeRegistry = new HashMap<String, String>();
			}

			this._fileTypeRegistry.put(filePattern, scope);
		}
	}

	/**
	 * clear - note that this only clears the bundle properties and not the bundle elements
	 */
	public void clearMetadata()
	{
		this._author = null;
		this._copyright = null;
		this._description = null;
		this._license = null;
		this._licenseUrl = null;
	}

	/**
	 * getAuthor
	 * 
	 * @return
	 */
	public String getAuthor()
	{
		return this._author;
	}

	/**
	 * getBundleDirectory
	 * 
	 * @return
	 */
	public File getBundleDirectory()
	{
		return this._bundleDirectory;
	}

	/**
	 * getBundlePrecedence
	 * 
	 * @return
	 */
	public BundlePrecedence getBundlePrecedence()
	{
		return this._bundlePrecedence;
	}

	/**
	 * getCommandByName
	 * 
	 * @return
	 */
	public CommandElement getCommandByName(String name)
	{
		CommandElement result = null;

		if (StringUtil.isEmpty(name) == false)
		{
			// NOTE: we use getCommands here so we don't have to sync this block. getCommands returns a fresh List each
			// time it is invoked and it handles syncing for us
			List<CommandElement> commands = this.getCommands();

			for (CommandElement command : commands)
			{
				if (name.equals(command.getDisplayName()))
				{
					result = command;
					break;
				}
			}
		}

		return result;
	}

	/**
	 * getCommands
	 * 
	 * @return
	 */
	public List<CommandElement> getCommands()
	{
		List<CommandElement> result = new ArrayList<CommandElement>();

		synchronized (this._children)
		{
			for (AbstractBundleElement element : this._children)
			{
				if (element instanceof CommandElement)
				{
					result.add((CommandElement) element);
				}
			}
		}

		return result;
	}

	/**
	 * getCopyright
	 * 
	 * @return
	 */
	public String getCopyright()
	{
		return this._copyright;
	}

	/**
	 * getDecreaseIndentMarkers
	 * 
	 * @return
	 */
	public Map<ScopeSelector, RubyRegexp> getDecreaseIndentMarkers()
	{
		Map<ScopeSelector, RubyRegexp> result;

		if (this._decreaseIndentMarkers == null)
		{
			result = Collections.emptyMap();
		}
		else
		{
			result = Collections.unmodifiableMap(this._decreaseIndentMarkers);
		}

		return result;
	}

	/**
	 * getDefaultName
	 * 
	 * @return
	 */
	protected String getDefaultName()
	{
		return BundleUtils.getDefaultBundleName(this.getPath());
	}

	/**
	 * getDescription
	 * 
	 * @return
	 */
	public String getDescription()
	{
		return this._description;
	}

	/**
	 * getDisplayName
	 * 
	 * @return
	 */
	public String getDisplayName()
	{
		String result = super.getDisplayName();

		if (StringUtil.isEmpty(result))
		{
			result = this.getDefaultName();
		}

		return result;
	}

	/**
	 * getElementName
	 */
	protected String getElementName()
	{
		return "bundle"; //$NON-NLS-1$
	}

	/**
	 * getEnvs
	 * 
	 * @return
	 */
	public List<EnvironmentElement> getEnvs()
	{
		List<EnvironmentElement> result = new ArrayList<EnvironmentElement>();

		synchronized (this._children)
		{
			for (AbstractBundleElement element : this._children)
			{
				if (element instanceof EnvironmentElement)
				{
					result.add((EnvironmentElement) element);
				}
			}
		}

		return result;
	}

	/**
	 * getFileTypeRegistry
	 * 
	 * @return
	 */
	public Map<String, String> getFileTypeRegistry()
	{
		return this._fileTypeRegistry;
	}

	/**
	 * getFileTypes
	 * 
	 * @return
	 */
	List<String> getFileTypes()
	{
		return this._fileTypes;
	}

	/**
	 * getFoldingStartMarkers
	 * 
	 * @return
	 */
	public Map<ScopeSelector, RubyRegexp> getFoldingStartMarkers()
	{
		Map<ScopeSelector, RubyRegexp> result;

		if (this._foldingStartMarkers == null)
		{
			result = Collections.emptyMap();
		}
		else
		{
			result = Collections.unmodifiableMap(this._foldingStartMarkers);
		}

		return result;
	}

	/**
	 * getFoldingStopMarkers
	 * 
	 * @return
	 */
	public Map<ScopeSelector, RubyRegexp> getFoldingStopMarkers()
	{
		Map<ScopeSelector, RubyRegexp> result;

		if (this._foldingStopMarkers == null)
		{
			result = Collections.emptyMap();
		}
		else
		{
			result = Collections.unmodifiableMap(this._foldingStopMarkers);
		}

		return result;
	}

	/**
	 * getFoldingStartMarkers
	 * 
	 * @return
	 */
	public Map<ScopeSelector, RubyRegexp> getIncreaseIndentMarkers()
	{
		Map<ScopeSelector, RubyRegexp> result;

		if (this._increaseIndentMarkers == null)
		{
			result = Collections.emptyMap();
		}
		else
		{
			result = Collections.unmodifiableMap(this._increaseIndentMarkers);
		}

		return result;
	}

	/**
	 * getLicense
	 * 
	 * @return
	 */
	public String getLicense()
	{
		return this._license;
	}

	/**
	 * getLicenseUrl
	 * 
	 * @return
	 */
	public String getLicenseUrl()
	{
		return this._licenseUrl;
	}

	/**
	 * getLoadPaths
	 * 
	 * @return
	 */
	public List<String> getLoadPaths()
	{
		List<String> result = new LinkedList<String>();

		result.add(BundleUtils.getBundleLibDirectory(this.getBundleDirectory()));

		return result;
	}

	/**
	 * getMenus
	 * 
	 * @return
	 */
	public List<MenuElement> getMenus()
	{
		List<MenuElement> result = new ArrayList<MenuElement>();

		synchronized (this._children)
		{
			for (AbstractBundleElement element : this._children)
			{
				if (element instanceof MenuElement)
				{
					result.add((MenuElement) element);
				}
			}
		}

		return result;
	}

	/**
	 * getPairs
	 * 
	 * @return
	 */
	public List<SmartTypingPairsElement> getPairs()
	{
		List<SmartTypingPairsElement> result = new ArrayList<SmartTypingPairsElement>();

		synchronized (this._children)
		{
			for (AbstractBundleElement element : this._children)
			{
				if (element instanceof SmartTypingPairsElement)
				{
					result.add((SmartTypingPairsElement) element);
				}
			}
		}

		return result;
	}

	/**
	 * getProjectTemplates
	 * 
	 * @return
	 */
	public List<ProjectTemplateElement> getProjectTemplates()
	{
		List<ProjectTemplateElement> result = new ArrayList<ProjectTemplateElement>();

		synchronized (this._children)
		{
			for (AbstractBundleElement element : this._children)
			{
				if (element instanceof ProjectTemplateElement)
				{
					result.add((ProjectTemplateElement) element);
				}
			}
		}

		return result;
	}

	/**
	 * getProjectTemplates
	 * 
	 * @param type
	 * @return
	 */
	public List<ProjectTemplateElement> getProjectTemplatesByType(Type type)
	{
		List<ProjectTemplateElement> result = new ArrayList<ProjectTemplateElement>();

		// NOTE: we use getProjectTemplates here so we don't have to sync this block. getProjectTemplates returns a
		// fresh List each time it is invoked and it handles syncing for us
		List<ProjectTemplateElement> templates = this.getProjectTemplates();

		for (ProjectTemplateElement template : templates)
		{
			// type "all" is always included
			if (template.getType() == type || template.getType() == Type.ALL)
			{
				result.add(template);
			}
		}

		return result;
	}

	/**
	 * getGitRepo
	 * 
	 * @return
	 */
	public String getRepository()
	{
		return this._repository;
	}

	/**
	 * getVisibilityContext
	 * 
	 * @return
	 */
	private BundleEntry.VisibilityContext getVisibilityContext()
	{
		BundleEntry entry = BundleManager.getInstance().getBundleEntry(this.getDisplayName());
		BundleEntry.VisibilityContext context = null;

		if (entry != null)
		{
			context = entry.getVisibilityContext();
		}

		return context;
	}

	/**
	 * hasChildren
	 * 
	 * @return
	 */
	public boolean hasChildren()
	{
		boolean result = false;

		synchronized (this._children)
		{
			result = this._children.size() > 0;
		}

		return result;
	}

	/**
	 * hasMetadata
	 * 
	 * @return
	 */
	public boolean hasMetadata()
	{
		return (this._author != null || this._copyright != null || this._description != null || this._license != null || this._licenseUrl != null);
	}

	/**
	 * isEmpty
	 * 
	 * @return
	 */
	public boolean isEmpty()
	{
		return this.hasMetadata() == false && this.hasChildren() == false;
	}

	/**
	 * isReference
	 * 
	 * @return
	 */
	public boolean isReference()
	{
		// NOTE: we need to check the actual display name field and not the
		// calculated display name we generate in this class
		String displayName = super.getDisplayName();

		return !StringUtil.isEmpty(displayName) && StringUtil.areNotEqual(displayName, this.getDefaultName());
	}

	/**
	 * isVisible
	 * 
	 * @return
	 */
	public boolean isVisible()
	{
		return this._visible;
	}

	/**
	 * printBody
	 * 
	 * @return
	 */
	public void printBody(SourcePrinter printer)
	{
		printer.printWithIndent("bundle_precedence: ").println(this._bundlePrecedence.toString()); //$NON-NLS-1$
		printer.printWithIndent("path: ").println(this.getPath()); //$NON-NLS-1$
		printer.printWithIndent("author: ").println(this._author); //$NON-NLS-1$
		printer.printWithIndent("copyright: ").println(this._copyright); //$NON-NLS-1$
		printer.printWithIndent("description: ").println(this._description); //$NON-NLS-1$
		printer.printWithIndent("repository: ").println(this._repository); //$NON-NLS-1$

		// output commands
		for (CommandElement command : this.getCommands())
		{
			command.toSource(printer);
		}

		// output menus
		for (MenuElement menu : this.getMenus())
		{
			menu.toSource(printer);
		}
	}

	/**
	 * removeChild
	 * 
	 * @param element
	 */
	public void removeChild(AbstractBundleElement element)
	{
		boolean removed = false;

		// disassociate element with this bundle
		element.setOwningBundle(null);

		synchronized (this._children)
		{
			BundleEntry.VisibilityContext context = this.getVisibilityContext();

			removed = this._children.remove(element);

			if (context != null)
			{
				context.fireVisibilityEvents();
			}
		}

		if (removed)
		{
			// special case for menus so they can remove their children so they will fire events
			if (element instanceof MenuElement)
			{
				((MenuElement) element).removeChildren();
			}

			// make sure elements are no longer tracked in AbstractElement
			AbstractElement.unregisterElement(element);
		}
	}

	/**
	 * setAuthor
	 * 
	 * @param author
	 */
	public void setAuthor(String author)
	{
		this._author = author;
	}

	/**
	 * setCopyright
	 * 
	 * @param copyright
	 */
	public void setCopyright(String copyright)
	{
		this._copyright = copyright;
	}

	/**
	 * setDescription
	 * 
	 * @param description
	 */
	public void setDescription(String description)
	{
		this._description = description;
	}

	/**
	 * setFoldingMarkers
	 * 
	 * @param scope
	 * @param startRegexp
	 * @param endRegexp
	 */
	public void setFoldingMarkers(String scope, RubyRegexp startRegexp, RubyRegexp endRegexp)
	{
		if (!StringUtil.isEmpty(scope) && startRegexp != null && startRegexp.isNil() == false && endRegexp != null && endRegexp.isNil() == false)
		{
			// store starting regular expression
			if (this._foldingStartMarkers == null)
			{
				this._foldingStartMarkers = new HashMap<ScopeSelector, RubyRegexp>();
			}

			this._foldingStartMarkers.put(new ScopeSelector(scope), startRegexp);

			// store ending regular expression
			if (this._foldingStopMarkers == null)
			{
				this._foldingStopMarkers = new HashMap<ScopeSelector, RubyRegexp>();
			}

			this._foldingStopMarkers.put(new ScopeSelector(scope), endRegexp);
		}
	}

	/**
	 * setIndentMarkers
	 * 
	 * @param scope
	 * @param startRegexp
	 * @param endRegexp
	 */
	public void setIndentMarkers(String scope, RubyRegexp startRegexp, RubyRegexp endRegexp)
	{
		if (!StringUtil.isEmpty(scope) && startRegexp != null && startRegexp.isNil() == false && endRegexp != null && endRegexp.isNil() == false)
		{
			// store increasing regular expression
			if (this._increaseIndentMarkers == null)
			{
				this._increaseIndentMarkers = new HashMap<ScopeSelector, RubyRegexp>();
			}

			this._increaseIndentMarkers.put(new ScopeSelector(scope), startRegexp);

			// store decreasing regular expression
			if (this._decreaseIndentMarkers == null)
			{
				this._decreaseIndentMarkers = new HashMap<ScopeSelector, RubyRegexp>();
			}

			this._decreaseIndentMarkers.put(new ScopeSelector(scope), endRegexp);
		}
	}

	/**
	 * setLicense
	 * 
	 * @param license
	 */
	public void setLicense(String license)
	{
		this._license = license;
	}

	/**
	 * setLicenseUrl
	 * 
	 * @param licenseUrl
	 */
	public void setLicenseUrl(String licenseUrl)
	{
		this._licenseUrl = licenseUrl;
	}

	/**
	 * setGitRepo
	 * 
	 * @param gitRepo
	 */
	public void setRepository(String gitRepo)
	{
		this._repository = gitRepo;
	}

	/**
	 * setVisible
	 * 
	 * @param flag
	 */
	public void setVisible(boolean flag)
	{
		this._visible = flag;
	}
}
