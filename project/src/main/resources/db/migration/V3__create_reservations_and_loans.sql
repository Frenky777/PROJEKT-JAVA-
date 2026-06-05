  CREATE TABLE reservations (
      id          BIGSERIAL PRIMARY KEY,
      user_id     BIGINT NOT NULL REFERENCES users(id),
      book_id     BIGINT NOT NULL REFERENCES books(id),
      status      VARCHAR(20) NOT NULL,
      reserved_at TIMESTAMP NOT NULL DEFAULT NOW(),
      expires_at  TIMESTAMP
  );

  CREATE TABLE loan_history (
      id          BIGSERIAL PRIMARY KEY,
      user_id     BIGINT NOT NULL REFERENCES users(id),
      book_id     BIGINT NOT NULL REFERENCES books(id),
      status      VARCHAR(20) NOT NULL,
      borrowed_at TIMESTAMP NOT NULL DEFAULT NOW(),
      due_at      TIMESTAMP NOT NULL,
      returned_at TIMESTAMP
  );