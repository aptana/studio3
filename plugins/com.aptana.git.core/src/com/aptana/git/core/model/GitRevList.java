package com.aptana.git.core.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.aptana.git.core.GitPlugin;

public class GitRevList
{
	private GitRepository repository;
	private String lastSha;
	private List<GitCommit> commits;

	public GitRevList(GitRepository repo)
	{
		repository = repo;
	}

	void readCommitsForce(boolean force)
	{
		// We use refparse to get the commit sha that we will parse. That way,
		// we can check if the current branch is the same as the previous one
		// and in that case we don't have to reload the revision list.

		// If no branch is selected, don't do anything
		if (repository.currentBranch == null)
			return;

		GitRevSpecifier newRev = repository.currentBranch;
		String newSha = null;
		if (!force && newRev != null && newRev.isSimpleRef())
		{
			newSha = repository.parseReference(newRev.simpleRef());
			if (newSha.equals(lastSha))
				return;
		}
		lastSha = newSha;

		final GitRevSpecifier toWalk = newRev;
		Job job = new Job("walk revision list")
		{
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				walkRevisionListWithSpecifier(toWalk, -1);
				return Status.OK_STATUS;
			}
		};
		job.setSystem(true);
		job.schedule();
	}

	public void walkRevisionListWithSpecifier(GitRevSpecifier rev, int max)
	{
		long start = System.currentTimeMillis();
		List<GitCommit> revisions = new ArrayList<GitCommit>();

		// GitGrapher g = new GitGrapher(repository);

		String formatString = "--pretty=format:%H\01%e\01%an\01%s\01%b\01%P\01%at";
		boolean showSign = rev.hasLeftRight();

		if (showSign)
			formatString += "\01%m";

		List<String> arguments = new ArrayList<String>();
		arguments.add("log");
		arguments.add("-z");
		arguments.add("--early-output");
		arguments.add("--topo-order");
		arguments.add("--children");
		if (max > 0)
			arguments.add("-" + max); // only last N revs
		arguments.add(formatString);

		if (rev == null)
			arguments.add("HEAD");
		else
			arguments.addAll(rev.parameters());

		String directory = rev.getWorkingDirectory() != null ? rev.getWorkingDirectory() : repository
				.workingDirectory();

		try
		{
			Process p = GitExecutable.instance().run(directory, arguments.toArray(new String[arguments.size()]));
			InputStream stream = p.getInputStream();

			int num = 0;
			while (true)
			{
				String sha = getline(stream, '\1');
				if (sha == null)
					break;

				// We reached the end of some temporary output. Show what we have
				// until now, and then start again. The sha of the next thing is still
				// in this buffer. So, we use a substring of current input.
				if (sha.charAt(1) == 'i') // Matches 'Final output'
				{
					num = 0;
					setCommits(revisions, false);
					// g = new GitGrapher(repository);
					revisions = new ArrayList<GitCommit>();

					// If the length is < 40, then there are no commits.. quit now
					if (sha.length() < 40)
						break;

					int startIndex = sha.length() - 40;
					sha = sha.substring(startIndex, startIndex + 40);
				}

				// From now on, 1.2 seconds
				String encoding = getline(stream, '\1', "UTF-8");
				// String encoding = null;
				// if (encoding_str.length() != 0)
				// {
				// if (encodingMap.hasKey(encoding_str)) {
				// encoding = encodingMap.get(encoding_str);
				// } else {
				// encoding =
				// CFStringConvertEncodingToNSStringEncoding(CFStringConvertIANACharSetNameToEncoding((CFStringRef)[NSString
				// stringWithUTF8String:encoding_str.c_str()]));
				// encodingMap.put(encoding_str, encoding);
				// }
				// }

				GitCommit newCommit = new GitCommit(repository, sha);

				String author = getline(stream, '\1', encoding);
				String subject = getline(stream, '\1', encoding);
				String body = getline(stream, '\1', encoding);
				String parentString = getline(stream, '\1');
				if (parentString != null && parentString.length() != 0)
				{
					if (((parentString.length() + 1) % 41) != 0)
					{
						GitPlugin.logError("invalid parents: " + parentString.length(), null);
						continue;
					}
					int nParents = (parentString.length() + 1) / 41;
					List<String> parents = new ArrayList<String>(nParents);
					for (int parentIndex = 0; parentIndex < nParents; ++parentIndex)
					{
						int stringIndex = parentIndex * 41;
						parents.add(parentString.substring(stringIndex, stringIndex + 40));
					}

					newCommit.setParents(parents);
				}

				long time = readLong(stream); // read 10 chars as a string and parse into a long

				newCommit.setSubject(subject);
				newCommit.setComment(body);
				newCommit.setAuthor(author);
				newCommit.setTimestamp(time);

				if (showSign)
				{
					stream.read(); // Remove separator
					char c = (char) stream.read();
					if (c != '>' && c != '<' && c != '^' && c != '-')
						GitPlugin.logError("Error loading commits: sign not correct", null);
					newCommit.setSign(c);
				}

				int read = stream.read();
				if (read != 0 && read != -1)
					System.out.println("Error");

				revisions.add(newCommit);
				// g.decorateCommit(newCommit);

				if (read == -1)
					break;

				if (++num % 1000 == 0)
					setCommits(revisions, false);
			}

			long duration = System.currentTimeMillis() - start;
			logInfo("Loaded " + num + " commits in " + duration + " ms");
			// Make sure the commits are stored before exiting.
			setCommits(revisions, true);
			p.waitFor();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void logInfo(String string)
	{
		if (GitPlugin.getDefault() != null)
			GitPlugin.logInfo(string);
		else
			System.out.println(string);
	}

	private long readLong(InputStream stream)
	{
		StringBuilder builder = new StringBuilder();
		while (true)
		{
			try
			{
				int read = stream.read();
				if (read == -1)
					break;
				builder.append((char) read);
				if (builder.length() == 10)
					break;
			}
			catch (IOException e)
			{
				break;
			}
		}
		// Since we get time in seconds since epoch, not ms we need to multiply by 1000
		long time = Long.parseLong(builder.toString()) * 1000;
		// HACK for some reason my times are 5 minutes off the console/GitX. Adjust 5 mins
		return time + (5 * 60 * 1000);
	}

	private void setCommits(List<GitCommit> revisions, boolean b)
	{
		if (this.commits == null)
			this.commits = new ArrayList<GitCommit>();
		this.commits.addAll(revisions);
	}

	private String getline(InputStream stream, char c)
	{
		byte[] bytes = read(stream, c);
		if (bytes == null || bytes.length == 0)
			return null;
		return new String(bytes);
	}

	private String getline(InputStream stream, char c, String encoding) throws UnsupportedEncodingException
	{
		if (encoding == null || encoding.length() == 0)
			return getline(stream, c);
		byte[] bytes = read(stream, c);
		return new String(bytes, encoding);
	}

	private byte[] read(InputStream stream, char c)
	{
		List<Byte> list = new ArrayList<Byte>();
		while (true)
		{
			try
			{
				int read = stream.read();
				if (read == -1)
					break;
				char readC = (char) read;
				if (readC == c)
					break;
				list.add((byte) read);
			}
			catch (IOException e)
			{
				break;
			}
		}
		byte[] bytes = new byte[list.size()];
		int i = 0;
		for (Byte by : list)
		{
			bytes[i++] = by.byteValue();
		}
		return bytes;
	}

	public List<GitCommit> getCommits()
	{
		return Collections.unmodifiableList(this.commits);
	}
}
