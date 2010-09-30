package com.aptana.editor.css.contentassist.model;

import java.util.List;

public interface ICSSMetadataElement
{

	/**
	 * getDescription;
	 */
	public String getDescription();

	/**
	 * getName
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * getUserAgents
	 * 
	 * @return
	 */
	public List<UserAgentElement> getUserAgents();

	/**
	 * getUserAgentNames
	 * 
	 * @return
	 */
	public String[] getUserAgentNames();

	/**
	 * getExample
	 * 
	 * @return
	 */
	public String getExample();

}