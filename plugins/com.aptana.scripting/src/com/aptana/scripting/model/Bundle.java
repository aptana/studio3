package com.aptana.scripting.model;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jruby.anno.JRubyMethod;

public class Bundle
{
	private static final Snippet[] NO_SNIPPETS = new Snippet[0];
	private static final Command[] NO_COMMANDS = new Command[0];

	private String _path;
	private String _displayName;
	private String _author;
	private String _copyright;
	private String _license;
	private String _licenseUrl;
	private String _gitRepo;
	
	private List<Snippet> _snippets;
	private List<Command> _commands;
	private Map<String,Snippet> _snippetsByPath;
	private Map<String,Command> _commandsByPath;

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
			
			if (this._commandsByPath == null)
			{
				this._commandsByPath = new HashMap<String, Command>();
			}

			this._commands.add(command);
			this._commandsByPath.put(command.getPath(), command);
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
			
			if (this._snippetsByPath == null)
			{
				this._snippetsByPath = new HashMap<String, Snippet>();
			}

			this._snippets.add(snippet);
			this._snippetsByPath.put(snippet.getPath(), snippet);
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
	 * getCommands
	 * 
	 * @return
	 */
	public Command[] getCommands()
	{
		Command[] result = NO_COMMANDS;

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
		Snippet[] result = NO_SNIPPETS;

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

				this._commandsByPath.remove(command.getPath());
				command.setPath(newCommandPath);
				this._commandsByPath.put(newCommandPath, command);
			}
		}

		// update snippet paths
		if (this._snippets != null)
		{
			for (Snippet snippet : this._snippets)
			{
				String newSnippetPath = path + snippet.getPath().substring(oldPathLength);

				this._snippetsByPath.remove(snippet.getPath());
				snippet.setPath(newSnippetPath);
				this._snippetsByPath.put(newSnippetPath, snippet);
			}
		}
	}

	/**
	 * removeCommand
	 * 
	 * @param path
	 */
	@JRubyMethod(name = "remove_command")
	public void removeCommand(String path)
	{
		Command command = this._commandsByPath.get(path);
		
		if (command != null)
		{
			this._commandsByPath.remove(path);
			this._commands.remove(command);
		}
	}
	
	/**
	 * removeSnippet
	 * 
	 * @param path
	 */
	@JRubyMethod(name = "remove_snippet")
	public void removeSnippet(String path)
	{
		Snippet snippet = this._snippetsByPath.get(path);
		
		if (snippet != null)
		{
			this._snippetsByPath.remove(path);
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
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		
		// open bundle
		writer.append("bundle \"").append(this._displayName).println("\" {");
		
		// show body
		writer.append("  path: ").println(this._path);
		
		// output commands
		if (this._commands != null)
		{
			for (Command command : this._commands)
			{
				writer.print(command.toString());
			}
		}
		
		// output snippets
		if (this._snippets != null)
		{
			for (Snippet snippet : this._snippets)
			{
				writer.print(snippet.toString());
			}
		}
		
		// close bundle
		writer.print("}");
		
		return sw.toString();
	}
}
