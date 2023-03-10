/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ratpack.guice

import ratpack.impose.Impositions
import ratpack.test.internal.RatpackGroovyDslSpec

class ImposingBindingsSpec extends RatpackGroovyDslSpec {

  def "can impose"() {
    when:
    bindings {
      bindInstance(String, "foo")
    }
    handlers {
      get {
        render get(String)
      }
    }

    then:
    def t = Impositions.of {
      it.add(BindingsImposition.of { it.bindInstance(String, "bar") })
    } impose {
      text
    }

    t == "bar"
  }

  def "can impose on impositions"() {
    when:
    bindings {
      bindInstance(String, "foo")
    }
    handlers {
      get {
        render get(String)
      }
    }

    then:
    def t = Impositions.of {
      it.add(BindingsImposition.of { it.bindInstance(String, "bar") })
    } impose {
      Impositions.of {
        it.add(BindingsImposition.of { it.bindInstance(String, "baz") })
      } impose {
        text
      }
    }

    t == "baz"
  }
}
