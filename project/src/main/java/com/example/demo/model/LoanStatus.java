package com.example.demo.model;

public enum LoanStatus implements LibraryStatus {

    BORROWED {
        @Override
        public boolean isActive() {
            return true;
        }

        @Override
        public String description() {
            return "Wypożyczona";
        }

        @Override
        public LoanStatus markReturned() {
            return RETURNED;
        }
    },

    OVERDUE {
        @Override
        public boolean isActive() {
            return true;
        }

        @Override
        public String description() {
            return "Po terminie zwrotu";
        }

        @Override
        public LoanStatus markReturned() {
            return RETURNED;
        }
    },

    RETURNED {
        @Override
        public boolean isActive() {
            return false;
        }

        @Override
        public String description() {
            return "Zwrócona";
        }

        @Override
        public LoanStatus markReturned() {
            throw new IllegalStateException("Ta książka została już wcześniej zwrócona!");
        }
    };

    public abstract LoanStatus markReturned();
}
