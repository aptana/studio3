package com.aptana.scripting.model;

import java.util.ArrayList;
import java.util.List;

import org.jruby.anno.JRubyMethod;

public class BundleElement extends AbstractElement
{
	private String _author;
	private String _copyright;
	private String _description;
	private String _license;
	private String _licenseUrl;
	private String _gitRepo;
	
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
	}

	/**
	 * addCommand
	 * 
	 * @param command
	 */
	@JRubyMethod(name = "add_command")
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
		}
	}
	
	/**
	 * addMenu
	 * 
	 * @param snippet
	 */
	@JRubyMethod(name = "add_menu")
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
		}
	}

	/**
	 * addSnippet
	 * 
	 * @param snippet
	 */
	@JRubyMethod(name = "add_snippet")
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
	 * getAuthor
	 * 
	 * @return
	 */
	@JRubyMethod(name = "author")
	public String getAuthor()
	{
		return this._author;
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
	@JRubyMethod(name = "copyright")
	public String getCopyright()
	{
		return this._copyright;
	}

	/**
	 * getDescription
	 * 
	 * @return
	 */
	@JRubyMethod(name = "description")
	public String getDescription()
	{
		return this._description;
	}

	/**
	 * getGitRepo
	 * 
	 * @return
	 */
	@JRubyMethod(name = "git_repo")
	public String getGitRepo()
	{
		return this._gitRepo;
	}

	/**
	 * getLicense
	 * 
	 * @return
	 */
	@JRubyMethod(name = "license")
	public String getLicense()
	{
		return this._license;
	}

	/**
	 * getLicenseUrl
	 * 
	 * @return
	 */
	@JRubyMethod(name = "license_url")
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
	@JRubyMethod(name = "remove_command")
	public void removeCommand(CommandElement command)
	{
		if (this._commands != null)
		{
			this._commands.remove(command);
		}
	}
	
	/**
	 * removeMenu
	 * 
	 * @param command
	 */
	@JRubyMethod(name = "remove_menu")
	public void removeMenu(MenuElement menu)
	{
		if (this._menus != null)
		{
			this._menus.remove(menu);
		}
	}
	
	/**
	 * removeSnippet
	 * 
	 * @param snippet
	 */
	@JRubyMethod(name = "remove_snippet")
	public void removeSnippet(SnippetElement snippet)
	{
		if (this._snippets != null)
		{
			this._snippets.remove(snippet);
		}
	}
	
	/**
	 * setAuthor
	 * 
	 * @param author
	 */
	@JRubyMethod(name = "author=")
	public void setAuthor(String author)
	{
		this._author = author;
	}

	/**
	 * setCopyright
	 * 
	 * @param copyright
	 */
	@JRubyMethod(name = "copyright=")
	public void setCopyright(String copyright)
	{
		this._copyright = copyright;
	}
	
	/**
	 * setDescription
	 * 
	 * @param description
	 */
	@JRubyMethod(name = "description=")
	public void setDescription(String description)
	{
		this._description = description;
	}

	/**
	 * setGitRepo
	 * 
	 * @param gitRepo
	 */
	@JRubyMethod(name = "git_repo=")
	public void setGitRepo(String gitRepo)
	{
		this._gitRepo = gitRepo;
	}

	/**
	 * setLicense
	 * 
	 * @param license
	 */
	@JRubyMethod(name = "license=")
	public void setLicense(String license)
	{
		this._license = license;
	}

	/**
	 * setLicenseUrl
	 * 
	 * @param licenseUrl
	 */
	@JRubyMethod(name = "license_url=")
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
