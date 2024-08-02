package com.hz6826.clockin.server;

import com.hz6826.clockin.command.CommandManager;
import com.hz6826.clockin.config.BasicConfig;
import com.hz6826.clockin.sql.DatabaseManager;
import com.hz6826.clockin.sql.MySQLDatabaseManager;
import com.hz6826.clockin.sql.model.interfaces.UserWithAccountAbstract;
import net.fabricmc.api.DedicatedServerModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class ClockInServer implements DedicatedServerModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("ClockIn");
	public static DatabaseManager DATABASE_MANAGER;

	@Override
	public void onInitializeServer() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Clock In is loading!");

		// Initialize config, command manager, and database manager
		BasicConfig config = BasicConfig.getConfig();
		config.load();
		CommandManager commandManager = new CommandManager();
		if (config.getDatabaseType().equals("mysql")) {
			DatabaseManager databaseManager = new MySQLDatabaseManager();
			try {
				databaseManager.getConn();
			} catch (SQLException e) {
				LOGGER.error("Couldn't connect to SQL server!" + e.getMessage());
				return;
			}
			databaseManager.createTables();
			DATABASE_MANAGER = databaseManager;
//			UserWithAccountAbstract user = DATABASE_MANAGER.getOrCreateUser("890af6d3-7114-4648-867e-66c2ccdf9069", "OakPlatinum");
//			LOGGER.warn(user.getPlayerName());
//			user.setRaffleTicket(55);
		}

	}
}