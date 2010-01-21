package com.aptana.scripting.model;

import java.util.Map;

public interface EnvironmentContributor
{
	Map<String,String> toEnvironment();
}
