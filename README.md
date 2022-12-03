# crm
├── crm-base-- 通用代码
├── crm-util -- 工具类
└── crm-vo-- 实体类

├── crm-config-- 配置类
├── crm-enums-- 枚举类（分配转台类，和开发状态类）
├── crm-dao-- 数据访问层，具体到对于某个表的增删改查
├── crm-service-- 服务层，主要负责业务模块的应用逻辑和应用设计
└── crm-controller-- 控制层，负责请求转发，接受页面过来的参数，传给Service处理，接到返回值，再传给页面

├── crm-model-- 对象模型类
└── crm-query-- 多条件查询封装类

├── crm-interceptor-- 拦截器类（非法访问拦截）
├── crm-aspect-- 切面类，PermissionProxy权限代理
├── crm-annoation-- 注解类
├── crm-exceptions-- 自定义异常处理类
└── GlobalExceptionResolver 全局异常统一处理类

| 技术       | 说明                | 官网                                           |
| ---------- | ------------------- | ---------------------------------------------- |
| SpringBoot | 容器+MVC框架        | https://spring.io/projects/spring-boot         |
| MyBatis    | ORM框架             | http://www.mybatis.org/mybatis-3/zh/index.html |
| PageHelper | MyBatis物理分页插件 | http://git.oschina.net/free/Mybatis_PageHelper |
| Swagger-UI | 文档生成工具        | https://github.com/swagger-api/swagger-ui      |
| layUI      | Web UI组件库        | https://layer.layui.com/                       |
