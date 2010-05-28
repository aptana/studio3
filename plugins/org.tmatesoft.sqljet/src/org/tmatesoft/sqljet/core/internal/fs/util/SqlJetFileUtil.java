/**
 * SqlJetFileUtil.java
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
package org.tmatesoft.sqljet.core.internal.fs.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

import org.tmatesoft.sqljet.core.internal.SqlJetUtility;

/**
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public class SqlJetFileUtil {

    public static final int ATTEMPTS_COUNT = SqlJetUtility.getIntSysProp("sqljet.fs.win32_retry_count", 100);
    public static final SqlJetOsType OS = new SqlJetOsType();

    public static boolean deleteFile(File file) {
        return deleteFile(file, false);
    }

    public static boolean deleteFile(File file, boolean sync) {

        if (file == null) {
            return true;
        }
        if (OS.isWindows()) {
            sync = true;
        }
        if (!sync || file.isDirectory() || !file.exists()) {
            return file.delete();
        }
        long sleep = 1;
        for (int i = 0; i < ATTEMPTS_COUNT; i++) {
            if (file.delete() && !file.exists()) {
                return true;
            }
            if (!file.exists()) {
                return true;
            }
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                return false;
            }
            if (sleep < 128) {
                sleep = sleep * 2;
            }
        }
        return false;
    }

    public static RandomAccessFile openFile(File file, String mode) throws FileNotFoundException {

        if (file == null) {
            return null;
        }
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (OS.isWindows()) {
            long sleep = 1;
            for (int i = 0; i < ATTEMPTS_COUNT; i++) {
                try {
                    return new RandomAccessFile(file, mode);
                } catch (FileNotFoundException e) {
                    try {
                        Thread.sleep(sleep);
                    } catch (InterruptedException e1) {
                        return null;
                    }
                }
                if (sleep < 128) {
                    sleep = sleep * 2;
                }
            }
        }
        return new RandomAccessFile(file, mode);
    }

    public static class SqlJetOsType {

        public boolean isWindows;
        public boolean isOS2;
        public boolean isOSX;
        public boolean isBSD;
        public boolean isLinux;
        public boolean isSolaris;
        public boolean isOpenVMS;

        public boolean is32Bit;
        public boolean is64Bit;

        public SqlJetOsType() {

            final String osName = System.getProperty("os.name");
            final String osNameLC = osName == null ? null : osName.toLowerCase();

            boolean windows = osName != null && osNameLC.indexOf("windows") >= 0;
            if (!windows && osName != null) {
                windows = osNameLC.indexOf("os/2") >= 0;
                isOS2 = windows;
            } else {
                isOS2 = false;
            }

            isWindows = windows;
            isOSX = osName != null && (osNameLC.indexOf("mac") >= 0 || osNameLC.indexOf("darwin") >= 0);
            isLinux = osName != null && (osNameLC.indexOf("linux") >= 0 || osNameLC.indexOf("hp-ux") >= 0);
            isBSD = !isLinux && osName != null && osNameLC.indexOf("bsd") >= 0;
            isSolaris = !isLinux && !isBSD && osName != null
                    && (osNameLC.indexOf("solaris") >= 0 || osNameLC.indexOf("sunos") >= 0);
            isOpenVMS = !isOSX && osName != null && osNameLC.indexOf("openvms") >= 0;

            if (!isWindows && !isOSX && !isLinux && !isBSD && !isSolaris && !isOpenVMS && !isOS2) {
                // fallback to some default.
                isLinux = true;
            }

            is32Bit = "32".equals(System.getProperty("sun.arch.data.model", "32"));
            is64Bit = "64".equals(System.getProperty("sun.arch.data.model", "64"));

        }

        public boolean isWindows() {
            return isWindows;
        }

        public boolean isOS2() {
            return isOS2;
        }

        public boolean isOSX() {
            return isOSX;
        }

        public boolean isBSD() {
            return isBSD;
        }

        public boolean isLinux() {
            return isLinux;
        }

        public boolean isSolaris() {
            return isSolaris;
        }

        public boolean isOpenVMS() {
            return isOpenVMS;
        }

        public boolean is32Bit() {
            return is32Bit;
        }

        public boolean is64Bit() {
            return is64Bit;
        }

    }

}
