package com.edge.agent.utils;

import cn.hutool.core.util.ObjectUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author zyq
 */
public class StringUtil {
    private StringUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static List<String> jsonToList(String str, String split) {
        List<String> result = new ArrayList<>();
        if (ObjectUtil.isEmpty(str)) {
            return result;
        }
        if (str.startsWith("[")) {
            str = str.substring(1, str.length());
        }
        if (str.endsWith("]")) {
            str = str.substring(0, str.length() - 1);
        }
        result = Arrays.asList(str.split(split));
        return result;
    }
}
