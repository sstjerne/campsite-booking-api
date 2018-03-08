package com.sstjerne.campsite.booking.api.repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sstjerne.campsite.booking.api.model.Booking;


@Repository
public interface BookingRepository extends PagingAndSortingRepository<Booking, UUID>, JpaSpecificationExecutor<Booking>  {

	@Query("SELECT l FROM Booking l JOIN l.campsite c WHERE l.id = :id AND c.id = :campsiteId")
	Booking findBy(@Param("campsiteId") long campsiteId, @Param("id") UUID id);

	@Query("SELECT l FROM Booking l JOIN l.campsite c WHERE c.id = :campsiteId AND ((l.checkIn BETWEEN :fromDate AND :toDate) OR (l.checkOut BETWEEN :fromDate AND :toDate)) ORDER BY l.checkIn ")
	Optional<List<Booking>> findAllBookingBy(@Param("campsiteId") Long campsiteId, @Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);
	
	@Query("SELECT COUNT(l) FROM Booking l JOIN l.campsite c WHERE c.id = :campsiteId AND ((l.checkIn BETWEEN :fromDate AND :toDate) OR (l.checkOut BETWEEN :fromDate AND :toDate))")
	Optional<Long> checkAvailability(@Param("campsiteId") Long campsiteId, @Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);
	
}