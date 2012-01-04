/**
 * Copyright © 2010-2011 Nokia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.restdriver;

import java.util.regex.Pattern;

import com.github.restdriver.matchers.MatchesRegex;

public final class Matchers {
    
    private Matchers() {
    }
    
    public static MatchesRegex matchingRegex(String pattern) {
        return new MatchesRegex(Pattern.compile(pattern));
    }
    
    public static MatchesRegex matchingRegex(Pattern pattern) {
        return new MatchesRegex(pattern);
    }
    
}
