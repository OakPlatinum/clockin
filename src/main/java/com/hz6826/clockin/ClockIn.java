package com.hz6826.clockin;

import com.hz6826.clockin.config.BasicConfig;
import com.hz6826.clockin.sql.model.interfaces.UserInterface;
import com.hz6826.clockin.sql.model.interfaces.UserWithAccountAbstract;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hz6826.clockin.command.CommandManager;
import com.hz6826.clockin.sql.*;

import java.sql.Date;

public class ClockIn implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("ClockIn");

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Clock In was loaded successfully!");

		// Initialize config, command manager, and database manager
		BasicConfig config = BasicConfig.getConfig();
		config.load();
		CommandManager commandManager = new CommandManager();
		if (config.getDatabaseType().equals("mysql")) {
			DatabaseManager databaseManager = new MySQLDatabaseManager();
			databaseManager.createTables();
			UserWithAccountAbstract user = databaseManager.getOrCreateUser("fe736038-102e-365b-a97e-eabc472944ec", "Player31");
			LOGGER.info(user.getPlayerName());
			user.setBalance(100.5);
			user.setRaffleTicket(13);
		}

	}
}