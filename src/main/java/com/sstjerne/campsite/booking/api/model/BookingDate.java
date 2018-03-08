package com.sstjerne.campsite.booking.api.model;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BookingDate {

    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate date;

    private CampsiteStatus campsite;

    public BookingDate(LocalDate date, CampsiteStatus status) {
    	this.date = date;
    	this.campsite = status;
    }


}