package com.example.demo.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LoanStatusTest {

    @Test
    void borrowed_isActiveAndTransitionsToReturned() {
        assertThat(LoanStatus.BORROWED.isActive()).isTrue();
        assertThat(LoanStatus.BORROWED.description()).isEqualTo("Wypożyczona");
        assertThat(LoanStatus.BORROWED.markReturned()).isEqualTo(LoanStatus.RETURNED);
    }

    @Test
    void overdue_isActiveAndTransitionsToReturned() {
        assertThat(LoanStatus.OVERDUE.isActive()).isTrue();
        assertThat(LoanStatus.OVERDUE.description()).isEqualTo("Po terminie zwrotu");
        assertThat(LoanStatus.OVERDUE.markReturned()).isEqualTo(LoanStatus.RETURNED);
    }

    @Test
    void returned_isNotActiveAndRejectsReturn() {
        assertThat(LoanStatus.RETURNED.isActive()).isFalse();
        assertThat(LoanStatus.RETURNED.description()).isEqualTo("Zwrócona");
        assertThatThrownBy(() -> LoanStatus.RETURNED.markReturned())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Ta książka została już wcześniej zwrócona!");
    }
}
