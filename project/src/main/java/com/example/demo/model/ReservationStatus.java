package com.example.demo.model;

public enum ReservationStatus implements LibraryStatus {

    ACTIVE {
        @Override
        public boolean isActive() {
            return true;
        }

        @Override
        public String description() {
            return "Aktywna";
        }
    },

    CANCELLED {
        @Override
        public boolean isActive() {
            return false;
        }

        @Override
        public String description() {
            return "Anulowana";
        }
    },

    FULFILLED {
        @Override
        public boolean isActive() {
            return false;
        }

        @Override
        public String description() {
            return "Zrealizowana";
        }
    },

    EXPIRED {
        @Override
        public boolean isActive() {
            return false;
        }

        @Override
        public String description() {
            return "Wygasła";
        }
    };
}
