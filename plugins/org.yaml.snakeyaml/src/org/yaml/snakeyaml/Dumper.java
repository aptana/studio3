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

import java.io.Writer;
import java.util.Iterator;

import org.yaml.snakeyaml.emitter.Emitter;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.resolver.Resolver;
import org.yaml.snakeyaml.serializer.Serializer;

/**
 * @deprecated Dumper's functionality was moved to Yaml
 */
public class Dumper {
    protected final Representer representer;
    protected final DumperOptions options;
    private boolean attached = false;

    public Dumper(Representer representer, DumperOptions options) {
        this.representer = representer;
        representer.setDefaultFlowStyle(options.getDefaultFlowStyle());
        representer.setDefaultScalarStyle(options.getDefaultScalarStyle());
        representer.getPropertyUtils().setAllowReadOnlyProperties(
                options.isAllowReadOnlyProperties());
        this.options = options;
    }

    public Dumper(DumperOptions options) {
        this(new Representer(), options);
    }

    public Dumper(Representer representer) {
        this(representer, new DumperOptions());
    }

    public Dumper() {
        this(new Representer(), new DumperOptions());
    }

    public void dump(Iterator<? extends Object> iter, Writer output, Resolver resolver) {
        Serializer s = new Serializer(new Emitter(output, options), resolver, options);
        try {
            s.open();
            while (iter.hasNext()) {
                representer.represent(s, iter.next());
            }
            s.close();
        } catch (java.io.IOException e) {
            throw new YAMLException(e);
        }
    }

    /**
     * Because Dumper is stateful it cannot be shared
     */
    void setAttached() {
        if (!attached) {
            attached = true;
        } else {
            throw new YAMLException("Dumper cannot be shared.");
        }
    }
}
