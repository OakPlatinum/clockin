package com.hz6826.clockin.init;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.hz6826.clockin.ClockIn;

import java.io.File;

public class ConfigLoader {
    public static final String default_file_name = "clockin.json";

    public static ClockInConfig load(String file_name){
        ObjectMapper mapper = new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        try {
            if (!new File("./config/" + file_name).exists()) {
                mapper.writeValue(new File("./config/" + file_name), new ClockInConfig());
            }
            return mapper.readValue(new File("./config/" + file_name), ClockInConfig.class);
        } catch (Exception e) {
            ClockIn.LOGGER.error("Failed to load config file! Use default config.", e);
        }
        return new ClockInConfig();
    }

    public static ClockInConfig load(){
        return load(default_file_name);
    }

    public static void save(ClockInConfig config, String file_name){
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(new File("./config/" + file_name), config);
        } catch (Exception e) {
            ClockIn.LOGGER.error("Failed to save config file!", e);
        }
    }
}
