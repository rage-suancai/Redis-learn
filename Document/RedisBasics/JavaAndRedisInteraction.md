## 使用Java与Redis交互
既然了解了如何通过命令窗口操作Redis数据库 那么我们如何使用Java来操作呢?

这里我们需要使用到Jedis框架 它能够实现Java与Redis数据库的交互 依赖:

```xml
                        <dependencies>
                            <dependency>
                                <groupId>redis.clients</groupId>
                                <artifactId>jedis</artifactId>
                                <version>4.0.0</version>
                            </dependency>
                        </dependencies>
```

### 基本操作
我们来看看如何连接Redis数据库 非常简单 只需要创建一个对象即可:

```java
                        public static void main(String[] args) {
    
                            // 创建Jedis对象
                            Jedis jedis = new Jedis("localhost", 6379);
                          	
  	                        // 使用之后关闭连接
                          	jedis.close();
                              
                        }
```

通过Jedis对象 我们就可以直接调用命令的同名方法来执行Redis命令了 比如:

```java
                        public static void main(String[] args) {
    
                            // 直接使用try-with-resouse 省去close
                            try(Jedis jedis = new Jedis("192.168.10.3", 6379)) {
                                
                                jedis.set("test", "lbwnb"); // 等同于set test lbwnb命令
                                System.out.println(jedis.get("test")); // 等同于get test命令
                                
                            }
                            
                        }
```

Hash类型的数据也是这样:

```java
                        public static void main(String[] args) {
    
                            try(Jedis jedis = new Jedis("192.168.10.3", 6379)) {
                                
                                jedis.hset("hhh", "name", "sxc"); // 等同于hset hhh name sxc
                                jedis.hset("hhh", "sex", "19"); // 等同于hset hhh age 19
                                jedis.hgetAll("hhh").forEach((k, v) -> System.out.println(k+": "+v));
                                
                            }
                            
                        }
```

我们接着来看看列表操作:

```java
                        public static void main(String[] args) {
    
                            try(Jedis jedis = new Jedis("192.168.10.3", 6379)) {
                                
                                jedis.lpush("mylist", "111", "222", "333"); // 等同于lpush mylist 111 222 333命令
                                jedis.lrange("mylist", 0, -1)
                                        .forEach(System.out::println); // 等同于lrange mylist 0 -1
                                
                            }
                            
                        }
```

实际上我们只需要按照对应的操作去调用同名方法即可 所有的类型封装Jedis已经帮助我们完成了

### SpringBoot整合Redis
我们接着来看如何在SpringBoot项目中整合Redis操作框架 只需要一个starter即可 但是它底层没有用Jedis 而是Lettuce:

```xml
                        <dependency>
                            <groupId>org.springframework.boot</groupId>
                            <artifactId>spring-boot-starter-data-redis</artifactId>
                        </dependency>
```

starter提供的默认配置会去连接本地的Redis服务器 并使用0号数据库 当然你也可以手动进行修改:

```yaml
                        spring:
                          data:
                            redis:
                              # 端口
                              port: 6379
                              # Redis服务器地址
                              host: 192.168.43.129
                              # 使用几号数据库
                              database: 1
```

starter已经给我们提供了两个默认的模板类:

```java
                            @Configuration(
                                proxyBeanMethods = false
                            )
                            @ConditionalOnClass({RedisOperations.class})
                            @EnableConfigurationProperties({RedisProperties.class})
                            @Import({LettuceConnectionConfiguration.class, JedisConnectionConfiguration.class})
                            public class RedisAutoConfiguration {
                                public RedisAutoConfiguration() {
                                }
                            
                                @Bean
                                @ConditionalOnMissingBean(
                                    name = {"redisTemplate"}
                                )
                                @ConditionalOnSingleCandidate(RedisConnectionFactory.class)
                                public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
                                    RedisTemplate<Object, Object> template = new RedisTemplate();
                                    template.setConnectionFactory(redisConnectionFactory);
                                    return template;
                                }
                            
                                @Bean
                                @ConditionalOnMissingBean
                                @ConditionalOnSingleCandidate(RedisConnectionFactory.class)
                                public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
                                    return new StringRedisTemplate(redisConnectionFactory);
                                }
                            }
```

那么如何去使用这两个模板类呢? 我们可以直接注入`StringRedisTemplate`来使用模板:

```java
                            @SpringBootTest
                            class SpringBootTestApplicationTests {
                            
                                @Autowired
                                StringRedisTemplate template;
                            
                                @Test
                                void contextLoads() {
                                    
                                    ValueOperations<String, String> operations = template.opsForValue();
                                    operations.set("c", "xxxxx"); // 设置值
                                    System.out.println(operations.get("c")); // 获取值
                                  	
                                    template.delete("c"); // 删除键
                                    System.out.println(template.hasKey("c")); // 判断是否包含键
                                    
                                }
                            
                            }
```

实际上所有的值的操作都被封装到了`ValueOperations`对象中 而普通的键操作直接通过模板对象就可以使用了 大致使用方式其实和Jedis一致

我们接着来看看事务操作 由于Spring没有专门的Redis事务管理器 所以只能借用JDBC提供的 只不过无所谓 正常情况下反正我们也要用到这玩意:

```xml
                            <dependency>
                                <groupId>org.springframework.boot</groupId>
                                <artifactId>spring-boot-starter-jdbc</artifactId>
                            </dependency>
                            <dependency>
                                <groupId>mysql</groupId>
                                <artifactId>mysql-connector-java</artifactId>
                            </dependency>
```

```java
                            @Service
                            public class RedisService {
                            
                                @Resource
                                StringRedisTemplate template;
                            
                                @PostConstruct
                                public void init() {
                                    template.setEnableTransactionSupport(true); // 需要开启事务
                                }
                            
                                @Transactional // 需要添加此注解
                                public void test() {
                                    
                                    template.multi();
                                    template.opsForValue().set("d", "xxxxx");
                                    template.exec();
                                    
                                }
                                
                            }
```

我们还可以为RedisTemplate对象配置一个Serializer来实现对象的JSON存储:

```java
                            @Test
                            void contextLoad2() {
    
                                // 注意Student需要实现序列化接口才能存入Redis
                                template.opsForValue().set("student", new Student());
                                System.out.println(template.opsForValue().get("student"));
                                
                            }
```