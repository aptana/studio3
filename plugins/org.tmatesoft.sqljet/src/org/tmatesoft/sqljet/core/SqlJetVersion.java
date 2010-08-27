/**
 * SqlJetVersion.java
 * Copyright (C) 2009-2010 TMate Software Ltd
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * For information on how to redistribute this software under
 * the terms of a license other than GNU General Public License
 * contact TMate Software at support@sqljet.com
 */
package org.tmatesoft.sqljet.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * SQLJet's version.
 * 
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public class SqlJetVersion {
    
    private SqlJetVersion() {
    }

    private static String PROPERTIES_PATH = "/sqljet.build.properties";

    private static Properties ourProperties;

    private static final String VERSION_MAJOR_PROPERTY = "sqljet.version.major";
    private static final String VERSION_MINOR_PROPERTY = "sqljet.version.minor";
    private static final String VERSION_MICRO_PROPERTY = "sqljet.version.micro";
    private static final String VERSION_BUILD_PROPERTY = "sqljet.version.build";

    private static final String VERSION_MAJOR_DEFAULT = "1";
    private static final String VERSION_MINOR_DEFAULT = "0";
    private static final String VERSION_MICRO_DEFAULT = "3";

    private static final String VERSION_BUILD_DEFAULT = "0";

    /**
     * Get SqlJet version as a String.
     * 
     * @return SqlJet library version in form MAJOR.MINOT.MICRO.bBUILD_NUMBER
     */
    public static String getVersionString() {
        loadProperties();
        StringBuffer version = new StringBuffer();
        version.append(getMajorVersion());
        version.append('.');
        version.append(getMinorVersion());
        version.append('.');
        version.append(getMicroVersion());
        version.append('.');
        version.append('b');
        if (getBuildNumber() > 0) {
            version.append(getBuildNumber());
        } else {
            version.append("Local");
        }
        return version.toString();
    }

    /**
     * Get SQLJet's major version.
     * 
     * @return major version.
     */
    public static int getMajorVersion() {
        loadProperties();
        try {
            return Integer.parseInt(ourProperties.getProperty(VERSION_MAJOR_PROPERTY, VERSION_MAJOR_DEFAULT));
        } catch (NumberFormatException nfe) {
            //
        }
        return Integer.parseInt(VERSION_MAJOR_DEFAULT);
    }

    /**
     * Get SQLJet's minor version.
     * 
     * @return minor version.
     */
    public static int getMinorVersion() {
        loadProperties();
        try {
            return Integer.parseInt(ourProperties.getProperty(VERSION_MINOR_PROPERTY, VERSION_MINOR_DEFAULT));
        } catch (NumberFormatException nfe) {
            //
        }
        return Integer.parseInt(VERSION_MINOR_DEFAULT);
    }

    /**
     * Get SQLJet's micro version.
     * 
     * @return micro version.
     */
    public static int getMicroVersion() {
        loadProperties();
        try {
            return Integer.parseInt(ourProperties.getProperty(VERSION_MICRO_PROPERTY, VERSION_MICRO_DEFAULT));
        } catch (NumberFormatException nfe) {
            //
        }
        return Integer.parseInt(VERSION_MICRO_DEFAULT);
    }

    /**
     * Get SQLJet's build number.
     * 
     * @return build number.
     */
    public static long getBuildNumber() {
        loadProperties();
        try {
            return Long.parseLong(ourProperties.getProperty(VERSION_BUILD_PROPERTY, VERSION_BUILD_DEFAULT));
        } catch (NumberFormatException nfe) {
            //
        }
        return 0;
    }

    private static void loadProperties() {
        synchronized (SqlJetVersion.class) {
            if (ourProperties != null) {
                return;
            }
            InputStream is = SqlJetVersion.class.getResourceAsStream(PROPERTIES_PATH);
            ourProperties = new Properties();
            if (is == null) {
                return;
            }
            try {
                ourProperties.load(is);
            } catch (IOException e) {
                //
            } finally {
                try {

                    is.close();
                } catch (IOException e) {
                    // 
                }
            }
        }
    }

}
