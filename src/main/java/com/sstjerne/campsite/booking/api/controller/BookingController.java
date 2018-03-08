package com.sstjerne.campsite.booking.api.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

import com.sstjerne.campsite.booking.api.model.Booking;
import com.sstjerne.campsite.booking.api.model.BookingDate;
import com.sstjerne.campsite.booking.api.model.ResponseMessage;
import com.sstjerne.campsite.booking.api.service.BookingService;

@RestController
@RequestMapping("/booking")
public class BookingController {

	private static final Log logger = LogFactory.getLog(BookingController.class);

	@Autowired
	private BookingService bookingService;

	@RequestMapping(method = RequestMethod.GET)
	public Page<Booking> getAll(
			@RequestParam(value = "campsiteId", required = false) final Long campsiteId,
			@RequestParam(value = "from", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") final LocalDate fromDate,
			@RequestParam(value = "to", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") final LocalDate toDate,
			@RequestParam(value = "email", required = false) final String email, @RequestParam(value = "fullname", required = false) final String fullname,
			@RequestParam(value = "page", required = true, defaultValue = "0") Integer page,
			@RequestParam(value = "size", required = true, defaultValue = "50") Integer size, HttpServletRequest request, HttpServletResponse response)
			throws NoSuchRequestHandlingMethodException {

		Page<Booking> bookings = bookingService.getAll(campsiteId, fromDate, toDate, email, fullname, page, size);

		return bookings;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, consumes = { "application/json", "application/xml" }, produces = { "application/json",
			"application/xml" })
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public Booking get(
			@PathVariable("id") final UUID id, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		Booking bookingP = bookingService.getBy(id);

		response.setHeader("Location", request.getRequestURL().append("/").append(bookingP.getId()).toString());
		return bookingP;
	}

	@RequestMapping(value = "/availability", method = RequestMethod.GET)
	public List<BookingDate> availability(@RequestParam(value = "campsiteId", required = false) final Long campsiteId,
			@RequestParam(value = "month", required = false) final Integer month,
			@RequestParam(value = "from", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") final LocalDate fromDate,
			@RequestParam(value = "to", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") final LocalDate toDate, 
			HttpServletRequest request,
			HttpServletResponse response) {

		List<BookingDate> bookings = bookingService.getAvailability(campsiteId, month, fromDate, toDate);

		return bookings;
	}

	@RequestMapping(method = RequestMethod.POST, consumes = { "application/json", "application/xml" }, produces = { "application/json", "application/xml" })
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<ResponseMessage> create(@RequestBody Booking booking, 
			@RequestParam(value = "campsiteId", required = false) final Long campsiteId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		Booking bookingP = bookingService.create(campsiteId, booking);

		response.setHeader("Location", request.getRequestURL().append("/").append(bookingP.getId()).toString());

		ResponseMessage message = new ResponseMessage();
		message.setMessage("The Booking was created succesfully, the identifier is " + bookingP.getId());
		message.setReason(HttpStatus.CREATED.getReasonPhrase());
		message.setCode(HttpStatus.CREATED.value());

		return new ResponseEntity<ResponseMessage>(message, HttpStatus.CREATED);

	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<ResponseMessage> cancel(@PathVariable("id") final UUID id, HttpServletRequest request, HttpServletResponse response) {
		bookingService.cancel(id);

		ResponseMessage message = new ResponseMessage();
		message.setMessage("The Booking was cancelled succesfully, the identifier was " + id);
		message.setReason(HttpStatus.NO_CONTENT.getReasonPhrase());
		message.setCode(HttpStatus.NO_CONTENT.value());

		return new ResponseEntity<ResponseMessage>(message, HttpStatus.NO_CONTENT);

	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public ResponseEntity<ResponseMessage> update(@RequestBody Booking booking, @PathVariable("id") final UUID id, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		bookingService.update(id, booking);
		response.setHeader("Location", request.getRequestURL().append("/").append(id).toString());

		ResponseMessage message = new ResponseMessage();
		message.setMessage("The Booking was updated succesfully, the identifier is " + id);
		message.setReason(HttpStatus.OK.getReasonPhrase());
		message.setCode(HttpStatus.OK.value());

		return new ResponseEntity<ResponseMessage>(message, HttpStatus.OK);

	}

}