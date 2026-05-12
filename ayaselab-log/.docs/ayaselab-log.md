# AyaseLab 日志组件设计文档

## 1. 背景与目标

AyaseLab 需要一套统一日志组件，支持两类日志场景：

- `TraceLog`：记录一次接口请求从进入系统到返回响应的完整生命周期。
- `MiniLog`（待重命名）：记录单次事件结果，例如 Job 执行结果、服务启动结果、一次关键动作执行结果。

本阶段目标：

- 统一日志字段标准，便于检索与排障。
- 统一日志目录与文件规范，先落地到本地文件。
- 设计可扩展的组件结构，后续可平滑接入 ELK / Loki / ClickHouse 等日志平台。

---

## 2. 日志类型命名建议

`MiniLog` 语义偏弱，不够表达“单次事件记录”的业务含义。建议改为：

- **首选：`EventLog`**
- 备选：`ActionLog`、`OneShotLog`

推荐使用 `EventLog` 的原因：

- 语义清晰：表示“事件日志”，天然覆盖 Job、启动、定时任务、管理动作等。
- 易扩展：后续可细分 `eventType`（`JOB`、`STARTUP`、`SCHEDULED_TASK`、`MANUAL_OP`）。
- 行业通用度高，团队认知成本低。

**本文后续统一采用 `EventLog` 作为 `MiniLog` 的替代命名。**

---

## 3. 本地文件目录设计

项目根目录下统一落盘：

- `./Log/TraceLog`：TraceLog 文件目录
- `./Log/EventLog`：EventLog 文件目录（原 MiniLog）

说明：

- 路径使用“项目根目录相对路径”。
- 组件初始化时自动检查目录，不存在则自动创建。

---

## 4. 日志格式规范

### 4.1 推荐格式

建议采用 **JSON 单行（JSON Lines）**：每条日志一行 JSON。

优点：

- 结构化程度高，后续接入日志平台无需二次解析。
- 可读性和机器处理能力平衡较好。
- 对字段扩展更友好。

### 4.2 编码与时区

- 编码：`UTF-8`
- 时间：`ISO-8601`（含时区偏移），例如 `2026-04-22T17:10:18.235+08:00`

---

## 5. TraceLog 字段设计（接口全生命周期）

### 5.1 必填字段

- `logType`：固定值 `TRACE`
- `timestamp`：日志产生时间
- `traceId`：链路 ID（一次请求全程不变）
- `spanId`：当前处理节点 ID（单体可先固定或简化）
- `serviceName`：服务名（如 `ayaselab-web`）
- `environment`：环境（`local/dev/uat/prod`）
- `host`：主机标识（主机名/IP）
- `thread`：线程名
- `level`：日志级别（通常 `INFO`）
- `protocol`：`HTTP` / `RPC`
- `method`：HTTP 方法（`GET/POST/...`）
- `path`：请求路径（不含 query）
- `route`：路由模板（如 `/version/index`，可选生成）
- `statusCode`：响应状态码
- `success`：是否成功（布尔）
- `durationMs`：总耗时（毫秒）
- `requestTime`：请求进入时间
- `responseTime`：响应返回时间

### 5.2 建议字段

- `appId`：应用 ID
- `cluster`：Apollo 集群或部署集群
- `clientIp`：调用方 IP
- `userAgent`：请求头 User-Agent
- `referer`：来源页
- `requestId`：网关或上游传入请求 ID
- `bizCode`：业务结果码
- `errorCode`：错误码
- `errorMessage`：错误摘要（建议脱敏）
- `exceptionClass`：异常类名
- `requestSize`：请求体字节数
- `responseSize`：响应体字节数
- `operatorId`：操作用户 ID（登录态）
- `tenantId`：租户 ID（多租户场景）
- `tags`：业务标签数组（如 `['version','public-api']`）

### 5.3 请求/响应内容字段（受控）

- `requestBody`：请求体摘要（可截断）
- `responseBody`：响应体摘要（可截断）

策略建议：

- 默认关闭完整 body 落盘，仅保留摘要。
- 单条最大长度限制（例如 4KB）。
- 对敏感字段做脱敏（手机号、身份证、token、密码）。

---

## 6. EventLog 字段设计（单次事件日志）

### 6.1 必填字段

- `logType`：固定值 `EVENT`
- `timestamp`：事件时间
- `eventId`：事件唯一 ID
- `eventType`：事件类型（`JOB`、`STARTUP`、`TASK`、`MANUAL_OP`）
- `eventName`：事件名称（如 `UserSyncJob`、`ApplicationStartup`）
- `serviceName`：服务名
- `environment`：环境
- `host`：主机标识
- `thread`：线程名
- `level`：日志级别
- `success`：是否成功
- `durationMs`：执行耗时（无耗时场景可填 0）
- `message`：结果说明（简短文本）

### 6.2 建议字段

- `traceId`：若事件来源于请求链路则关联
- `triggerType`：触发方式（`SCHEDULED`、`MANUAL`、`SYSTEM`）
- `triggerBy`：触发人/触发系统
- `jobName`：任务名
- `jobGroup`：任务分组
- `runAt`：计划执行时间
- `startTime`：开始时间
- `endTime`：结束时间
- `resultCode`：结果码
- `errorCode`：错误码
- `errorMessage`：错误摘要
- `exceptionClass`：异常类名
- `retryCount`：重试次数
- `nextRetryTime`：下次重试时间
- `ext`：扩展字段对象（JSON）

---

## 7. 文件命名与滚动策略

### 7.1 文件命名

支持按“天维度”拆分，使用 `yyyy-MM-dd.log` 完全可行。推荐在“按类型分目录”的前提下使用同名日期文件：

- TraceLog：`YYYY-MM-DD.log`
- EventLog：`YYYY-MM-DD.log`

例如：

- `./Log/TraceLog/2026-04-22.log`
- `./Log/EventLog/2026-04-22.log`

说明：

- 因为 TraceLog 与 EventLog 已分目录，文件名可相同，不会冲突。
- 日期建议以服务本地时区生成，避免跨天切分混乱。

### 7.2 滚动策略

建议“双策略”并存：

- 按天滚动：每天新文件
- 按大小滚动：单文件超过阈值（如 100MB）自动分片

分片命名示例：

- `2026-04-22.1.log`
- `2026-04-22.2.log`

### 7.3 保留与清理

- 本地保留天数：建议 7~30 天（本地开发可 7 天）
- 自动清理：每日凌晨清理过期文件
- 可选压缩：`gzip` 历史文件

---

## 8. 组件分层设计

### 8.1 核心模块建议

- `LogContext`：统一上下文（traceId、operatorId、tenantId 等）
- `TraceLogger`：TraceLog 入口
- `EventLogger`：EventLog 入口
- `LogFormatter`：对象 -> JSON 行
- `LogWriter`：异步写文件
- `LogMasker`：敏感字段脱敏
- `LogRetentionManager`：过期文件清理

### 8.2 写入模型

建议采用异步写入：

- 业务线程只负责组装日志并入队
- 单独写线程批量刷盘
- 队列满时策略可配（丢弃低优先级/阻塞短时/降级同步）

---

## 9. TraceLog 生命周期采集建议

一次 HTTP 请求建议至少打 3 个节点（也可聚合为 1 条总结日志）：

- `REQUEST_IN`：收到请求
- `BIZ_DONE`：核心业务处理完成
- `RESPONSE_OUT`：响应返回

若只保留 1 条总结日志，则应保证 `durationMs/statusCode/success/error*` 完整。

---

## 10. 安全与合规

- 禁止明文记录：密码、token、密钥、证件完整号、银行卡号。
- 对手机号/身份证做掩码。
- 错误堆栈建议分级：开发环境可全量，生产环境建议摘要 + 错误码。
- 对超大对象统一截断，避免日志爆量。

---

## 11. 配置项建议（组件级）

建议提供统一配置：

- `log.baseDir`：默认 `./Log`
- `log.trace.enabled`
- `log.event.enabled`
- `log.level`
- `log.maxFileSizeMb`
- `log.retentionDays`
- `log.async.queueSize`
- `log.async.flushIntervalMs`
- `log.masking.enabled`
- `log.body.capture.enabled`
- `log.body.maxLength`

---

## 12. 示例日志

### 12.1 TraceLog 示例

```json
{"logType":"TRACE","timestamp":"2026-04-22T17:20:00.125+08:00","traceId":"9fa0b84d8e9f4f2e","spanId":"1","serviceName":"ayaselab-web","environment":"local","host":"127.0.0.1","thread":"http-nio-8080-exec-1","level":"INFO","protocol":"HTTP","method":"GET","path":"/version/index","statusCode":200,"success":true,"durationMs":12,"requestTime":"2026-04-22T17:20:00.113+08:00","responseTime":"2026-04-22T17:20:00.125+08:00","clientIp":"127.0.0.1"}
```

### 12.2 EventLog 示例

```json
{"logType":"EVENT","timestamp":"2026-04-22T17:21:00.001+08:00","eventId":"evt-20260422172100001","eventType":"JOB","eventName":"UserSyncJob","serviceName":"ayaselab-core","environment":"local","host":"127.0.0.1","thread":"pool-1-thread-1","level":"INFO","success":true,"durationMs":352,"message":"sync finished","triggerType":"SCHEDULED","retryCount":0}
```

---

## 13. Kafka + Elasticsearch 接入设计（生产化）

### 13.1 总体链路

建议链路：

- 应用本地落盘（`./Log/TraceLog`、`./Log/EventLog`）
- Log Agent 采集文件并推送 Kafka
- Kafka 作为缓冲与解耦层
- Logstash / Kafka Connect 消费 Kafka 写入 Elasticsearch
- Kibana 查询与可视化

### 13.2 准备工作清单

#### A. 应用侧

- 日志格式固定为 JSON 单行（已在本文定义）
- 每条日志必须包含 `logType`、`serviceName`、`environment`、`timestamp`
- 开启本地滚动与保留策略，保证采集窗口内日志不被提前删除

#### B. 采集层（建议 Filebeat/Vector/Fluent Bit 任选一种）

- 监控目录：
- `./Log/TraceLog/*.log`
- `./Log/EventLog/*.log`
- 按 JSON 解析，失败日志打入错误通道
- 为每条日志补充采集元数据（`agentHost`、`filePath`、`offset`）

#### C. Kafka 层

- 建议 Topic：
- `ayase.trace.log`
- `ayase.event.log`
- 分区数按吞吐规划（初期可 3~6，后续扩展）
- 副本数生产建议 `>=3`
- 保留策略建议按容量与时长双控（如 3~7 天）

#### D. 入库层（Logstash/Kafka Connect）

- 消费 Kafka JSON 消息并写入 ES
- 失败重试与死信队列（DLQ）必须开启
- 建议按天写索引：
- `ayase-trace-YYYY.MM.DD`
- `ayase-event-YYYY.MM.DD`

#### E. Elasticsearch + Kibana

- 创建 Index Template，定义字段类型（keyword/text/date/long/boolean）
- 为 `traceId`、`eventId`、`serviceName`、`environment`、`statusCode` 建索引映射
- 使用 ILM（Index Lifecycle Management）管理冷热分层和过期删除
- 在 Kibana 建立 Data View 与常用 Dashboard（错误率、P95 耗时、Job 成功率）

### 13.3 字段映射建议（关键字段）

- `timestamp` -> `date`
- `traceId` / `eventId` / `serviceName` / `environment` / `logType` -> `keyword`
- `message` / `errorMessage` -> `text` + `keyword`（可选 multi-field）
- `durationMs` / `statusCode` / `retryCount` -> `long` / `integer`
- `success` -> `boolean`

### 13.4 生产可用性门槛

要达到“可用于生产”，建议至少满足以下条件：

- 高可用：
- Kafka 多副本，ES 多节点，采集器支持断点续传
- 可靠性：
- 至少一次投递链路（At-Least-Once），并具备去重策略（必要时）
- 可观测性：
- 采集延迟、Kafka 堆积、消费失败率、ES 写入失败率都有监控告警
- 安全：
- 传输链路 TLS、Kafka/ES 鉴权、敏感字段脱敏
- 成本治理：
- 日志分级、采样策略、冷热分层、保留天数策略明确
- 灾备：
- 关键索引快照与恢复演练

### 13.5 结论

“本地文件 -> Kafka -> Elasticsearch”这条链路是业界常见生产方案，方向正确；但是否“足够生产”取决于是否补齐高可用、重试/DLQ、监控告警、生命周期治理与安全策略。以上门槛补齐后即可支撑复杂项目生产使用。

---

## 14. 本阶段落地边界

当前阶段仅本地文件，明确不包含：

- 日志平台采集与检索（ELK/Loki）
- 分布式追踪系统（SkyWalking/Zipkin）深度对接
- 告警规则引擎

后续可在不改字段协议的前提下平滑升级。

---

## 15. 结论

- 两类日志建议命名为：`TraceLog` + `EventLog`。
- 本地目录建议：`./Log/TraceLog` 与 `./Log/EventLog`。
- TraceLog 用于请求全链路，EventLog 用于单次事件结果。
- 支持按天拆分为 `yyyy-MM-dd.log`，在分目录场景下可直接采用。
- 统一 JSON 单行格式 + 异步写入 + 脱敏策略 + Kafka/ES 生产化治理，是复杂项目可持续演进的基础方案。
