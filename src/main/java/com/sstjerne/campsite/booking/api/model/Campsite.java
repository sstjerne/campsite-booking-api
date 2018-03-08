package com.sstjerne.campsite.booking.api.model;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

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
public class Campsite {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @NotNull(message = "error.campsite.name.notnull")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "error.campsite.description.notnull")
    @Column(nullable = false)
    private String description;

    @OneToMany(mappedBy = "campsite", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Booking> bookings;


}