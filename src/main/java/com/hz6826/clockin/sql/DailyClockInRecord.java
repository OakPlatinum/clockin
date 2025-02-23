package com.hz6826.clockin.sql;

import io.ebean.Model;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.sql.Time;

@Entity
@Table(name = "daily_clock_in_records")
@Getter
@Setter
public class DailyClockInRecord extends Model {
    @Id
    private long id;
    @Column(name = "date")
    private Date date;
    @Column(name = "uuid")
    private String uuid;
    @Column(name = "time")
    private Time time;

    public DailyClockInRecord(Date date, String uuid, Time time) {
        this.date = date;
        this.uuid = uuid;
        this.time = time;
    }
}
