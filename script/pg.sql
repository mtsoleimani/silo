


-- --------------------------------------------------------
-- --------------------------------------------------------

CREATE TABLE IF NOT EXISTS silo (
  id serial8 PRIMARY KEY NOT NULL,
  topic varchar(255) NOT NULL,
  message TEXT NOT NULL,
  created bigint NOT NULL DEFAULT 0
);


CREATE INDEX idx_topic ON silo (topic);

-- --------------------------------------------------------
-- --------------------------------------------------------



