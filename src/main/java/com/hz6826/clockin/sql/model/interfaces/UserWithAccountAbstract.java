package com.hz6826.clockin.sql.model.interfaces;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.SERVER)
public abstract class UserWithAccountAbstract implements UserInterface, EconomyAccount {}
