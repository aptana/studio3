package ca.mestevens.ios.xcode.parser.models;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import ca.mestevens.ios.xcode.parser.exceptions.FileReferenceDoesNotExistException;
import ca.mestevens.ios.xcode.parser.exceptions.InvalidObjectFormatException;
import ca.mestevens.ios.xcode.parser.utils.ObjectParser;

public class XCodeProject
{

	public Integer getArchiveVersion()
	{
		return archiveVersion;
	}

	public List<String> getClasses()
	{
		return classes;
	}

	public Integer getObjectVersion()
	{
		return objectVersion;
	}

	public List<PBXBuildFile> getBuildFiles()
	{
		return buildFiles;
	}

	public PBXContainerItemProxy getContainerItemProxy()
	{
		return containerItemProxy;
	}

	public List<PBXFileElement> getFileReferences()
	{
		return fileReferences;
	}

	public List<PBXBuildPhase> getFrameworksBuildPhases()
	{
		return frameworksBuildPhases;
	}

	public List<PBXFileElement> getGroups()
	{
		return groups;
	}

	public List<PBXTarget> getNativeTargets()
	{
		return nativeTargets;
	}

	public List<PBXTarget> getAggregateTargets()
	{
		return aggregateTargets;
	}

	public List<PBXTarget> getLegacyTargets()
	{
		return legacyTargets;
	}

	public List<PBXBuildPhase> getResourcesBuildPhases()
	{
		return resourcesBuildPhases;
	}

	public List<PBXBuildPhase> getSourcesBuildPhases()
	{
		return sourcesBuildPhases;
	}

	public PBXTargetDependency getTargetDependency()
	{
		return targetDependency;
	}

	public List<PBXFileElement> getVariantGroups()
	{
		return variantGroups;
	}

	public List<XCBuildConfiguration> getBuildConfigurations()
	{
		return buildConfigurations;
	}

	public List<XCConfigurationList> getConfigurationLists()
	{
		return configurationLists;
	}

	public List<PBXBuildPhase> getAppleScriptBuildPhases()
	{
		return appleScriptBuildPhases;
	}

	public List<PBXBuildPhase> getCopyFilesBuildPhases()
	{
		return copyFilesBuildPhases;
	}

	public List<PBXBuildPhase> getHeadersBuildPhases()
	{
		return headersBuildPhases;
	}

	public List<PBXBuildPhase> getShellScriptBuildPhases()
	{
		return shellScriptBuildPhases;
	}

	public CommentedIdentifier getRootObject()
	{
		return rootObject;
	}

	public Integer archiveVersion;
	public List<String> classes;
	public Integer objectVersion;

	// Objects
	public List<PBXBuildFile> buildFiles;
	public PBXContainerItemProxy containerItemProxy;
	public List<PBXFileElement> fileReferences;
	public List<PBXBuildPhase> frameworksBuildPhases;
	public List<PBXFileElement> groups;
	public List<PBXTarget> nativeTargets;
	public List<PBXTarget> aggregateTargets;
	public List<PBXTarget> legacyTargets;
	public PBXProject project;
	public List<PBXBuildPhase> resourcesBuildPhases;
	public List<PBXBuildPhase> sourcesBuildPhases;
	public PBXTargetDependency targetDependency;
	public List<PBXFileElement> variantGroups;
	public List<XCBuildConfiguration> buildConfigurations;
	public List<XCConfigurationList> configurationLists;
	public List<PBXBuildPhase> appleScriptBuildPhases;
	public List<PBXBuildPhase> copyFilesBuildPhases;
	public List<PBXBuildPhase> headersBuildPhases;
	public List<PBXBuildPhase> shellScriptBuildPhases;

	public CommentedIdentifier rootObject;

	/**
	 * The constructor to construct a representation of the .pbxproj file of the .xcodeproj file
	 *
	 * @param projectPath
	 *            A path to your .xcodeproj or .pbxproj file.
	 * @throws InvalidObjectFormatException
	 *             If something in the project file isn't formatted correctly.
	 */
	public XCodeProject(String projectPath) throws InvalidObjectFormatException
	{
		if (projectPath.endsWith(".xcodeproj"))
		{
			projectPath.concat("/project.pbxproj");
		}
		else if (!projectPath.endsWith(".pbxproj"))
		{
			throw new InvalidObjectFormatException("Invalid xcodeproj or pbxproj path.");
		}
		try
		{
			Path path = Paths.get(projectPath);
			String projectString = new String(Files.readAllBytes(path));
			projectString = projectString.trim();
			projectString = projectString.substring(projectString.indexOf('{') + 1);
			ObjectParser parser = new ObjectParser(projectString);
			String nextObject = parser.parseNextObject();
			while (nextObject != null)
			{
				nextObject = nextObject.substring(0, nextObject.length() - 1);
				String[] splitObject = nextObject.split("=");
				String key = splitObject[0].trim();
				String value = "";
				for (int i = 1; i < splitObject.length; i++)
				{
					if (i > 1)
					{
						value += "=";
					}
					value += splitObject[i];
				}
				value = value.trim();
				if (key.equals("archiveVersion"))
				{
					this.archiveVersion = Integer.valueOf(value);
				}
				else if (key.equals("classes"))
				{
					this.classes = new ArrayList<String>();
				}
				else if (key.equals("objectVersion"))
				{
					this.objectVersion = Integer.valueOf(value);
				}
				else if (key.equals("rootObject"))
				{
					this.rootObject = parser.getCommentedIdentifier(value);
				}
				else if (key.equals("objects"))
				{
					String section = getSection(value, "PBXBuildFile");
					this.buildFiles = getList(PBXBuildFile.class, section);
					section = getSection(value, "PBXContainerItemProxy");
					this.containerItemProxy = getObject(PBXContainerItemProxy.class, section);
					section = getSection(value, "PBXFileReference");
					this.fileReferences = getList(PBXFileElement.class, section);
					section = getSection(value, "PBXFrameworksBuildPhase");
					this.frameworksBuildPhases = getList(PBXBuildPhase.class, section);
					section = getSection(value, "PBXGroup");
					this.groups = getList(PBXFileElement.class, section);
					section = getSection(value, "PBXNativeTarget");
					this.nativeTargets = getList(PBXTarget.class, section);
					section = getSection(value, "PBXAggregateTarget");
					this.aggregateTargets = getList(PBXTarget.class, section);
					section = getSection(value, "PBXLegacyTarget");
					this.legacyTargets = getList(PBXTarget.class, section);
					section = getSection(value, "PBXProject");
					this.project = getObject(PBXProject.class, section);
					section = getSection(value, "PBXResourcesBuildPhase");
					this.resourcesBuildPhases = getList(PBXBuildPhase.class, section);
					section = getSection(value, "PBXSourcesBuildPhase");
					this.sourcesBuildPhases = getList(PBXBuildPhase.class, section);
					section = getSection(value, "PBXTargetDependency");
					this.targetDependency = getObject(PBXTargetDependency.class, section);
					section = getSection(value, "PBXVariantGroup");
					this.variantGroups = getList(PBXFileElement.class, section);
					section = getSection(value, "XCBuildConfiguration");
					this.buildConfigurations = getList(XCBuildConfiguration.class, section);
					section = getSection(value, "XCConfigurationList");
					this.configurationLists = getList(XCConfigurationList.class, section);
					section = getSection(value, "PBXAppleScriptBuildPhase");
					this.appleScriptBuildPhases = getList(PBXBuildPhase.class, section);
					section = getSection(value, "PBXCopyFilesBuildPhase");
					this.copyFilesBuildPhases = getList(PBXBuildPhase.class, section);
					section = getSection(value, "PBXHeadersBuildPhase");
					this.headersBuildPhases = getList(PBXBuildPhase.class, section);
					section = getSection(value, "PBXShellScriptBuildPhase");
					this.shellScriptBuildPhases = getList(PBXBuildPhase.class, section);
				}
				nextObject = parser.parseNextObject();
			}
		}
		catch (Exception ex)
		{
			throw new InvalidObjectFormatException(ex);
		}
	}

	@Override
	public String toString()
	{
		String returnString = "";
		returnString += "// !$*UTF8*$!\n";
		returnString += "{\n";
		returnString += "\tarchiveVersion = " + this.archiveVersion + ";\n";
		returnString += "\tclasses = {\n";
		for (String clazz : classes)
		{
			returnString += "\t" + clazz + "\n";
		}
		returnString += "\t};\n";
		returnString += "\tobjectVersion = " + this.objectVersion + ";\n";
		returnString += "\tobjects = {\n\n";
		if (this.buildFiles != null && this.buildFiles.size() > 0)
		{
			returnString += "/* Begin PBXBuildFile section */\n";
			for (PBXBuildFile buildFile : this.buildFiles)
			{
				returnString += buildFile.toString(2) + "\n";
			}
			returnString += "/* End PBXBuildFile section */\n\n";
		}
		if (this.containerItemProxy != null)
		{
			returnString += "/* Begin PBXContainerItemProxy section */\n";
			returnString += this.containerItemProxy.toString(2) + "\n";
			returnString += "/* End PBXContainerItemProxy section */\n\n";
		}
		if (this.fileReferences != null && this.fileReferences.size() > 0)
		{
			returnString += "/* Begin PBXFileReference section */\n";
			for (PBXFileElement fileReference : fileReferences)
			{
				returnString += fileReference.toString(2) + "\n";
			}
			returnString += "/* End PBXFileReference section */\n\n";
		}
		if (this.frameworksBuildPhases != null && this.frameworksBuildPhases.size() > 0)
		{
			returnString += "/* Begin PBXFrameworksBuildPhase section */\n";
			for (PBXBuildPhase frameworksBuildPhase : this.frameworksBuildPhases)
			{
				returnString += frameworksBuildPhase.toString(2) + "\n";
			}
			returnString += "/* End PBXFrameworksBuildPhase section */\n\n";
		}
		if (this.groups != null && this.groups.size() > 0)
		{
			returnString += "/* Begin PBXGroup section */\n";
			for (PBXFileElement group : this.groups)
			{
				returnString += group.toString(2) + "\n";
			}
			returnString += "/* End PBXGroup section */\n\n";
		}
		if (this.nativeTargets != null && this.nativeTargets.size() > 0)
		{
			returnString += "/* Begin PBXNativeTarget section */\n";
			for (PBXTarget nativeTarget : this.nativeTargets)
			{
				returnString += nativeTarget.toString(2) + "\n";
			}
			returnString += "/* End PBXNativeTarget section */\n\n";
		}
		if (this.aggregateTargets != null && this.aggregateTargets.size() > 0)
		{
			returnString += "/* Begin PBXAggregateTarget section */\n";
			for (PBXTarget aggregateTarget : this.aggregateTargets)
			{
				returnString += aggregateTarget.toString(2) + "\n";
			}
			returnString += "/* End PBXAggregateTarget section */\n\n";
		}
		if (this.legacyTargets != null && this.legacyTargets.size() > 0)
		{
			returnString += "/* Begin PBXLegacyTarget section */\n";
			for (PBXTarget legacyTarget : this.legacyTargets)
			{
				returnString += legacyTarget.toString(2) + "\n";
			}
			returnString += "/* End PBXLegacyTarget section */\n\n";
		}
		if (this.project != null)
		{
			returnString += "/* Begin PBXProject section */\n";
			returnString += this.project.toString(2) + "\n";
			returnString += "/* End PBXProject section */\n\n";
		}
		if (this.resourcesBuildPhases != null && this.resourcesBuildPhases.size() > 0)
		{
			returnString += "/* Begin PBXResourcesBuildPhase section */\n";
			for (PBXBuildPhase resourcesBuildPhase : this.resourcesBuildPhases)
			{
				returnString += resourcesBuildPhase.toString(2) + "\n";
			}
			returnString += "/* End PBXResourcesBuildPhase section */\n\n";
		}
		if (this.sourcesBuildPhases != null && this.sourcesBuildPhases.size() > 0)
		{
			returnString += "/* Begin PBXSourcesBuildPhase section */\n";
			for (PBXBuildPhase sourcesBuildPhase : this.sourcesBuildPhases)
			{
				returnString += sourcesBuildPhase.toString(2) + "\n";
			}
			returnString += "/* End PBXSourcesBuildPhase section */\n\n";
		}
		if (this.targetDependency != null)
		{
			returnString += "/* Begin PBXTargetDependency section */\n";
			returnString += this.targetDependency.toString(2) + "\n";
			returnString += "/* End PBXTargetDependency section */\n\n";
		}
		if (this.variantGroups != null && this.variantGroups.size() > 0)
		{
			returnString += "/* Begin PBXVariantGroup section */\n";
			for (PBXFileElement variantGroup : this.variantGroups)
			{
				returnString += variantGroup.toString(2) + "\n";
			}
			returnString += "/* End PBXVariantGroup section */\n\n";
		}
		if (this.buildConfigurations != null && this.buildConfigurations.size() > 0)
		{
			returnString += "/* Begin XCBuildConfiguration section */\n";
			for (XCBuildConfiguration buildConfiguration : this.buildConfigurations)
			{
				returnString += buildConfiguration.toString(2) + "\n";
			}
			returnString += "/* End XCBuildConfiguration section */\n\n";
		}
		if (this.configurationLists != null && this.configurationLists.size() > 0)
		{
			returnString += "/* Begin XCConfigurationList section */\n";
			for (XCConfigurationList configurationList : this.configurationLists)
			{
				returnString += configurationList.toString(2) + "\n";
			}
			returnString += "/* End XCConfigurationList section */\n\n";
		}
		if (this.appleScriptBuildPhases != null && this.appleScriptBuildPhases.size() > 0)
		{
			returnString += "/* Begin PBXAppleScriptBuildPhase section */\n";
			for (PBXBuildPhase appleScriptBuildPhase : this.appleScriptBuildPhases)
			{
				returnString += appleScriptBuildPhase.toString(2) + "\n";
			}
			returnString += "/* End PBXAppleScriptBuildPhase section */\n\n";
		}
		if (this.copyFilesBuildPhases != null && this.copyFilesBuildPhases.size() > 0)
		{
			returnString += "/* Begin PBXCopyFilesBuildPhase section */\n";
			for (PBXBuildPhase copyFilesBuildPhase : this.copyFilesBuildPhases)
			{
				returnString += copyFilesBuildPhase.toString(2) + "\n";
			}
			returnString += "/* End PBXCopyFilesBuildPhase section */\n\n";
		}
		if (this.headersBuildPhases != null && this.headersBuildPhases.size() > 0)
		{
			returnString += "/* Begin PBXHeadersBuildPhase section */\n";
			for (PBXBuildPhase headersBuildPhase : this.headersBuildPhases)
			{
				returnString += headersBuildPhase.toString(2) + "\n";
			}
			returnString += "/* End PBXHeadersBuildPhase section */\n\n";
		}
		if (this.shellScriptBuildPhases != null && this.shellScriptBuildPhases.size() > 0)
		{
			returnString += "/* Begin PBXShellScriptBuildPhase section */\n";
			for (PBXBuildPhase shellScriptBuildPhase : this.shellScriptBuildPhases)
			{
				returnString += shellScriptBuildPhase.toString(2) + "\n";
			}
			returnString += "/* End PBXShellScriptBuildPhase section */\n\n";
		}
		returnString += "\t};\n";
		returnString += "\trootObject = " + this.rootObject.toString() + ";\n";
		returnString += "}";
		return returnString;
	}

	private String getSection(String body, String sectionName)
	{
		String endString = "/* End " + sectionName + " section */";
		int startIndex = body.indexOf("/* Begin " + sectionName + " section */");
		int endIndex = body.indexOf(endString);
		if (startIndex == -1 || endIndex == -1)
		{
			return "";
		}
		return body.substring(startIndex, endIndex + endString.length());
	}

	private <T> T getObject(Class<T> clazz, String section) throws NoSuchMethodException, SecurityException,
			InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		if (section == null || section.equals(""))
		{
			return null;
		}
		ObjectParser objectsParser = new ObjectParser(section);
		String objectString = objectsParser.parseNextObject();
		Constructor<T> constructor = clazz.getConstructor(String.class);
		T object = constructor.newInstance(objectString);
		return object;
	}

	private <T> List<T> getList(Class<T> clazz, String section)
			throws InvalidObjectFormatException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException
	{
		ObjectParser objectsParser = new ObjectParser(section);
		String objectString = objectsParser.parseNextObject();
		List<T> objects = new ArrayList<T>();
		while (objectString != null)
		{
			Constructor<T> constructor = clazz.getConstructor(String.class);
			T object = constructor.newInstance(objectString);
			objects.add(object);
			objectString = objectsParser.parseNextObject();
		}
		return objects;
	}

	/**
	 * Method to add a file reference to the project file. This will assume a sourceTree of "<group>".
	 *
	 * @param filePath
	 *            The path to the file you want to add (including the file name).
	 * @return The PBXFileElement object representing the new file element.
	 */
	public PBXFileElement createFileReference(String filePath)
	{
		return createFileReference(filePath, "\"<group>\"");
	}

	/**
	 * Method to add a file reference to the project file.
	 *
	 * @param filePath
	 *            The path to the file you want to add (including the file name).
	 * @param sourceTree
	 *            The source tree where the file is located. Usually will be "<group>".
	 * @return The PBXFileElement object representing the new file element.
	 */
	public PBXFileElement createFileReference(String filePath, String sourceTree)
	{
		PBXFileElement fileReference = new PBXFileElement(filePath, sourceTree);
		if (!this.fileReferences.contains(fileReference))
		{
			this.fileReferences.add(fileReference);
		}
		return fileReference;
	}

	/**
	 * This will create a build file from the file reference that is specified by the file references file path.
	 *
	 * @param filePath
	 *            The file path of the file reference.
	 * @throws FileReferenceDoesNotExistException
	 *             If there is no file reference for the file path.
	 * @return The PBXBuildFile object representing this new build file.
	 */
	public PBXBuildFile createBuildFileFromFileReferencePath(String filePath) throws FileReferenceDoesNotExistException
	{
		return createBuildFileFromFileReferencePath(filePath, Paths.get(filePath).getFileName().toString());
	}

	/**
	 * This will create a build file from the file reference that is specified by the file references file path.
	 *
	 * @param filePath
	 *            The file path of the file reference.
	 * @param buildFileName
	 *            The name that will be in the comments for the build file.
	 * @throws FileReferenceDoesNotExistException
	 *             If there is no file reference for the file path.
	 * @return The PBXBuildFile object representing this new build file.
	 */
	public PBXBuildFile createBuildFileFromFileReferencePath(String filePath, String buildFileName)
			throws FileReferenceDoesNotExistException
	{
		for (PBXFileElement fileReference : this.fileReferences)
		{
			if (fileReference.getPath().equals(filePath))
			{
				String reference = fileReference.getReference().getIdentifier();
				PBXBuildFile buildFile = new PBXBuildFile(buildFileName, reference);
				if (!this.buildFiles.contains(buildFile))
				{
					this.buildFiles.add(buildFile);
				}
				return buildFile;
			}
		}
		throw new FileReferenceDoesNotExistException("No file reference for file at path \"" + filePath + "\" found.");
	}

	/**
	 * This will create an empty group in the project file. It will place the group in the first target's group.
	 *
	 * @param groupName
	 *            The name of the group.
	 * @return The PBXFileElement object representing this new group.
	 */
	public PBXFileElement createGroup(String groupName)
	{
		String mainGroup = this.project.getMainGroup().getIdentifier();
		String firstGroup = mainGroup;
		for (PBXFileElement group : this.groups)
		{
			if (group.getReference().getIdentifier().equals(mainGroup))
			{
				if (group.getChildren().size() > 0)
				{
					firstGroup = group.getChildren().get(0).getIdentifier();
				}
			}
		}
		return createGroup(groupName, new ArrayList<CommentedIdentifier>(), firstGroup);
	}

	/**
	 * This will create an empty group in the project file.
	 *
	 * @param groupName
	 *            The name of the group.
	 * @param parentGroup
	 *            The base group that the group will be included in.
	 * @return The PBXFileElement object representing this new group.
	 */
	public PBXFileElement createGroup(String groupName, String parentGroup)
	{
		return createGroup(groupName, new ArrayList<CommentedIdentifier>(), parentGroup);
	}

	/**
	 * This will create a group in the project file. There can be multiple groups with the same name, so check before
	 * creating a new one.
	 *
	 * @param groupName
	 *            The name of the group.
	 * @param groupChildren
	 *            The identifiers and comments that will be in the group.
	 * @param parentGroup
	 *            The base group that the group will be included in.
	 * @return The PBXFileElement object representing this new group.
	 */
	public PBXFileElement createGroup(String groupName, List<CommentedIdentifier> groupChildren, String parentGroup)
	{
		PBXFileElement group = new PBXFileElement("PBXGroup", groupName, "\"<group>\"");
		for (CommentedIdentifier child : groupChildren)
		{
			group.addChild(child);
		}
		// There can be multiple groups of the same name
		this.groups.add(group);
		for (PBXFileElement groupElement : this.groups)
		{
			if (groupElement.getReference().getIdentifier().equals(parentGroup))
			{
				groupElement.addChild(group.getReference());
			}
		}
		return group;
	}

	/**
	 * Add an apple script build phase to the project.
	 *
	 * @param targetIdentifier
	 *            The identifier of the target you want to add the build phase to.
	 * @param buildPhase
	 *            The object representing the build phase.
	 */
	public void addAppleScriptBuildPhase(String targetIdentifier, PBXBuildPhase buildPhase)
	{
		addBuildPhaseToTarget(targetIdentifier, buildPhase.getReference());
		this.appleScriptBuildPhases.add(buildPhase);
	}

	/**
	 * Add a copy files build phase to the project.
	 *
	 * @param targetIdentifier
	 *            The identifier of the target you want to add the build phase to.
	 * @param buildPhase
	 *            The object representing the build phase.
	 */
	public void addCopyFilesBuildPhase(String targetIdentifier, PBXBuildPhase buildPhase)
	{
		addBuildPhaseToTarget(targetIdentifier, buildPhase.getReference());
		this.copyFilesBuildPhases.add(buildPhase);
	}

	/**
	 * Add a frameworks build phase to the project.
	 *
	 * @param targetIdentifier
	 *            The identifier of the target you want to add the build phase to.
	 * @param buildPhase
	 *            The object representing the build phase.
	 */
	public void addFrameworksBuildPhase(String targetIdentifier, PBXBuildPhase buildPhase)
	{
		addBuildPhaseToTarget(targetIdentifier, buildPhase.getReference());
		this.frameworksBuildPhases.add(buildPhase);
	}

	/**
	 * Add a headers build phase to the project.
	 *
	 * @param targetIdentifier
	 *            The identifier of the target you want to add the build phase to.
	 * @param buildPhase
	 *            The object representing the build phase.
	 */
	public void addHeadersBuildPhase(String targetIdentifier, PBXBuildPhase buildPhase)
	{
		addBuildPhaseToTarget(targetIdentifier, buildPhase.getReference());
		this.headersBuildPhases.add(buildPhase);
	}

	/**
	 * Add a resources build phase to the project.
	 *
	 * @param targetIdentifier
	 *            The identifier of the target you want to add the build phase to.
	 * @param buildPhase
	 *            The object representing the build phase.
	 */
	public void addResourcesBuildPhase(String targetIdentifier, PBXBuildPhase buildPhase)
	{
		addBuildPhaseToTarget(targetIdentifier, buildPhase.getReference());
		this.resourcesBuildPhases.add(buildPhase);
	}

	/**
	 * Add a shell script build phase to the project.
	 *
	 * @param targetIdentifier
	 *            The identifier of the target you want to add the build phase to.
	 * @param buildPhase
	 *            The object representing the build phase.
	 */
	public void addShellScriptBuildPhase(String targetIdentifier, PBXBuildPhase buildPhase)
	{
		addBuildPhaseToTarget(targetIdentifier, buildPhase.getReference());
		this.shellScriptBuildPhases.add(buildPhase);
	}

	/**
	 * Add a sources build phase to the project.
	 *
	 * @param targetIdentifier
	 *            The identifier of the target you want to add the build phase to.
	 * @param buildPhase
	 *            The object representing the build phase.
	 */
	public void addSourcesBuildPhase(String targetIdentifier, PBXBuildPhase buildPhase)
	{
		addBuildPhaseToTarget(targetIdentifier, buildPhase.getReference());
		this.sourcesBuildPhases.add(buildPhase);
	}

	private void removeBuildPhaseFromTargets(String identifier)
	{
		for (PBXTarget target : this.nativeTargets)
		{
			for (CommentedIdentifier buildPhase : target.getBuildPhases())
			{
				if (buildPhase.getIdentifier().equals(identifier))
				{
					// this.nativeTargets.set(index, target);
					target.getBuildPhases().remove(buildPhase);
					break;
				}
			}
		}
	}

	private void addBuildPhaseToTarget(String targetIdentifier, CommentedIdentifier buildPhaseIdentifier)
	{
		for (PBXTarget target : this.nativeTargets)
		{
			if (target.getReference().getIdentifier().equals(targetIdentifier))
			{
				target.getBuildPhases().add(buildPhaseIdentifier);
			}
		}
	}

	/**
	 * Get the apple script build phase for the provided identifier.
	 *
	 * @param identifier
	 *            The identifier that is used to identify the build phase.
	 * @return The apple script build phase for the identifier, or null if no build phase was found.
	 */
	public PBXBuildPhase getAppleScriptBuildPhaseWithIdentifier(String identifier)
	{
		return getBuildPhaseWithIdentifier(this.appleScriptBuildPhases, identifier);
	}

	/**
	 * Get the copy files build phase for the provided identifier.
	 *
	 * @param identifier
	 *            The identifier that is used to identify the build phase.
	 * @return The copy files build phase for the identifier, or null if no build phase was found.
	 */
	public PBXBuildPhase getCopyFilesBuildPhaseWithIdentifier(String identifier)
	{
		return getBuildPhaseWithIdentifier(this.copyFilesBuildPhases, identifier);
	}

	/**
	 * Get the frameworks build phase for the provided identifier.
	 *
	 * @param identifier
	 *            The identifier that is used to identify the build phase.
	 * @return The frameworks build phase for the identifier, or null if no build phase was found.
	 */
	public PBXBuildPhase getFrameworksBuildPhaseWithIdentifier(String identifier)
	{
		return getBuildPhaseWithIdentifier(this.frameworksBuildPhases, identifier);
	}

	/**
	 * Get the headers build phase for the provided identifier.
	 *
	 * @param identifier
	 *            The identifier that is used to identify the build phase.
	 * @return The headers build phase for the identifier, or null if no build phase was found.
	 */
	public PBXBuildPhase getHeadersBuildPhaseWithIdentifier(String identifier)
	{
		return getBuildPhaseWithIdentifier(this.headersBuildPhases, identifier);
	}

	/**
	 * Get the resources build phase for the provided identifier.
	 *
	 * @param identifier
	 *            The identifier that is used to identify the build phase.
	 * @return The resources build phase for the identifier, or null if no build phase was found.
	 */
	public PBXBuildPhase getResourcesBuildPhaseWithIdentifier(String identifier)
	{
		return getBuildPhaseWithIdentifier(this.resourcesBuildPhases, identifier);
	}

	/**
	 * Get the shell script build phase for the provided identifier.
	 *
	 * @param identifier
	 *            The identifier that is used to identify the build phase.
	 * @return The shell script build phase for the identifier, or null if no build phase was found.
	 */
	public PBXBuildPhase getShellScriptBuildPhaseWithIdentifier(String identifier)
	{
		return getBuildPhaseWithIdentifier(this.shellScriptBuildPhases, identifier);
	}

	/**
	 * Get the sources build phase for the provided identifier.
	 *
	 * @param identifier
	 *            The identifier that is used to identify the build phase.
	 * @return The sources build phase for the identifier, or null if no build phase was found.
	 */
	public PBXBuildPhase getSourcesBuildPhaseWithIdentifier(String identifier)
	{
		return getBuildPhaseWithIdentifier(this.sourcesBuildPhases, identifier);
	}

	private PBXBuildPhase getBuildPhaseWithIdentifier(List<PBXBuildPhase> buildPhases, String identifier)
	{
		for (PBXBuildPhase buildPhase : buildPhases)
		{
			if (buildPhase.getReference().getIdentifier().equals(identifier))
			{
				return buildPhase;
			}
		}
		return null;
	}

	/**
	 * Remove the build phase with the provided identifier from the apple scripts build phase section
	 *
	 * @param identifier
	 *            The identifier that is used to identify the build phase.
	 */
	public void removeAppleScriptBuildPhaseWithIdentifier(String identifier)
	{
		removeBuildPhaseFromTargets(identifier);
		for (PBXBuildPhase buildPhase : this.appleScriptBuildPhases)
		{
			if (buildPhase.getReference().getIdentifier().equals(identifier))
			{
				this.appleScriptBuildPhases.remove(buildPhase);
			}
		}
	}

	/**
	 * Remove the build phase with the provided identifier from the copy files build phase section
	 *
	 * @param identifier
	 *            The identifier that is used to identify the build phase.
	 */
	public void removeCopyFilesBuildPhaseWithIdentifier(String identifier)
	{
		removeBuildPhaseFromTargets(identifier);
		for (PBXBuildPhase buildPhase : this.copyFilesBuildPhases)
		{
			if (buildPhase.getReference().getIdentifier().equals(identifier))
			{
				this.copyFilesBuildPhases.remove(buildPhase);
			}
		}
	}

	/**
	 * Remove the build phase with the provided identifier from the frameworks build phase section
	 *
	 * @param identifier
	 *            The identifier that is used to identify the build phase.
	 */
	public void removeFrameworksBuildPhaseWithIdentifier(String identifier)
	{
		removeBuildPhaseFromTargets(identifier);
		for (PBXBuildPhase buildPhase : this.frameworksBuildPhases)
		{
			if (buildPhase.getReference().getIdentifier().equals(identifier))
			{
				this.frameworksBuildPhases.remove(buildPhase);
			}
		}
	}

	/**
	 * Remove the build phase with the provided identifier from the headers build phase section
	 *
	 * @param identifier
	 *            The identifier that is used to identify the build phase.
	 */
	public void removeHeadersBuildPhaseWithIdentifier(String identifier)
	{
		removeBuildPhaseFromTargets(identifier);
		for (PBXBuildPhase buildPhase : this.headersBuildPhases)
		{
			if (buildPhase.getReference().getIdentifier().equals(identifier))
			{
				this.headersBuildPhases.remove(buildPhase);
			}
		}
	}

	/**
	 * Remove the build phase with the provided identifier from the resources build phase section
	 *
	 * @param identifier
	 *            The identifier that is used to identify the build phase.
	 */
	public void removeResourcesBuildPhaseWithIdentifier(String identifier)
	{
		removeBuildPhaseFromTargets(identifier);
		for (PBXBuildPhase buildPhase : this.resourcesBuildPhases)
		{
			if (buildPhase.getReference().getIdentifier().equals(identifier))
			{
				this.resourcesBuildPhases.remove(buildPhase);
			}
		}
	}

	/**
	 * Remove the build phase with the provided identifier from the shell scripts build phase section
	 *
	 * @param identifier
	 *            The identifier that is used to identify the build phase.
	 */
	public void removeShellScriptBuildPhaseWithIdentifier(String identifier)
	{
		removeBuildPhaseFromTargets(identifier);
		for (PBXBuildPhase buildPhase : this.shellScriptBuildPhases)
		{
			if (buildPhase.getReference().getIdentifier().equals(identifier))
			{
				this.shellScriptBuildPhases.remove(buildPhase);
			}
		}
	}

	/**
	 * Remove the build phase with the provided identifier from the sources build phase section
	 *
	 * @param identifier
	 *            The identifier that is used to identify the build phase.
	 */
	public void removeSourcesBuildPhaseWithIdentifier(String identifier)
	{
		removeBuildPhaseFromTargets(identifier);
		for (PBXBuildPhase buildPhase : this.sourcesBuildPhases)
		{
			if (buildPhase.getReference().getIdentifier().equals(identifier))
			{
				this.sourcesBuildPhases.remove(buildPhase);
			}
		}
	}

	/**
	 * Adds or updates the value of 'key' to 'value' in the build configuration specified by the identifier.
	 *
	 * @param buildConfigurationIdentifier
	 *            The identifier for the configuration.
	 * @param key
	 *            The key of the build setting you want to add/update.
	 * @param value
	 *            The value of the build setting you want to add/update.
	 */
	public void setBuildConfigurationProperty(String buildConfigurationIdentifier, String key, String value)
	{
		for (XCBuildConfiguration configuration : this.buildConfigurations)
		{
			if (configuration.getReference().getIdentifier().equals(buildConfigurationIdentifier))
			{
				configuration.setBuildSetting(key, value);
			}
		}
	}

	/**
	 * Adds or updates the value of 'key' to 'value' in the build configuration specified by the identifier.
	 *
	 * @param buildConfigurationIdentifier
	 *            The identifier for the configuration.
	 * @param key
	 *            The key of the build setting you want to add/update.
	 * @param values
	 *            The value of the build setting you want to add/update as a list.
	 */
	public void setBuildConfigurationProperty(String buildConfigurationIdentifier, String key, List<String> values)
	{
		for (XCBuildConfiguration configuration : this.buildConfigurations)
		{
			if (configuration.getReference().getIdentifier().equals(buildConfigurationIdentifier))
			{
				configuration.setBuildSetting(key, values);
			}
		}
	}

	/**
	 * Get the value for 'key' in the build configuration specified by the identifier.
	 *
	 * @param buildConfigurationIdentifier
	 *            The identifier for the build configuration.
	 * @param key
	 *            The key of the build property you want the value of.
	 * @return The value for the build configuration property, or null if it doesn't exist.
	 */
	public String getBuildConfigurationProperty(String buildConfigurationIdentifier, String key)
	{
		for (XCBuildConfiguration configuration : this.buildConfigurations)
		{
			if (configuration.getReference().getIdentifier().equals(buildConfigurationIdentifier))
			{
				return configuration.getBuildSetting(key);
			}
		}
		return null;
	}

	/**
	 * Get the value for 'key' in the build configuration specified by the identifier as a list.
	 *
	 * @param buildConfigurationIdentifier
	 *            The identifier for the build configuration.
	 * @param key
	 *            The key of the build property you want the value of.
	 * @return A list of values for the build configuration property, or null if it doesn't exist or isn't a list.
	 */
	public List<String> getBuildConfigurationPropertyAsList(String buildConfigurationIdentifier, String key)
	{
		for (XCBuildConfiguration configuration : this.buildConfigurations)
		{
			if (configuration.getReference().getIdentifier().equals(buildConfigurationIdentifier))
			{
				return configuration.getBuildSettingAsList(key);
			}
		}
		return null;
	}

	/**
	 * Gets the group with a matching identifier from the PBXGroup section.
	 *
	 * @param identifier
	 *            The identifier of the group.
	 * @return The group that matches the identifier, or null if none is found.
	 */
	public PBXFileElement getGroupWithIdentifier(String identifier)
	{
		for (PBXFileElement group : this.groups)
		{
			if (group.getReference() != null && group.getReference().getIdentifier().equals(identifier))
			{
				return group;
			}
		}
		return null;
	}

	/**
	 * Gets the variant group with a matching identifier from the PBXVariantGroup section.
	 *
	 * @param identifier
	 *            The identifier of the variant group.
	 * @return The variant group that matches the identifier, or null if none is found.
	 */
	public PBXFileElement getVariantGroupWithIdentifier(String identifier)
	{
		for (PBXFileElement variantGroup : this.variantGroups)
		{
			if (variantGroup.getReference() != null && variantGroup.getReference().getIdentifier().equals(identifier))
			{
				return variantGroup;
			}
		}
		return null;
	}

	/**
	 * Gets the build file with a matching identifier from the PBXBuildFile section.
	 *
	 * @param identifier
	 *            The identifier of the build file.
	 * @return The build file that matches the identifier, or null if none is found.
	 */
	public PBXBuildFile getBuildFileWithIdentifier(String identifier)
	{
		for (PBXBuildFile buildFile : this.buildFiles)
		{
			if (buildFile.getReference() != null && buildFile.getReference().getIdentifier().equals(identifier))
			{
				return buildFile;
			}
		}
		return null;
	}

	/**
	 * Gets the build file with a matching file reference from the PBXBuildFile section.
	 *
	 * @param fileRef
	 *            The file reference of the build file.
	 * @return The list of build files that matches the file reference, or an empty list if none are found.
	 */
	public List<PBXBuildFile> getBuildFileWithFileRef(String fileRef)
	{
		List<PBXBuildFile> buildFiles = new ArrayList<PBXBuildFile>();
		for (PBXBuildFile buildFile : this.buildFiles)
		{
			if (buildFile.getFileRef() != null && buildFile.getFileRef().getIdentifier().equals(fileRef))
			{
				buildFiles.add(buildFile);
			}
		}
		return buildFiles;
	}

	/**
	 * Gets the build file that matches the file reference with the specified path.
	 *
	 * @param fileRefPath
	 *            The path of the file reference that the build file references.
	 * @return The list of build files that match the file path, or an empty list if none are found.
	 */
	public List<PBXBuildFile> getBuildFileWithFileRefPath(String fileRefPath)
	{
		for (PBXFileElement fileReference : this.fileReferences)
		{
			if (fileReference.getPath() != null && fileReference.getPath().equals(fileRefPath))
			{
				return getBuildFileWithFileRef(fileReference.getReference().getIdentifier());
			}
		}
		return new ArrayList<PBXBuildFile>();
	}

	/**
	 * Gets the file references that matches the path in the PBXFileReference section.
	 *
	 * @param fileRefPath
	 *            The path of the file reference.
	 * @return The file reference, or null if none is found.
	 */
	public PBXFileElement getFileReferenceWithPath(String fileRefPath)
	{
		for (PBXFileElement fileReference : this.fileReferences)
		{
			if (fileReference.getPath() != null && fileReference.getPath().equals(fileRefPath))
			{
				return fileReference;
			}
		}
		return null;
	}

	/**
	 * Gets the native target with a matching identifier from the PBXNativeTarget section.
	 *
	 * @param identifier
	 *            The identifier of the native target.
	 * @return The native target, or null if none is found.
	 */
	public PBXTarget getNativeTargetWithIdentifier(String identifier)
	{
		for (PBXTarget nativeTarget : this.nativeTargets)
		{
			if (nativeTarget.getReference() != null && nativeTarget.getReference().getIdentifier().equals(identifier))
			{
				return nativeTarget;
			}
		}
		return null;
	}

	/**
	 * Gets the legacy taget with a matching identifier from the PBXLegacyTarget section.
	 *
	 * @param identifier
	 *            The identifier of the legacy target.
	 * @return The legacy target, or null if none is found.
	 */
	public PBXTarget getLegacyTargetWithIdentifier(String identifier)
	{
		for (PBXTarget legacyTarget : this.legacyTargets)
		{
			if (legacyTarget.getReference() != null && legacyTarget.getReference().getIdentifier().equals(identifier))
			{
				return legacyTarget;
			}
		}
		return null;
	}

	/**
	 * Gets the aggregate target with a matching identifier from the PBXAggregateTarget section.
	 *
	 * @param identifier
	 *            The identifier of the aggregate target.
	 * @return The aggregate target, or null if none is found.
	 */
	public PBXTarget getAggregateTargetWithIdentifier(String identifier)
	{
		for (PBXTarget aggregateTarget : this.aggregateTargets)
		{
			if (aggregateTarget.getReference() != null
					&& aggregateTarget.getReference().getIdentifier().equals(identifier))
			{
				return aggregateTarget;
			}
		}
		return null;
	}

	/**
	 * Gets the build configuration with a matching identifier from the XCBuildConfiguration section.
	 *
	 * @param identifier
	 *            The identifier of the build configuration.
	 * @return The build configuration, or null if none is found.
	 */
	public XCBuildConfiguration getBuildConfigurationWithIdentifier(String identifier)
	{
		for (XCBuildConfiguration buildConfiguration : this.buildConfigurations)
		{
			if (buildConfiguration.getReference() != null
					&& buildConfiguration.getReference().getIdentifier().equals(identifier))
			{
				return buildConfiguration;
			}
		}
		return null;
	}

	/**
	 * Gets the configuration list with a matching identifier from the XCConfigurationList section.
	 *
	 * @param identifier
	 *            The identifier of the configuration list.
	 * @return The configuration list, or null if none is found.
	 */
	public XCConfigurationList getConfigurationListWithIdentifier(String identifier)
	{
		for (XCConfigurationList configurationList : this.configurationLists)
		{
			if (configurationList.getReference() != null
					&& configurationList.getReference().getIdentifier().equals(identifier))
			{
				return configurationList;
			}
		}
		return null;
	}

	public PBXProject getProject()
	{
		return project;
	}

}
