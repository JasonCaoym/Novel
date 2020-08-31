package com.duoyue.lib.base.log;

import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Logger
{
    private static final String APP_TAG = "DuoYue@";

    private static final int LEVEL_DEBUG = 0;
    private static final int LEVEL_INFO = 1;
    private static final int LEVEL_WARN = 2;
    private static final int LEVEL_ERROR = 3;
    //private static final int LOG_LIMIT = 1024 * 4;
    private static final int LOG_LIMIT = 1024 * 3 + 512;

    public static void d(String tag, String log, Object... args)
    {
        format(LEVEL_DEBUG, tag, log, args);
    }

    public static void i(String tag, String log, Object... args)
    {
        format(LEVEL_INFO, tag, log, args);
    }

    public static void w(String tag, String log, Object... args)
    {
        format(LEVEL_WARN, tag, log, args);
    }

    public static void e(String tag, String log, Object... args)
    {
        format(LEVEL_ERROR, tag, log, args);
    }

    private static void format(int level, String tag, String log, Object... args)
    {
        if (!isDebug())
        {
            //非Debug模式, 不输出日志.
            return;
        }
        if (log == null)
        {
            return;
        }

        List<String> list = convertStringList(args);
        if (list == null || list.isEmpty())
        {
            print(level, tag, log);
            return;
        }

        StringBuffer buffer = new StringBuffer();
        Pattern pattern = Pattern.compile("\\{\\}");
        Matcher matcher = pattern.matcher(log);
        for (int i = 0; i < list.size(); i++)
        {
            if (matcher.hitEnd())
            {
                buffer.append(list.get(i));
            } else if (matcher.find())
            {
                matcher.appendReplacement(buffer, escape(list.get(i)));
            } else
            {
                matcher.appendTail(buffer);
                buffer.append(list.get(i));
            }
        }
        print(level, tag, buffer.toString());
    }

    private static void print(int level, String tag, String log)
    {
        //字符串超出限制长度时，自动分割。使用正则表达式可还原: [\r\n]*.*?\(@[\d]+/[\d]+\)
        int length = log.getBytes().length;
        if (length > LOG_LIMIT)
        {
            int pCount = length / LOG_LIMIT + (length % LOG_LIMIT > 0 ? 1 : 0);
            int pLength = log.length() / pCount;
            int index = 0;
            for (int i = 0; i < pCount - 1; i++)
            {
                println(level, tag, String.format("(@%d/%d)%s", i + 1, pCount, log.substring(index, index += pLength)));
            }
            println(level, tag, String.format("(@%d/%d)%s", pCount, pCount, log.substring(index)));
        } else
        {
            println(level, tag, log);
        }
    }

    private static void println(int level, String tag, String log)
    {
        String logTag = APP_TAG + tag;
        switch (level)
        {
            default:
            case LEVEL_DEBUG:
                Log.d(logTag, log);
                break;
            case LEVEL_INFO:
                Log.i(logTag, log);
                break;
            case LEVEL_WARN:
                Log.w(logTag, log);
                break;
            case LEVEL_ERROR:
                Log.e(logTag, log);
                break;
        }
    }

    private static List<String> convertStringList(Object... args)
    {
        if (args != null)
        {
            List<String> list = new ArrayList<>();
            for (Object arg : args)
            {
                list.add(convertString(arg));
            }
            return list;
        }
        return null;
    }

    private static String convertString(Object obj)
    {
        if (obj != null)
        {
            if (obj instanceof Throwable)
            {
                return getStackTraceString((Throwable) obj);
            } else if (obj instanceof Collection)
            {
                return getCollectionString((Collection) obj);
            } else if (obj instanceof Map)
            {
                return getMapString((Map) obj);
            } else if (obj.getClass().isArray())
            {
                return getArrayString(obj);
            }
        }
        return String.valueOf(obj);
    }

    private static String getCollectionString(Collection collection)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("[\n");
        for (Object obj : collection)
        {
            builder.append("\t");
            builder.append(String.valueOf(obj));
            builder.append("\n");
        }
        builder.append("]");
        return builder.toString();
    }

    private static String getMapString(Map map)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("[\n");
        for (Object obj : map.entrySet())
        {
            Map.Entry entry = (Map.Entry) obj;
            builder.append("\t");
            builder.append("(");
            builder.append(String.valueOf(entry.getKey()));
            builder.append(", ");
            builder.append(String.valueOf(entry.getValue()));
            builder.append(")");
            builder.append("\n");
        }
        builder.append("]");
        return builder.toString();
    }

    private static String getArrayString(Object array)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("[\n");
        int length = Array.getLength(array);
        for (int i = 0; i < length; i++)
        {
            builder.append("\t");
            builder.append(String.valueOf(Array.get(array, i)));
            builder.append("\n");
        }
        builder.append("]");
        return builder.toString();
    }

    private static String escape(String data)
    {
        int length = data.length();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++)
        {
            char c = data.charAt(i);
            switch (c)
            {
                case '\\':
                    builder.append("\\\\");
                    break;
                case '$':
                    builder.append("\\$");
                    break;
                default:
                    builder.append(c);
                    break;
            }
        }
        return builder.toString();
    }


    /**
     * 通过Throwable对象获取详细错误日志信息.
     *
     * @param throwable
     * @return
     */
    public static String getStackTraceString(Throwable throwable)
    {
        if (throwable == null)
        {
            return "";
        }
        StringWriter sw = null;
        PrintWriter pw = null;
        try
        {
            sw = new StringWriter();
            pw = new PrintWriter(sw);
            throwable.printStackTrace(pw);
            pw.flush();
            return "\n" + sw.toString();
        } catch (Throwable th)
        {
            return throwable.getMessage();
        } finally
        {
            if (pw != null)
            {
                pw.close();
            }
            if (sw != null)
            {
                try
                {
                    sw.close();
                } catch (Exception e)
                {
                }
            }
        }
    }

    /**
     * 是否为Debug模式.
     * @return
     */
    public static boolean isDebug()
    {
        return false;
    }
}
