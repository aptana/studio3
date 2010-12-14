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

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

/**
 * Convenience utility to serialize JavaBeans.
 */
public class JavaBeanDumper {
    private boolean useGlobalTag;
    private FlowStyle flowStyle;
    private DumperOptions options;
    private Representer representer;
    private Set<Class<? extends Object>> classTags;
    private final BeanAccess beanAccess;

    /**
     * Create Dumper for JavaBeans
     * 
     * @param useGlobalTag
     *            true to emit the global tag with the class name
     */
    public JavaBeanDumper(boolean useGlobalTag, BeanAccess beanAccess) {
        this.useGlobalTag = useGlobalTag;
        this.beanAccess = beanAccess;
        this.flowStyle = FlowStyle.BLOCK;
        classTags = new HashSet<Class<? extends Object>>();
    }

    public JavaBeanDumper(boolean useGlobalTag) {
        this(useGlobalTag, BeanAccess.DEFAULT);
    }

    public JavaBeanDumper(BeanAccess beanAccess) {
        this(false, beanAccess);
    }

    /**
     * Create Dumper for JavaBeans. Use "tag:yaml.org,2002:map" as the root tag.
     */
    public JavaBeanDumper() {
        this(BeanAccess.DEFAULT);
    }

    public JavaBeanDumper(Representer representer, DumperOptions options) {
        if (representer == null) {
            throw new NullPointerException("Representer must be provided.");
        }
        if (options == null) {
            throw new NullPointerException("DumperOptions must be provided.");
        }
        this.options = options;
        this.representer = representer;
        this.beanAccess = null; // bean access in not used if representer
        // supplied
    }

    /**
     * Serialize JavaBean
     * 
     * @param data
     *            JavaBean instance to serialize
     * @param output
     *            destination
     */
    public void dump(Object data, Writer output) {
        DumperOptions doptions;
        if (this.options == null) {
            doptions = new DumperOptions();
            if (!useGlobalTag) {
                doptions.setExplicitRoot(Tag.MAP);
            }
            doptions.setDefaultFlowStyle(flowStyle);
        } else {
            doptions = this.options;
        }
        Representer repr;
        if (this.representer == null) {
            repr = new Representer();
            repr.getPropertyUtils().setBeanAccess(beanAccess);
            for (Class<? extends Object> clazz : classTags) {
                repr.addClassTag(clazz, Tag.MAP);
            }
        } else {
            repr = this.representer;
        }
        Yaml dumper = new Yaml(repr, doptions);
        dumper.dump(data, output);
    }

    /**
     * Serialize JavaBean
     * 
     * @param data
     *            JavaBean instance to serialize
     * @return serialized YAML document
     */
    public String dump(Object data) {
        StringWriter buffer = new StringWriter();
        dump(data, buffer);
        return buffer.toString();
    }

    public boolean isUseGlobalTag() {
        return useGlobalTag;
    }

    public void setUseGlobalTag(boolean useGlobalTag) {
        this.useGlobalTag = useGlobalTag;
    }

    public FlowStyle getFlowStyle() {
        return flowStyle;
    }

    public void setFlowStyle(FlowStyle flowStyle) {
        this.flowStyle = flowStyle;
    }

    /**
     * Skip global tag with the specified class in a type-safe collection
     * 
     * @param clazz
     *            JavaBean <code>Class</code> to represent as Map
     */
    public void setMapTagForBean(Class<? extends Object> clazz) {
        classTags.add(clazz);
    }
}
