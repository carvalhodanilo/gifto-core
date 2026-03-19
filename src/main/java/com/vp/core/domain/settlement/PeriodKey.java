package com.vp.core.domain.settlement;

import com.vp.core.domain.ValueObject;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.time.temporal.IsoFields;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Identificador canônico de período em formato ISO 8601: YYYY-Wnn (ex: 2026-W11).
 * Semana = segunda a domingo. Valida formato e (opcionalmente) rejeita semana futura.
 */
public final class PeriodKey extends ValueObject {

    private static final Pattern PATTERN = Pattern.compile("^(\\d{4})-W(0?[1-9]|[1-4][0-9]|5[0-3])$");

    private final String value;

    private PeriodKey(final String value) {
        this.value = Objects.requireNonNull(value, "periodKey must not be null");
    }

    /**
     * Retorna o período da semana ISO atual (data de hoje).
     */
    public static PeriodKey current() {
        final var now = LocalDate.now();
        final int year = now.get(IsoFields.WEEK_BASED_YEAR);
        final int week = now.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        final var value = year + "-W" + String.format("%02d", week);
        return new PeriodKey(value);
    }

    /**
     * Retorna o período da semana ISO anterior (semana fechada).
     * Usado para rodar o batch: só se liquida o período anterior, pois o atual ainda está em aberto.
     */
    public static PeriodKey previous() {
        final var lastWeek = LocalDate.now().minusWeeks(1);
        final int year = lastWeek.get(IsoFields.WEEK_BASED_YEAR);
        final int week = lastWeek.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        final var value = year + "-W" + String.format("%02d", week);
        return new PeriodKey(value);
    }

    /**
     * Cria um PeriodKey a partir da string. Formato esperado: YYYY-Wnn (ex: 2026-W11).
     *
     * @throws IllegalArgumentException se o formato for inválido ou a semana estiver fora do range (W01–W53).
     */
    public static PeriodKey from(final String value) {
        final var trimmed = value == null ? "" : value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Period key is required. Use ISO week format: YYYY-Wnn (e.g. 2026-W11).");
        }
        final var matcher = PATTERN.matcher(trimmed);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(
                    "Invalid period key format. Use ISO week: YYYY-Wnn (e.g. 2026-W11). Received: " + value
            );
        }
        final int week = Integer.parseInt(matcher.group(2));
        if (week < 1 || week > 53) {
            throw new IllegalArgumentException("Week must be between 01 and 53. Received: " + value);
        }
        return new PeriodKey(trimmed);
    }

    /**
     * Retorna a segunda-feira (inclusive) da semana ISO deste período.
     */
    public LocalDate getStartDateInclusive() {
        final var parts = value.split("-W");
        final int year = Integer.parseInt(parts[0]);
        final int week = Integer.parseInt(parts[1]);
        return LocalDate.now()
                .with(IsoFields.WEEK_BASED_YEAR, year)
                .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week)
                .with(ChronoField.DAY_OF_WEEK, 1); // 1 = Monday
    }

    /**
     * Retorna o domingo (inclusive) da semana ISO deste período.
     */
    public LocalDate getEndDateInclusive() {
        return getStartDateInclusive().plusDays(6);
    }

    /**
     * Retorna true se a semana ainda não começou (segunda-feira da semana é no futuro).
     */
    public boolean isFuture() {
        try {
            return getStartDateInclusive().isAfter(LocalDate.now());
        } catch (Exception e) {
            return false;
        }
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final PeriodKey periodKey = (PeriodKey) o;
        return value.equals(periodKey.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
