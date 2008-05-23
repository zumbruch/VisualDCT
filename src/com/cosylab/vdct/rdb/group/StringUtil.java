package com.cosylab.vdct.rdb.group;

public class StringUtil
{
    // Check if String has some valid content
    public static boolean isValid(String s)
    {
        return s != null  &&  s.length()>0;
    }

    public static boolean isValid(Object o)
    {
        return o != null  &&  o.toString().length()>0;
    }

    // make non-null String
    public static String makeNN(String s)
    {
        if (s == null)
            return "";
        return s;
    }

    // make non-null String
    public static String makeNN(Object o)
    {
        if (o == null)
            return "";
        return o.toString();
    }

    public static boolean isEqual(Object a, Object b)
    {
        return StringUtil.makeNN(a).equals(StringUtil.makeNN(b));
    }

    public static boolean isNumeric(String s)
    {
        int i, l = s.length();
        for (i=0; i<l; ++i)
            if (Character.getType(s.charAt(i)) !=
                Character.DECIMAL_DIGIT_NUMBER)
                return false;
        return true;
    }
};
