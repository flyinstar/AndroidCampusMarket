# 校园集市 · 校园二手物品交易平台

校园范围内的 C2C 二手物品交易 App：**注册登录 → 发布/浏览闲置 → 私聊议价 → 线下交易**，无线上支付，交易由买卖双方线下完成。实现遵循《开发文档.md》（v1.0）。

## 功能一览

| 模块 | 说明 |
|---|---|
| 账号 | 邮箱+密码注册/登录（JWT 7 天有效）、记住密码、修改资料/密码 |
| 首页 | 商品流（热门/最新）、分类快捷、关键词搜索、下拉刷新+分页加载 |
| 分类 | 教材教辅 / 电子产品 / 生活用品 / 服饰鞋包 / 运动户外 / 其他 |
| 发布 | 相册选图(≤6) → 压缩 → 逐张上传 → 填写标题/价格/分类/成色/描述 |
| 商品详情 | 多图浏览、收藏/取消、评论区、查看发布者信用 |
| 聊天 | 会话列表 + 单聊，**3 秒轮询**增量拉取新消息，进出页面自动启停 |
| 订单 | 买家下单 → 卖家接单（约定见面时间/地点）→ 线下完成 / 取消，联动商品状态 |
| 我的 | 我的发布（上下架/删除）、我的收藏、我的订单、退出登录 |

## 技术栈

- **Android**：纯 Java + MVP（BaseView/BasePresenter/BaseActivity/BaseFragment），Retrofit2 + OkHttp3 + Gson，Glide，SharedPreferences；minSdk 23 / targetSdk 33
- **服务端**：Spring Boot 2.7.18 + MyBatis + MySQL；JWT（jjwt）；本地磁盘图片存储 `/images/**`
- **数据库**：MySQL 8.0+/9.x，`campus_trade`（utf8mb4），DDL 见 `server/sql/schema.sql`

## 目录结构

```
CampusMarket/
├── app/                      # Android 客户端（com.campus.trade，MVP）
│   └── src/main/java/com/campus/trade/
│       ├── base/             # BaseView / BasePresenter / BaseActivity / BaseFragment
│       ├── network/          # ApiService / ApiClient / AuthInterceptor(自动带Token)
│       ├── model/            # entity + repository（数据仓库）
│       ├── presenter/        # auth / product / message / order / user 契约与实现
│       ├── ui/               # activity / fragment / adapter
│       └── utils/            # SharedPrefUtils / ImageLoader / 图片压缩 / 时间
├── server/                   # Spring Boot 服务端（com.campus.trade）
│   ├── pom.xml
│   ├── sql/                  # schema.sql（建库） / demo_data.sql（演示数据）
│   ├── .env.example          # 环境变量样例（真实凭据放 .env.local，不入库）
│   └── src/main/
│       ├── java/...          # controller/service/mapper/entity/dto/config/utils/common
│       └── resources/        # application.yml + mapper/*.xml
├── docs/CHECKLIST.md         # 对照《开发文档.md》第六部分的验收清单
└── 开发文档.md                  # 需求与技术文档
```

## 快速开始

### 1. 准备数据库

```bash
# 用管理员账号执行建库脚本（含 8 张表与分类初始数据，可重复执行）
mysql -uroot -p < server/sql/schema.sql

# 建议创建最小权限应用账号（只授 campus_trade 库）
mysql -uroot -p -e "
CREATE USER 'campus_app'@'localhost' IDENTIFIED BY '<你的强密码>';
GRANT SELECT, INSERT, UPDATE, DELETE ON campus_trade.* TO 'campus_app'@'localhost';
"
```

可选演示数据：`mysql -uroot -p < server/sql/demo_data.sql`

### 2. 启动服务端

```bash
cd server
cp .env.example .env.local   # 填写 DB_USERNAME / DB_PASSWORD / JWT_SECRET
# Linux/macOS 加载:  set -a; source .env.local; set +a
# PowerShell 加载:  按 .env.example 顶部注释逐行设置

mvn clean package -DskipTests
java -jar target/campus-trade-server.jar
```

启动后：接口 `http://localhost:8080/`，上传图片访问 `http://localhost:8080/images/<文件名>`。
完整接口清单与订单状态机见 [`server/README.md`](server/README.md)。

**环境变量**（全部凭据不入库）：

| 变量 | 说明 | 默认 |
|---|---|---|
| `DB_HOST` / `DB_PORT` / `DB_NAME` | 数据库连接 | `localhost` / `3306` / `campus_trade` |
| `DB_USERNAME` / `DB_PASSWORD` | 数据库账号（**必填**） | 无 |
| `JWT_SECRET` | 签名密钥（**必填**，建议 32+ 位随机） | 无 |
| `UPLOAD_DIR` | 图片存储目录 | `./uploads/` |
| `ACCESS_URL` | 图片访问前缀 | `http://localhost:8080/images/` |

### 3. 运行 Android 客户端

用 Android Studio 打开工程根目录，Gradle 同步后运行 `app`。

- **模拟器**：服务端地址默认 `http://10.0.2.2:8080/`（无需改动）
- **真机**：手机与电脑同一局域网，修改 `app/src/main/java/com/campus/trade/network/ApiClient.java` 中 `BASE_URL` 为电脑局域网 IP（末尾保留 `/`），并放行电脑 8080 端口

## 开发与验收

- 需求文档：`开发文档.md`
- 检查清单：`docs/CHECKLIST.md`（对照文档第六部分：后端 8 项 / Android 7 项）

## 目录与仓库说明

- 构建产物、IDE 配置、`local.properties`、`server/.env.local` 等均已通过 `.gitignore` 排除，不会提交。
- Android 客户端含 Glide/Retrofit 注解处理器等依赖，首次 Gradle 同步需联网下载。