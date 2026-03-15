package com.vp.core.infrastructure.shared;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class LocationEmbeddable {

    @Column(name = "location_street")
    private String street;

    @Column(name = "location_number")
    private String number;

    @Column(name = "location_neighborhood")
    private String neighborhood;

    @Column(name = "location_complement")
    private String complement;

    @Column(name = "location_city")
    private String city;

    @Column(name = "location_state")
    private String state;

    @Column(name = "location_country")
    private String country;

    @Column(name = "location_postal_code")
    private String postalCode;

    protected LocationEmbeddable() {
    }

    public static LocationEmbeddable of(
            final String street,
            final String number,
            final String neighborhood,
            final String complement,
            final String city,
            final String state,
            final String country,
            final String postalCode
    ) {
        final var e = new LocationEmbeddable();
        e.street = street;
        e.number = number;
        e.neighborhood = neighborhood;
        e.complement = complement;
        e.city = city;
        e.state = state;
        e.country = country;
        e.postalCode = postalCode;
        return e;
    }

    public String getStreet() { return street; }
    public String getNumber() { return number; }
    public String getNeighborhood() { return neighborhood; }
    public String getComplement() { return complement; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getCountry() { return country; }
    public String getPostalCode() { return postalCode; }
}
