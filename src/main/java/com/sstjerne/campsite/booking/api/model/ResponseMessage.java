package com.sstjerne.campsite.booking.api.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ResponseMessage {

	private String message;
	private String reason;
	private Integer code;

}
