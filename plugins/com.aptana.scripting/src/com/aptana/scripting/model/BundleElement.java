package com.aptana.scripting.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BundleElement extends AbstractElement
{
	private static final String BUNDLE_DIRECTORY_SUFFIX = ".rr-bundle"; //$NON-NLS-1$
	
	private String _author;
	private String _copyright;
	private String _description;
	private String _license;
	private String _licenseUrl;
	private String _gitRepo;
	
	private File _bundleDirectory;
	private BundleScope _bundleScope;
	private List<MenuElement> _menus;
	private List<CommandElement> _commands;

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
			if (this._commands == null)
			{
				this._commands = new ArrayList<CommandElement>();
			}

			// NOTE: Should we prevent the same element from being added twice?
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
			
			// NOTE: Should we prevent the same element from being added twice?
			this._menus.add(menu);
			
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
		String result = super.getDisplayName();
		
		if (result == null || result.length() == 0)
		{
			String path = this.getPath();
			
			if (path != null && path.length() > 0)
			{
				File file = new File(path);
				
				result = file.getName();
				
				if (result.endsWith(BUNDLE_DIRECTORY_SUFFIX))
				{
					result = result.substring(0, result.length() - BUNDLE_DIRECTORY_SUFFIX.length());
				}
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
		
		return displayName != null && displayName.length() > 0;
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
			AbstractElement.unregisterElement(command);
			
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
		if (this._menus != null && this._menus.remove(menu))
		{
			AbstractElement.unregisterElement(menu);
			
			menu.removeChildren();
			
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
		printer.printWithIndent("bundle \"").print(this.getDisplayName()).println("\" {").increaseIndent(); //$NON-NLS-1$ //$NON-NLS-2$
		
		// show body
		printer.printWithIndent("bundle_scope: ").println(this._bundleScope.toString()); //$NON-NLS-1$
		printer.printWithIndent("path: ").println(this.getPath()); //$NON-NLS-1$
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
		
		// close bundle
		printer.decreaseIndent().printlnWithIndent("}"); //$NON-NLS-1$
	}
}
