# 1. Spring Boot、Spring MVC 和 Spring 有什么区别？

- Spring

Spring 框架就像一个家族，有众多衍生产品例如boot、security、jpa等。但它们的基础都是Spring的[IOC](https://github.com/DuHouAn/Java-Notes/blob/master/Spring/01SpringIOC.md)和 [AOP](https://github.com/DuHouAn/Java-Notes/blob/master/Spring/02SpringAOP.md)，在此两者的基础上实现了其他延伸产品的高级功能。

- Spring MVC

Spring MVC 提供了一种轻度耦合的方式来开发WEB应用。它是Spring的一个模块，是一个WEB框架。通过Dispatcher Servlet、ModelAndView 和 View Resolver，使得WEB开发变得更加容易。解决的问题邻域是网站应用或服务开发——URL路由、Session、模板引擎、静态Web资源等。

- Spring Boot

实现了自动配置，降低了项目搭建的复杂度。它主要是为了解决使用Spring框架需要进行大量配置过于麻烦的问题，所以它并不是用来替代Spring的解决方案，而是和Spring框架紧密结合用于提升Spring开发者体验的工具。同时它集成了大量常用的第三方库配置，例如JDBC、Redis、Mongo、Kafka等，Spring Boot应用中这些第三方库几乎可以零配置的开箱即用。



所以，用最简练的语言概括就是:
Spring 是一个“引擎”;
Spring MVC 是基于Spring的一个 MVC 框架;
Spring Boot 是基于Spring4的条件注册的一套快速开发整合包。

<div align="center">
<img src="https://user-gold-cdn.xitu.io/2018/8/6/1650d48b3fc268db?imageView2/0/w/1280/h/960/format/webp/ignore-error/1"/>    
</div>

 # 2. Spring Boot的自动配置是如何实现的？

Spring Boot项目的启动类只有一个注解@SpringBootApplication和一个run方法：

```java
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

直接看@SpringBootApplication的代码：

```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(
    excludeFilters = {@Filter(
    type = FilterType.CUSTOM,
    classes = {TypeExcludeFilter.class}
), @Filter(
    type = FilterType.CUSTOM,
    classes = {AutoConfigurationExcludeFilter.class}
)}
)
public @interface SpringBootApplication {
    @AliasFor(
        annotation = EnableAutoConfiguration.class
    )
    Class<?>[] exclude() default {};

    @AliasFor(
        annotation = EnableAutoConfiguration.class
    )
    String[] excludeName() default {};

    @AliasFor(
        annotation = ComponentScan.class,
        attribute = "basePackages"
    )
    String[] scanBasePackages() default {};

    @AliasFor(
        annotation = ComponentScan.class,
        attribute = "basePackageClasses"
    )
    Class<?>[] scanBasePackageClasses() default {};
}
```

@SpringBootApplication：包含了@SpringBootConfiguration（打开是@Configuration），@EnableAutoConfiguration，@ComponentScan注解。

## @Configuration

JavaConfig形式的Spring Ioc容器的配置类使用的那个@Configuration，SpringBoot社区推荐使用基于JavaConfig的配置形式，所以，这里的启动类标注了@Configuration之后，本身其实也是一个IoC容器的配置类。
 对比一下传统XML方式和config配置方式的区别：

- XML声明和定义配置方式：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:aop="http://www.springframework.org/schema/aop"
	xmlns:context="http://www.springframework.org/schema/context" xmlns:tx="http://www.springframework.org/schema/tx"
	xsi:schemaLocation="http://www.springframework.org/schema/beans 
						http://www.springframework.org/schema/beans/spring-beans-3.0.xsd
						http://www.springframework.org/schema/aop 
						http://www.springframework.org/schema/aop/spring-aop-3.0.xsd
						http://www.springframework.org/schema/context 
						http://www.springframework.org/schema/context/spring-context-3.0.xsd
						http://www.springframework.org/schema/tx 
						http://www.springframework.org/schema/tx/spring-tx-3.0.xsd
	">
	<bean id="app" class="com..." />
复制代码
```

- 用一个过滤器举例，JavaConfig的配置方式是这样：

```
@Configuration
public class DruidConfiguration {    
    @Bean
    public FilterRegistrationBean statFilter(){
        //创建过滤器
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new WebStatFilter());
        //设置过滤器过滤路径
        filterRegistrationBean.addUrlPatterns("/*");
        //忽略过滤的形式
        filterRegistrationBean.addInitParameter("exclusions","*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        return filterRegistrationBean;
    }
}

复制代码
```

**任何一个标注了@Configuration的Java类定义都是一个JavaConfig配置类。**
 **任何一个标注了@Bean的方法，其返回值将作为一个bean定义注册到Spring的IoC容器，方法名将默认成该bean定义的id。**

## @ComponentScan

@ComponentScan对应XML配置中的元素，@ComponentScan的功能其实就是自动扫描并加载符合条件的组件（比如@Component和@Repository等）或者bean定义，最终将这些bean定义加载到IoC容器中。
 我们可以通过basePackages等属性来细粒度的定制@ComponentScan自动扫描的范围，如果不指定，则默认Spring框架实现会从声明@ComponentScan所在类的package进行扫描。
 `注：所以SpringBoot的启动类最好是放在root package下，因为默认不指定basePackages。`

## @EnableAutoConfiguration

（**核心内容**）看英文意思就是自动配置，概括一下就是，借助@Import的帮助，将所有符合自动配置条件的bean定义加载到IoC容器。

```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@AutoConfigurationPackage
@Import({AutoConfigurationImportSelector.class})
public @interface EnableAutoConfiguration {
    String ENABLED_OVERRIDE_PROPERTY = "spring.boot.enableautoconfiguration";

    Class<?>[] exclude() default {};

    String[] excludeName() default {};
}
```

里面最关键的是@Import(**EnableAutoConfigurationImportSelector.class**)，借助EnableAutoConfigurationImportSelector，@EnableAutoConfiguration可以帮助SpringBoot应用将所有符合条件的@Configuration配置都加载到当前SpringBoot创建并使用的IoC容器。该配置模块的主要使用到了SpringFactoriesLoader。

## SpringFactoriesLoader详解

SpringFactoriesLoader为Spring工厂加载器，该对象提供了loadFactoryNames方法，入参为factoryClass和classLoader即需要传入工厂类名称和对应的类加载器，方法会根据指定的classLoader，加载该类加器搜索路径下的指定文件，即spring.factories文件，传入的工厂类为接口，而文件中对应的类则是接口的实现类，或最终作为实现类。

```java
public abstract class SpringFactoriesLoader {
    //...
    public static <T> List<T> loadFactories(Class<T> factoryClass, ClassLoader classLoader) {
        ...
    }
    public static List<String> loadFactoryNames(Class<?> factoryClass, ClassLoader classLoader) {
        ....
    }
}
```

所以文件中一般为如下图这种一对多的类名集合，获取到这些实现类的类名后，loadFactoryNames方法返回类名集合，方法调用方得到这些集合后，再通过**反射**获取这些类的类对象、构造方法，最终生成实例。



![img](https://user-gold-cdn.xitu.io/2018/8/6/1650e0c6e28a0407?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

 下图有助于我们形象理解自动配置流程

![img](https://user-gold-cdn.xitu.io/2018/8/6/1650e0e47481e59a?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)



## AutoConfigurationImportSelector

继续上面讲的AutoConfigurationImportSelector.class。该类主要关注selectImports方法

```java
public String[] selectImports(AnnotationMetadata annotationMetadata) {
        if (!this.isEnabled(annotationMetadata)) {
            return NO_IMPORTS;
        } else {
            AutoConfigurationMetadata autoConfigurationMetadata = AutoConfigurationMetadataLoader.loadMetadata(this.beanClassLoader);
            AnnotationAttributes attributes = this.getAttributes(annotationMetadata);
            List<String> configurations = this.getCandidateConfigurations(annotationMetadata, attributes);
            configurations = this.removeDuplicates(configurations);
            Set<String> exclusions = this.getExclusions(annotationMetadata, attributes);
            this.checkExcludedClasses(configurations, exclusions);
            configurations.removeAll(exclusions);
            configurations = this.filter(configurations, autoConfigurationMetadata);
            this.fireAutoConfigurationImportEvents(configurations, exclusions);
            return StringUtils.toStringArray(configurations);
        }
    }
```



该方法在springboot启动流程——bean实例化前被执行，返回要实例化的类信息列表。如果获取到类信息，spring可以通过类加载器将类加载到 JVM 中，现在我们已经通过spring-boot的starter依赖方式依赖了我们需要的组件，那么这些组件的类信息在select方法中就可以被获取到。

```java
protected List<String> getCandidateConfigurations(AnnotationMetadata metadata, AnnotationAttributes attributes) {
        List<String> configurations = SpringFactoriesLoader.loadFactoryNames(this.getSpringFactoriesLoaderFactoryClass(), this.getBeanClassLoader());
        Assert.notEmpty(configurations, "No auto configuration classes found in META-INF/spring.factories. If you are using a custom packaging, make sure that file is correct.");
        return configurations;
    }
```

方法中的getCandidateConfigurations方法，其返回一个自动配置类的类名列表，方法调用了loadFactoryNames方法，查看该方法

```java
public static List<String> loadFactoryNames(Class<?> factoryClass, @Nullable ClassLoader classLoader) {
        String factoryClassName = factoryClass.getName();
        return (List)loadSpringFactories(classLoader).getOrDefault(factoryClassName, Collections.emptyList());
    }
```

自动配置器会跟根据传入的factoryClass.getName()到项目系统路径下所有的spring.factories文件中找到相应的key，从而加载里面的类。我们就选取这个mybatis-spring-boot-autoconfigure下的spring.factories文件

```factories
# Auto Configure
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration
```

进入org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration中，又是一堆注解

```java
@org.springframework.context.annotation.Configuration
@ConditionalOnClass({SqlSessionFactory.class, SqlSessionFactoryBean.class})
@ConditionalOnBean({DataSource.class})
@EnableConfigurationProperties({MybatisProperties.class})
@AutoConfigureAfter({DataSourceAutoConfiguration.class})
public class MybatisAutoConfiguration
{
  private static final Logger logger = LoggerFactory.getLogger(MybatisAutoConfiguration.class);
  private final MybatisProperties properties;
  private final Interceptor[] interceptors;
  private final ResourceLoader resourceLoader;
  private final DatabaseIdProvider databaseIdProvider;
  private final List<ConfigurationCustomizer> configurationCustomizers;
```

- @Spring的Configuration是一个通过注解标注的springBean，
- @ConditionalOnClass({ SqlSessionFactory.class, SqlSessionFactoryBean.class})这个注解的意思是：当存在SqlSessionFactory.class, SqlSessionFactoryBean.class这两个类时才解析MybatisAutoConfiguration配置类,否则不解析这一个配置类。我们需要mybatis为我们返回会话对象，就必须有会话工厂相关类
- @CondtionalOnBean(DataSource.class):只有处理已经被声明为bean的dataSource
- @ConditionalOnMissingBean(MapperFactoryBean.class)这个注解的意思是如果容器中不存在name指定的bean则创建bean注入，否则不执行

以上配置可以保证sqlSessionFactory、sqlSessionTemplate、dataSource等mybatis所需的组件均可被自动配置，@Configuration注解已经提供了Spring的上下文环境，所以以上组件的配置方式与Spring启动时通过mybatis.xml文件进行配置起到一个效果。

 **只要一个基于SpringBoot项目的类路径下存在SqlSessionFactory.class, SqlSessionFactoryBean.class，并且容器中已经注册了dataSourceBean，就可以触发自动化配置**，意思说我们**只要在maven的项目中加入了mybatis所需要的若干依赖，就可以触发自动配置**，但引入mybatis原生依赖的话，每集成一个功能都要去修改其自动化配置类，那就得不到开箱即用的效果了。所以Spring-boot为我们提供了统一的starter可以直接配置好相关的类，触发自动配置所需的依赖(mybatis)如下：

```xml
<dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
</dependency>
```

因为maven依赖的传递性，我们只要依赖starter就可以依赖到所有需要自动配置的类，实现开箱即用的功能。也体现出Springboot简化了Spring框架带来的大量XML配置以及复杂的依赖管理，让开发人员可以更加关注业务逻辑的开发。

# 3. Spring Bean 的作用域

- singleton：Spring 的默认作用域，容器里拥有唯一的Bean，适合无状态的Bean；
- prototype：针对每个getBean请求，容器都会创建一个Bean，要谨慎使用，因为频繁地创建销毁Bean有很大的系统开销；

> 以下三种仅针对于Web应用

- request：会为每个Http 请求创建一个 Bean 实例
- session：会为每个session创建一个 Bean 实例
- globalSession：会为每个全局Http Session创建一个 Bean 实例，该作用域仅仅对Portlet规范有效

# 4. Spring Bean的生命周期

[查看详细](https://github.com/DuHouAn/Java-Notes/blob/master/Spring/05Spring%E4%B8%ADBean%E7%9A%84%E7%94%9F%E5%91%BD%E5%91%A8%E6%9C%9F.md)

# 参考资料

- <https://juejin.im/post/5b679fbc5188251aad213110#heading-0>