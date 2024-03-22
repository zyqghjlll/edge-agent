package com.edge.agent.utils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author ：wq
 * @date ：2022/11/11 14:04
 * @description：JdbcUtil工具类
 */
@Slf4j
@Component
public class JdbcUtil {
    /**
     * 连接超时时间
     */
    private static long timeout;

    @Value("${jdbc.connect.timeout:3}")
    public void setTimeout(long timeout) {
        JdbcUtil.timeout = timeout;
    }

    /**
     * 创建连接 JDBC
     *
     * @param userName
     * @param password
     * @param url
     * @param driverClass
     * @return
     * @throws Exception
     */
    public static Connection connCommon(String userName, String password, String url, String driverClass) throws Exception {
        final Connection[] conn = {null};
        ExecutorService executorService = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
        try {
            Callable<String> task = createConnection(userName, password, url, driverClass, conn);
            Future<String> future = executorService.submit(task);
            future.get(1, TimeUnit.SECONDS);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            executorService.shutdown();
        }
        return conn[0];
    }

    /**
     * 线程创建连接
     *
     * @param userName
     * @param password
     * @param url
     * @param driverClass
     * @param conn
     * @return
     */
    private static Callable<String> createConnection(String userName, String password, String url, String driverClass, Connection[] conn) {
        Callable<String> task = () -> {
            //执行耗时代码
            try {
                Class.forName(driverClass);
                conn[0] = DriverManager.getConnection(url, userName, password);
            } catch (Exception e) {
                log.error("jdbc连接失败," + e.getMessage());
            }
            return "jdbc连接成功";
        };
        return task;
    }

    /**
     * 查询
     */
    public static ResultSet query(Connection conn, String sql) {
        ResultSet resultSet = null;
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("jdbc执行sql失败,sql--> " + sql);
        }
        return resultSet;
    }

    /**
     * 执行语句
     */
    @SneakyThrows
    public static int executeUpdate(Connection conn, String sql) {
        int result = 0;
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = conn.prepareStatement(sql);
            result = preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("jdbc执行sql失败,sql--> " + sql);
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
        return result;
    }

    @SneakyThrows
    public static boolean executeBatch(Connection conn, String sql) {
        boolean isSuccess = false;
        PreparedStatement preparedStatement = null;
        try {
            conn.setAutoCommit(false);
            preparedStatement = conn.prepareStatement(sql);
            int[] result = preparedStatement.executeBatch();
            conn.commit();
            isSuccess = true;
            for (int i : result) {
                if (i == 0) {
                    conn.rollback();
                    isSuccess = false;
                }
            }
            conn.setAutoCommit(true);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("jdbc执行sql失败,sql--> " + sql);
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            return isSuccess;
        }
    }

    /**
     * 执行语句
     */
    @SneakyThrows
    public static int executeInsert(Connection conn, String sql) {
        int save = 0;
        Statement statement = null;
        try {
//			preparedStatement = conn.prepareStatement(sql);
//			save = preparedStatement.executeUpdate();
            statement = conn.createStatement();
            save = statement.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
//			log.error("jdbc执行sql失败,sql--> "+sql);
        } finally {
            if (statement != null) {
                statement.close();
            }
            return save;
        }
    }

    /**
     * 转对象
     *
     * @param rs
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> convertList(ResultSet rs, Class<T> clazz) {
        List<T> list = new ArrayList<T>();
        try {
            //获取ResultSet对象的列编号、类型和属性
            ResultSetMetaData data = rs.getMetaData();
            //获取列数
            int column = data.getColumnCount();
            //获取本类所有的属性
            Field[] fields = clazz.getDeclaredFields();
            //开始遍历结果集
            while (rs.next()) {
                //创建类类型实例
                T t = clazz.newInstance();
                for (int i = 1; i <= column; i++) {
                    //每一列的值
                    Object value = rs.getObject(i);
                    //获取每一列名称（别名）
                    String columnName = data.getColumnLabel(i);
                    //遍历所有属性对象
                    for (Field field : fields) {
                        //获取属性名
                        String name = field.getName();
                        //打破封装，忽略对封装修饰符的检测
                        field.setAccessible(true);
                        if (name.equals(columnName)) {
                            field.set(t, value);
                        }
                    }
                }
                list.add(t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return list;
        }
    }


    /**
     * 批量插入
     *
     * @param conn
     * @param data
     */
    public static void insertBatch(Connection conn, String sql, Map<Integer, List<Object>> data) {
        PreparedStatement prepareStatement = null;
        try {
            prepareStatement = conn.prepareStatement(sql);
            for (Map.Entry<Integer, List<Object>> entry : data.entrySet()) {
                List<Object> props = entry.getValue();
                for (int i = 0; i < props.size(); i++) {
                    prepareStatement.setObject(i + 1, props.get(i));
                }
            }
            prepareStatement.addBatch();
            prepareStatement.executeBatch();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
