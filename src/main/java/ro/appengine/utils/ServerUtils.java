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

package ro.appengine.utils;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.utils.SystemProperty;
import com.google.apphosting.api.ApiProxy;

import javax.servlet.http.HttpServletRequest;

public class ServerUtils {
    /**
     * Checks the request for 2 headers that app engine sets for the tasks.<br />
     * This is considered safe according to https://developers.google.com/appengine/docs/java/taskqueue/overview-push#Java_Task_execution
     *
     * @return true is the current request runs as a task
     */
    public static boolean isTask(HttpServletRequest req) {
        String taskName = req.getHeader("X-AppEngine-TaskName");
        String taskQueue = req.getHeader("X-AppEngine-QueueName");
        return ((taskName != null) && !taskName.isEmpty() && (taskQueue != null) && !taskQueue.isEmpty());
    }

    /**
     * @return the email from user service or the email from the request if is Task
     */
    public static String getCurrentEmail(HttpServletRequest req) {
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        if (isTask(req)) {
            return req.getParameter("_currentEmail");
        } else if (user != null) {
            return user.getEmail();
        }
        return null;
    }


    /**
     * @return true is the server runs locally
     */
    public static boolean isLocalServer() {
        return (SystemProperty.environment.value() == SystemProperty.Environment.Value.Development);
    }

    /**
     *
     * @return the app-id without the s~ at the front
     */
    public static String getAppId() {
        String result = ApiProxy.getCurrentEnvironment().getAppId();
        return result.substring(result.indexOf("~") + 1);
    }

    /**
     *
     * @return the app version; usualy in the format moduleName:majorVersion.minorVersion
     */
    public static String getServerVersion() {
        return SystemProperty.applicationVersion.get();
    }


}
