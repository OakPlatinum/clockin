package com.hz6826.clockin.server;

import com.hz6826.clockin.ClockIn;
import com.hz6826.clockin.command.CommandManager;
import com.hz6826.clockin.config.BasicConfig;
import com.hz6826.clockin.sql.DatabaseManager;
import com.hz6826.clockin.sql.MySQLDatabaseManager;
import com.hz6826.clockin.sql.SQLiteDatabaseManager;
import net.fabricmc.api.DedicatedServerModInitializer;
import org.slf4j.Logger;

import java.sql.SQLException;

public class ClockInServer implements DedicatedServerModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    private static final Logger LOGGER = ClockIn.LOGGER;
	public static DatabaseManager DBM;

	@Override
	public void onInitializeServer() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Clock In is loading!");

		// Initialize config, command manager, and database manager
		BasicConfig config = BasicConfig.getConfig();
		new CommandManager();
		if (config.getDatabaseType().equals("mysql")) {
			DBM = new MySQLDatabaseManager();
		} else if (config.getDatabaseType().equals("sqlite")) {
			DBM = new SQLiteDatabaseManager();
		}
		try {
			DBM.getConn();
		} catch (SQLException e) {
			LOGGER.error("Couldn't connect to SQL server!" + e.getMessage());
			return;
		}
		DBM.createTables();

	}
}