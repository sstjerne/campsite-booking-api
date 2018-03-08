package com.sstjerne.campsite.booking.api.repository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sstjerne.campsite.booking.api.model.Customer;

@Repository
public interface CustomerRepository extends PagingAndSortingRepository<Customer, Long> {

    Customer findByEmail(@Param("email") String email);
}