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

import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.yaml.snakeyaml.composer.Composer;
import org.yaml.snakeyaml.constructor.BaseConstructor;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.emitter.Emitter;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.parser.Parser;
import org.yaml.snakeyaml.parser.ParserImpl;
import org.yaml.snakeyaml.reader.StreamReader;
import org.yaml.snakeyaml.reader.UnicodeReader;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.resolver.Resolver;
import org.yaml.snakeyaml.serializer.Serializer;

/**
 * Public YAML interface. Each Thread must have its own instance.
 */
public class Yaml {
    protected final Resolver resolver;
    private String name;
    protected BaseConstructor constructor;
    protected Representer representer;
    protected DumperOptions options;

    /**
     * Create Yaml instance. It is safe to create a few instances and use them
     * in different Threads.
     */
    public Yaml() {
        this(new Constructor(), new Representer(), new DumperOptions(), new Resolver());
    }

    /**
     * Create Yaml instance.
     * 
     * @param options
     *            DumperOptions to configure outgoing objects
     */
    public Yaml(DumperOptions options) {
        this(new Constructor(), new Representer(), options);
    }

    /**
     * Create Yaml instance. It is safe to create a few instances and use them
     * in different Threads.
     * 
     * @param representer
     *            Representer to emit outgoing objects
     */
    public Yaml(Representer representer) {
        this(new Constructor(), representer);
    }

    /**
     * Create Yaml instance. It is safe to create a few instances and use them
     * in different Threads.
     * 
     * @param constructor
     *            BaseConstructor to construct incoming documents
     */
    public Yaml(BaseConstructor constructor) {
        this(constructor, new Representer());
    }

    /**
     * Create Yaml instance. It is safe to create a few instances and use them
     * in different Threads.
     * 
     * @param constructor
     *            BaseConstructor to construct incoming documents
     * @param representer
     *            Representer to emit outgoing objects
     */
    public Yaml(BaseConstructor constructor, Representer representer) {
        this(constructor, representer, new DumperOptions());
    }

    /**
     * Create Yaml instance. It is safe to create a few instances and use them
     * in different Threads.
     * 
     * @param representer
     *            Representer to emit outgoing objects
     * @param options
     *            DumperOptions to configure outgoing objects
     */
    public Yaml(Representer representer, DumperOptions options) {
        this(new Constructor(), representer, options, new Resolver());
    }

    /**
     * Create Yaml instance. It is safe to create a few instances and use them
     * in different Threads.
     * 
     * @param constructor
     *            BaseConstructor to construct incoming documents
     * @param representer
     *            Representer to emit outgoing objects
     * @param options
     *            DumperOptions to configure outgoing objects
     */
    public Yaml(BaseConstructor constructor, Representer representer, DumperOptions options) {
        this(constructor, representer, options, new Resolver());
    }

    /**
     * Create Yaml instance. It is safe to create a few instances and use them
     * in different Threads.
     * 
     * @param constructor
     *            BaseConstructor to construct incoming documents
     * @param representer
     *            Representer to emit outgoing objects
     * @param options
     *            DumperOptions to configure outgoing objects
     * @param resolver
     *            Resolver to detect implicit type
     */
    public Yaml(BaseConstructor constructor, Representer representer, DumperOptions options,
            Resolver resolver) {
        if(!constructor.isExplicitPropertyUtils()) {
            constructor.setPropertyUtils(representer.getPropertyUtils());
        } else if(!representer.isExplicitPropertyUtils()) {
            representer.setPropertyUtils(constructor.getPropertyUtils());
        }
        this.constructor = constructor;
        representer.setDefaultFlowStyle(options.getDefaultFlowStyle());
        representer.setDefaultScalarStyle(options.getDefaultScalarStyle());
        representer.getPropertyUtils().setAllowReadOnlyProperties(
                options.isAllowReadOnlyProperties());
        this.representer = representer;
        this.options = options;
        this.resolver = resolver;
        this.name = "Yaml:" + System.identityHashCode(this);
    }

    /**
     * Serialize a Java object into a YAML String.
     * 
     * @param data
     *            Java object to be Serialized to YAML
     * @return YAML String
     */
    public String dump(Object data) {
        List<Object> list = new ArrayList<Object>(1);
        list.add(data);
        return dumpAll(list.iterator());
    }

    /**
     * Serialize a sequence of Java objects into a YAML String.
     * 
     * @param data
     *            Iterator with Objects
     * @return YAML String with all the objects in proper sequence
     */
    public String dumpAll(Iterator<? extends Object> data) {
        StringWriter buffer = new StringWriter();
        dumpAll(data, buffer);
        return buffer.toString();
    }

    /**
     * Serialize a Java object into a YAML stream.
     * 
     * @param data
     *            Java object to be Serialized to YAML
     * @param output
     *            stream to write to
     */
    public void dump(Object data, Writer output) {
        List<Object> list = new ArrayList<Object>(1);
        list.add(data);
        dumpAll(list.iterator(), output);
    }

    /**
     * Serialize a sequence of Java objects into a YAML stream.
     * 
     * @param data
     *            Iterator with Objects
     * @param output
     *            stream to write to
     */
    public void dumpAll(Iterator<? extends Object> data, Writer output) {
        Serializer s = new Serializer(new Emitter(output, options), resolver, options);
        try {
            s.open();
            while (data.hasNext()) {
                representer.represent(s, data.next());
            }
            s.close();
        } catch (java.io.IOException e) {
            throw new YAMLException(e);
        }
    }

    /**
     * Parse the first YAML document in a String and produce the corresponding
     * Java object. (Because the encoding in known BOM is not respected.)
     * 
     * @param yaml
     *            YAML data to load from (BOM must not be present)
     * @return parsed object
     */
    public Object load(String yaml) {
        return load(new StringReader(yaml));
    }

    /**
     * Parse the first YAML document in a stream and produce the corresponding
     * Java object.
     * 
     * @param io
     *            data to load from (BOM is respected and removed)
     * @return parsed object
     */
    public Object load(InputStream io) {
        return load(new UnicodeReader(io));
    }

    /**
     * Parse the first YAML document in a stream and produce the corresponding
     * Java object.
     * 
     * @param io
     *            data to load from (BOM must not be present)
     * @return parsed object
     */
    public Object load(Reader io) {
        Composer composer = new Composer(new ParserImpl(new StreamReader(io)), resolver);
        constructor.setComposer(composer);
        return constructor.getSingleData();
    }

    /**
     * Parse all YAML documents in a String and produce corresponding Java
     * objects.
     * 
     * @param yaml
     *            YAML data to load from (BOM must not be present)
     * @return an iterator over the parsed Java objects in this String in proper
     *         sequence
     */
    public Iterable<Object> loadAll(Reader yaml) {
        Composer composer = new Composer(new ParserImpl(new StreamReader(yaml)), resolver);
        constructor.setComposer(composer);
        Iterator<Object> result = new Iterator<Object>() {
            public boolean hasNext() {
                return constructor.checkData();
            }

            public Object next() {
                return constructor.getData();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
        return new YamlIterable(result);
    }

    private class YamlIterable implements Iterable<Object> {
        private Iterator<Object> iterator;

        public YamlIterable(Iterator<Object> iterator) {
            this.iterator = iterator;
        }

        public Iterator<Object> iterator() {
            return iterator;
        }

    }

    /**
     * Parse all YAML documents in a String and produce corresponding Java
     * objects. (Because the encoding in known BOM is not respected.)
     * 
     * @param yaml
     *            YAML data to load from (BOM must not be present)
     * @return an iterator over the parsed Java objects in this String in proper
     *         sequence
     */
    public Iterable<Object> loadAll(String yaml) {
        return loadAll(new StringReader(yaml));
    }

    /**
     * Parse all YAML documents in a stream and produce corresponding Java
     * objects.
     * 
     * @param yaml
     *            YAML data to load from (BOM is respected and ignored)
     * @return an iterator over the parsed Java objects in this stream in proper
     *         sequence
     */
    public Iterable<Object> loadAll(InputStream yaml) {
        return loadAll(new UnicodeReader(yaml));
    }

    /**
     * Parse the first YAML document in a stream and produce the corresponding
     * representation tree.
     * 
     * @param yaml
     *            YAML document
     * @return parsed root Node for the specified YAML document
     */
    public Node compose(Reader yaml) {
        Composer composer = new Composer(new ParserImpl(new StreamReader(yaml)), resolver);
        constructor.setComposer(composer);
        return composer.getSingleNode();
    }

    /**
     * Parse all YAML documents in a stream and produce corresponding
     * representation trees.
     * 
     * @param yaml
     *            stream of YAML documents
     * @return parsed root Nodes for all the specified YAML documents
     */
    public Iterable<Node> composeAll(Reader yaml) {
        final Composer composer = new Composer(new ParserImpl(new StreamReader(yaml)), resolver);
        constructor.setComposer(composer);
        Iterator<Node> result = new Iterator<Node>() {
            public boolean hasNext() {
                return composer.checkNode();
            }

            public Node next() {
                return composer.getNode();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
        return new NodeIterable(result);
    }

    private class NodeIterable implements Iterable<Node> {
        private Iterator<Node> iterator;

        public NodeIterable(Iterator<Node> iterator) {
            this.iterator = iterator;
        }

        public Iterator<Node> iterator() {
            return iterator;
        }
    }

    /**
     * Add an implicit scalar detector. If an implicit scalar value matches the
     * given regexp, the corresponding tag is assigned to the scalar.
     * 
     * @deprecated use Tag instead of String
     * @param tag
     *            tag to assign to the node
     * @param regexp
     *            regular expression to match against
     * @param first
     *            a sequence of possible initial characters or null (which means
     *            any).
     * 
     */
    public void addImplicitResolver(String tag, Pattern regexp, String first) {
        addImplicitResolver(new Tag(tag), regexp, first);
    }

    /**
     * Add an implicit scalar detector. If an implicit scalar value matches the
     * given regexp, the corresponding tag is assigned to the scalar.
     * 
     * @param tag
     *            tag to assign to the node
     * @param regexp
     *            regular expression to match against
     * @param first
     *            a sequence of possible initial characters or null (which means
     *            any).
     */
    public void addImplicitResolver(Tag tag, Pattern regexp, String first) {
        resolver.addImplicitResolver(tag, regexp, first);
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Get a meaningful name. It simplifies debugging in a multi-threaded
     * environment. If nothing is set explicitly the address of the instance is
     * returned.
     * 
     * @return human readable name
     */
    public String getName() {
        return name;
    }

    /**
     * Set a meaningful name to be shown in toString()
     * 
     * @param name
     *            human readable name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Parse a YAML stream and produce parsing events.
     * 
     * @param yaml
     *            YAML document(s)
     * @return parsed events
     */
    public Iterable<Event> parse(Reader yaml) {
        final Parser parser = new ParserImpl(new StreamReader(yaml));
        Iterator<Event> result = new Iterator<Event>() {
            public boolean hasNext() {
                return parser.peekEvent() != null;
            }

            public Event next() {
                return parser.getEvent();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
        return new EventIterable(result);
    }

    private class EventIterable implements Iterable<Event> {
        private Iterator<Event> iterator;

        public EventIterable(Iterator<Event> iterator) {
            this.iterator = iterator;
        }

        public Iterator<Event> iterator() {
            return iterator;
        }
    }

    public void setBeanAccess(BeanAccess beanAccess) {
        constructor.getPropertyUtils().setBeanAccess(beanAccess);
        representer.getPropertyUtils().setBeanAccess(beanAccess);
    }

    // deprecated
    /**
     * @deprecated use with Constructor instead of Loader
     */
    public Yaml(Loader loader) {
        this(loader, new Dumper(new DumperOptions()));
    }

    /**
     * @deprecated use with Constructor instead of Loader
     */
    public Yaml(Loader loader, Dumper dumper) {
        this(loader, dumper, new Resolver());
    }

    /**
     * @deprecated use with Constructor instead of Loader
     */
    public Yaml(Loader loader, Dumper dumper, Resolver resolver) {
        this(loader.constructor, dumper.representer, dumper.options, resolver);
    }

    /**
     * Create Yaml instance. It is safe to create a few instances and use them
     * in different Threads.
     * 
     * @param dumper
     *            Dumper to emit outgoing objects
     */
    @SuppressWarnings("deprecation")
    public Yaml(Dumper dumper) {
        this(new Constructor(), dumper.representer, dumper.options);
    }
}
