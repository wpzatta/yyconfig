
# 愿景
  Yyconfig会作为一个 面对对象的配置中心加上内存数据库 ， 但你写代码时，只要告诉 哪些对象交给yyconfig管理。 yyconfig会在提供页面操作，新增，修改删除。  让你写代码时，完全只考虑对象， 不用考虑数据库的存在。同时 不需要 进行 页面管理的开发   

# Features
* ** 携程apollo的不足**
  * 设计过于复杂,资源消耗过多，部署复杂，实际很多应用不需要兼容C#.net
  * 项目内部依赖过于混乱,对于中小企业的配置中心应用不够简单
  * client升级， 根据应用去读取对应的配置
  * 数据的安全性，部分数据需要加密展示，或者不展示，如用户名，密码（只能授权用户查看）
  * 接口安全问题： 目前接口可以直接通过页面的get请求获取-》 改造方案通过JWT通过各个app生成token来进行登陆验证
  * 增强权限控制，权限控制，目前只到组，而且同一时间只能一个组看到对应app。管理员应该看到所有，一个人只能属于一个部门，只能看到一个部门的项目; 改进方案 一个人下挂多个项目，  同时共有项目可以属于部门。 这个部门的项目可以继承这个部门的共有项目
  * 事件监听机制，改成内部消息
  * 应用分类，目前应用没有类型区分，为每个应用做对应的资源下拉。实际生产过程中应该安装业务相关，和业务无关（基础资源区分）
  * 页面UI，前端框架升级， 计划采用react进行对Anglar1的重构
  
  
* ** 增强改造点**  
  *  Yyconfig成为springboot体系标准配置中心,springboot版本升级2.0
  *  代码注解配置 自动映射到配置中心,支持 对象的数据 的映射和 展示
  *  用户在配置中心勾选所需要的命名空间， 然后在client端实时生效
  *  支持对象
  *  操作审计日志优化
  