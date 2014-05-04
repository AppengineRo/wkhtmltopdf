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
