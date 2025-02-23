package com.hz6826.clockin;

import com.hz6826.clockin.init.ClockInConfig;
import com.hz6826.clockin.init.ConfigLoader;
import com.hz6826.clockin.init.item.Coins;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClockIn implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final String MOD_ID = "clockin";
    public static final Logger LOGGER = LoggerFactory.getLogger("ClockIn");
	public static final ClockInConfig CONFIG = ConfigLoader.load();

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Clock In (common) is loading!");

		if (CONFIG.enablePhysicalCurrency) Coins.register();

		// Initialize config, command manager, and database manager
		// CommandManager commandManager = new CommandManager();

	}
}