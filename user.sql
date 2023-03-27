--
-- 数据库： `yunyou`
--

-- --------------------------------------------------------

--
-- 表的结构 `user`
--

CREATE TABLE `user` (
  `userAccount` varchar(255) NOT NULL,
  `userPassword` varchar(255) NOT NULL,
  `userName` varchar(255) NOT NULL,
  `state` int(1) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- 转存表中的数据 `user`
--

INSERT INTO `user` (`userAccount`, `userPassword`, `userName`, `state`) VALUES
('1', '1', 'admin', 0),
('12', '12', '12', 0),
('123', '123', 'admin', 0),
('1234', '123', 'guest', 1),
('123456', '123', 'ab', 0),
('1234567', '123', 'abc', 0),
('12345678', '123', 'abcd', 0),
('1234567890', '123', 'abcde', 0);

--
-- 转储表的索引
--

--
-- 表的索引 `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`userAccount`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
