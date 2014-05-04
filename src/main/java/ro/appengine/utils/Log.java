package ro.appengine.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Log {
    /**
     * Logs the objects with the level Warning <br />
     * This is the <b>3rd level</b> in App Engine
     *
     * @param obj the objects to log
     */
    public static void w(Object... obj) {
        echo(Level.WARNING, obj);
    }

    /**
     * Logs the objects with the level Debug (Config) <br />
     * This is the <b>1st (lowest) level</b> in App Engine <br /><br />
     * <b style="color:red">This will not print on localhost</b>
     *
     * @param obj the objects to log
     */
    public static void d(Object... obj) {
        echo(Level.CONFIG, obj);
    }

    /**
     * Logs the objects with the level Debug (Config) <br />
     * This is the <b>1st (lowest) level</b> in App Engine <br /><br />
     * <b style="color:red">This will not print on localhost</b>
     *
     * @param obj the objects to log
     */
    public static void c(Object... obj) {
        echo(Level.CONFIG, obj);
    }

    /**
     * Logs the objects with the level Severe <br />
     * This is the <b>4th (highest) level</b> in App Engine
     *
     * @param obj the objects to log
     */
    public static void s(Object... obj) {
        echo(Level.SEVERE, obj);
    }

    /**
     * Logs the objects with the level Info <br />
     * This is the <b>2nd level</b> in App Engine
     *
     * @param obj the objects to log
     */
    public static void i(Object... obj) {
        echo(Level.INFO, obj);
    }

    /**
     * Logs the objects with the level Info <br />
     * This is the <b>2nd level</b> in App Engine
     *
     * @param obj the objects to log
     */
    public static void log(Object... obj) {
        echo(obj);
    }

    /**
     * Logs the objects with custom level <br />
     * Custom levels in order:
     * <ol>
     * <li>Debug (Config)</li>
     * <li>Info</li>
     * <li>Warning</li>
     * <li>Severe</li>
     * </ol>
     *
     * @param level the log level
     * @param obj   the objects to log
     */
    public static void log(Level level, Object... obj) {
        echo(level, obj);
    }

    /**
     * Logs the objects with the level Info <br />
     * This is the <b>2nd level</b> in App Engine
     *
     * @param obj the objects to log
     */
    public static void echo(Object... obj) {
        echo(Level.INFO, obj);
    }

    /**
     * Logs the objects with custom level <br />
     * Custom levels in order:
     * <ol>
     * <li>Debug (Config)</li>
     * <li>Info</li>
     * <li>Warning</li>
     * <li>Severe</li>
     * </ol>
     *
     * @param level the log level
     * @param obj   the objects to log
     */
    public static void echo(Level level, Object... obj) {
        Logger log = Logger.getLogger("Logs");
        if (ServerUtils.isLocalServer()) {
            if (level.equals(Level.CONFIG)) {
                return;
            }
        }
        if (obj.length == 1) {
            if(obj[0] instanceof Throwable) {
                Throwable e = (Throwable) obj[0];
                log.log(level, e.getMessage(), e);
            } else if (obj[0] instanceof String) {
                echo(log, level, (String) obj[0]);
            } else {
                echo(log, level, GsonUtils.getGsonPrettyPrint().toJson(obj[0]));
            }
        } else {
            boolean exceptionFoud = false;
            for (Object o : obj) {
                if (o instanceof Exception) {
                    exceptionFoud = true;
                    break;
                }
            }
            if (exceptionFoud) {
                //print them one by one
                for (Object o : obj) {
                    if(o instanceof Throwable) {
                        Throwable e = (Throwable) o;
                        log.log(level, e.getMessage(), e);
                    } else if (o instanceof String) {
                        echo(log, level, (String) o);
                    } else {
                        echo(log, level, GsonUtils.getGsonPrettyPrint().toJson(o));
                    }
                }
            } else {
                echo(log, level, GsonUtils.getGsonPrettyPrint().toJson(obj));
            }
        }

    }

    private static void echo(Logger log, Level level, String message) {
        StackTraceElement[] traceElements = Thread.currentThread().getStackTrace();
        StackTraceElement goodTrace = null;
        for (StackTraceElement trace : traceElements) {
            if(!trace.getClassName().equals(Log.class.getName()) && !trace.getClassName().equals(Thread.class.getName())){
                goodTrace = trace;
                break;
            }
        }
        if (goodTrace != null) {
            String fullClassName = goodTrace.getClassName();
            String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
            String methodName = goodTrace.getMethodName();
            int lineNumber = goodTrace.getLineNumber();
            if (ServerUtils.isLocalServer()) {
                //aparently i should use new PrintWriter(new OutputStreamWriter(System.out, "UTF8"));  not sysout
                System.out.println(className + "." + methodName + "():" + lineNumber + "  " + message);
            } else {
                log.log(level, className + "." + methodName + "():" + lineNumber + "  " + message);
            }
        } else {
            if (ServerUtils.isLocalServer()) {
                System.out.println(message);
            } else {
                log.log(level, message);
            }
        }
    }
}
