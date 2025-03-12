package com.hz6826.clockin.init;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import java.util.Map;
import java.util.Set;

import com.hz6826.clockin.ClockIn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.DatabaseMetaData;
import java.sql.Statement;


/*
 * 储存了sql的信息
 * */
class SQLPair {
    public String dataBaseName;
    public JsonObject sql_tables;  // ???

    public SQLPair(String dataBaseName, JsonObject sql_tables) {
        this.sql_tables = sql_tables;
        this.databaseName = databaseName;
    }
};

public class DatabaseInit {

    private SQLPair sql;
    private Connection connection;
    private String DataBaseName;

    /*
     *   构造器初始化sql表,并根据数据库类型建立表
     * */
    public DatabaseInit(Connection DBconnection) {
        DatabaseMetaData metaData = null;
        try {
            metaData = DBconnection.getMetaData();
        } catch (SQLException e) {
            throw new RuntimeException("get database metadata error");
        }

        String dataBaseName = "";
        try {
            dataBaseName = metaData.getDatabaseProductName();
        } catch (SQLException e) {
            throw new RuntimeException("get database type error");
        }


        JsonArray sql_list = JsonParser.parseString(loadSource("sql/index.sql")).getAsJsonObject().getAsJsonArray();

        if (sql_list.getAsJsonArray().isEmpty()) {
            throw new RuntimeException("no sql list");
        }

        boolean databaseExists = false;

        for (JsonElement databaseElm : sql_list) {
            if (databaseElm.getAsString().equalsIgnoreCase(dataBaseName)) {
                databaseExists = true;
                break;
            }
        }

        if (!databaseExists) {
            throw new RuntimeException("no sql list");
        }

        //查找是否有匹配的数据库文件

        JsonObject sqlJson = JsonParser.parseString(loadSource("sql" + dataBaseName + "/index.json")).getAsJsonObject().getAsJsonObject();
        if (!sqlJson.get("sql_name").getAsString().equalsIgnoreCase(dataBaseName)) {
            throw new RuntimeException("sql name not match,may be the sql is bad");
        }
        ;

        JsonObject table_sql_list_array = sqlJson.get("tables").getAsJsonObject();

        this.sql = new sql_list(dataBaseName, table_sql_list_array);

        //有则添加

        //没有报错
    }


    public boolean export_data(String path) {

        return true;
    }

    public boolean import_data(String path) {
        return true;
    }

    public boolean createTable(String name) {

        String sql=this.sql.sql_tables.get(name).getAsString();
        if (sql == null) {
            return false;
        }
        try (Statement statement = this.connection.createStatement()) {
            statement.executeUpdate(sql);

        } catch (SQLException e) {
           throw  new RuntimeException("create table error",e);
        }
        return true;
    }


    public boolean deleteTable(String name) {
        
        return true;
    }

    public boolean createAll(String name) {
        try{
            this.connection.setAutoCommit(false);

        }catch (SQLException e){
            throw  new RuntimeException("can not create Transaction",e);
        }

        JsonObject jsonObject = this.sql.sql_tables;

        // 获取所有键值对
        Set<Map.Entry<String, com.google.gson.JsonElement>> entries = jsonObject.entrySet();

        // 遍历并打印所有数据

        String currentKey="";
        try {
            for (Map.Entry<String, com.google.gson.JsonElement> entry : entries) {
                currentKey= entry.getKey();
                String value = entry.getValue().getAsString(); // 根据值的类型选择合适的方法
                this.createTable(value);
            }
            // 提交事务
            this.connection.commit();
            this.connection.setAutoCommit(true);

        } catch (SQLException e) {
            // 回滚事务
            ClockIn.LOGGER.error("create failed:{}", currentKey);
            if (this.connection != null) {
                try {
                    this.connection.rollback();
                }
                catch (SQLException rollback) {
                    throw  new RuntimeException("rollback error",rollback);
                }
            }
        }finally {
            if (this.connection != null) {
                try {
                    this.connection.setAutoCommit(true);
                    this.connection.close();
                } catch (SQLException e) {
                    ClockIn.LOGGER.error("close connection error",e);
                }
            }
        }
        return true;
    }

    public boolean deleteAll() {
        return true;
    }

    public boolean checkTable() {
        return true;
    }

    /**
     * 从资源里加载文件
     *
     * @param fileName 资源名称
     * @return 资源内容
     */
    private static String loadSource(String fileName) {
        ClassLoader classLoader = DatabaseInit.class.getClassLoader();
        try (InputStream is = classLoader.getResourceAsStream("{filename}")) {
            if (is == null) {
                ClockIn.LOGGER.error("Source file {} not found! This is technically our fault. Please report it to us!", fileName);
                return "";
            }
            try (Scanner scanner = new Scanner(is, StandardCharsets.UTF_8)) {
                scanner.useDelimiter("\\A");
                return scanner.hasNext() ? scanner.next() : "";
            }
        } catch (IOException e) {
            ClockIn.LOGGER.error("An exception occured when loading source {}! This is technically our fault. Please report it to us!", fileName, e);
        }
        return "";
    }

}
