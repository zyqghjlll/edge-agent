package com.edge.agent.utils;

import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

/**
 * @author zyq
 * @description 日志工具类
 */
@Slf4j
public class SysLogger {
    private SysLogger() {
        throw new IllegalStateException("Utility class");
    }

    public static void info(String format, Object... param) {
        log.info(replace(stringFormat(format, param)));
    }

    public static void error(Throwable throwable) {
        log.error(replace("异常信息:" + throwable.getMessage()));
    }

    public static void error(Throwable throwable, String format, Object... param) {
        log.error(replace(stringFormat(format, param) + "异常信息:" + throwable.getMessage()));
    }

    public static void warn(String format, Object... param) {
        log.warn(replace(stringFormat(format, param)));
    }

    public static void warn(Throwable throwable, String format, Object... param) {
        log.warn(replace(stringFormat(format, param) + ", reason:" + throwable.getMessage()));
    }

    public static void debug(String format, Object... param) {
        log.info(replace(stringFormat(format, param)));
    }

    public static String stringFormat(String format, Object... param) {
        String result = "";
        try {
            ArrayList<String> paramList = new ArrayList<>();
            if (ObjectUtil.isNotEmpty(param)) {
                for (int i = 0; i < param.length; i++) {
                    // 创建ObjectMapper对象
                    ObjectMapper mapper = new ObjectMapper();
                    // 将Java对象转换为JSON对象
                    String jsonString = mapper.writeValueAsString(param[i]);
                    paramList.add(jsonString);
                }
            }
            result = String.format(format, paramList.toArray());
        } catch (Exception e) {
            error(e, "An exception occurred while logger format. format:[%s]", format);
        }
        return result;
    }

    private static String replace(String str) {
        if (ObjectUtil.isNotEmpty(str)) {
            str = str.replaceAll("\n", "换行").replaceAll("\r", "空格").replaceAll("\\\\", "斜杠");
        }
        return str;
    }
}
