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

import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;

/**
 * Represent JavaBeans
 */
public class Representer extends SafeRepresenter {

    public Representer() {
        this.representers.put(null, new RepresentJavaBean());
    }

    protected class RepresentJavaBean implements Represent {
        public Node representData(Object data) {
            try {
                return representJavaBean(getProperties(data.getClass()), data);
            } catch (IntrospectionException e) {
                throw new YAMLException(e);
            }
        }
    }

    /**
     * Tag logic:<br/>
     * - explicit root tag is set in serializer <br/>
     * - if there is a predefined class tag it is used<br/>
     * - a global tag with class name is always used as tag. The JavaBean parent
     * of the specified JavaBean may set another tag (tag:yaml.org,2002:map)
     * when the property class is the same as runtime class
     * 
     * @param properties
     *            JavaBean getters
     * @param javaBean
     *            instance for Node
     * @return Node to get serialized
     */
    protected MappingNode representJavaBean(Set<Property> properties, Object javaBean) {
        List<NodeTuple> value = new ArrayList<NodeTuple>(properties.size());
        Tag tag;
        Tag customTag = classTags.get(javaBean.getClass());
        tag = customTag != null ? customTag : new Tag(javaBean.getClass());
        // flow style will be chosen by BaseRepresenter
        MappingNode node = new MappingNode(tag, value, null);
        representedObjects.put(objectToRepresent, node);
        boolean bestStyle = true;
        for (Property property : properties) {
            Object memberValue = property.get(javaBean);
            NodeTuple tuple = representJavaBeanProperty(javaBean, property, memberValue, customTag);
            if (tuple == null) {
                continue;
            }
            if (((ScalarNode) tuple.getKeyNode()).getStyle() != null) {
                bestStyle = false;
            }
            Node nodeValue = tuple.getValueNode();
            if (!((nodeValue instanceof ScalarNode && ((ScalarNode) nodeValue).getStyle() == null))) {
                bestStyle = false;
            }
            value.add(tuple);
        }
        if (defaultFlowStyle != null) {
            node.setFlowStyle(defaultFlowStyle);
        } else {
            node.setFlowStyle(bestStyle);
        }
        return node;
    }

    /**
     * Represent one JavaBean property.
     * 
     * @param javaBean
     *            - the instance to be represented
     * @param property
     *            - the property of the instance
     * @param propertyValue
     *            - value to be represented
     * @param customTag
     *            - user defined Tag
     * @return NodeTuple to be used in a MappingNode. Return null to skip the
     *         property
     */
    protected NodeTuple representJavaBeanProperty(Object javaBean, Property property,
            Object propertyValue, Tag customTag) {
        ScalarNode nodeKey = (ScalarNode) representData(property.getName());
        boolean hasAlias = false;
        if (this.representedObjects.containsKey(propertyValue)) {
            // the first occurrence of the node must keep the tag
            hasAlias = true;
        }
        Node nodeValue = representData(propertyValue);
        // if possible try to avoid a global tag with a class name
        if (nodeValue instanceof MappingNode && !hasAlias) {
            // the node is a map, set or JavaBean
            if (!Map.class.isAssignableFrom(propertyValue.getClass())) {
                // the node is set or JavaBean
                if (customTag == null && !nodeValue.getTag().equals(Tag.SET)) {
                    // custom tag is not defined, set must keep the '!!set' tag
                    if (property.getType() == propertyValue.getClass()) {
                        // we do not need global tag because the property
                        // Class is the same as the runtime class
                        nodeValue.setTag(Tag.MAP);
                    }
                }
            }
        } else if (propertyValue != null && Enum.class.isAssignableFrom(propertyValue.getClass())) {
            nodeValue.setTag(Tag.STR);
        }
        if (nodeValue.getNodeId() != NodeId.scalar && !hasAlias) {
            // generic collections
            checkGlobalTag(property, nodeValue, propertyValue);
        }
        return new NodeTuple(nodeKey, nodeValue);
    }

    /**
     * Remove redundant global tag for a type safe (generic) collection if it is
     * the same as defined by the JavaBean property
     * 
     * @param property
     *            - JavaBean property
     * @param node
     *            - representation of the property
     * @param object
     *            - instance represented by the node
     */
    @SuppressWarnings("unchecked")
    protected void checkGlobalTag(Property property, Node node, Object object) {
        Class<?>[] arguments = property.getActualTypeArguments();
        if (arguments != null) {
            if (node.getNodeId() == NodeId.sequence) {
                // apply map tag where class is the same
                Class<? extends Object> t = arguments[0];
                SequenceNode snode = (SequenceNode) node;
                Iterable<Object> memberList;
                if (object.getClass().isArray()) {
                    memberList = Arrays.asList((Object[]) object);
                } else {
                    // list
                    memberList = (Iterable<Object>) object;
                }
                Iterator<Object> iter = memberList.iterator();
                for (Node childNode : snode.getValue()) {
                    Object member = iter.next();
                    if (member != null && t.equals(member.getClass())
                            && childNode.getNodeId() == NodeId.mapping) {
                        childNode.setTag(Tag.MAP);
                    }
                }
            } else if (object instanceof Set) {
                Class t = arguments[0];
                MappingNode mnode = (MappingNode) node;
                Iterator<NodeTuple> iter = mnode.getValue().iterator();
                Set set = (Set) object;
                for (Object member : set) {
                    NodeTuple tuple = iter.next();
                    if (t.equals(member.getClass())
                            && tuple.getKeyNode().getNodeId() == NodeId.mapping) {
                        tuple.getKeyNode().setTag(Tag.MAP);
                    }
                }
            } else if (node.getNodeId() == NodeId.mapping) {
                Class keyType = arguments[0];
                Class valueType = arguments[1];
                MappingNode mnode = (MappingNode) node;
                for (NodeTuple tuple : mnode.getValue()) {
                    resetTag(keyType, tuple.getKeyNode());
                    resetTag(valueType, tuple.getValueNode());
                }
            }
        }
    }

    private void resetTag(Class<? extends Object> type, Node node) {
        Tag tag = node.getTag();
        if (tag.matches(type)) {
            if (Enum.class.isAssignableFrom(type)) {
                node.setTag(Tag.STR);
            } else {
                node.setTag(Tag.MAP);
            }
        }
    }

    /**
     * Get JavaBean properties to be serialised. The order is respected. This
     * method may be overridden to provide custom property selection or order.
     * 
     * @param type
     *            - JavaBean to inspect the properties
     * @return properties to serialise
     */
    protected Set<Property> getProperties(Class<? extends Object> type)
            throws IntrospectionException {
        return getPropertyUtils().getProperties(type);
    }

}
