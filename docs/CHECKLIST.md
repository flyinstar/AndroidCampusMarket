# 开发检查清单对照（《开发文档.md》第六部分）

> 状态：D = 已离线完成代码/资源，待网络恢复后编译验证；✅ = 已在本机完成。

## 后端

| # | 检查项 | 状态 | 说明 |
|---|--------|------|------|
| 1 | MySQL 数据库创建并初始化分类表数据 | ✅ | 本机 campus_trade 库已建（server/sql/schema.sql） |
| 2 | upload-dir 目录创建并赋予读写权限 | ✅/D | server/uploads 已创建；运行期自动 mkdirs |
| 3 | JWT 密钥配置完成 | D | application.yml app.jwt.secret/expiration，支持环境变量覆盖 |
| 4 | Swagger 访问正常（可选） | — | 文档标注可选，未引入 springdoc（减少依赖） |
| 5 | 登录接口调通返回 Token | D | POST /v1/auth/login，网络恢复后 Postman 验证 |
| 6 | 图片上传接口调通返回 URL | D | POST /v1/upload/image（需登录） |
| 7 | 商品发布接口调通（含图片URL列表） | D | POST /v1/products + product_image 批量写入 |
| 8 | 消息轮询接口调通（返回>sinceId消息） | D | GET /v1/messages/poll + conversations 未读数 |

## Android

| # | 检查项 | 状态 | 说明 |
|---|--------|------|------|
| 1 | 网络层 ApiClient 配置正确 IP | D | BASE_URL 默认 10.0.2.2:8080（模拟器），真机改局域网 IP |
| 2 | AuthInterceptor 正常注入 Token | D | network/interceptor/AuthInterceptor |
| 3 | 登录页跳转主页 | D | LoginActivity → MainActivity |
| 4 | 商品列表正常展示（含图片） | D | HomeFragment + ProductListActivity（Glide 封面图） |
| 5 | 发布商品流程（选图→压缩→上传→提交）完整 | D | PublishActivity + ImageCompressUtil + 逐张上传 |
| 6 | 聊天进入开始轮询、离开停止轮询 | D | ChatPresenter（3s Handler 轮询）+ onResume/onPause |
| 7 | 内存泄漏检测 / 轮询 Handler 正确释放 | D | BasePresenter 弱引用 + detachView 停轮询移除回调（LeakCanary 可选未集成） |

## 待办（网络恢复后）

1. 首次下载依赖：Retrofit 2.9 / OkHttp logging 4.10 / Glide 4.15.1 / Gson 2.10.1（本地无缓存）。
2. 安装/使用 Maven，构建并启动服务端：`cd server && mvn spring-boot:run`。
3. Gradle 同步并运行 App（模拟器/真机），按上面 D 项逐条回归。
4. （可选）执行 server/sql/demo_data.sql 造数验证列表/详情/聊天。
