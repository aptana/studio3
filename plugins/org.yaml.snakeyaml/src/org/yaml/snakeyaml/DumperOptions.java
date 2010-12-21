/**
 * Copyright (c) 2008-2010, http://code.google.com/p/snakeyaml/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.yaml.snakeyaml;

import java.util.Map;

import org.yaml.snakeyaml.emitter.Emitter;
import org.yaml.snakeyaml.emitter.ScalarAnalysis;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.Tag;

/**
 * @see <a href="http://pyyaml.org/wiki/PyYAML">PyYAML</a> for more information
 */
public class DumperOptions {
    /**
     * YAML provides a rich set of scalar styles. Block scalar styles include
     * the literal style and the folded style; flow scalar styles include the
     * plain style and two quoted styles, the single-quoted style and the
     * double-quoted style. These styles offer a range of trade-offs between
     * expressive power and readability.
     * 
     * @see http://yaml.org/spec/1.1/#id858081
     */
    public enum ScalarStyle {
        DOUBLE_QUOTED(new Character('"')), SINGLE_QUOTED(new Character('\'')), LITERAL(
                new Character('|')), FOLDED(new Character('>')), PLAIN(null);
        private Character styleChar;

        private ScalarStyle(Character style) {
            this.styleChar = style;
        }

        public Character getChar() {
            return styleChar;
        }

        @Override
        public String toString() {
            return "Scalar style: '" + styleChar + "'";
        }

        public static ScalarStyle createStyle(Character style) {
            if (style == null) {
                return PLAIN;
            } else {
                switch (style) {
                case '"':
                    return DOUBLE_QUOTED;
                case '\'':
                    return SINGLE_QUOTED;
                case '|':
                    return LITERAL;
                case '>':
                    return FOLDED;
                default:
                    throw new YAMLException("Unknown scalar style character: " + style);
                }
            }
        }
    }

    /**
     * Block styles use indentation to denote nesting and scope within the
     * document. In contrast, flow styles rely on explicit indicators to denote
     * nesting and scope.
     * 
     * @see 3.2.3.1. Node Styles (http://yaml.org/spec/1.1)
     */
    public enum FlowStyle {
        FLOW(Boolean.TRUE), BLOCK(Boolean.FALSE), AUTO(null);

        private Boolean styleBoolean;

        private FlowStyle(Boolean flowStyle) {
            styleBoolean = flowStyle;
        }

        public Boolean getStyleBoolean() {
            return styleBoolean;
        }

        @Override
        public String toString() {
            return "Flow style: '" + styleBoolean + "'";
        }
    }

    /**
     * Platform dependent line break.
     */
    public enum LineBreak {
        WIN("\r\n"), MAC("\r"), UNIX("\n");

        private String lineBreak;

        private LineBreak(String lineBreak) {
            this.lineBreak = lineBreak;
        }

        public String getString() {
            return lineBreak;
        }

        @Override
        public String toString() {
            return "Line break: " + name();
        }

        public static LineBreak getPlatformLineBreak() {
            String platformLineBreak = System.getProperty("line.separator");
            for (LineBreak lb : values()) {
                if (lb.lineBreak.equals(platformLineBreak)) {
                    return lb;
                }
            }
            return LineBreak.UNIX;
        }
    }

    /**
     * Specification version. Currently supported 1.0 and 1.1
     */
    public enum Version {
        V1_0(new Integer[] { 1, 0 }), V1_1(new Integer[] { 1, 1 });

        private Integer[] version;

        private Version(Integer[] version) {
            this.version = version;
        }

        public Integer[] getArray() {
            return version;
        }

        @Override
        public String toString() {
            return "Version: " + version[0] + "." + version[1];
        }
    }

    private ScalarStyle defaultStyle = ScalarStyle.PLAIN;
    private FlowStyle defaultFlowStyle = FlowStyle.AUTO;
    private boolean canonical = false;
    private boolean allowUnicode = true;
    private boolean allowReadOnlyProperties = false;
    private int indent = 2;
    private int bestWidth = 80;
    private LineBreak lineBreak = LineBreak.UNIX;
    private boolean explicitStart = false;
    private boolean explicitEnd = false;
    private Tag explicitRoot = null;
    private Version version = null;
    private Map<String, String> tags = null;
    private Boolean prettyFlow = false;

    public boolean isAllowUnicode() {
        return allowUnicode;
    }

    /**
     * Specify whether to emit non-ASCII printable Unicode characters (to
     * support ASCII terminals). The default value is true.
     * 
     * @param allowUnicode
     *            if allowUnicode is false then all non-ASCII characters are
     *            escaped
     */
    public void setAllowUnicode(boolean allowUnicode) {
        this.allowUnicode = allowUnicode;
    }

    public ScalarStyle getDefaultScalarStyle() {
        return defaultStyle;
    }

    /**
     * Set default style for scalars. See YAML 1.1 specification, 2.3 Scalars
     * (http://yaml.org/spec/1.1/#id858081)
     * 
     * @param defaultStyle
     *            set the style for all scalars
     */
    public void setDefaultScalarStyle(ScalarStyle defaultStyle) {
        if (defaultStyle == null) {
            throw new NullPointerException("Use ScalarStyle enum.");
        }
        this.defaultStyle = defaultStyle;
    }

    public void setIndent(int indent) {
        if (indent < Emitter.MIN_INDENT) {
            throw new YAMLException("Indent must be at least " + Emitter.MIN_INDENT);
        }
        if (indent > Emitter.MAX_INDENT) {
            throw new YAMLException("Indent must be at most " + Emitter.MAX_INDENT);
        }
        this.indent = indent;
    }

    public int getIndent() {
        return this.indent;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public Version getVersion() {
        return this.version;
    }

    /**
     * Force the emitter to produce a canonical YAML document.
     * 
     * @param canonical
     *            true produce canonical YAML document
     * @return
     */
    public void setCanonical(boolean canonical) {
        this.canonical = canonical;
    }

    public boolean isCanonical() {
        return this.canonical;
    }

    /**
     * Force the emitter to produce a pretty YAML document when using the flow
     * style.
     * 
     * @param prettyFlow
     *            true produce pretty flow YAML document
     */
    public void setPrettyFlow(boolean prettyFlow) {
        this.prettyFlow = prettyFlow;
    }

    public boolean isPrettyFlow() {
        return this.prettyFlow;
    }

    /**
     * Specify the preferred width to emit scalars. When the scalar
     * representation takes more then the preferred with the scalar will be
     * split into a few lines. The default is 80.
     * 
     * @param bestWidth
     *            the preferred with for scalars.
     */
    public void setWidth(int bestWidth) {
        this.bestWidth = bestWidth;
    }

    public int getWidth() {
        return this.bestWidth;
    }

    public LineBreak getLineBreak() {
        return lineBreak;
    }

    public void setDefaultFlowStyle(FlowStyle defaultFlowStyle) {
        if (defaultFlowStyle == null) {
            throw new NullPointerException("Use FlowStyle enum.");
        }
        this.defaultFlowStyle = defaultFlowStyle;
    }

    public FlowStyle getDefaultFlowStyle() {
        return defaultFlowStyle;
    }

    public Tag getExplicitRoot() {
        return explicitRoot;
    }

    /**
     * @param expRoot
     *            tag to be used for the root node. (JavaBeans may use
     *            Tag.MAP="tag:yaml.org,2002:map")
     * @deprecated use Tag instead of String
     */
    public void setExplicitRoot(String expRoot) {
        setExplicitRoot(new Tag(expRoot));
    }

    /**
     * @param expRoot
     *            tag to be used for the root node. (JavaBeans may use
     *            Tag.MAP="tag:yaml.org,2002:map")
     */
    public void setExplicitRoot(Tag expRoot) {
        if (expRoot == null) {
            throw new NullPointerException("Root tag must be specified.");
        }
        this.explicitRoot = expRoot;
    }

    /**
     * Specify the line break to separate the lines. It is platform specific:
     * Windows - "\r\n", MacOS - "\r", Linux - "\n". The default value is the
     * one for Linux.
     */
    public void setLineBreak(LineBreak lineBreak) {
        if (lineBreak == null) {
            throw new NullPointerException("Specify line break.");
        }
        this.lineBreak = lineBreak;
    }

    public boolean isExplicitStart() {
        return explicitStart;
    }

    public void setExplicitStart(boolean explicitStart) {
        this.explicitStart = explicitStart;
    }

    public boolean isExplicitEnd() {
        return explicitEnd;
    }

    public void setExplicitEnd(boolean explicitEnd) {
        this.explicitEnd = explicitEnd;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    // TODO should use Tag ???
    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    /**
     * Define the ScalarStyle to be used for emitting scalars
     * 
     * @param analysis
     *            - Scalar meta data
     * @param style
     *            - automatically detected style
     * @return ScalarStyle to be used for scalar
     */
    public ScalarStyle calculateScalarStyle(ScalarAnalysis analysis, ScalarStyle style) {
        return style;
    }

    /**
     * Report whether read-only JavaBean properties (the ones without setters)
     * should be included in the YAML document
     * 
     * @return false when read-only JavaBean properties are not emitted
     */
    public boolean isAllowReadOnlyProperties() {
        return allowReadOnlyProperties;
    }

    /**
     * Set to true to include read-only JavaBean properties (the ones without
     * setters) in the YAML document. By default these properties are not
     * included to be able to parse later the same JavaBean.
     * 
     * @param allowReadOnlyProperties
     *            - true to dump read-only JavaBean properties
     */
    public void setAllowReadOnlyProperties(boolean allowReadOnlyProperties) {
        this.allowReadOnlyProperties = allowReadOnlyProperties;
    }
}
