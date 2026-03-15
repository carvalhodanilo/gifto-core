package com.vp.core.domain.valueObjects;

import com.vp.core.domain.ValueObject;
import com.vp.core.domain.validation.ValidationHandler;

public class Location extends ValueObject {

    private final String street;
    private final String city;
    private final String neighborhood;
    private final String number;
    private final String state;
    private final String complement;
    private final String country;
    private final String postalCode;

    private Location(
            final String street,
            final String city,
            final String neighborhood,
            final String number,
            final String state,
            final String complement,
            final String country,
            final String postalCode
    ) {
        this.street = street;
        this.number = number;
        this.neighborhood = neighborhood;
        this.city = city;
        this.state = state;
        this.complement = complement;
        this.country = country;
        this.postalCode = postalCode;
    }

    public static Location with(
            final String street,
            final String number,
            final String neighborhood,
            final String complement,
            final String city,
            final String state,
            final String country,
            final String postalCode
    ) {
        return new Location(street, city, neighborhood, number, state, complement, country, postalCode);
    }

    public static Location empty() {
        return null;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public String getNumber() {
        return number;
    }

    public String getState() {
        return state;
    }

    public String getComplement() {
        return complement;
    }

    public String getCountry() {
        return country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void validate(final ValidationHandler handler) {
        new LocationValidator(this, handler).validate();
    }
}
