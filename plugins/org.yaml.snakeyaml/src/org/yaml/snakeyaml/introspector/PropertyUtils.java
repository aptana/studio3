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

package org.yaml.snakeyaml.introspector;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.yaml.snakeyaml.error.YAMLException;

public class PropertyUtils {

    private final Map<Class<?>, Map<String, Property>> propertiesCache = new HashMap<Class<?>, Map<String, Property>>();
    private final Map<Class<?>, Set<Property>> readableProperties = new HashMap<Class<?>, Set<Property>>();
    private BeanAccess beanAccess = BeanAccess.DEFAULT;
    private boolean allowReadOnlyProperties = false;

    private Map<String, Property> getPropertiesMap(Class<?> type,
            BeanAccess bAccess) throws IntrospectionException {
        if (propertiesCache.containsKey(type)) {
            return propertiesCache.get(type);
        }

        Map<String, Property> properties = new HashMap<String, Property>();
        switch (bAccess) {
            case FIELD:
                for (Class<?> c = type; c != null; c = c.getSuperclass()) {
                    for (Field field : c.getDeclaredFields()) {
                        int modifiers = field.getModifiers();
                        if (!Modifier.isStatic(modifiers)
                                && !Modifier.isTransient(modifiers)
                                && !properties.containsKey(field.getName())) {
                            properties.put(field.getName(), new FieldProperty(
                                    field));
                        }
                    }
                }
                break;
            default:
                // add JavaBean properties
                for (PropertyDescriptor property : Introspector.getBeanInfo(type)
                                                               .getPropertyDescriptors()) {
                    Method readMethod = property.getReadMethod();
                    if (readMethod == null
                            || !readMethod.getName().equals("getClass")) {
                        properties.put(property.getName(), new MethodProperty(
                                property));
                    }
                }

                // add public fields
                for (Field field : type.getFields()) {
                    int modifiers = field.getModifiers();
                    if (!Modifier.isStatic(modifiers)
                            && !Modifier.isTransient(modifiers)) {
                        properties.put(field.getName(),
                                       new FieldProperty(field));
                    }
                }
                break;
        }
        if (properties.isEmpty()) {
            throw new YAMLException("No JavaBean properties found in "
                    + type.getName());
        }
        propertiesCache.put(type, properties);
        return properties;
    }

    public Set<Property> getProperties(Class<? extends Object> type)
            throws IntrospectionException {
        return getProperties(type, beanAccess);
    }

    public Set<Property> getProperties(Class<? extends Object> type,
            BeanAccess bAccess) throws IntrospectionException {
        if (readableProperties.containsKey(type)) {
            return readableProperties.get(type);
        }
        Set<Property> properties = new TreeSet<Property>();
        Collection<Property> props = getPropertiesMap(type, bAccess).values();
        for (Property property : props) {
            if (property.isReadable()
                    && (allowReadOnlyProperties || property.isWritable())) {
                properties.add(property);
            }
        }

        if (properties.isEmpty()) {
            throw new YAMLException("No JavaBean properties found in "
                    + type.getName());
        }

        readableProperties.put(type, properties);
        return properties;
    }

    public Property getProperty(Class<? extends Object> type, String name)
            throws IntrospectionException {
        return getProperty(type, name, beanAccess);
    }

    public Property getProperty(Class<? extends Object> type, String name,
            BeanAccess bAccess) throws IntrospectionException {
        Map<String, Property> properties = getPropertiesMap(type, bAccess);
        Property property = properties.get(name);
        if (property == null || !property.isWritable()) {
            throw new YAMLException("Unable to find property '" + name
                    + "' on class: " + type.getName());
        }
        return property;
    }

    public void setBeanAccess(BeanAccess beanAccess) {
        if (this.beanAccess != beanAccess) {
            this.beanAccess = beanAccess;
            propertiesCache.clear();
            readableProperties.clear();
        }
    }

    public void setAllowReadOnlyProperties(boolean allowReadOnlyProperties) {
        if (this.allowReadOnlyProperties != allowReadOnlyProperties) {
            this.allowReadOnlyProperties = allowReadOnlyProperties;
            readableProperties.clear();
        }
    }

//    private volatile static PropertyUtils singleton;
//
//    public static PropertyUtils getSingleton() {
//        if (singleton == null) {
//            synchronized (PropertyUtils.class) {
//                if (singleton == null) {
//                    singleton = new PropertyUtils();
//                }
//            }
//        }
//        return singleton;
//    }
}
