package com.edge.agent.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author zyq
 */
public class CommonUtil {
    private CommonUtil() {
        throw new IllegalStateException("Utility class");
    }

    public  static Long getId() {
        return IdUtil.getSnowflake().nextId();
    }

    public  static Date getDate() {
        return getFormat(new Date());
    }

    public  static String getDateString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return dateFormat.format(new Date());
    }

    public  static Date getFormat(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return DateUtil.parse(dateFormat.format(date));
    }
}
