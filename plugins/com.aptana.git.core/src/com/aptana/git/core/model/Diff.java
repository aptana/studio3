package com.aptana.git.core.model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Diff
{

	private static final String DEV_NULL = "/dev/null";

	private boolean isBinary;
	private String oldName;
	private String newName;
	private String content;
	private boolean hasModeChange;
	private String oldMode;
	private String newMode;
	private GitCommit commit;

	private Diff(GitCommit commit, boolean binary, String startname, String endname, String diffContent,
			boolean modeChange, String oldMode, String newMode)
	{
		this.commit = commit;
		this.isBinary = binary;
		this.oldName = startname;
		this.newName = endname;
		this.content = diffContent;
		this.hasModeChange = modeChange;
		this.oldMode = oldMode;
		this.newMode = newMode;
	}

	private static List<Diff> parse(GitCommit commit, String content)
	{
		boolean header = false;
		boolean binary = false;
		boolean mode_change = false;
		boolean readPrologue = false;
		String startname = "";
		String endname = "";
		String new_mode = "";
		String old_mode = "";
		String diffContent = "";
		List<Diff> files = new ArrayList<Diff>();

		String[] lines = content.split("\r|\n|\r\n");
		for (int lineno = 0; lineno < lines.length; lineno++)
		{
			String l = lines[lineno];
			if (l == null || l.length() == 0)
				continue;
			char firstChar = l.charAt(0);

			if (firstChar == 'd' && l.charAt(1) == 'i')
			{ // "diff", i.e. new file, we have to reset everything
				header = true; // diff always starts with a header

				// Finish last file
				if (!readPrologue)
					readPrologue = true;
				else
					files
							.add(new Diff(commit, binary, startname, endname, diffContent, mode_change, old_mode,
									new_mode));
				diffContent = "";
				startname = "";
				endname = "";

				old_mode = "";
				new_mode = "";
				binary = false;
				mode_change = false;

				Matcher m = Pattern.compile("^diff --git (a\\/)+(.*) (b\\/)+(.*)$").matcher(l);
				if (m.find())
				{ // there are cases when we need to capture filenames from
					startname = m.group(2); // the diff line, like with mode-changes.
					endname = m.group(4); // this can get overwritten later if there is a diff or if
				} // the file is binary

				continue;
			}

			if (header)
			{
				if (firstChar == 'n')
				{
					Matcher m = Pattern.compile("^new file mode .*$").matcher(l);
					if (m.find())
						startname = DEV_NULL;

					m = Pattern.compile("^new mode (.*)$").matcher(l);
					if (m.find())
					{
						mode_change = true;
						new_mode = m.group(1);
					}
					continue;
				}
				if (firstChar == 'o')
				{
					Matcher m = Pattern.compile("^old mode (.*)$").matcher(l);
					if (m.find())
					{
						mode_change = true;
						old_mode = m.group(1);
					}
					continue;
				}

				if (firstChar == 'd')
				{
					Matcher m = Pattern.compile("^deleted file mode .*$").matcher(l);
					if (m.find())
						endname = DEV_NULL;
					continue;
				}
				if (firstChar == '-')
				{
					Matcher m = Pattern.compile("^--- (a\\/)?(.*)$").matcher(l);
					if (m.find())
						startname = m.group(2);
					continue;
				}
				if (firstChar == '+')
				{
					Matcher m = Pattern.compile("^\\+\\+\\+ (b\\/)?(.*)$").matcher(l);
					if (m.find())
						endname = m.group(2);
					continue;
				}
				// If it is a complete rename, we don't know the name yet
				// We can figure this out from the 'rename from.. rename to.. thing
				if (firstChar == 'r')
				{
					Matcher m = Pattern.compile("^rename (from|to) (.*)$").matcher(l);
					if (m.find())
					{
						if (m.group(1).equals("from"))
							startname = m.group(2);
						else
							endname = m.group(2);
					}
					continue;
				}
				if (firstChar == 'B') // "Binary files .. and .. differ"
				{
					binary = true;
					// We might not have a diff from the binary file if it's new.
					// So, we use a regex to figure that out

					Matcher m = Pattern.compile("^Binary files (a\\/)?(.*) and (b\\/)?(.*) differ$").matcher(l);
					if (m.find())
					{
						startname = m.group(2);
						endname = m.group(4);
					}
				}

				// Finish the header
				if (firstChar == '@')
					header = false;
				else
					continue;
			}

			// sindex = "index=" + lindex.toString() + " ";
			// if (firstChar == "+") {
			// // Highlight trailing whitespace
			// if (m = l.match(/\s+$/))
			// l = l.replace(/\s+$/, "<span class='whitespace'>" + m + "</span>");
			//
			// line1 += "\n";
			// line2 += ++hunk_start_line_2 + "\n";
			// diffContent += "<div " + sindex + "class='addline'>" + l + "</div>";
			// } else if (firstChar == "-") {
			// line1 += ++hunk_start_line_1 + "\n";
			// line2 += "\n";
			// diffContent += "<div " + sindex + "class='delline'>" + l + "</div>";
			// } else if (firstChar == "@") {
			// if (header) {
			// header = false;
			// }
			//
			// if (m = l.match(/@@ \-([0-9]+),?\d* \+(\d+),?\d* @@/))
			// {
			// hunk_start_line_1 = parseInt(m[1]) - 1;
			// hunk_start_line_2 = parseInt(m[2]) - 1;
			// }
			// line1 += "...\n";
			// line2 += "...\n";
			// diffContent += "<div " + sindex + "class='hunkheader'>" + l + "</div>";
			// } else if (firstChar == " ") {
			// line1 += ++hunk_start_line_1 + "\n";
			// line2 += ++hunk_start_line_2 + "\n";
			// diffContent += "<div " + sindex + "class='noopline'>" + l + "</div>";
			// }
			// lindex++;
			diffContent += l + "\n";
		}
		files.add(new Diff(commit, binary, startname, endname, diffContent, mode_change, old_mode, new_mode));
		return files;
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
		return parse(gitCommit, GitExecutable.instance().outputForCommand(gitCommit.repository().workingDirectory(),
				"show", "--pretty=raw", "-M", "--no-color", gitCommit.sha()));
	}

}
