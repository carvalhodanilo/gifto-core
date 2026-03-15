package com.vp.core.domain.valueObjects;

import com.vp.core.domain.validation.ValidationHandler;
import com.vp.core.domain.validation.Validator;

public class LocationValidator extends Validator {

    private final Location location;

    public static final int MAX_LENGTH = 50;
    public static final int MIN_LENGTH = 3;

    public LocationValidator(Location location, ValidationHandler handler) {
        super(handler);
        this.location = location;
    }

    @Override
    public void validate() {
        checkStreetConstraints();
        checkCityConstraints();
        checkNeighborhoodConstraints();
        checkNumberConstraints();
        checkStateConstraints();
        checkComplementConstraints();
        checkCountryConstraints();
        checkPostalCodeConstraints();
    }

    private void checkStreetConstraints() {
        final var street = location.getStreet();
        if (appendErrorIfIsNullOrEmpty(street, "street")) return;
        appendErrorIfIsIncorrectLength(street, "street", MIN_LENGTH, MAX_LENGTH);
    }

    private void checkCityConstraints() {
        final var city = location.getCity();
        if (appendErrorIfIsNullOrEmpty(city, "city")) return;
        appendErrorIfIsIncorrectLength(city, "city", MIN_LENGTH, MAX_LENGTH);
    }

    private void checkNeighborhoodConstraints() {
        final var neighborhood = location.getNeighborhood();
        if (appendErrorIfIsNullOrEmpty(neighborhood, "neighborhood")) return;
        appendErrorIfIsIncorrectLength(neighborhood, "neighborhood", MIN_LENGTH, MAX_LENGTH);
    }

    private void checkNumberConstraints() {
        final var number = location.getNumber();
        if (appendErrorIfIsNullOrEmpty(number, "number")) return;
    }

    private void checkStateConstraints() {
        final var state = location.getState();
        if (appendErrorIfIsNullOrEmpty(state, "state")) return;
        appendErrorIfIsIncorrectLength(state, "state", MIN_LENGTH, MAX_LENGTH);
    }

    private void checkComplementConstraints() {
        final var complement = location.getComplement();
        if (complement == null || complement.isBlank()) return;
        appendErrorIfIsIncorrectLength(complement, "complement", MIN_LENGTH, MAX_LENGTH);
    }

    private void checkCountryConstraints() {
        final var country = location.getCountry();
        if (appendErrorIfIsNullOrEmpty(country, "country")) return;
        appendErrorIfIsIncorrectLength(country, "country", MIN_LENGTH, MAX_LENGTH);
    }

    private void checkPostalCodeConstraints() {
        final var postalCode = location.getPostalCode();
        if (appendErrorIfIsNullOrEmpty(postalCode, "postalCode")) return;
        appendErrorIfIsIncorrectLength(postalCode, "postalCode", MIN_LENGTH, MAX_LENGTH);
    }
}
