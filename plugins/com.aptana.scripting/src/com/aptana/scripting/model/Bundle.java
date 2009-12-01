package com.aptana.scripting.model;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.jruby.anno.JRubyMethod;

public class Bundle
{
	private String _path;
	private String _displayName;
	private String _author;
	private String _copyright;
	private String _license;
	private String _licenseUrl;
	private String _gitRepo;
	
	private List<Menu> _menus;
	private List<Snippet> _snippets;
	private List<Command> _commands;

	/**
	 * Bundle
	 * 
	 * @param path
	 */
	public Bundle(String path)
	{
		this._path = path;
	}

	/**
	 * addCommand
	 * 
	 * @param command
	 */
	@JRubyMethod(name = "add_command")
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
	 * addMenu
	 * 
	 * @param snippet
	 */
	@JRubyMethod(name = "add_menu")
	public void addMenu(Menu menu)
	{
		if (menu != null)
		{
			if (this._menus == null)
			{
				this._menus = new ArrayList<Menu>();
			}
			
			this._menus.add(menu);
		}
	}

	/**
	 * addSnippet
	 * 
	 * @param snippet
	 */
	@JRubyMethod(name = "add_snippet")
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
	 * findCommandsFromPath
	 * 
	 * @param path
	 * @return
	 */
	public Command[] findCommandsFromPath(String path)
	{
		List<Command> result = new ArrayList<Command>();
		
		if (path != null && path.length() > 0 && this._commands != null)
		{
			for (Command command : this._commands)
			{
				if (path.equals(command.getPath()))
				{
					result.add(command);
				}
			}
		}
		
		return result.toArray(new Command[result.size()]);
	}
	
	/**
	 * findMenusFromPath
	 * 
	 * @param path
	 * @return
	 */
	public Menu[] findMenusFromPath(String path)
	{
		List<Menu> result = new ArrayList<Menu>();
		
		if (path != null && path.length() > 0 && this._menus != null)
		{
			for (Menu menu : this._menus)
			{
				if (path.equals(menu.getPath()))
				{
					result.add(menu);
				}
			}
		}
		
		return result.toArray(new Menu[result.size()]);
	}
	
	/**
	 * findSnippetsFromPath
	 * 
	 * @param path
	 * @return
	 */
	public Snippet[] findSnippetsFromPath(String path)
	{
		List<Snippet> result = new ArrayList<Snippet>();
		
		if (path != null && path.length() > 0 && this._snippets != null)
		{
			for (Snippet snippet : this._snippets)
			{
				if (path.equals(snippet.getPath()))
				{
					result.add(snippet);
				}
			}
		}
		
		return result.toArray(new Snippet[result.size()]);
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
	 * getCommands
	 * 
	 * @return
	 */
	public Command[] getCommands()
	{
		Command[] result = BundleManager.NO_COMMANDS;

		if (this._commands != null && this._commands.size() > 0)
		{
			result = this._commands.toArray(new Command[this._commands.size()]);
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
	 * getDisplayName
	 * 
	 * @return
	 */
	@JRubyMethod(name = "display_name")
	public String getDisplayName()
	{
		return this._displayName;
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
	public Menu[] getMenus()
	{
		Menu[] result = BundleManager.NO_MENUS;
		
		if (this._menus != null && this._menus.size() > 0)
		{
			result = this._menus.toArray(new Menu[this._menus.size()]);
		}
		
		return result;
	}
	
	/**
	 * getPath
	 * 
	 * @return
	 */
	@JRubyMethod(name = "path")
	public String getPath()
	{
		return this._path;
	}

	/**
	 * getSnippets
	 * 
	 * @return
	 */
	public Snippet[] getSnippets()
	{
		Snippet[] result = BundleManager.NO_SNIPPETS;

		if (this._snippets != null && this._snippets.size() > 0)
		{
			result = this._snippets.toArray(new Snippet[this._snippets.size()]);
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
			for (Command command : this._commands)
			{
				String newCommandPath = path + command.getPath().substring(oldPathLength);

				command.setPath(newCommandPath);
			}
		}

		// update snippet paths
		if (this._snippets != null)
		{
			for (Snippet snippet : this._snippets)
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
	public void removeCommand(Command command)
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
	public void removeMenu(Menu menu)
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
	public void removeSnippet(Snippet snippet)
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
	 * setDisplayName
	 * 
	 * @param displayName
	 */
	@JRubyMethod(name = "display_name=")
	public void setDisplayName(String displayName)
	{
		this._displayName = displayName;
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
	public String toSource()
	{
		SourcePrinter printer = new SourcePrinter();
		
		// open bundle
		printer.printWithIndent("bundle \"").print(this._displayName).println("\" {").increaseIndent(); //$NON-NLS-1$ //$NON-NLS-2$
		
		// show body
		printer.printWithIndent("path: ").println(this._path); //$NON-NLS-1$
		
		// output commands
		if (this._commands != null)
		{
			for (Command command : this._commands)
			{
				command.toSource(printer);
			}
		}
		
		// output menus
		if (this._menus != null)
		{
			for (Menu menu : this._menus)
			{
				menu.toSource(printer);
			}
		}
		
		// output snippets
		if (this._snippets != null)
		{
			for (Snippet snippet : this._snippets)
			{
				snippet.toSource(printer);
			}
		}
		
		// close bundle
		printer.decreaseIndent().printlnWithIndent("}"); //$NON-NLS-1$
		
		return printer.toString();
	}
}
