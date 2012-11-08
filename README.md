###基于mybatis的自制框架

#### 主要增加了通用分页方法:
通过修改源码(org.apache.ibatis.binding.MapperProxy)实现

* 在mapper方法使用注解 @Pagination 申明分页查询
* 是用拦截器生成数据库分页方言
* 在(org.apache.ibatis.binding.PageMapperMethod) 中组装分页结果对象(org.nerv.framework.util.Page)

缺点:

* 修改了源码。运行时必须将修改的org.apache.ibatis.binding.MapperProxy拷至本地
* 分页结果必须制定mapresult，不支持`List<Map>`结果类型


