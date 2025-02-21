package com.hz6826.clockin.sql_old.model.mysql;

import com.hz6826.clockin.sql_old.model.interfaces.GlobalShopInterface;

public class GlobalShop implements GlobalShopInterface {
    private String ownerUuid;  // If admin, ownerUuid is 00000000-0000-0000-0000-000000000000
    private Boolean isAdmin;
    
}
