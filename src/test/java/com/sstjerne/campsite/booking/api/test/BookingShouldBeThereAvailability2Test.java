package com.sstjerne.campsite.booking.api.test;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sstjerne.campsite.booking.api.model.Booking;
import com.sstjerne.campsite.booking.api.model.Customer;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.MOCK)
@ContextConfiguration(classes = com.sstjerne.campsite.booking.api.Application.class)
@ActiveProfiles("test")
public class BookingShouldBeThereAvailability2Test {

	@Autowired
	WebApplicationContext context;

	@Autowired
	ObjectMapper objectMapper;

	private MockMvc mvc;

	@Value("${campsite.default.id}")
	private String campsiteId;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		mvc = MockMvcBuilders.webAppContextSetup(context).build();
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	}


	/***
	 * Creates three booking of 1, 2 & 3 days, but there is one day free between them. Finally the booking are
	 * cancelled.
	 * 
	 * @throws Exception
	 */
	@Test
	public void shouldBeThereAvailability_2() throws Exception {

		Customer customer = new Customer();
		customer.setEmail("customer1@test.com");
		customer.setFullname("Customer 1");

		Booking booking = new Booking();

		booking.setCustomer(customer);

		LocalDate from1 = LocalDate.now().plusMonths(2);
		LocalDate to1 = from1;
		booking.setCheckIn(from1);
		booking.setCheckOut(to1);

		String writeValueAsString = objectMapper.writeValueAsString(booking);
		System.out.println(writeValueAsString);
		byte[] bookingJson1 = writeValueAsString.getBytes();

		// CREATE
		MvcResult result = mvc.perform(post("/booking/").content(bookingJson1).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andReturn();
		String bookingId1 = Util.getResourceIdFromUrl(result.getResponse().getRedirectedUrl());

		customer.setEmail("customer2@test.com");
		customer.setFullname("Customer 2");

		booking.setCustomer(customer);

		LocalDate from2 = to1.plusDays(2);
		LocalDate to2 = from2.plusDays(1);
		booking.setCheckIn(from2);
		booking.setCheckOut(to2);

		writeValueAsString = objectMapper.writeValueAsString(booking);
		System.out.println(writeValueAsString);
		byte[] bookingJson2 = writeValueAsString.getBytes();

		// CREATE
		result = mvc.perform(post("/booking/").content(bookingJson2).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andReturn();
		String bookingId2 = Util.getResourceIdFromUrl(result.getResponse().getRedirectedUrl());

		customer.setEmail("customer3@test.com");
		customer.setFullname("Customer 3");

		booking.setCustomer(customer);

		LocalDate from3 = to2.plusDays(2);
		LocalDate to3 = from3.plusDays(2);
		booking.setCheckIn(from3);
		booking.setCheckOut(to3);

		writeValueAsString = objectMapper.writeValueAsString(booking);
		System.out.println(writeValueAsString);
		byte[] bookingJson3 = writeValueAsString.getBytes();

		// CREATE
		result = mvc.perform(post("/booking/").content(bookingJson3).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andReturn();
		String bookingId3 = Util.getResourceIdFromUrl(result.getResponse().getRedirectedUrl());

		// RETRIEVE
		// mvc.perform(get("/booking/").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
		// .andExpect(status().isOk())
		// .andExpect(jsonPath("$.totalElements", is(3)));

		// CHECK Availability
		// RETRIEVE
		mvc.perform(get("/booking/" + "/availability?from=" + from1.toString() + "&to=" + to3.toString()).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(jsonPath("$.length()", is(8)))

				.andExpect(jsonPath("$.[0].date", is(from1.toString()))).andExpect(jsonPath("$.[0].campsite", is("BOOKED")))
				.andExpect(jsonPath("$.[1].date", is(from1.plusDays(1).toString()))).andExpect(jsonPath("$.[1].campsite", is("FREE")))
				.andExpect(jsonPath("$.[2].date", is(from1.plusDays(2).toString()))).andExpect(jsonPath("$.[2].campsite", is("BOOKED")))
				.andExpect(jsonPath("$.[3].date", is(from1.plusDays(3).toString()))).andExpect(jsonPath("$.[3].campsite", is("BOOKED")))

				.andExpect(jsonPath("$.[4].date", is(from1.plusDays(4).toString()))).andExpect(jsonPath("$.[4].campsite", is("FREE")))
				.andExpect(jsonPath("$.[5].date", is(from1.plusDays(5).toString()))).andExpect(jsonPath("$.[5].campsite", is("BOOKED")))
				.andExpect(jsonPath("$.[6].date", is(from1.plusDays(6).toString()))).andExpect(jsonPath("$.[6].campsite", is("BOOKED")))
				.andExpect(jsonPath("$.[7].date", is(from1.plusDays(7).toString()))).andExpect(jsonPath("$.[7].campsite", is("BOOKED")))

		;

		// DELETE
		mvc.perform(delete("/booking/" + bookingId1)).andExpect(status().isNoContent());
		// DELETE
		mvc.perform(delete("/booking/" + bookingId2)).andExpect(status().isNoContent());
		// DELETE
		mvc.perform(delete("/booking/" + bookingId3)).andExpect(status().isNoContent());

	}

	/***
	 * Creates three booking of 1, 2 & 3 days, but there is one day free between them. Check days availables. The days
	 * of them changed, the update should be do properly.Finally the booking are cancelled.
	 * 
	 * @throws Exception
	 */
//	@Test
	public void shouldBeThereAvailability_3() throws Exception {

		Customer customer = new Customer();
		customer.setEmail("customer1@test.com");
		customer.setFullname("Customer 1");

		Booking booking = new Booking();

		booking.setCustomer(customer);

		LocalDate from1 = LocalDate.now().plusMonths(3);
		LocalDate to1 = from1;
		booking.setCheckIn(from1);
		booking.setCheckOut(to1);

		String writeValueAsString = objectMapper.writeValueAsString(booking);
		System.out.println(writeValueAsString);
		byte[] bookingJson1 = writeValueAsString.getBytes();

		// CREATE
		MvcResult result = mvc.perform(post("/booking/").content(bookingJson1).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andReturn();
		String bookingId1 = Util.getResourceIdFromUrl(result.getResponse().getRedirectedUrl());

		customer.setEmail("customer2@test.com");
		customer.setFullname("Customer 2");

		booking.setCustomer(customer);

		LocalDate from2 = to1.plusDays(2);
		LocalDate to2 = from2.plusDays(1);
		booking.setCheckIn(from2);
		booking.setCheckOut(to2);

		writeValueAsString = objectMapper.writeValueAsString(booking);
		System.out.println(writeValueAsString);
		byte[] bookingJson2 = writeValueAsString.getBytes();

		// CREATE
		result = mvc.perform(post("/booking/").content(bookingJson2).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andReturn();
		String bookingId2 = Util.getResourceIdFromUrl(result.getResponse().getRedirectedUrl());

		customer.setEmail("customer3@test.com");
		customer.setFullname("Customer 3");

		booking.setCustomer(customer);

		LocalDate from3 = to2.plusDays(2);
		LocalDate to3 = from3.plusDays(2);
		booking.setCheckIn(from3);
		booking.setCheckOut(to3);

		writeValueAsString = objectMapper.writeValueAsString(booking);
		System.out.println(writeValueAsString);
		byte[] bookingJson3 = writeValueAsString.getBytes();

		// CREATE
		result = mvc.perform(post("/booking/").content(bookingJson3).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andReturn();
		String bookingId3 = Util.getResourceIdFromUrl(result.getResponse().getRedirectedUrl());

		// CHECK Availability
		// RETRIEVE
		mvc.perform(get("/booking/" + "/availability?from=" + from1.toString() + "&to=" + to3.toString()).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(jsonPath("$.length()", is(8)))

				.andExpect(jsonPath("$.[0].date", is(from1.toString()))).andExpect(jsonPath("$.[0].campsite", is("BOOKED")))
				.andExpect(jsonPath("$.[1].date", is(from1.plusDays(1).toString()))).andExpect(jsonPath("$.[1].campsite", is("FREE")))
				.andExpect(jsonPath("$.[2].date", is(from1.plusDays(2).toString()))).andExpect(jsonPath("$.[2].campsite", is("BOOKED")))
				.andExpect(jsonPath("$.[3].date", is(from1.plusDays(3).toString()))).andExpect(jsonPath("$.[3].campsite", is("BOOKED")))

				.andExpect(jsonPath("$.[4].date", is(from1.plusDays(4).toString()))).andExpect(jsonPath("$.[4].campsite", is("FREE")))
				.andExpect(jsonPath("$.[5].date", is(from1.plusDays(5).toString()))).andExpect(jsonPath("$.[5].campsite", is("BOOKED")))
				.andExpect(jsonPath("$.[6].date", is(from1.plusDays(6).toString()))).andExpect(jsonPath("$.[6].campsite", is("BOOKED")))
				.andExpect(jsonPath("$.[7].date", is(from1.plusDays(7).toString()))).andExpect(jsonPath("$.[7].campsite", is("BOOKED")))

		;

		// UPDATE - Increase one day
		booking.setCheckIn(from1);
		booking.setCheckOut(to1.plusDays(1));
		byte[] bookingJson = objectMapper.writeValueAsString(booking).getBytes();
		result = mvc.perform(put("/booking/" + bookingId1).content(bookingJson).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();

		// CHECK Availability
		// RETRIEVE
		mvc.perform(get("/booking/" + "/availability?from=" + from1.toString() + "&to=" + to3.toString()).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(jsonPath("$.length()", is(8)))

				.andExpect(jsonPath("$.[0].date", is(from1.toString()))).andExpect(jsonPath("$.[0].campsite", is("BOOKED")))
				.andExpect(jsonPath("$.[1].date", is(from1.plusDays(1).toString()))).andExpect(jsonPath("$.[1].campsite", is("BOOKED")))
				.andExpect(jsonPath("$.[2].date", is(from1.plusDays(2).toString()))).andExpect(jsonPath("$.[2].campsite", is("BOOKED")))
				.andExpect(jsonPath("$.[3].date", is(from1.plusDays(3).toString()))).andExpect(jsonPath("$.[3].campsite", is("BOOKED")))

				.andExpect(jsonPath("$.[4].date", is(from1.plusDays(4).toString()))).andExpect(jsonPath("$.[4].campsite", is("FREE")))
				.andExpect(jsonPath("$.[5].date", is(from1.plusDays(5).toString()))).andExpect(jsonPath("$.[5].campsite", is("BOOKED")))
				.andExpect(jsonPath("$.[6].date", is(from1.plusDays(6).toString()))).andExpect(jsonPath("$.[6].campsite", is("BOOKED")))
				.andExpect(jsonPath("$.[7].date", is(from1.plusDays(7).toString()))).andExpect(jsonPath("$.[7].campsite", is("BOOKED")))

		;

		// UPDATE - Reduce one day
		booking.setCheckIn(from2);
		booking.setCheckOut(to2.minusDays(1));
		bookingJson = objectMapper.writeValueAsString(booking).getBytes();
		result = mvc.perform(put("/booking/" + bookingId2).content(bookingJson).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();

		// CHECK Availability
		// RETRIEVE
		mvc.perform(get("/booking/" + "/availability?from=" + from1.toString() + "&to=" + to3.toString()).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(jsonPath("$.length()", is(8)))

				.andExpect(jsonPath("$.[0].date", is(from1.toString()))).andExpect(jsonPath("$.[0].campsite", is("BOOKED")))
				.andExpect(jsonPath("$.[1].date", is(from1.plusDays(1).toString()))).andExpect(jsonPath("$.[1].campsite", is("BOOKED")))
				.andExpect(jsonPath("$.[2].date", is(from1.plusDays(2).toString()))).andExpect(jsonPath("$.[2].campsite", is("BOOKED")))
				.andExpect(jsonPath("$.[3].date", is(from1.plusDays(3).toString()))).andExpect(jsonPath("$.[3].campsite", is("FREE")))

				.andExpect(jsonPath("$.[4].date", is(from1.plusDays(4).toString()))).andExpect(jsonPath("$.[4].campsite", is("FREE")))
				.andExpect(jsonPath("$.[5].date", is(from1.plusDays(5).toString()))).andExpect(jsonPath("$.[5].campsite", is("BOOKED")))
				.andExpect(jsonPath("$.[6].date", is(from1.plusDays(6).toString()))).andExpect(jsonPath("$.[6].campsite", is("BOOKED")))
				.andExpect(jsonPath("$.[7].date", is(from1.plusDays(7).toString()))).andExpect(jsonPath("$.[7].campsite", is("BOOKED")))

		;

		// UPDATE - Reduce one day
		booking.setCheckIn(from3.plusDays(1));
		booking.setCheckOut(to3.minusDays(1));
		bookingJson = objectMapper.writeValueAsString(booking).getBytes();
		result = mvc.perform(put("/booking/" + bookingId3).content(bookingJson).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();

		// CHECK Availability
		// RETRIEVE
		mvc.perform(get("/booking/" + "/availability?from=" + from1.toString() + "&to=" + to3.toString()).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(jsonPath("$.length()", is(8)))

				.andExpect(jsonPath("$.[0].date", is(from1.toString()))).andExpect(jsonPath("$.[0].campsite", is("BOOKED")))
				.andExpect(jsonPath("$.[1].date", is(from1.plusDays(1).toString()))).andExpect(jsonPath("$.[1].campsite", is("BOOKED")))
				.andExpect(jsonPath("$.[2].date", is(from1.plusDays(2).toString()))).andExpect(jsonPath("$.[2].campsite", is("BOOKED")))
				.andExpect(jsonPath("$.[3].date", is(from1.plusDays(3).toString()))).andExpect(jsonPath("$.[3].campsite", is("FREE")))

				.andExpect(jsonPath("$.[4].date", is(from1.plusDays(4).toString()))).andExpect(jsonPath("$.[4].campsite", is("FREE")))
				.andExpect(jsonPath("$.[5].date", is(from1.plusDays(5).toString()))).andExpect(jsonPath("$.[5].campsite", is("FREE")))
				.andExpect(jsonPath("$.[6].date", is(from1.plusDays(6).toString()))).andExpect(jsonPath("$.[6].campsite", is("BOOKED")))
				.andExpect(jsonPath("$.[7].date", is(from1.plusDays(7).toString()))).andExpect(jsonPath("$.[7].campsite", is("FREE")));

		// DELETE
		mvc.perform(delete("/booking/" + bookingId1)).andExpect(status().isNoContent());
		// DELETE
		mvc.perform(delete("/booking/" + bookingId2)).andExpect(status().isNoContent());
		// DELETE
		mvc.perform(delete("/booking/" + bookingId3)).andExpect(status().isNoContent());

	}

}