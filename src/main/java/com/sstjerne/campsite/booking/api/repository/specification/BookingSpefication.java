package com.sstjerne.campsite.booking.api.repository.specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.sstjerne.campsite.booking.api.model.Booking;
import com.sstjerne.campsite.booking.api.model.Campsite;

public class BookingSpefication {

	public static Specification<Booking> buildPredicates(final LocalDate fromDate, final LocalDate toDate, final Long campsiteID, final String email,
			final String fullname) {

		return new Specification<Booking>() {

			public Predicate toPredicate(Root<Booking> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicatesList = new ArrayList<Predicate>();

				if (fullname != null && !fullname.isEmpty()) {
					Expression<String> literal = cb.literal((String) "%" + fullname + "%");
					Path<String> path = root.get("fullname");
					predicatesList.add(cb.like(path, literal));
				}

				if (email != null && !email.isEmpty()) {
					Expression<String> literal = cb.literal((String) "%" + email + "%");
					Path<String> path = root.get("email");
					predicatesList.add(cb.like(path, literal));
				}

				if (fromDate != null && toDate != null) {
					Path<LocalDate> path = root.get("fromDate");
					predicatesList.add(cb.greaterThanOrEqualTo(path, fromDate));
					path = root.get("toDate");
					predicatesList.add(cb.lessThanOrEqualTo(path, toDate));
				} else if (fromDate != null) {
					Path<LocalDate> path = root.get("fromDate");
					predicatesList.add(cb.greaterThanOrEqualTo(path, fromDate));
				} else if (toDate != null) {
					Path<LocalDate> path = root.get("toDate");
					predicatesList.add(cb.lessThanOrEqualTo(path, toDate));
				}

				if (campsiteID != null && campsiteID.longValue() > 0l) {
					Path<Campsite> parentPath = root.get("campsite");
					Path<String> path = parentPath.get("id");
					predicatesList.add(cb.equal(path, campsiteID));
				}

				if (predicatesList.isEmpty()) {
					return null;
				} else {
					Predicate list2[] = new Predicate[predicatesList.size()];
					list2 = predicatesList.toArray(list2);
					return cb.or(list2);
				}

			}
		};
	}

}
