package com.hz6826.clockin.sql.model.mysql;

import com.hz6826.clockin.sql.model.interfaces.GlobalShopInterface;

public class GlobalShop implements GlobalShopInterface {
    private String owner_uuid;  // If admin, owner_uuid is 00000000-0000-0000-0000-000000000000
}
