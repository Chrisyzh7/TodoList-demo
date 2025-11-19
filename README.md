# Spring Boot TODO List 应用

这是一个基于 **Spring Boot 3** 和 **SQLite** 构建的待办事项管理应用。项目实现了前后端分离架构（尽管部署在一起），后端提供 RESTful API，前端使用原生 HTML/CSS/JS 进行交互。

## ✨ 功能特性

- **任务管理**：创建、删除、标记完成/未完成。
- **完整编辑**：支持修改任务标题、描述和优先级。
- **优先级管理**：支持高（🔥）、中（🟡）、低（❄️）三种优先级。
- **高级搜索与过滤**：支持按关键词模糊搜索，并结合完成状态进行过滤。
- **智能排序**：支持按“更新时间”、“创建时间”以及“优先级”（高->中->低）进行动态排序。
- **数据持久化**：使用 SQLite 文件数据库，无需额外安装数据库服务。
- **友好交互**：前端包含加载动画、空状态提示、相对时间显示及弹窗编辑。

## 🛠 技术栈

- **后端**：Java 17, Spring Boot 3.x, Spring Data JPA, SQLite
- **前端**：HTML5, CSS3, JavaScript (ES6+)
- **构建工具**：Maven

## 🚀 快速开始

### 1. 环境要求
- JDK 17 或更高版本
- Maven 3.6+ (如果使用 IDE 内置 Maven 则无需安装)

### 2. 运行项目

#### 方法 A：使用 IntelliJ IDEA (推荐)
1. 打开项目，等待 Maven 依赖下载完成。
2. 找到 `src/main/java/com/zhyuan/todolist/ToDoListApplication.java`。
3. 点击运行按钮 (Run)。

#### 方法 B：使用命令行
在项目根目录下运行：
```bash
mvn spring-boot:run
```
### 3. 访问应用

项目启动成功后（控制台看到 `Started ToDoListApplication`），打开浏览器访问：

👉 **http://localhost:8080/demo.html**

## 📂 数据库说明

项目启动后，会自动在根目录生成一个名为 `todos.db` 的 SQLite 数据库文件。

- **注意**：如果需要修改实体类结构（例如添加字段），建议在开发环境中直接删除 `todos.db` 文件并重启应用，Hibernate 会自动重新生成正确的表结构。