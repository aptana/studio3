/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.core.tests.session;

import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.xml.parsers.*;
import org.eclipse.core.runtime.Platform;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

@SuppressWarnings({"unchecked", "rawtypes"})
public class SetupManager {
	public class SetupException extends Exception {
		/**
		 * All serializable objects should have a stable serialVersionUID
		 */
		private static final long serialVersionUID = 1L;

		public SetupException(String message, Throwable cause) {
			super(message, cause);
		}

		public SetupException(String message) {
			super(message);
		}
	}

	private static SetupManager instance;
	private static final String SETUP_DEBUG = "setup.debug";
	private static final String SETUP_FILES = "setup.files";
	private static final String SETUP_OPTIONS = "setup.options";
	private static final String SETUP_OVERRIDE_ECLIPSEARGS = "setup.override.eclipseArgs";
	private static final String SETUP_OVERRIDE_SYSTEMPROPERTIES = "setup.override.systemProperties";
	private static final String SETUP_OVERRIDE_VMARGS = "setup.override.vmArgs";
	private String defaultOptionSetIds = "";
	private Map setupById;
	private Collection setups;

	private static boolean contains(Object[] set, Object element) {
		for (int i = 0; i < set.length; i++)
			if (element.equals(set[i]))
				return true;
		return false;
	}

	public synchronized static SetupManager getInstance() throws SetupException {
		if (instance != null)
			return instance;
		instance = new SetupManager();
		return instance;
	}

	public static boolean inDebugMode() {
		return Boolean.getBoolean(SETUP_DEBUG);
	}

	public static void main(String[] args) throws Exception {
		SetupManager manager = SetupManager.getInstance();
		System.out.println(manager.getDefaultSetup());
	}

	static String[] parseItems(String string) {
		if (string == null)
			return new String[0];
		StringTokenizer tokenizer = new StringTokenizer(string, ","); //$NON-NLS-1$
		if (!tokenizer.hasMoreTokens())
			return new String[0];
		String first = tokenizer.nextToken().trim();
		if (!tokenizer.hasMoreTokens())
			return new String[] {first};
		ArrayList items = new ArrayList();
		items.add(first);
		do {
			items.add(tokenizer.nextToken().trim());
		} while (tokenizer.hasMoreTokens());
		return (String[]) items.toArray(new String[items.size()]);
	}

	protected SetupManager() throws SetupException {
		setups = new ArrayList();
		setupById = new HashMap();
		try {
			loadSetups();
		} catch (SetupException e) {
			throw e;
		} catch (Exception e) {
			throw new SetupException("Problems initializing SetupManager", e);
		}
	}

	public Setup buildSetup(String[] optionSets) {
		Setup defaultSetup = Setup.getDefaultSetup(this);
		for (Iterator i = setups.iterator(); i.hasNext();) {
			Setup customSetup = (Setup) i.next();
			if ((customSetup.getId() == null || contains(optionSets, customSetup.getId())) && customSetup.isSatisfied(optionSets))
				defaultSetup.merge(customSetup);
		}
		defaultSetup.setEclipseArguments(parseOptions(System.getProperty(SETUP_OVERRIDE_ECLIPSEARGS)));
		defaultSetup.setVMArguments(parseOptions(System.getProperty(SETUP_OVERRIDE_VMARGS)));
		defaultSetup.setSystemProperties(parseOptions(System.getProperty(SETUP_OVERRIDE_SYSTEMPROPERTIES)));
		return defaultSetup;
	}

	private String getAttribute(NamedNodeMap attributes, String name) {
		Node selected = attributes.getNamedItem(name);
		return selected == null ? null : selected.getNodeValue();
	}

	private String[] getDefaultOptionSets() {
		return parseItems(System.getProperty(SETUP_OPTIONS, defaultOptionSetIds));
	}

	/**
	 * Returns a brand new setup object configured according to the current
	 * default setup settings.
	 * 
	 * @return a new setup object
	 */
	public Setup getDefaultSetup() {
		return buildSetup(getDefaultOptionSets());
	}

	public Setup getSetup(String id) {
		return (Setup) setupById.get(id);
	}

	private void loadEclipseArgument(Setup newSetup, Element toParse) {
		newSetup.setEclipseArgument(toParse.getAttribute("option"), toParse.getAttribute("value"));
	}

	private void loadProperty(Setup newSetup, Element toParse) {
		newSetup.setSystemProperty(toParse.getAttribute("key"), toParse.getAttribute("value"));
	}

	private void loadSetup(Element markup) {
		NamedNodeMap attributes = markup.getAttributes();
		if (attributes == null)
			return;
		Setup newSetup = new Setup(this);
		newSetup.setId(getAttribute(attributes, "id"));
		newSetup.setName(getAttribute(attributes, "name"));
		String timeout = getAttribute(attributes, "timeout");
		newSetup.setBaseSetups(parseItems(getAttribute(attributes, "base")));
		newSetup.setRequiredSets(parseItems(getAttribute(attributes, "with")));

		if (timeout != null)
			newSetup.setTimeout(Integer.parseInt(timeout));
		NodeList children = markup.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node next = children.item(i);
			if (!(next instanceof Element))
				continue;
			Element toParse = (Element) next;
			if (toParse.getTagName().equals("eclipseArg"))
				loadEclipseArgument(newSetup, toParse);
			else if (toParse.getTagName().equals("vmArg"))
				loadVMArgument(newSetup, toParse);
			else if (toParse.getTagName().equals("systemProperty"))
				loadProperty(newSetup, toParse);
		}
		setups.add(newSetup);
		if (newSetup.getId() != null)
			setupById.put(newSetup.getId(), newSetup);
	}

	private void loadSetups() throws ParserConfigurationException, FactoryConfigurationError, SAXException, IOException, SetupException {
		String setupFilesProperty = System.getProperty(SETUP_FILES);
		boolean defaultLocation = false;
		if (setupFilesProperty == null) {
			setupFilesProperty = "default-setup.xml";
			defaultLocation = true;
		}
		String[] setupFileNames = parseItems(setupFilesProperty);
		File[] setupFiles = new File[setupFileNames.length];
		int found = 0;
		for (int i = 0; i < setupFiles.length; i++) {
			setupFiles[found] = new File(setupFileNames[i]);
			if (!setupFiles[found].isFile()) {
				if (!defaultLocation)
					// warn if user-provided location does not exist
					System.out.println("No setup files found at '" + setupFiles[i].getAbsolutePath() + "'. ");
				continue;
			}
			found++;
		}
		if (found == 0) {
			if (Platform.isRunning()) {
				// No setup descriptions found, only the default setup will be available
				return;
			}
			// no setup files found, and we are not running in Eclipse...  
			throw new SetupException("No setup descriptions found. Ensure you are specifying the path for an existing setup file (e.g. -Dsetup.files=<setup-file-location1>[...,<setup-file-locationN>])");
		}
		DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		for (int fileIndex = 0; fileIndex < found; fileIndex++) {
			Document doc = docBuilder.parse(setupFiles[fileIndex]);
			Element root = doc.getDocumentElement();
			String setupDefaultOptionSets = root.getAttribute("default");
			if (setupDefaultOptionSets != null)
				defaultOptionSetIds = defaultOptionSetIds == null ? setupDefaultOptionSets : (defaultOptionSetIds + ',' + setupDefaultOptionSets);
			NodeList optionSets = root.getChildNodes();
			for (int i = 0; i < optionSets.getLength(); i++) {
				Node next = optionSets.item(i);
				if (!(next instanceof Element))
					continue;
				Element toParse = (Element) next;
				if (!toParse.getTagName().equals("optionSet"))
					continue;
				loadSetup(toParse);
			}
		}
	}

	private void loadVMArgument(Setup newSetup, Element toParse) {
		newSetup.setVMArgument(toParse.getAttribute("option"), toParse.getAttribute("value"));
	}

	/*
	 * Use a double equal sign to escape and equal to treat it from becoming
	 * a key/value pair
	 */
	private static Map parseOptions(String options) {
		if (options == null)
			return Collections.EMPTY_MAP;
		Map result = new HashMap();
		StringTokenizer tokenizer = new StringTokenizer(options.trim(), ";");
		while (tokenizer.hasMoreTokens()) {
			String option = tokenizer.nextToken();
			int separatorIndex = option.indexOf('=');
			if (separatorIndex == -1 || separatorIndex == option.length() - 1) { // property with no value defined
				result.put(option, "");
				continue;
			}
			// the 90% case is that we won't have an escaped equals so check to see if we can short-circuit
			if (option.indexOf("==") == -1) {
				String key = option.substring(0, separatorIndex);
				String value = option.substring(separatorIndex + 1);
				result.put(key, value);
				continue;
			}
			// otherwise we have an escaped equals somewhere in this option
			int valueStart = -1;
			// strip out the key (first non-escaped equal)
			StringBuffer key = new StringBuffer();
			for (int i = 0; i < option.length(); i++) {
				char c = option.charAt(i);
				// if we don't have an equal sign, then just add it to the key
				if (c != '=') {
					key.append(c);
					continue;
				}
				i++;
				if (i >= option.length())
					break;
				char next = option.charAt(i);
				if (next == '=') {
					key.append('=');
					continue;
				}
				// we had a single equal
				valueStart = i;
				break;
			}
			String value = "";
			// now get the value. replace == by =
			if (valueStart > -1)
				value = option.substring(valueStart).replaceAll("==", "=");
			result.put(key.toString(), value);
		}
		return result;
	}
}
