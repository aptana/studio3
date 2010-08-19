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
}
