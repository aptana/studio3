/*
 * Copyright (c) 2015, 2016, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package com.oracle.js.parser.ir;

import java.util.List;

import com.oracle.js.parser.TokenType;

/**
 * Module information.
 */
public final class Module {
    /**
     * The synthetic binding name assigned to export default declarations with unnamed expressions.
     */
    public static final String DEFAULT_EXPORT_BINDING_NAME = "*default*";
    public static final String DEFAULT_NAME = "default";
    public static final String STAR_NAME = "*";

    public static final class ExportEntry {
        private final String exportName;
        private final String moduleRequest;
        private final String importName;
        private final String localName;

        private ExportEntry(String exportName, String moduleRequest, String importName, String localName) {
            this.exportName = exportName;
            this.moduleRequest = moduleRequest;
            this.importName = importName;
            this.localName = localName;
        }

        public static ExportEntry exportStarFrom(String moduleRequest) {
            return new ExportEntry(null, moduleRequest, STAR_NAME, null);
        }

        public static ExportEntry exportDefault() {
            return exportDefault(DEFAULT_EXPORT_BINDING_NAME);
        }

        public static ExportEntry exportDefault(String localName) {
            return new ExportEntry(DEFAULT_NAME, null, null, localName);
        }

        public static ExportEntry exportSpecifier(String exportName, String localName) {
            return new ExportEntry(exportName, null, null, localName);
        }

        public static ExportEntry exportSpecifier(String exportName) {
            return exportSpecifier(exportName, exportName);
        }

        public ExportEntry withFrom(@SuppressWarnings("hiding") String moduleRequest) {
            return new ExportEntry(exportName, moduleRequest, localName, null);
        }

        public String getExportName() {
            return exportName;
        }

        public String getModuleRequest() {
            return moduleRequest;
        }

        public String getImportName() {
            return importName;
        }

        public String getLocalName() {
            return localName;
        }

        @Override
        public String toString() {
            return "ExportEntry [exportName=" + exportName + ", moduleRequest=" + moduleRequest + ", importName=" + importName + ", localName=" + localName + "]";
        }
    }

    public static final class ImportEntry {
        private final String moduleRequest;
        private final IdentNode importName;
        private final IdentNode localName;

        private ImportEntry(String moduleRequest, IdentNode importName, IdentNode localName) {
            this.moduleRequest = moduleRequest;
            this.importName = importName;
            this.localName = localName;
        }

        public static ImportEntry importDefault(IdentNode localName) {
            return new ImportEntry(null, new IdentNode(TokenType.IDENT.ordinal(), 0, DEFAULT_NAME), localName);
        }

        public static ImportEntry importStarAsNameSpaceFrom(IdentNode localNameSpace) {
            return new ImportEntry(null, new IdentNode(TokenType.MUL.ordinal(), 0, STAR_NAME), localNameSpace);
        }

        public static ImportEntry importSpecifier(IdentNode importName, IdentNode localName) {
            return new ImportEntry(null, importName, localName);
        }

        public static ImportEntry importSpecifier(IdentNode importName) {
            return importSpecifier(importName, importName);
        }

        public ImportEntry withFrom(@SuppressWarnings("hiding") String moduleRequest) {
            return new ImportEntry(moduleRequest, importName, localName);
        }

        public String getModuleRequest() {
            return moduleRequest;
        }

        public String getImportName() {
            return importName.getName();
        }

        public String getLocalName() {
            return localName.getName();
        }

        @Override
        public String toString() {
            return "ImportEntry [moduleRequest=" + moduleRequest + ", importName=" + importName + ", localName=" + localName + "]";
        }

		public IdentNode getLocalNameNode()
		{
			return localName;
		}

		public IdentNode getImportNameNode()
		{
			return importName;
		}
    }

    private final List<String> requestedModules;
    private final List<ImportEntry> importEntries;
    private final List<ExportEntry> localExportEntries;
    private final List<ExportEntry> indirectExportEntries;
    private final List<ExportEntry> starExportEntries;

    public Module(List<String> requestedModules, List<ImportEntry> importEntries, List<ExportEntry> localExportEntries, List<ExportEntry> indirectExportEntries,
                    List<ExportEntry> starExportEntries) {
        this.requestedModules = requestedModules;
        this.importEntries = importEntries;
        this.localExportEntries = localExportEntries;
        this.indirectExportEntries = indirectExportEntries;
        this.starExportEntries = starExportEntries;
    }

    public List<String> getRequestedModules() {
        return requestedModules;
    }

    public List<ImportEntry> getImportEntries() {
        return importEntries;
    }

    public List<ExportEntry> getLocalExportEntries() {
        return localExportEntries;
    }

    public List<ExportEntry> getIndirectExportEntries() {
        return indirectExportEntries;
    }

    public List<ExportEntry> getStarExportEntries() {
        return starExportEntries;
    }

    @Override
    public String toString() {
        return "Module [requestedModules=" + requestedModules + ", importEntries=" + importEntries + ", localExportEntries=" + localExportEntries + ", indirectExportEntries=" +
                        indirectExportEntries + ", starExportEntries=" + starExportEntries + "]";
    }
}
