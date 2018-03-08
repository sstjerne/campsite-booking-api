package com.sstjerne.campsite.booking.api.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.sstjerne.campsite.booking.api.model.Campsite;

@Repository
public interface CampsiteRepository extends PagingAndSortingRepository<Campsite, Long> {

}