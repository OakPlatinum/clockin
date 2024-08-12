package com.hz6826.clockin;

import com.hz6826.clockin.config.BasicConfig;
import com.hz6826.clockin.item.Coins;
import com.hz6826.clockin.sql.model.interfaces.UserInterface;
import com.hz6826.clockin.sql.model.interfaces.UserWithAccountAbstract;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hz6826.clockin.command.CommandManager;
import com.hz6826.clockin.sql.*;

import java.sql.Date;
import java.sql.SQLException;

public class ClockIn implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final String MOD_ID = "clockin";
    public static final Logger LOGGER = LoggerFactory.getLogger("ClockIn");

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Clock In is loading!");
		BasicConfig config = BasicConfig.getConfig();
		config.load();
		Coins.register();

		// Initialize config, command manager, and database manager
		// CommandManager commandManager = new CommandManager();

	}
}