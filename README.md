# common-utils

## 通用工具包

工具分为6个模块，均是日常积累的一些常用工具

- 加密
- 本地io && 网络io
- 金额计算
- 多语言异常
- 线程
- 时间工具类

## Document

一般后面跟Util的工具类均为静态方法，可以拿过来直接用，主要有加密，io，金额计算

注意：BeanUtil、ReflexUtils类使用了反射，会产生性能问题，如果需要毫秒级相应的接口，不建议使用

#### io && 网络

这里主要说下`HttpsClientUtil`是支持https双向加密的，如果需要得话把这个类复制一下，放到自己项目里改jks以及密钥这些参数

#### 多语言异常

因为项目中可能涉及不同国家报错本地化的问题，所以简易设计了这样一个工具包

优点：灵活，支持多语言，支持format
缺点：多服务使用时需要同时更新多个系统文件，简易把配置文件放在网络位置，各项目运行时去取

使用方法：
在resources资源路径下建立message文件夹，创建`error_default.properties`文件
`error_default.properties`是在不传本地化时的默认选择，其他语言例如英语，创建`error_en.properties`

使用时`ErrorMessage.of("code", "异常参数")`，这里的异常参数指的是类似`log.info("{}", 123);`
你可以在`error_default.properties`中配置`P-000-000-0001=test data {}`
使用`ErrorMessage.of("P-000-000-0001", "测试")`
异常信息为：test data 测试

想要抛出这个异常`throw new ExtendRuntimeException(ErrorMessage.of("P-000-000-0001", "测试"))`即可

同时你可以基于这个异常基类实现带有自己逻辑的异常类

`ExceptionHandler`这个接口是一个概念，当你使用`DDD`领域建模时
假设你负责订单领域，在订单领域的某个类中抛出了一个ExtendRuntimeException异常
但你需要在最外层调用的地方进行统一处理或使用spring的全局异常处理器，同时又不希望错误了拦截的别人的异常，导致他人的异常控制逻辑失效

你可以在每个领域实现一个`ExceptionHandler`，交由运行时异常处理中心来管理，当异常过来，首先判断异常领域（需要自己实现对应领域的异常例如`OrderExtendRuntimeException`）
其中新增一个方法返回该领域名称或code，对应`ExceptionHandler#ofExceptionDomain`方法返回的，找到对应的处理器集中处理

#### RedisDistributedLock

Redis分布式锁，这个也很简单，但使用了jdk7的`AutoCloseable`也就是`try-with-resource`
可以这么使用
```
try (RedisDistributedLock redisDistributedLock = new RedisDistributedLock(redisTemplate)) {
    redisDistributedLock.lock();
    //do something
} catch (Exception ignored) {

}
```

#### 线程

关于线程，我的想法是全局拥有一个线程池即可，只要提交到线程池中就会执行你的逻辑
`ExecutorPool`就是利用这样的想法来实现的，可以实现不同的`WorkAction`，也可以使用lambda表达式

但这里后续会加入start里面，由`application.yml`配置线程池属性，自动装配到bean容器里，用的话@Atuowried就可以了

另外一个分段式线程池`AbstractCompletableFuturePool`，使用方法见`DemoCompletableFuturePool`

#### 时间

这里分三个部分，`LocalTimeUtils`使用`LocalTime`计算时间，因为`DataTime`有线程问题

时间统计注解，这个注解作用是统计你每个接口的相应时间
```java
/**
 * since 2020/1/4
 *
 * @author eddie
 */
@Slf4j
@RestController
public class TestConfigController {

    @GetMapping("/get")
    @TimeCalculation("获取数据")
    public String get() {
        return "123";
    }
}
```

同时`application.yml`中
```yaml
spring:
  el-util:
    enable-time-calculation: true
```

请求这个接口就会打印出：
```
2020-02-22 00:20:58.924  INFO 2695 --- [nio-8085-exec-2] c.e.c.t.a.a.a.TimeCalculationProcess     : 控制器: com.el.security.controller.TestConfigController, 方法名: get, 方法描述:获取数据, 花费: [2]ms
```

这个处理器后续还会有拓展的选项，也是利用自动装配来做，如果完成了会来更新

第三块是滑动时间窗，这个详细描述我在csdn有博客：
https://blog.csdn.net/Summerdream1996/article/details/103547186

## Open source license
GNU GENERAL PUBLIC LICENSE