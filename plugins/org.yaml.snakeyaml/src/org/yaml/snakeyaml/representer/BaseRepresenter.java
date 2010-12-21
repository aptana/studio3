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

package org.yaml.snakeyaml.representer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.DumperOptions.ScalarStyle;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.serializer.Serializer;

/**
 * Represent basic YAML structures: scalar, sequence, mapping
 * 
 * @see <a href="http://pyyaml.org/wiki/PyYAML">PyYAML</a> for more information
 */
public abstract class BaseRepresenter {
    @SuppressWarnings("unchecked")
    protected final Map<Class, Represent> representers = new HashMap<Class, Represent>();
    /**
     * in Java 'null' is not a type. So we have to keep the null representer
     * separately otherwise it will coincide with the default representer which
     * is stored with the key null.
     */
    protected Represent nullRepresenter;
    @SuppressWarnings("unchecked")
    //the order is important (map can be also a sequence of key-values)
    protected final Map<Class, Represent> multiRepresenters = new LinkedHashMap<Class, Represent>();
    private Character defaultStyle;
    protected Boolean defaultFlowStyle;
    protected final Map<Object, Node> representedObjects = new IdentityHashMap<Object, Node>();
    private final Set<Object> objectKeeper = new HashSet<Object>();
    protected Object objectToRepresent;
    private PropertyUtils propertyUtils;
    private boolean explicitPropertyUtils = false;

    public void represent(Serializer serializer, Object data) throws IOException {
        Node node = representData(data);
        serializer.serialize(node);
        representedObjects.clear();
        objectKeeper.clear();
        objectToRepresent = null;
    }

    @SuppressWarnings("unchecked")
    protected Node representData(Object data) {
        objectToRepresent = data;
        if (!ignoreAliases(data)) {
            // check for identity
            if (representedObjects.containsKey(objectToRepresent)) {
                Node node = representedObjects.get(objectToRepresent);
                return node;
            }
        }
        // check for null first
        if (data == null) {
            Node node = nullRepresenter.representData(data);
            return node;
        }
        // check the same class
        Node node;
        Class clazz = data.getClass();
        if (representers.containsKey(clazz)) {
            Represent representer = representers.get(clazz);
            node = representer.representData(data);
        } else {
            // check the parents
            for (Class repr : multiRepresenters.keySet()) {
                if (repr.isInstance(data)) {
                    Represent representer = multiRepresenters.get(repr);
                    node = representer.representData(data);
                    return node;
                }
            }
            // check array of primitives
            if (clazz.isArray()) {
                throw new YAMLException("Arrays of primitives are not fully supported.");
            }
            // check defaults
            if (multiRepresenters.containsKey(null)) {
                Represent representer = multiRepresenters.get(null);
                node = representer.representData(data);
            } else {
                Represent representer = representers.get(null);
                node = representer.representData(data);
            }
        }
        return node;
    }

    protected Node representScalar(Tag tag, String value, Character style) {
        if (style == null) {
            style = this.defaultStyle;
        }
        Node node = new ScalarNode(tag, value, null, null, style);
        representedObjects.put(objectToRepresent, node);
        return node;
    }

    protected Node representScalar(Tag tag, String value) {
        return representScalar(tag, value, null);
    }

    protected Node representSequence(Tag tag, Iterable<? extends Object> sequence, Boolean flowStyle) {
        int size = 10;// default for ArrayList
        if (sequence instanceof List<?>) {
            size = ((List<?>) sequence).size();
        }
        List<Node> value = new ArrayList<Node>(size);
        SequenceNode node = new SequenceNode(tag, value, flowStyle);
        representedObjects.put(objectToRepresent, node);
        boolean bestStyle = true;
        for (Object item : sequence) {
            Node nodeItem = representData(item);
            if (!((nodeItem instanceof ScalarNode && ((ScalarNode) nodeItem).getStyle() == null))) {
                bestStyle = false;
            }
            value.add(nodeItem);
        }
        if (flowStyle == null) {
            if (defaultFlowStyle != null) {
                node.setFlowStyle(defaultFlowStyle);
            } else {
                node.setFlowStyle(bestStyle);
            }
        }
        return node;
    }

    protected Node representMapping(Tag tag, Map<? extends Object, Object> mapping,
            Boolean flowStyle) {
        List<NodeTuple> value = new ArrayList<NodeTuple>(mapping.size());
        MappingNode node = new MappingNode(tag, value, flowStyle);
        representedObjects.put(objectToRepresent, node);
        boolean bestStyle = true;
        for (Object itemKey : mapping.keySet()) {
            Object itemValue = mapping.get(itemKey);
            Node nodeKey = representData(itemKey);
            Node nodeValue = representData(itemValue);
            if (!((nodeKey instanceof ScalarNode && ((ScalarNode) nodeKey).getStyle() == null))) {
                bestStyle = false;
            }
            if (!((nodeValue instanceof ScalarNode && ((ScalarNode) nodeValue).getStyle() == null))) {
                bestStyle = false;
            }
            value.add(new NodeTuple(nodeKey, nodeValue));
        }
        if (flowStyle == null) {
            if (defaultFlowStyle != null) {
                node.setFlowStyle(defaultFlowStyle);
            } else {
                node.setFlowStyle(bestStyle);
            }
        }
        return node;
    }

    protected abstract boolean ignoreAliases(Object data);

    public void setDefaultScalarStyle(ScalarStyle defaultStyle) {
        this.defaultStyle = defaultStyle.getChar();
    }

    public void setDefaultFlowStyle(FlowStyle defaultFlowStyle) {
        this.defaultFlowStyle = defaultFlowStyle.getStyleBoolean();
    }

    public void setPropertyUtils(PropertyUtils propertyUtils) {
        this.propertyUtils = propertyUtils;
        this.explicitPropertyUtils = true;
    }

    public final PropertyUtils getPropertyUtils() {
        if (propertyUtils == null) {
            propertyUtils = new PropertyUtils();
        }
        return propertyUtils;
    }

    public final boolean isExplicitPropertyUtils() {
        return explicitPropertyUtils;
    }

}
