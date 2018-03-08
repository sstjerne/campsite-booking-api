package com.sstjerne.campsite.booking.api.service;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sstjerne.campsite.booking.api.exception.CustomMethodArgumentNotValidException;
import com.sstjerne.campsite.booking.api.model.Booking;
import com.sstjerne.campsite.booking.api.model.BookingDate;
import com.sstjerne.campsite.booking.api.model.Campsite;
import com.sstjerne.campsite.booking.api.model.CampsiteStatus;
import com.sstjerne.campsite.booking.api.model.Customer;
import com.sstjerne.campsite.booking.api.repository.BookingRepository;
import com.sstjerne.campsite.booking.api.repository.CampsiteRepository;
import com.sstjerne.campsite.booking.api.repository.CustomerRepository;
import com.sstjerne.campsite.booking.api.repository.specification.BookingSpefication;

@Service
@Transactional
public class BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    @Autowired
    private BookingRepository repository;

    @Autowired
    private CampsiteRepository campsiteRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
	@Value("${campsite.default.id}")
	private Long defaultCampsiteID;

    public BookingService() {
    }

    /**
     * Create a booking, first validate the input, then generate all model, and save.
     * 
     * 
     * @param campsiteId
     * @param booking
     * @return
     * @throws Exception
     */
    public Booking create(Long campsiteId, Booking booking) throws Exception {
		if (booking == null || booking.getCustomer() == null) {
			throw new EntityNotFoundException(Booking.class.getSimpleName());
		}
		
		if (campsiteId == null) {
			campsiteId = defaultCampsiteID;
		}

		if (booking.getCheckIn() == null || booking.getCheckOut() == null) {
			throw new CustomMethodArgumentNotValidException("error.booking.date.range");
		}

		LocalDate today = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		if (booking.getCheckIn().atStartOfDay().isBefore(today.atStartOfDay())) {
			throw new CustomMethodArgumentNotValidException("error.booking.checkin.presentOrFuture");
		}
		
		long days = Duration.between(booking.getCheckIn().atStartOfDay(), booking.getCheckOut().atStartOfDay()).toDays();
		if (days > 2) {
			throw new CustomMethodArgumentNotValidException("error.booking.date.outofrange");
		}
		
		verifyAvailability(null, booking, campsiteId, booking.getCheckIn(), booking.getCheckOut());

		Campsite campsite = campsiteRepository.findOne(campsiteId);
		if (campsite == null) {
			throw new EntityNotFoundException(Campsite.class.getSimpleName());
		}
		
		Customer customer = customerRepository.findByEmail(booking.getCustomer().getEmail());
		
		if (customer == null) {
			customer = customerRepository.save(booking.getCustomer());
		}
		Date created = new Date();
		
		booking.setCreated(created);
		booking.setCampsite(campsite);
		booking.setCustomer(customer);
		booking.setConfirmed(true);
		
		
        Booking save = repository.save(booking);
        
        if (campsite.getBookings() == null) {
        	campsite.setBookings(new HashSet<Booking>());
        }
        campsite.getBookings().add(save);

        if (customer.getBookings() == null) {
        	customer.setBookings(new HashSet<Booking>());
        }      
        customer.getBookings().add(save);

        customerRepository.save(customer);
        campsiteRepository.save(campsite);
        
		return booking;
    }

    @Transactional(readOnly = true)
    public Booking getBy(UUID id) {
         Booking bookingPM = repository.findOne(id);
        
		if (bookingPM == null) {
			throw new EntityNotFoundException(Booking.class.getSimpleName());
		}
		
		return bookingPM;
    }

    public void update(UUID id, Booking booking) throws CustomMethodArgumentNotValidException {
    	Booking bookingPM = repository.findOne(id);
		if (bookingPM == null) {
			throw new EntityNotFoundException(Booking.class.getSimpleName());
		}

		if (booking.getCheckIn() == null || booking.getCheckOut() == null) {
			throw new CustomMethodArgumentNotValidException("error.booking.date.range");
		}

		LocalDate from = booking.getCheckIn();
		LocalDate to = booking.getCheckOut();
		

		long days = Duration.between(from.atStartOfDay(), to.atStartOfDay()).toDays();
		if (days > 2) {
			throw new CustomMethodArgumentNotValidException("error.booking.date.outofrange");
		}
		
		verifyAvailability(id, booking, bookingPM.getCampsite().getId(), from, to);
		
		
		LocalDate today = LocalDate.now();
		if (from.atStartOfDay().isEqual(today.atStartOfDay()) || from.atStartOfDay().isBefore(today.atStartOfDay())) {
			bookingPM.setCheckOut(booking.getCheckOut());
			repository.save(bookingPM);
		}else { // The Booking is not in course, so it should change the start/finish range
			bookingPM.setCheckIn(booking.getCheckIn());
			bookingPM.setCheckOut(booking.getCheckOut());
			repository.save(bookingPM);
		}
    }

	private void verifyAvailability(UUID id, Booking booking, Long campsiteID, LocalDate from, LocalDate to) throws CustomMethodArgumentNotValidException {
		Optional<List<Booking>> checkAvailability = repository.findAllBookingBy(campsiteID,booking.getCheckIn(),booking.getCheckOut());
		if (checkAvailability.isPresent()) { //Means that there are two booking for that periods, so it cann't change
			long count = checkAvailability.get()
				.stream()
				.filter(p -> !(from.isEqual(p.getCheckIn()) && to.isEqual(p.getCheckOut())))
				.filter(p -> !p.getId().equals(id))
				.count();
			
			if (count > 0l) {
				throw new CustomMethodArgumentNotValidException("error.booking.date.notavailable");
			}
		}
	}

	public void cancel(UUID id) {
    	Booking booking = repository.findOne(id);
		if (booking == null) {
			throw new EntityNotFoundException(Booking.class.getSimpleName());
		}
        repository.delete(booking);
    }

    public Page<Booking> getAll(Long campsiteID, LocalDate from, LocalDate to, String email, String fullname, Integer page, Integer size) {
        Specification<Booking> spec = BookingSpefication.buildPredicates(from, to, campsiteID, email, fullname);
		Page<Booking>  pageOfBookings = repository.findAll(spec, new PageRequest(page, size));
        return pageOfBookings;
    }

    /**
     * Return all days status (booked/free)
     * 
     * @param campsiteId
     * @param month
     * @param fromDate
     * @param toDate
     * @return
     */
    @Transactional(readOnly = true)
	public List<BookingDate> getAvailability(Long campsiteId, Integer month, LocalDate fromDate, LocalDate toDate) {
		// TODO: Manage by whole month 
    	if (campsiteId == null) {
    		campsiteId = defaultCampsiteID;
    	}
    	
    	if (month != null && month >=1 && month <= 12) {
    		fromDate = LocalDate.now().withMonth(month).withDayOfMonth(1);
    		toDate = LocalDate.now().withMonth(month).plusMonths(1).withDayOfMonth(1).minusDays(1);
    	}

		Map<LocalDate, BookingDate> availability = Stream.iterate(fromDate, date -> date.plusDays(1))
				.limit(ChronoUnit.DAYS.between(fromDate, toDate) + 1)
				.map(d -> new BookingDate(d, CampsiteStatus.FREE))
				.collect(Collectors.toMap(BookingDate::getDate, Function.identity()));


		LocalDate from = fromDate.minusDays(1);
		LocalDate to = toDate.plusDays(1);
		Map<LocalDate, BookingDate> booked = repository.findAllBookingBy(campsiteId,from,to).get()
				.stream()
				.map(new Function<Booking, List<BookingDate>>() {
	                  @Override
	                  public List<BookingDate> apply(Booking temp) {
	              		LocalDate from1 = temp.getCheckIn();
	            		LocalDate to1 = temp.getCheckOut();
	            		List<BookingDate> dates = Stream.iterate(from1, date -> date.plusDays(1))
	            			.limit(ChronoUnit.DAYS.between(from1, to1) + 1)
	            			.map(d -> new BookingDate(d, CampsiteStatus.BOOKED))
	            			.collect(Collectors.toList());
	            		return dates;
	            	}
	              })
				.flatMap(x -> x.stream())
				.filter( x -> from.isBefore(x.getDate()) && to.isAfter(x.getDate()))
				.collect(Collectors.toMap(BookingDate::getDate,	Function.identity()));
		
		
		availability.putAll(booked);
		
		List<BookingDate> collect = availability.values().stream().sorted(Comparator.comparing(BookingDate::getDate)).parallel().collect(Collectors.toList());;
		
		return collect;

	}


}