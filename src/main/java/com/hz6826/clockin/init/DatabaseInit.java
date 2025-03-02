package com.hz6826.clockin.init;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Scanner;

import com.google.gson.Gson;

import com.hz6826.clockin.ClockIn;
import org.jetbrains.annotations.Nullable;

public class DatabaseInit {

    private HashMap<String,String> sql;

    public DatabaseInit(){
        //读取配置文件
        Gson gson = new Gson();



        //获取当前数据库的类型

        //查找是否有匹配的数据库文件

        //有则添加

        //没有报错
    }

    public boolean createTable(String name){

        return true;
    }
    public boolean deleteTable(String name) {
        return true;
    }
    public boolean createAll(String name){
        return true;
    }
    public boolean deleteAll(){
        return true;
    }
    public boolean checkTable(){
        return true;
    }

    /**
     * 从资源里加载文件
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
        } catch (IOException e){
            ClockIn.LOGGER.error("An exception occured when loading source {}! This is technically our fault. Please report it to us!", fileName, e);
        }
        return "";
    }

}
