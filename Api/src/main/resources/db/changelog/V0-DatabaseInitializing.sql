CREATE TABLE account (
    id BIGSERIAL PRIMARY KEY,
    login VARCHAR(64) NOT NULL,
    password VARCHAR(64) NOT NULL,
    username VARCHAR(128) NOT NULL,
    country VARCHAR(64),
    region VARCHAR(64),
    town VARCHAR(64),
    avatar_link VARCHAR(256),
    seller_rating INT NOT NULL
);

CREATE TABLE advertisement (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL,
    title VARCHAR(128) NOT NULL,
    description TEXT,
    preview_link VARCHAR(256),
    published TIMESTAMP NOT NULL,
    price NUMERIC(12, 2),
    country VARCHAR(64),
    region VARCHAR(64),
    town VARCHAR(64),
    is_paid BOOLEAN NOT NULL,
    is_closed BOOLEAN NOT NULL,

    CONSTRAINT fk_account_id
        FOREIGN KEY (account_id)
        REFERENCES account(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE category (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(64) NOT NULL
);

CREATE TABLE rating (
    id BIGSERIAL PRIMARY KEY,
    seller_id BIGINT NOT NULL,
    reviewer_id BIGINT NOT NULL,
    score INT NOT NULL,
    comment VARCHAR(500),
    written_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_seller_id
        FOREIGN KEY (seller_id)
        REFERENCES account(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_reviewer_id
        FOREIGN KEY (reviewer_id)
        REFERENCES account(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE comment (
    id BIGSERIAL PRIMARY KEY,
    advertisement_id BIGINT NOT NULL,
    account_id BIGINT NOT NULL,
    sent_at TIMESTAMP NOT NULL,
    content VARCHAR(1000) NOT NULL,

    CONSTRAINT fk_advertisement_id
        FOREIGN KEY (advertisement_id)
        REFERENCES advertisement(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_account_id
        FOREIGN KEY (account_id)
        REFERENCES account(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE message (
    id BIGSERIAL PRIMARY KEY,
    sender_id BIGINT NOT NULL,
    reciever_id BIGINT NOT NULL,
    sent_at TIMESTAMP NOT NULL,
    content VARCHAR(1000) NOT NULL,

    CONSTRAINT fk_sender_id
        FOREIGN KEY (sender_id)
        REFERENCES account(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_reciever_id
        FOREIGN KEY (reciever_id)
        REFERENCES account(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE role (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(32) NOT NULL
);

CREATE TABLE advertisement_category (
    advertisement_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,

    CONSTRAINT fk_advertisement_id
        FOREIGN KEY (advertisement_id)
        REFERENCES advertisement(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_category_id
        FOREIGN KEY (category_id)
        REFERENCES category(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE account_role (
    account_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,

    CONSTRAINT fk_account_id
        FOREIGN KEY (account_id)
        REFERENCES account(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_role_id
        FOREIGN KEY (role_id)
        REFERENCES role(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);