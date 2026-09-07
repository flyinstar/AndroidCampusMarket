-- =============================================================
-- 可选演示数据（不会自动执行；需要时手动导入）
-- 导入前请先执行 schema.sql。
-- 两个演示账号：
--   alice@campus.edu / 123456
--   bob@campus.edu   / 123456
-- 注意：password_hash 是 "SHA-256(明文+salt)" 的十六进制串。
-- =============================================================
USE campus_trade;

INSERT INTO `user` (`email`, `password_hash`, `salt`, `nickname`, `grade`, `major`, `credit_score`) VALUES
('alice@campus.edu', '8a8d1b8b5f1a94b3ec20644a04d50a9f4c5b0b8b5c4f0d0e1f2a3b4c5d6e7f80', 'Ab3cDeFgH1', '爱丽丝', '2023级', '计算机科学', 100),
('bob@campus.edu',   '9b9e2c9c6g2b05c4fd30755b05e61ba0g5d6c1c9c6d5g1e0f2b3c4d5e6f7a90', 'Xy9zWvUtS2', '鲍勃',   '2022级', '电子信息工程', 100);

-- 说明：上方两个 password_hash 仅为示例占位，登录请先通过 /v1/auth/register 注册真实账号，
-- 或用 register 接口注册后把 userId 填到下面商品里。下面示例商品 user_id 默认指向 1。

INSERT INTO `product` (`title`, `description`, `price`, `user_id`, `category_id`, `condition`, `status`) VALUES
('高等数学教材（上下册）', '同济第七版，笔记很少，九成新。', 25.00, 1, 1, 2, 1),
('小米手环8', '使用三个月，功能正常，箱说全。', 120.00, 1, 2, 2, 1),
('宿舍小台灯', '可充电夹式，白光三档。', 15.00, 2, 3, 3, 1);
