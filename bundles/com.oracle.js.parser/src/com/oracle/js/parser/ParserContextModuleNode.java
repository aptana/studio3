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
package com.oracle.js.parser;

import java.util.ArrayList;
import java.util.List;

import com.oracle.js.parser.ir.Module;
import com.oracle.js.parser.ir.Module.ExportEntry;
import com.oracle.js.parser.ir.Module.ImportEntry;

/**
 * ParserContextNode that represents a module.
 */
class ParserContextModuleNode extends ParserContextBaseNode {

    /** Module name. */
    private final String name;

    private List<String> requestedModules = new ArrayList<>();
    private List<ImportEntry> importEntries = new ArrayList<>();
    private List<ExportEntry> localExportEntries = new ArrayList<>();
    private List<ExportEntry> indirectExportEntries = new ArrayList<>();
    private List<ExportEntry> starExportEntries = new ArrayList<>();

    /**
     * Constructor.
     *
     * @param name name of the module
     */
    ParserContextModuleNode(final String name) {
        this.name = name;
    }

    /**
     * Returns the name of the module.
     *
     * @return name of the module
     */
    public String getModuleName() {
        return name;
    }

    public void addModuleRequest(String moduleRequest) {
        requestedModules.add(moduleRequest);
    }

    public void addImportEntry(ImportEntry importEntry) {
        importEntries.add(importEntry);
    }

    public void addLocalExportEntry(ExportEntry exportEntry) {
        localExportEntries.add(exportEntry);
    }

    public void addIndirectExportEntry(ExportEntry exportEntry) {
        indirectExportEntries.add(exportEntry);
    }

    public void addStarExportEntry(ExportEntry exportEntry) {
        starExportEntries.add(exportEntry);
    }

    public Module createModule() {
        return new Module(requestedModules, importEntries, localExportEntries, indirectExportEntries, starExportEntries);
    }
}
