package com.aptana.scripting.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BundleElement extends AbstractElement
{
	private static final String BUNDLE_DIRECTORY_SUFFIX = ".rr-bundle";
	
	private String _author;
	private String _copyright;
	private String _description;
	private String _license;
	private String _licenseUrl;
	private String _gitRepo;
	
	private BundleScope _bundleScope;
	private List<MenuElement> _menus;
	private List<SnippetElement> _snippets;
	private List<CommandElement> _commands;

	/**
	 * Bundle
	 * 
	 * @param path
	 */
	public BundleElement(String path)
	{
		super(path);
		
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
			if (this._commands == null)
			{
				this._commands = new ArrayList<CommandElement>();
			}

			this._commands.add(command);

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
			if (this._menus == null)
			{
				this._menus = new ArrayList<MenuElement>();
			}
			
			this._menus.add(menu);
			
			menu.setOwningBundle(this);
			
			// fire add event
			BundleManager.getInstance().fireElementAddedEvent(menu);
		}
	}

	/**
	 * addSnippet
	 * 
	 * @param snippet
	 */
	public void addSnippet(SnippetElement snippet)
	{
		if (snippet != null)
		{
			if (this._snippets == null)
			{
				this._snippets = new ArrayList<SnippetElement>();
			}

			this._snippets.add(snippet);
			
			snippet.setOwningBundle(this);
			
			// fire add event
			BundleManager.getInstance().fireElementAddedEvent(snippet);
		}
	}

	/**
	 * findCommandsFromPath
	 * 
	 * @param path
	 * @return
	 */
	public CommandElement[] findCommandsFromPath(String path)
	{
		List<CommandElement> result = new ArrayList<CommandElement>();
		
		if (path != null && path.length() > 0 && this._commands != null)
		{
			for (CommandElement command : this._commands)
			{
				if (path.equals(command.getPath()))
				{
					result.add(command);
				}
			}
		}
		
		return result.toArray(new CommandElement[result.size()]);
	}
	
	/**
	 * findMenusFromPath
	 * 
	 * @param path
	 * @return
	 */
	public MenuElement[] findMenusFromPath(String path)
	{
		List<MenuElement> result = new ArrayList<MenuElement>();
		
		if (path != null && path.length() > 0 && this._menus != null)
		{
			for (MenuElement menu : this._menus)
			{
				if (path.equals(menu.getPath()))
				{
					result.add(menu);
				}
			}
		}
		
		return result.toArray(new MenuElement[result.size()]);
	}
	
	/**
	 * findSnippetsFromPath
	 * 
	 * @param path
	 * @return
	 */
	public SnippetElement[] findSnippetsFromPath(String path)
	{
		List<SnippetElement> result = new ArrayList<SnippetElement>();
		
		if (path != null && path.length() > 0 && this._snippets != null)
		{
			for (SnippetElement snippet : this._snippets)
			{
				if (path.equals(snippet.getPath()))
				{
					result.add(snippet);
				}
			}
		}
		
		return result.toArray(new SnippetElement[result.size()]);
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

		if (this._commands != null && this._commands.size() > 0)
		{
			result = this._commands.toArray(new CommandElement[this._commands.size()]);
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
	 * getDisplayName
	 * 
	 * @return
	 */
	public String getDisplayName()
	{
		String result = this._displayName;
		
		if (result == null || result.length() == 0)
		{
			File path = new File(this._path);
			
			result = path.getName();
			
			if (result.endsWith(BUNDLE_DIRECTORY_SUFFIX))
			{
				result = result.substring(0, result.length() - BUNDLE_DIRECTORY_SUFFIX.length());
			}
		}
		
		return result;
	}
	
	/**
	 * getGitRepo
	 * 
	 * @return
	 */
	public String getGitRepo()
	{
		return this._gitRepo;
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
	 * getMenus
	 * 
	 * @return
	 */
	public MenuElement[] getMenus()
	{
		MenuElement[] result = BundleManager.NO_MENUS;
		
		if (this._menus != null && this._menus.size() > 0)
		{
			result = this._menus.toArray(new MenuElement[this._menus.size()]);
		}
		
		return result;
	}

	/**
	 * getSnippets
	 * 
	 * @return
	 */
	public SnippetElement[] getSnippets()
	{
		SnippetElement[] result = BundleManager.NO_SNIPPETS;

		if (this._snippets != null && this._snippets.size() > 0)
		{
			result = this._snippets.toArray(new SnippetElement[this._snippets.size()]);
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
		return this._commands != null && this._commands.size() > 0;
	}
	
	/**
	 * hasMetadata
	 * 
	 * @return
	 */
	public boolean hasMetadata()
	{
		return (
			this._author != null ||
			this._copyright != null ||
			this._description != null ||
			this._license != null ||
			this._licenseUrl != null
		);
	}
	
	/**
	 * hasMenus
	 * 
	 * @return
	 */
	public boolean hasMenus()
	{
		return this._menus != null && this._menus.size() > 0;
	}
	
	/**
	 * hasSnippets
	 * 
	 * @return
	 */
	public boolean hasSnippets()
	{
		return this._snippets != null && this._snippets.size() > 0;
	}
	
	/**
	 * isEmpty
	 * 
	 * @return
	 */
	public boolean isEmpty()
	{
		return this.hasMetadata() == false && this.hasCommands() == false && this.hasMenus() == false && this.hasSnippets() == false;
	}
	
	/**
	 * isReference
	 * 
	 * @return
	 */
	public boolean isReference()
	{
		return this._displayName != null && this._displayName.length() > 0;
	}
	
	/**
	 * moveTo
	 * 
	 * @param path
	 */
	void moveTo(String path)
	{
		int oldPathLength = this._path.length();

		// set new path
		this._path = path;

		// update command paths
		if (this._commands != null)
		{
			for (CommandElement command : this._commands)
			{
				String newCommandPath = path + command.getPath().substring(oldPathLength);

				command.setPath(newCommandPath);
			}
		}

		// update snippet paths
		if (this._snippets != null)
		{
			for (SnippetElement snippet : this._snippets)
			{
				String newSnippetPath = path + snippet.getPath().substring(oldPathLength);

				snippet.setPath(newSnippetPath);
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
		if (this._commands != null && this._commands.remove(command))
		{
			// fire delete event
			BundleManager.getInstance().fireElementDeletedEvent(command);
		}
	}
	
	/**
	 * removeMenu
	 * 
	 * @param command
	 */
	public void removeMenu(MenuElement menu)
	{
		if (this._menus != null && this._menus.remove(menu))
		{
			// fire delete event
			BundleManager.getInstance().fireElementDeletedEvent(menu);
		}
	}
	
	/**
	 * removeSnippet
	 * 
	 * @param snippet
	 */
	public void removeSnippet(SnippetElement snippet)
	{
		if (this._snippets != null && this._snippets.remove(snippet))
		{
			// fire delete event
			BundleManager.getInstance().fireElementDeletedEvent(snippet);
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
	public void setGitRepo(String gitRepo)
	{
		this._gitRepo = gitRepo;
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
	 * toSource
	 * 
	 * @return
	 */
	public void toSource(SourcePrinter printer)
	{
		// open bundle
		printer.printWithIndent("bundle \"").print(this._displayName).println("\" {").increaseIndent(); //$NON-NLS-1$ //$NON-NLS-2$
		
		// show body
		printer.printWithIndent("path: ").println(this._path); //$NON-NLS-1$
		printer.printWithIndent("name: ").println(this._displayName); //$NON-NLS-1$
		printer.printWithIndent("author: ").println(this._author); //$NON-NLS-1$
		printer.printWithIndent("copyright: ").println(this._copyright); //$NON-NLS-1$
		printer.printWithIndent("description: ").println(this._description); //$NON-NLS-1$
		printer.printWithIndent("git: ").println(this._gitRepo); //$NON-NLS-1$
		
		// output commands
		if (this._commands != null)
		{
			for (CommandElement command : this._commands)
			{
				command.toSource(printer);
			}
		}
		
		// output menus
		if (this._menus != null)
		{
			for (MenuElement menu : this._menus)
			{
				menu.toSource(printer);
			}
		}
		
		// output snippets
		if (this._snippets != null)
		{
			for (SnippetElement snippet : this._snippets)
			{
				snippet.toSource(printer);
			}
		}
		
		// close bundle
		printer.decreaseIndent().printlnWithIndent("}"); //$NON-NLS-1$
	}
}
