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

package org.yaml.snakeyaml.parser;

import org.yaml.snakeyaml.events.Event;

/**
 * Helper for {@link ParserImpl}. A grammar rule to apply given the symbols on
 * top of its stack and the next input token
 * 
 * @see http://en.wikipedia.org/wiki/LL_parser
 */
interface Production {
    public Event produce();
}
