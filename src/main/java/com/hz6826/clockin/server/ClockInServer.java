package com.hz6826.clockin.server;

import com.hz6826.clockin.ClockIn;
import com.hz6826.clockin.command.CommandManager;
import com.hz6826.clockin.init.DatabaseConn;
import com.hz6826.clockin.init.EventRegister;
import com.hz6826.clockin.sql_old.DatabaseManager;
import net.fabricmc.api.DedicatedServerModInitializer;

public class ClockInServer implements DedicatedServerModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static DatabaseManager DBM;

	@Override
	public void onInitializeServer() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		ClockIn.LOGGER.info("Clock In (server) is loading!");

		// Initialize config, command manager, and database manager

		CommandManager.bootstrap();

		DatabaseConn.bootstrap();

		// Register events
		EventRegister.bootstrap();

	}
}