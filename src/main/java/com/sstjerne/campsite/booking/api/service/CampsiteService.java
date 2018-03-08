package com.sstjerne.campsite.booking.api.service;

import java.io.IOException;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sstjerne.campsite.booking.api.model.Campsite;
import com.sstjerne.campsite.booking.api.repository.CampsiteRepository;

@Service
@Transactional
public class CampsiteService {

	private static final Logger log = LoggerFactory.getLogger(CampsiteService.class);

	@Autowired
	private CampsiteRepository repository;

	@Value("${campsite.default.id}")
	private Long defaultCampsiteID;

	public CampsiteService() {
	}

	@PostConstruct
	protected void init() throws IOException {
		
		
		Campsite pm = repository.findOne(defaultCampsiteID);

		if (pm == null) {
			Campsite campsite = new Campsite();
			campsite = new Campsite();
			campsite.setId(defaultCampsiteID);
			campsite.setCreated(new Date());
			campsite.setName("Pacific Ocean");
			campsite.setDescription("Underwater Volcanic Eruption form island");
			repository.save(campsite);

		}
	}

	public Campsite create(Campsite campsite) throws Exception {
		if (campsite == null) {
			throw new Exception("Error");
		}
		if (campsite.getId() != null) {
			Campsite c = repository.findOne(campsite.getId());
			if (c == null) {
				throw new Exception("Already exist or there is an error");
			}
		}
		campsite.setCreated(new Date());

		campsite = repository.save(campsite);

		return campsite;
	}

	public Campsite get(long id) {
		Campsite pm = repository.findOne(id);

		if (pm == null) {
			throw new EntityNotFoundException(Campsite.class.getSimpleName());
		}

		return repository.findOne(id);
	}

	public void update(Campsite campsite) {
		repository.save(campsite);
	}

	public void delete(Long id) {
		repository.delete(id);
	}

	public Page<Campsite> getAll(Integer page, Integer size) {
		Page<Campsite> pageOfCampsites = repository.findAll(new PageRequest(page, size));
		return pageOfCampsites;
	}
}