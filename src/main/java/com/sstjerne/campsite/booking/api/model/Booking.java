package com.sstjerne.campsite.booking.api.model;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(indexes = { @Index(name = "IDX_MYIDX1", columnList = "checkIn,checkOut") })
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Getter
@Setter
@RequiredArgsConstructor
public class Booking {

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "id", updatable = false, nullable = false)
	private UUID id;

    @JsonIgnore
    @ManyToOne(optional = false)
    private Campsite campsite;

	@Column(name = "campsite_id", insertable = false, updatable = false)
	private Long campsiteId;

	@ManyToOne(optional = false)
	private Customer customer;

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@JsonFormat(pattern = "yyyy-MM-dd")
	@NotNull(message = "error.booking.checkin.notnull")
	@Column(nullable = false)
	private LocalDate checkIn;

	@JsonFormat(pattern = "yyyy-MM-dd")
	@NotNull(message = "error.booking.checkout.notnull")
	@Column(nullable = false)
	private LocalDate checkOut;

	@Column(nullable = false)
	private Boolean confirmed;

}