package com.example.demo.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationStatusTest {

    @Test
    void active_isActive() {
        assertThat(ReservationStatus.ACTIVE.isActive()).isTrue();
        assertThat(ReservationStatus.ACTIVE.description()).isEqualTo("Aktywna");
    }

    @Test
    void terminalStatuses_areNotActive() {
        assertThat(ReservationStatus.CANCELLED.isActive()).isFalse();
        assertThat(ReservationStatus.FULFILLED.isActive()).isFalse();
        assertThat(ReservationStatus.EXPIRED.isActive()).isFalse();
    }

    @Test
    void descriptions() {
        assertThat(ReservationStatus.CANCELLED.description()).isEqualTo("Anulowana");
        assertThat(ReservationStatus.FULFILLED.description()).isEqualTo("Zrealizowana");
        assertThat(ReservationStatus.EXPIRED.description()).isEqualTo("Wygasła");
    }
}
