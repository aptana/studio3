package com.aptana.scripting.model;

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
	@JRubyMethod(name="add_snippet")
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
}
