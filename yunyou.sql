

--
-- 数据库： `yunyou`
--

-- --------------------------------------------------------

--
-- 表的结构 `feedback`
--

CREATE TABLE `feedback` (
  `id` int(11) NOT NULL,
  `userAccount` varchar(255) NOT NULL,
  `content` varchar(255) NOT NULL,
  `Stars` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- 转存表中的数据 `feedback`
--

INSERT INTO `feedback` (`id`, `userAccount`, `content`, `Stars`) VALUES
(14, '1', '', '0.0'),
(15, '1', '', '0.0'),
(16, '1', '', '0.0'),
(17, '1', '', '0.0'),
(18, '1', 'good', '5.0'),
(19, '1', '', '0.0'),
(20, '1', '', '0.0'),
(21, '1', '', '0.0'),
(22, '1', '', '0.0'),
(23, '1', '123', '5.0'),
(24, '1', 'Good!', '5.0'),
(25, '1', 'xx', '5.0'),
(26, '1', '非常好！', '5.0'),
(27, '1', '很好', '0.0'),
(28, '1', 'Good', '5.0');

-- --------------------------------------------------------

--
-- 表的结构 `trip`
--

CREATE TABLE `trip` (
  `id` int(255) NOT NULL,
  `userAccount` varchar(255) NOT NULL,
  `departure` varchar(255) NOT NULL,
  `destination` varchar(255) NOT NULL,
  `date` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- 转存表中的数据 `trip`
--

INSERT INTO `trip` (`id`, `userAccount`, `departure`, `destination`, `date`) VALUES
(1, '1', '广东', '北京', '2022年5月25日12时30分'),
(2, '1', '北京', '天津', '2022年5月19日20时22分43秒'),
(3, '1', '佛山', '深圳', '2022年4月22日22时39分40秒'),
(4, '1', '东莞', '长沙', '2022年4月22日22时42分4秒'),
(6, '1', '中山', '珠海', '2022年4月22日22时49分55秒'),
(7, '1', '广东', '北京', '2022年5月28日23时4分49秒'),
(8, '1', '广东', '湖南', '2022年5月31日3时20分'),
(9, '1', '广东', '福建', '2022年5月27日1时43分47秒'),
(10, '1', '广东', '四川', '2022年4月23日1时55分57秒'),
(11, '1', '广东', '上海', '2022年5月31日3时55分');

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
('12', '12', 'guset', 0),
('123', '123', 'a', 0),
('1234', '123', 'ab', 1),
('12345', '123', 'abc', 0),
('123456', '123', 'abcd', 0),
('1234567', '123', 'abcde', 0),
('12346789', '123', 'abcdef', 0);

--
-- 转储表的索引
--

--
-- 表的索引 `feedback`
--
ALTER TABLE `feedback`
  ADD PRIMARY KEY (`id`);

--
-- 表的索引 `trip`
--
ALTER TABLE `trip`
  ADD PRIMARY KEY (`id`);

--
-- 表的索引 `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`userAccount`);

--
-- 在导出的表使用AUTO_INCREMENT
--

--
-- 使用表AUTO_INCREMENT `feedback`
--
ALTER TABLE `feedback`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=29;

--
-- 使用表AUTO_INCREMENT `trip`
--
ALTER TABLE `trip`
  MODIFY `id` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;
COMMIT;
