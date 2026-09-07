# 校园二手交易平台 · 服务端 (campus-trade-server)

Spring Boot 2.7 + MyBatis + MySQL，对应《开发文档.md》第三部分。

## 目录结构

```
server/
├── pom.xml                         # Maven 工程（Spring Boot 2.7.18）
├── sql/
│   ├── schema.sql                  # 建库建表 + 分类初始化（已在本机执行）
│   └── demo_data.sql               # 可选演示数据（手动执行）
├── uploads/                        # 图片上传目录（运行时自动创建）
└── src/main/
    ├── java/com/campus/trade/      # 启动类 / controller / service / mapper / entity / dto / config / utils / common
    └── resources/
        ├── application.yml         # 数据源、JWT、上传目录等配置
        └── mapper/*.xml            # MyBatis SQL
```

## 数据库

- 库：`campus_trade`（utf8mb4），8 张表 + 6 条分类初始数据，由 `sql/schema.sql` 创建。
- 应用账号：自行创建（建议仅授 `campus_trade` 库 SELECT/INSERT/UPDATE/DELETE），例如：
  ```sql
  CREATE USER 'campus_app'@'localhost' IDENTIFIED BY '你的强密码';
  GRANT SELECT, INSERT, UPDATE, DELETE ON campus_trade.* TO 'campus_app'@'localhost';
  ```
- 连接信息与密钥**全部通过环境变量提供**（详见 `.env.example`），仓库不含真实密码。

## 本地运行

> 需要：JDK 11+（建议 17/21）、Maven 3.6+、MySQL。

```bash
cd server
# 1. 准备环境变量（按 .env.example 填写，勿提交 .env.local）
#    Linux/macOS:  set -a; source .env.local; set +a
#    PowerShell:   见 .env.example 顶部注释

# 2. （可选）以管理员重置数据库
mysql -uroot -p < sql/schema.sql

# 3. 构建并运行
mvn clean package -DskipTests
java -jar target/campus-trade-server.jar
# 或开发模式： mvn spring-boot:run
```

启动后访问：`http://localhost:8080/`；上传图片访问 `http://localhost:8080/images/<文件名>`。

生产注意：设置 `UPLOAD_DIR`（绝对路径）、`JWT_SECRET`、`ACCESS_URL` 环境变量；确认 8080 端口开放。

## 接口清单（前缀 /v1，除 auth 外均需 `Authorization: Bearer <token>`）

| 模块 | 方法与路径 | 说明 |
|---|---|---|
| 认证 | POST /auth/register | 注册 {email,password,nickname?} |
| 认证 | POST /auth/login | 登录，返回 token |
| 分类 | GET /categories | 分类列表 |
| 商品 | GET /products?categoryId&keyword&page&size&sort(hot/new) | 分页浏览（上架中） |
| 商品 | GET /products/{id} | 详情（浏览量+1） |
| 商品 | POST /products | 发布 {title,description,price,categoryId,condition,imageUrls[]} |
| 商品 | PUT /products/{id}/status?status=0\|1 | 下架/上架（卖家） |
| 商品 | DELETE /products/{id} | 删除（卖家，无订单时） |
| 商品 | POST /products/{id}/favorite | 收藏/取消收藏 |
| 商品 | GET /products/{id}/favorited | 是否已收藏 |
| 商品 | GET /products/{id}/comments?page&size | 评论列表 |
| 商品 | POST /products/{id}/comments | 发表评论 {content} |
| 用户 | GET /user/profile | 我的资料 |
| 用户 | PUT /user/profile | 修改资料（昵称/头像/手机/年级/专业） |
| 用户 | PUT /user/password | 修改密码 {oldPassword,newPassword} |
| 用户 | GET /user/products?status | 我的发布 |
| 用户 | GET /user/favorites | 我的收藏 |
| 订单 | POST /orders | 买家下单 {productId,remark?}（商品→已预约） |
| 订单 | GET /orders?status&role(buyer/seller) | 我的订单 |
| 订单 | PUT /orders/{id}/status | 状态流转 {status,meetingTime?,meetingPlace?}：1接单(卖家,需见面信息)/2完成/3取消 |
| 消息 | POST /messages | 发送 {toUserId,content,msgType?} |
| 消息 | GET /messages/conversations | 会话列表（含未读） |
| 消息 | GET /messages/poll?targetUserId&sinceId | 轮询新消息 |
| 消息 | GET /messages/history/{targetUserId}?page&size | 聊天历史（倒序分页） |
| 消息 | GET /messages/unread/count | 未读总数 |
| 消息 | PUT /messages/read?fromUserId | 标记已读 |
| 上传 | POST /upload/image (multipart file) | 上传图片，返回访问 URL |

统一响应：`{code,msg,data}`，code=0 成功。401 表示未登录/Token 过期。

## 订单状态机

- 订单 status：0 待确认 → 1 交易中 → 2 已完成；0/1 可 → 3 取消
- 商品 status：1 上架中 →（买家下单）2 已预约 →（完成）3 已售出；（取消）回 1
- 只有卖家可接单（0→1），接单必须填写见面时间/地点；完成与取消买卖双方均可发起。
