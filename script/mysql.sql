


-- --------------------------------------------------------
-- --------------------------------------------------------

CREATE TABLE IF NOT EXISTS `silo` (
  `id` int(12) NOT NULL,
  `topic` varchar(255) NOT NULL,
  `message` TEXT NOT NULL,
  `created` BIGINT(19) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

ALTER TABLE `silo` ADD PRIMARY KEY (`id`);
ALTER TABLE `silo` MODIFY `id` int(12) NOT NULL AUTO_INCREMENT;
ALTER TABLE `silo` ADD INDEX(`topic`);

-- --------------------------------------------------------
-- --------------------------------------------------------
