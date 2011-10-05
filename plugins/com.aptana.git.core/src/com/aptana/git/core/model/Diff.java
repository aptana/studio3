/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.core.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IStatus;

import com.aptana.core.logging.IdeLog;
import com.aptana.git.core.GitPlugin;

public class Diff
{

	private static final Pattern BINARY_FILES_DIFFER_PATTERN = Pattern
			.compile("^Binary files (a\\/)?(.*) and (b\\/)?(.*) differ$"); //$NON-NLS-1$
	private static final Pattern RENAME_PATTERN = Pattern.compile("^rename (from|to) (.*)$"); //$NON-NLS-1$
	private static final Pattern PLUS_PATTERN = Pattern.compile("^\\+\\+\\+ (b\\/)?(.*)$"); //$NON-NLS-1$
	private static final Pattern MINUS_PATTERN = Pattern.compile("^--- (a\\/)?(.*)$"); //$NON-NLS-1$
	private static final Pattern DELETED_FILE_MODE_PATTERN = Pattern.compile("^deleted file mode .*$"); //$NON-NLS-1$
	private static final Pattern OLD_MODE_PATTERN = Pattern.compile("^old mode (.*)$"); //$NON-NLS-1$
	private static final Pattern NEW_MODE_PATTERN = Pattern.compile("^new mode (.*)$"); //$NON-NLS-1$
	private static final Pattern NEW_FILE_MODE_PATTERN = Pattern.compile("^new file mode .*$"); //$NON-NLS-1$
	private static final Pattern DIFF_GIT_PATTERN = Pattern.compile("^diff --git (a\\/)+(.*) (b\\/)+(.*)$"); //$NON-NLS-1$

	private static final String DEV_NULL = "/dev/null"; //$NON-NLS-1$

	private boolean isBinary;
	private String oldName;
	private String newName;
	private GitCommit commit;

	private Diff(GitCommit commit, boolean binary, String startname, String endname)
	{
		this.commit = commit;
		this.isBinary = binary;
		this.oldName = startname;
		this.newName = endname;
	}

	private static List<Diff> parse(GitCommit commit, Reader content) throws IOException
	{
		long start = System.currentTimeMillis();

		boolean header = false;
		boolean binary = false;
		boolean readPrologue = false;
		String startname = ""; //$NON-NLS-1$
		String endname = ""; //$NON-NLS-1$
		List<Diff> files = new ArrayList<Diff>();

		BufferedReader buffReader = new BufferedReader(content);

		try
		{
			String l = null;
			while ((l = buffReader.readLine()) != null) // $codepro.audit.disable assignmentInCondition
			{
				if (l.length() == 0)
				{
					continue;
				}
				char firstChar = l.charAt(0);
				if (firstChar == 'd' && l.charAt(1) == 'i')
				{ // "diff", i.e. new file, we have to reset everything
					header = true; // diff always starts with a header

					// Finish last file
					if (!readPrologue)
					{
						readPrologue = true;
					}
					else
					{
						files.add(new Diff(commit, binary, startname, endname));
					}
					startname = ""; //$NON-NLS-1$
					endname = ""; //$NON-NLS-1$
					binary = false;

					Matcher m = DIFF_GIT_PATTERN.matcher(l);
					if (m.find())
					{ // there are cases when we need to capture filenames from
						startname = m.group(2); // the diff line, like with mode-changes.
						endname = m.group(4); // this can get overwritten later if there is a diff or if
					} // the file is binary

					continue;
				}

				if (!header)
				{
					continue;
				}

				switch (firstChar)
				{
					case 'n':
						Matcher m = NEW_FILE_MODE_PATTERN.matcher(l);
						if (m.find())
						{
							startname = DEV_NULL;
						}

						m = NEW_MODE_PATTERN.matcher(l);
						break;
					case 'o':
						m = OLD_MODE_PATTERN.matcher(l);
						break;
					case 'd':
						m = DELETED_FILE_MODE_PATTERN.matcher(l);
						if (m.find())
						{
							endname = DEV_NULL;
						}
						break;
					case '-':
						m = MINUS_PATTERN.matcher(l);
						if (m.find())
						{
							startname = m.group(2);
						}
						break;
					case '+':
						m = PLUS_PATTERN.matcher(l);
						if (m.find())
						{
							endname = m.group(2);
						}
						break;
					case 'r':
						// If it is a complete rename, we don't know the name yet
						// We can figure this out from the 'rename from.. rename to.. thing
						m = RENAME_PATTERN.matcher(l);
						if (m.find())
						{
							if (m.group(1).equals("from")) //$NON-NLS-1$
							{
								startname = m.group(2);
							}
							else
							{
								endname = m.group(2);
							}
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
			files.add(new Diff(commit, binary, startname, endname));
			log(MessageFormat.format(
					"Took {0}ms to parse out {1} diffs", (System.currentTimeMillis() - start), files.size())); //$NON-NLS-1$
		}
		finally
		{
			buffReader.close();
		}
		return files;
	}

	private static void log(String string)
	{
		if (GitPlugin.getDefault() != null)
		{
			IdeLog.logInfo(GitPlugin.getDefault(), string);
		}
		else
		{
			System.out.println(string);
		}
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
		{
			return oldName;
		}
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
	static List<Diff> create(GitCommit gitCommit)
	{
		try
		{
			IStatus result = gitCommit.repository().executeWithPromptHandling(GitRepository.ReadWrite.READ,
					"show", "--pretty=raw", "-M", "--no-color", gitCommit.sha()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			return parse(gitCommit, new StringReader(result.getMessage())); // $codepro.audit.disable closeWhereCreated
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
