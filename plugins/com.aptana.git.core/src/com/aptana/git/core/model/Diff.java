package com.aptana.git.core.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aptana.git.core.GitPlugin;

public class Diff
{

	private static final Pattern BINARY_FILES_DIFFER_PATTERN = Pattern
			.compile("^Binary files (a\\/)?(.*) and (b\\/)?(.*) differ$");
	private static final Pattern RENAME_PATTERN = Pattern.compile("^rename (from|to) (.*)$");
	private static final Pattern PLUS_PATTERN = Pattern.compile("^\\+\\+\\+ (b\\/)?(.*)$");
	private static final Pattern MINUS_PATTERN = Pattern.compile("^--- (a\\/)?(.*)$");
	private static final Pattern DELETED_FILE_MODE_PATTERN = Pattern.compile("^deleted file mode .*$");
	private static final Pattern OLD_MODE_PATTERN = Pattern.compile("^old mode (.*)$");
	private static final Pattern NEW_MODE_PATTERN = Pattern.compile("^new mode (.*)$");
	private static final Pattern NEW_FILE_MODE_PATTERN = Pattern.compile("^new file mode .*$");
	private static final Pattern DIFF_GIT_PATTERN = Pattern.compile("^diff --git (a\\/)+(.*) (b\\/)+(.*)$");

	private static final String DEV_NULL = "/dev/null";

	private boolean isBinary;
	private String oldName;
	private String newName;
	private boolean hasModeChange;
	private String oldMode;
	private String newMode;
	private GitCommit commit;

	private Diff(GitCommit commit, boolean binary, String startname, String endname, boolean modeChange,
			String oldMode, String newMode)
	{
		this.commit = commit;
		this.isBinary = binary;
		this.oldName = startname;
		this.newName = endname;
		this.hasModeChange = modeChange;
		this.oldMode = oldMode;
		this.newMode = newMode;
	}

	private static List<Diff> parse(GitCommit commit, Reader content) throws IOException
	{
		long start = System.currentTimeMillis();

		boolean header = false;
		boolean binary = false;
		boolean mode_change = false;
		boolean readPrologue = false;
		String startname = "";
		String endname = "";
		String new_mode = "";
		String old_mode = "";
		List<Diff> files = new ArrayList<Diff>();

		BufferedReader buffReader = new BufferedReader(content);

		String l = null;
		while ((l = buffReader.readLine()) != null)
		{
			if (l.length() == 0)
				continue;
			char firstChar = l.charAt(0);
			if (firstChar == 'd' && l.charAt(1) == 'i')
			{ // "diff", i.e. new file, we have to reset everything
				header = true; // diff always starts with a header

				// Finish last file
				if (!readPrologue)
					readPrologue = true;
				else
					files.add(new Diff(commit, binary, startname, endname, mode_change, old_mode, new_mode));
				startname = "";
				endname = "";
				old_mode = "";
				new_mode = "";
				binary = false;
				mode_change = false;

				Matcher m = DIFF_GIT_PATTERN.matcher(l);
				if (m.find())
				{ // there are cases when we need to capture filenames from
					startname = m.group(2); // the diff line, like with mode-changes.
					endname = m.group(4); // this can get overwritten later if there is a diff or if
				} // the file is binary

				continue;
			}

			if (!header)
				continue;

			switch (firstChar)
			{
				case 'n':
					Matcher m = NEW_FILE_MODE_PATTERN.matcher(l);
					if (m.find())
						startname = DEV_NULL;

					m = NEW_MODE_PATTERN.matcher(l);
					if (m.find())
					{
						mode_change = true;
						new_mode = m.group(1);
					}
					break;
				case 'o':
					m = OLD_MODE_PATTERN.matcher(l);
					if (m.find())
					{
						mode_change = true;
						old_mode = m.group(1);
					}
					break;
				case 'd':
					m = DELETED_FILE_MODE_PATTERN.matcher(l);
					if (m.find())
						endname = DEV_NULL;
					break;
				case '-':
					m = MINUS_PATTERN.matcher(l);
					if (m.find())
						startname = m.group(2);
					break;
				case '+':
					m = PLUS_PATTERN.matcher(l);
					if (m.find())
						endname = m.group(2);
					break;
				case 'r':
					// If it is a complete rename, we don't know the name yet
					// We can figure this out from the 'rename from.. rename to.. thing
					m = RENAME_PATTERN.matcher(l);
					if (m.find())
					{
						if (m.group(1).equals("from"))
							startname = m.group(2);
						else
							endname = m.group(2);
					}
					break;
				case 'B':
					binary = true;
					// We might not have a diff from the binary file if it's new.
					// So, we use a regex to figure that out
					m = BINARY_FILES_DIFFER_PATTERN.matcher(l);
					if (m.find())
					{
						startname = m.group(2);
						endname = m.group(4);
					}
					break;
				case '@': // Finish the header
					header = false;
					break;
				default:
					break;
			}
		}
		files.add(new Diff(commit, binary, startname, endname, mode_change, old_mode, new_mode));
		log("Took " + (System.currentTimeMillis() - start) + "ms to parse out " + files.size() + " diffs");
		return files;
	}

	private static void log(String string)
	{
		if (GitPlugin.getDefault() != null)
			GitPlugin.logInfo(string);
		else
			System.out.println(string);
	}

	public boolean fileCreated()
	{
		return oldName.equals(DEV_NULL);
	}

	public boolean fileDeleted()
	{
		return newName.equals(DEV_NULL);
	}

	public String fileName()
	{
		if (newName.equals(DEV_NULL))
			return oldName;
		return newName;
	}

	public GitCommit commit()
	{
		return commit;
	}

	public String oldName()
	{
		return oldName;
	}

	public String newName()
	{
		return newName;
	}

	/**
	 * Generates a List of Diff Objects, one for each file changed in the commit.
	 * 
	 * @param gitCommit
	 * @return
	 */
	public static List<Diff> create(GitCommit gitCommit)
	{
		try
		{
			String output = GitExecutable.instance().outputForCommand(gitCommit.repository().workingDirectory(),
					"show", "--pretty=raw", "-M", "--no-color", gitCommit.sha());
			return parse(gitCommit, new StringReader(output));
		}
		catch (IOException e)
		{
			return Collections.emptyList();
		}
	}

	public boolean isBinary()
	{
		return isBinary;
	}

	public boolean renamed()
	{
		return !fileCreated() && !fileDeleted() && (!newName.equals(oldName));
	}

}
