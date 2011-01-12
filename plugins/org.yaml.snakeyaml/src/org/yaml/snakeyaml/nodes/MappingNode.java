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

package org.yaml.snakeyaml.nodes;

import java.util.List;

import org.yaml.snakeyaml.error.Mark;

/**
 * Represents a map.
 * <p>
 * A map is a collection of unsorted key-value pairs.
 * </p>
 */
public class MappingNode extends CollectionNode {
    private Class<? extends Object> keyType;
    private Class<? extends Object> valueType;
    private List<NodeTuple> value;
    private boolean need2setTypes = true;

    public MappingNode(Tag tag, boolean resolved, List<NodeTuple> value, Mark startMark,
            Mark endMark, Boolean flowStyle) {
        super(tag, startMark, endMark, flowStyle);
        if (value == null) {
            throw new NullPointerException("value in a Node is required.");
        }
        this.value = value;
        keyType = Object.class;
        valueType = Object.class;
        this.resolved = resolved;
    }

    public MappingNode(Tag tag, List<NodeTuple> value, Boolean flowStyle) {
        this(tag, true, value, null, null, flowStyle);
    }

    @Override
    public NodeId getNodeId() {
        return NodeId.mapping;
    }

    /**
     * Returns the entries of this map.
     * 
     * @return List of entries.
     */
    public List<NodeTuple> getValue() {
        if (need2setTypes) {
            for (NodeTuple nodes : value) {
                nodes.getKeyNode().setType(keyType);
                nodes.getValueNode().setType(valueType);
            }
            need2setTypes = false;
        }
        return value;
    }

    public void setValue(List<NodeTuple> merge) {
        value = merge;
        need2setTypes = true;
    }

    public void setKeyType(Class<? extends Object> keyType) {
        this.keyType = keyType;
        need2setTypes = true;
    }

    public void setValueType(Class<? extends Object> valueType) {
        this.valueType = valueType;
        need2setTypes = true;
    }

    @Override
    public String toString() {
        String values;
        StringBuilder buf = new StringBuilder();
        for (NodeTuple node : getValue()) {
            buf.append("{ key=");
            buf.append(node.getKeyNode());
            buf.append("; value=");
            if (node.getValueNode() instanceof CollectionNode) {
                // to avoid overflow in case of recursive structures
                buf.append(System.identityHashCode(node.getValueNode()));
            } else {
                buf.append(node.toString());
            }
            buf.append(" }");
        }
        values = buf.toString();
        return "<" + this.getClass().getName() + " (tag=" + getTag() + ", values=" + values + ")>";
    }
}
