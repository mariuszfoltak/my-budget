CREATE TABLE users (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  passwordHash varchar(255) DEFAULT NULL,
  username varchar(255) DEFAULT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE accounts (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  name varchar(255) DEFAULT NULL,
  user_id bigint(20) DEFAULT NULL,
  PRIMARY KEY (id),
  KEY fk_accounts_user_id (user_id),
  CONSTRAINT fk_accounts_user_id FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE categories (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  name varchar(255) DEFAULT NULL,
  user_id bigint(20) DEFAULT NULL,
  parent_id bigint(20) DEFAULT NULL,
  PRIMARY KEY (id),
  KEY fk_categories_user_id (user_id),
  KEY fk_categories_parent_id (parent_id),
  CONSTRAINT fk_categories_user_id FOREIGN KEY (user_id) REFERENCES users (id),
  CONSTRAINT fk_categories_parent_id FOREIGN KEY (parent_id) REFERENCES categories (id)
);

CREATE TABLE tags (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  name varchar(255) DEFAULT NULL,
  user_id bigint(20) DEFAULT NULL,
  PRIMARY KEY (id),
  KEY fk_tags_user_id (user_id),
  CONSTRAINT fk_tags_user_id FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE transactions (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  amount double DEFAULT NULL,
  description varchar(255) DEFAULT NULL,
  transaction_date date DEFAULT NULL,
  account_id bigint(20) DEFAULT NULL,
  category_id bigint(20) DEFAULT NULL,
  PRIMARY KEY (id),
  KEY fk_transactions_account_id (account_id),
  KEY fk_transactions_category_id (category_id),
  CONSTRAINT fk_transactions_account_id FOREIGN KEY (account_id) REFERENCES accounts (id),
  CONSTRAINT fk_transactions_category_id FOREIGN KEY (category_id) REFERENCES categories (id)
);

CREATE TABLE transactions_tags (
  transaction_id bigint(20) NOT NULL,
  tag_id bigint(20) NOT NULL,
  KEY fk_transactions_tag_id (tag_id),
  KEY fk_tags_transaction_id (transaction_id),
  CONSTRAINT fk_transactions_tag_id FOREIGN KEY (tag_id) REFERENCES tags (id),
  CONSTRAINT fk_tags_transaction_id FOREIGN KEY (transaction_id) REFERENCES transactions (id)
);

