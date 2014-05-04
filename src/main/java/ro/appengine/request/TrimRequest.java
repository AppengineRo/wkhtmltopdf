/*
 * Copyright (c) 2013 Asociatia AppEngine . All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package ro.appengine.request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Just trim the request
 */
public class TrimRequest extends HttpServletRequestWrapper {
    public TrimRequest(HttpServletRequest request) {
        super(request);
    }

    public String sanitize(String input) {
        if (input == null) {
            return null;
        }
        return input.trim()/*.replaceAll("[ ]{2,}", " ")*/;
    }

    @Override
    public String getParameter(String paramName) {
        String value = super.getParameter(paramName);
        return this.sanitize(value);
    }

    /**
     * Ignores the leading and trailing spaces from values
     *
     */
    @Override
    public String[] getParameterValues(String paramName) {
        String[] parameterValues = super.getParameterValues(paramName);
        if(parameterValues!=null){
            for(int i=0;i<parameterValues.length;i++){
                parameterValues[i] = sanitize(parameterValues[i]);
            }
        }
        return parameterValues;
    }
}
