DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS items CASCADE;
DROP TABLE IF EXISTS requests CASCADE;
DROP TABLE IF EXISTS bookings CASCADE;
DROP TABLE IF EXISTS comments CASCADE;

CREATE TABLE IF NOT EXISTS users (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(512) NOT NULL,
  CONSTRAINT UQ_USER_EMAIL UNIQUE (email),
  CONSTRAINT pk_user PRIMARY KEY (id)
);

CREATE TABLE if NOT EXISTS requests (
	id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
	description VARCHAR NOT NULL,
	requestor_id BIGINT NOT NULL,
	creation_date TIMESTAMP WITHOUT TIME ZONE,
	CONSTRAINT pk_request PRIMARY KEY  (id),
	CONSTRAINT fk_request_users FOREIGN KEY (requestor_id) REFERENCES users ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS items (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  name VARCHAR(255) NOT NULL,
  description VARCHAR NOT null,
  is_available BOOLEAN not null,
  owner_id BIGINT not null,
  request_id BIGINT,
  CONSTRAINT pk_item PRIMARY KEY  (id),
  CONSTRAINT fk_item_users FOREIGN KEY (owner_id) REFERENCES users ON DELETE CASCADE,
  CONSTRAINT fk_item_requests FOREIGN KEY (request_id) REFERENCES requests ON DELETE CASCADE
  );

CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    item_id BIGINT,
    booker_id BIGINT,
    status VARCHAR,
    CONSTRAINT pk_booking PRIMARY KEY  (id),
    CONSTRAINT fk_booking_users FOREIGN KEY (booker_id) REFERENCES users ON DELETE CASCADE,
    CONSTRAINT fk_booking_items FOREIGN KEY (item_id) REFERENCES items ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS comments (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text VARCHAR NOT NULL,
    item_id BIGINT,
    author_id BIGINT,
    created TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_comments PRIMARY KEY  (id),
    CONSTRAINT fk_comments_users FOREIGN KEY (author_id) REFERENCES users ON DELETE CASCADE,
    CONSTRAINT fk_comments_items FOREIGN KEY (item_id) REFERENCES items ON DELETE CASCADE
);