package com.aptana.scripting.model;

import java.io.File;

public final class ProjectTemplate
{
	public enum Type
	{
		ALL, RUBY, PHP, WEB, PYTHON
	}

	private final Type fType;
	private final String fName;
	private final String fSourceLocation;
	private final String fDescription;
	private final File fDirectory;

	public ProjectTemplate(String type, String name, String sourceLocation, String description, File directory)
	{
		this(Type.valueOf(type.toUpperCase()), name, sourceLocation, description, directory);
	}

	public ProjectTemplate(Type type, String name, String sourceLocation, String description, File directory)
	{
		fType = type;
		fName = name;
		fSourceLocation = sourceLocation;
		fDescription = description;
		fDirectory = directory;
	}

	public Type getType()
	{
		return fType;
	}

	public String getName()
	{
		return fName;
	}

	public String getSourceLocation()
	{
		return fSourceLocation;
	}

	public String getDescription()
	{
		return fDescription;
	}

	public File getDirectory()
	{
		return fDirectory;
	}

	@Override
	public int hashCode()
	{
		int hash = 31 + getType().hashCode();
		hash = 31 * hash + getName().hashCode();
		hash = 31 * hash + getSourceLocation().hashCode();
		hash = 31 * hash + getDirectory().hashCode();
		return hash;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof ProjectTemplate))
		{
			return false;
		}
		ProjectTemplate other = (ProjectTemplate) obj;
		return getType() == other.getType() && getName().equals(other.getName())
				&& getSourceLocation().equals(other.getSourceLocation()) && getDirectory().equals(other.getDirectory());
	}

	@Override
	public String toString()
	{
		StringBuilder text = new StringBuilder();
		text.append("type = ").append(getType()).append("; "); //$NON-NLS-1$ //$NON-NLS-2$
		text.append("name = ").append(getName()).append("; "); //$NON-NLS-1$ //$NON-NLS-2$
		text.append("source location = ").append(getSourceLocation()).append("; "); //$NON-NLS-1$ //$NON-NLS-2$
		text.append("bundle location = ").append(getDirectory()); //$NON-NLS-1$
		return text.toString();
	}
}
