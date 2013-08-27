create table data_files (
  id INT AUTO_INCREMENT NOT NULL,
  created TIMESTAMP NOT NULL,
  fileName VARCHAR (255),
  digest VARCHAR (255),
  status VARCHAR (50) NOT NULL,
  INDEX (id)
) ENGINE=InnoDB;