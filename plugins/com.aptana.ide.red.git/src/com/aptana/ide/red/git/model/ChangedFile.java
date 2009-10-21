package com.aptana.ide.red.git.model;

import org.eclipse.core.runtime.Assert;

public class ChangedFile
{
	
	public enum Status
	{
		NEW, DELETED, MODIFIED
	}

	public ChangedFile(String path)
	{
		this.path = path;
	}
	
	String path;
	Status status;
	boolean hasStagedChanges;
	boolean hasUnstagedChanges;
	String commitBlobSHA;
	String commitBlobMode;

	public String getPath()
	{
		return path;
	}

	public Status getStatus()
	{
		return status;
	}

	public boolean hasStagedChanges()
	{
		return hasStagedChanges;
	}

	public boolean hasUnstagedChanges()
	{
		return hasUnstagedChanges;
	}

	public String getCommitBlobSHA()
	{
		return commitBlobSHA;
	}

	public String getCommitBlobMode()
	{
		return commitBlobMode;
	}
	
	public String indexInfo()
	{
		Assert.isTrue(status == Status.NEW || commitBlobSHA != null, "File is not new, but doesn't have an index entry!");
		if (commitBlobSHA == null)
			return "0 0000000000000000000000000000000000000000\t" + path + "\0";
		else
			return commitBlobMode + " " + commitBlobSHA + "\t" + path + "\0";
	}
}
