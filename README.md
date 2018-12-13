# 蛇形参数自动绑定，返回蛇形命名的json数据

> 需求: 前端传递蛇形命名的参数，后端java，参数命名规则是驼峰，
想自动地将蛇形命名的参数绑定到对应的驼峰命名的参数上

> 需求：java对象转成json格式，key值默认是驼峰，想转为蛇形命名

基于以上两个需求，需要自定义方法参数解析器（`HandlerMethodArgumentResolver`)，
以及配置`HttpMethodConverter`

项目使用的技术栈
1. springboot
2. spring-boot-starter-web
3. lombok

主要的配置都在类`com.tuyu.config.MessageConverter`中


- 配置类加上`@Configuration`注解
- 配置类继承WebMvcConfigurationSupport类，并重写`addArgumentResolvers`方法和`configureMessageConverters`
- 在重写`addArgumentResolvers`方法时，需要往HandlerMethodArgumentResolver列表中添加一个自定义的参数解析器
- 在重写`configureMessageConverters`方法时，需要往HttpMethodResolver列表中添加一个
MappingJackson2HttpMessageConverter对象，该对象需要配置ObjectWrapper,ObjectWrapper对象
需要设置PropertyNamingStrategy为SnakeCaseStrategy
