package com.aptana.scripting.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.jruby.RubyRegexp;

import com.aptana.scope.ScopeSelector;
import com.aptana.scripting.Activator;
import com.aptana.util.StringUtil;

public class BundleElement extends AbstractElement
{
	private static final String GENERIC_CONTENT_TYPE_ID = "com.aptana.editor.text.content-type.generic"; //$NON-NLS-1$

	private static final String BUNDLE_DIRECTORY_SUFFIX = ".ruble"; //$NON-NLS-1$

	private String _author;
	private String _copyright;
	private String _description;
	private String _license;
	private String _licenseUrl;
	private String _repository;

	private File _bundleDirectory;
	private BundleScope _bundleScope;
	private List<MenuElement> _menus;
	private List<CommandElement> _commands;

	private Object menuLock = new Object();
	private Object commandLock = new Object();

	private Map<String, String> _fileTypeRegistry;

	private Map<ScopeSelector, RubyRegexp> foldingStartMarkers;
	private Map<ScopeSelector, RubyRegexp> foldingStopMarkers;

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

			if (BundleManager.COMMANDS_DIRECTORY_NAME.equals(parentName)
					|| BundleManager.SNIPPETS_DIRECTORY_NAME.equals(parentName))
			{
				parentDirectory = parentDirectory.getParentFile();
			}

			this._bundleDirectory = parentDirectory.getAbsoluteFile();
		}

		// calculate the bundle scope
		this._bundleScope = BundleManager.getInstance().getBundleScopeFromPath(path);
	}

	/**
	 * addCommand
	 * 
	 * @param command
	 */
	public void addCommand(CommandElement command)
	{
		if (command != null)
		{
			synchronized (commandLock)
			{
				if (this._commands == null)
				{
					this._commands = new ArrayList<CommandElement>();
				}

				// NOTE: Should we prevent the same element from being added twice?
				this._commands.add(command);
			}

			command.setOwningBundle(this);

			// fire add event
			BundleManager.getInstance().fireElementAddedEvent(command);
		}
	}

	/**
	 * addMenu
	 * 
	 * @param snippet
	 */
	public void addMenu(MenuElement menu)
	{
		if (menu != null)
		{
			synchronized (menuLock)
			{
				if (this._menus == null)
				{
					this._menus = new ArrayList<MenuElement>();
				}

				// NOTE: Should we prevent the same element from being added twice?
				this._menus.add(menu);
			}

			menu.setOwningBundle(this);

			// fire add event
			BundleManager.getInstance().fireElementAddedEvent(menu);
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
	 * getBundleScope
	 * 
	 * @return
	 */
	public BundleScope getBundleScope()
	{
		return this._bundleScope;
	}

	/**
	 * getCommandByName
	 * 
	 * @return
	 */
	public CommandElement getCommandByName(String name)
	{
		CommandElement result = null;

		synchronized (commandLock)
		{
			if (name != null && name.length() > 0 && this._commands != null && this._commands.size() > 0)
			{
				for (CommandElement command : this._commands)
				{
					if (name.equals(command.getDisplayName()))
					{
						result = command;
						break;
					}
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
	public CommandElement[] getCommands()
	{
		CommandElement[] result = BundleManager.NO_COMMANDS;

		synchronized (commandLock)
		{
			if (this._commands != null && this._commands.size() > 0)
			{
				result = this._commands.toArray(new CommandElement[this._commands.size()]);
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
	 * getDescription
	 * 
	 * @return
	 */
	public String getDescription()
	{
		return this._description;
	}

	/**
	 * getDefaultName
	 * 
	 * @return
	 */
	protected String getDefaultName()
	{
		String path = this.getPath();
		String result = null;

		if (path != null && path.length() > 0)
		{
			File file = new File(path).getParentFile();

			result = file.getName();

			if (result.endsWith(BUNDLE_DIRECTORY_SUFFIX))
			{
				result = result.substring(0, result.length() - BUNDLE_DIRECTORY_SUFFIX.length());
			}
		}

		return result;
	}

	/**
	 * getDisplayName
	 * 
	 * @return
	 */
	public String getDisplayName()
	{
		String result = super.getDisplayName();

		if (result == null || result.length() == 0)
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
	 * getGitRepo
	 * 
	 * @return
	 */
	public String getRepository()
	{
		return this._repository;
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
		File bundleDirectory = new File(this.getPath()).getParentFile();

		return BundleManager.getInstance().getBundleLoadPaths(bundleDirectory);
	}

	/**
	 * getMenus
	 * 
	 * @return
	 */
	public MenuElement[] getMenus()
	{
		MenuElement[] result = BundleManager.NO_MENUS;

		synchronized (menuLock)
		{
			if (this._menus != null && this._menus.size() > 0)
			{
				result = this._menus.toArray(new MenuElement[this._menus.size()]);
			}
		}

		return result;
	}

	/**
	 * hasCommands
	 * 
	 * @return
	 */
	public boolean hasCommands()
	{
		boolean result = false;

		synchronized (commandLock)
		{
			if (this._commands != null)
			{
				result = this._commands.size() > 0;
			}
		}

		return result;
	}

	/**
	 * hasMenus
	 * 
	 * @return
	 */
	public boolean hasMenus()
	{
		boolean result = false;

		synchronized (menuLock)
		{
			if (this._menus != null)
			{
				result = this._menus.size() > 0;
			}
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
		return this.hasMetadata() == false && this.hasCommands() == false && this.hasMenus() == false;
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

		return displayName != null && displayName.length() > 0
				&& StringUtil.areNotEqual(displayName, this.getDefaultName());
	}

	/**
	 * printBody
	 * 
	 * @return
	 */
	public void printBody(SourcePrinter printer)
	{
		printer.printWithIndent("bundle_scope: ").println(this._bundleScope.toString()); //$NON-NLS-1$
		printer.printWithIndent("path: ").println(this.getPath()); //$NON-NLS-1$
		printer.printWithIndent("author: ").println(this._author); //$NON-NLS-1$
		printer.printWithIndent("copyright: ").println(this._copyright); //$NON-NLS-1$
		printer.printWithIndent("description: ").println(this._description); //$NON-NLS-1$
		printer.printWithIndent("repository: ").println(this._repository); //$NON-NLS-1$

		// output commands
		synchronized (commandLock)
		{
			if (this._commands != null)
			{
				for (CommandElement command : this._commands)
				{
					command.toSource(printer);
				}
			}
		}

		// output menus
		synchronized (menuLock)
		{
			if (this._menus != null)
			{
				for (MenuElement menu : this._menus)
				{
					menu.toSource(printer);
				}
			}
		}
	}

	/**
	 * removeCommand
	 * 
	 * @param command
	 */
	public void removeCommand(CommandElement command)
	{
		boolean removed = false;

		synchronized (commandLock)
		{
			if (this._commands != null && (removed = this._commands.remove(command)))
			{
				AbstractElement.unregisterElement(command);
			}
		}

		if (removed)
		{
			// fire delete event
			BundleManager.getInstance().fireElementDeletedEvent(command);
		}
	}

	/**
	 * removeElement
	 * 
	 * @param element
	 */
	public void removeElement(AbstractBundleElement element)
	{
		if (element instanceof CommandElement)
		{
			this.removeCommand((CommandElement) element);
		}
		else if (element instanceof MenuElement)
		{
			this.removeMenu((MenuElement) element);
		}
	}

	/**
	 * removeMenu
	 * 
	 * @param command
	 */
	public void removeMenu(MenuElement menu)
	{
		boolean removed = false;

		synchronized (menuLock)
		{
			if (this._menus != null && (removed = this._menus.remove(menu)))
			{
				AbstractElement.unregisterElement(menu);

				menu.removeChildren();
			}
		}

		if (removed)
		{
			// fire delete event
			BundleManager.getInstance().fireElementDeletedEvent(menu);
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
	 * setGitRepo
	 * 
	 * @param gitRepo
	 */
	public void setRepository(String gitRepo)
	{
		this._repository = gitRepo;
	}

	public void setFoldingMarkers(String scope, RubyRegexp startRegexp, RubyRegexp endRegexp)
	{
		if (foldingStopMarkers == null)
		{
			foldingStopMarkers = new HashMap<ScopeSelector, RubyRegexp>();
		}
		if (foldingStartMarkers == null)
		{
			foldingStartMarkers = new HashMap<ScopeSelector, RubyRegexp>();
		}
		foldingStartMarkers.put(new ScopeSelector(scope), startRegexp);
		foldingStopMarkers.put(new ScopeSelector(scope), endRegexp);
	}

	public void registerFileType(String fileType, String scope)
	{
		associateFileType(fileType);
		associateScope(fileType, scope);
	}

	public void associateScope(String filePattern, String scope)
	{
		// Store the filetype -> scope mapping for later lookup when we need to set up the scope in the editor
		if (_fileTypeRegistry == null)
		{
			_fileTypeRegistry = new HashMap<String, String>();
		}
		getFileTypeRegistry().put(filePattern, scope);
	}

	public void associateFileType(String fileType)
	{
		// We need to massage the argument into file name or extension and then create a bogus name when we want to see
		// if there's already a filetype for it!
		// Check to see if files of this type already have an association
		IContentType type = Platform.getContentTypeManager().findContentTypeFor(fileType.replaceAll("\\*", "star")); //$NON-NLS-1$ //$NON-NLS-2$
		if (type != null)
			return;
		type = Platform.getContentTypeManager().getContentType(GENERIC_CONTENT_TYPE_ID);
		try
		{
			int assocType = IContentType.FILE_NAME_SPEC;
			if (fileType.contains("*") && fileType.indexOf('.') != -1) //$NON-NLS-1$
			{
				assocType = IContentType.FILE_EXTENSION_SPEC;
				fileType = fileType.substring(fileType.indexOf('.') + 1);
			}
			type.addFileSpec(fileType, assocType);
		}
		catch (CoreException e)
		{
			Activator.logError(e.getMessage(), e);
		}
	}

	public void unassociateFileType(String fileType)
	{
		IContentType type = Platform.getContentTypeManager().getContentType(GENERIC_CONTENT_TYPE_ID);
		try
		{
			int assocType = IContentType.FILE_NAME_SPEC;
			if (fileType.contains("*") && fileType.indexOf('.') != -1) //$NON-NLS-1$
			{
				assocType = IContentType.FILE_EXTENSION_SPEC;
				fileType = fileType.substring(fileType.indexOf('.') + 1);
			}
			type.removeFileSpec(fileType, assocType);
		}
		catch (CoreException e)
		{
			Activator.logError(e.getMessage(), e);
		}
	}

	public Map<String, String> getFileTypeRegistry()
	{
		return _fileTypeRegistry;
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

	public Map<ScopeSelector, RubyRegexp> getFoldingStartMarkers()
	{
		return Collections.unmodifiableMap(foldingStartMarkers);
	}

	public Map<ScopeSelector, RubyRegexp> getFoldingStopMarkers()
	{
		return Collections.unmodifiableMap(foldingStopMarkers);
	}
}
