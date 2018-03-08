package com.sstjerne.campsite.booking.api.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Email;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Getter @Setter @RequiredArgsConstructor @EqualsAndHashCode
public class Customer {

	@Id
	@GeneratedValue
	private Long id;

    @NotNull(message = "error.customer.fullname.notnull")
	@Column(nullable = false)
	private String fullname;

    @NotNull(message = "error.customer.email.notnull")
	@Column(nullable = false, unique = true)
	@Email
	private String email;

	@OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
	@JsonIgnore
	private Set<Booking> bookings;

}