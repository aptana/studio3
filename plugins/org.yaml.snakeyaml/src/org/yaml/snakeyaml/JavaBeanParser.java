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

/**
 * Convenience utility to parse JavaBeans. The returned instance is enforced to
 * be of the same class as the provided argument. All the methods are Thread
 * safe. When the YAML document contains a global tag with the class definition
 * like '!!com.package.MyBean' it is ignored in favour of the runtime class.
 * 
 * @deprecated use JavaBeanLoader instead
 */
public class JavaBeanParser {

    private JavaBeanParser() {
    }

    /**
     * Parse the first YAML document in a stream and produce the corresponding
     * JavaBean.
     * 
     * @param yaml
     *            YAML document
     * @param javabean
     *            JavaBean class to be parsed
     * @return parsed JavaBean
     */
    public static <T> T load(String yaml, Class<T> javabean) {
        JavaBeanLoader<T> loader = new JavaBeanLoader<T>(javabean);
        return loader.load(yaml);
    }

    /**
     * Parse the first YAML document in a stream and produce the corresponding
     * JavaBean.
     * 
     * @param io
     *            data to load from (BOM is respected and removed)
     * @param javabean
     *            JavaBean class to be parsed
     * @return parsed JavaBean
     */
    public static <T> T load(InputStream io, Class<T> javabean) {
        JavaBeanLoader<T> loader = new JavaBeanLoader<T>(javabean);
        return loader.load(io);
    }

    /**
     * Parse the first YAML document in a stream and produce the corresponding
     * Java object.
     * 
     * @param io
     *            data to load from (BOM must not be present)
     * @param javabean
     *            JavaBean class to be parsed
     * @return parsed JavaBean
     */
    public static <T> T load(Reader io, Class<T> javabean) {
        JavaBeanLoader<T> loader = new JavaBeanLoader<T>(javabean);
        return loader.load(io);
    }
}
