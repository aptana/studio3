/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.test.internal.performance.db;

import java.util.Arrays;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.test.internal.performance.PerformanceTestPlugin;

/**
 * The <code>Variations</code> class represents a set of key/value pairs
 * and is used to tag data stored in the performance database and when
 * querying for data from the database. 
 */
public class Variations extends Properties {
    
    private static final long serialVersionUID= 1L;

    /**
     * Creates an empty set of key/value pairs.
     */
    public Variations() {
        //
    }

    /**
     * Creates a Variations object that is populated with a "config" and a "build" key/value pair.
     * @param configValue a value to store under the config key
     * @param buildValue a value to store under the build key
     * @deprecated Use the default constructor instead and fill in key/value pairs explicitely.
     */
    public Variations(String configValue, String buildValue) {
        if (configValue != null)
            put(PerformanceTestPlugin.CONFIG, configValue);
        if (buildValue != null)
            put(PerformanceTestPlugin.BUILD, buildValue);
    }

    /**
     * Creates a set of key/value pairs by parsing the given string.
     * The format of the string must be:
     * <pre>
     *   key1=value1;key2=value2; .... ; keyn=valuen
     * </pre>
     * @param keyValuePairs
     */
    public Variations(String keyValuePairs) {
        parsePairs(keyValuePairs);
    }

    public String toExactMatchString() {
        return toDB(this, false);
    }
    
    public String toQueryPattern() {
        return toDB(this, true);
    }

	public void parsePairs(String keyvaluepairs) {
        parse(keyvaluepairs, ";"); //$NON-NLS-1$
	}

	public void parseDB(String keyvaluepairs) {
        parse(keyvaluepairs, "|"); //$NON-NLS-1$
	}

    /**
     * parsing the given string as key/value pairs and stores them in Variations.
     * The string's format is an implementation detail of the database.
     * @param keyvaluepairs
     * @param separator
     */
    private void parse(String keyvaluepairs, String separator) {
		StringTokenizer st= new StringTokenizer(keyvaluepairs, separator);
		while (st.hasMoreTokens()) {
			String token= st.nextToken();
			int i= token.indexOf('=');
			if (i < 1)
			    throw new IllegalArgumentException("kev/value pair '" + token + "' is illformed"); //$NON-NLS-1$ //$NON-NLS-2$
			String value= token.substring(i+1);
			token= token.substring(0, i);
			//System.out.println(token + ": <" + value + ">");
			put(token, value);
		}	    
	}
    
	/*
	 * TODO: we need to escape '=' and ';' characters in key/values.
	 */
    private static String toDB(Properties keyValues, boolean asQuery) {
        Set set= keyValues.keySet();
        String[] keys= (String[]) set.toArray(new String[set.size()]);
        Arrays.sort(keys);
        StringBuffer sb= new StringBuffer();
        
        for (int i= 0; i < keys.length; i++) {
            if (asQuery)
                sb.append('%');
            String key= keys[i];
            String value= keyValues.getProperty(key);
            sb.append('|');
            sb.append(key);
            sb.append('=');
            if (value != null)
                sb.append(value);
            sb.append('|');
        }
        if (asQuery)
            sb.append('%');
	    return sb.toString();
    }
}
