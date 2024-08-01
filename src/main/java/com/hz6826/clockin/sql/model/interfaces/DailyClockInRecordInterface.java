package com.hz6826.clockin.sql.model.interfaces;

import java.sql.Date;
import java.sql.Time;

public interface DailyClockInRecordInterface{

    Date getDate();

    String getUuid();

    Time getTime();

    int getRank();
}
