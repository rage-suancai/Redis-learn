## 数据类型介绍
一个键值对除了存储一个String类型的值以外 还支持多种常用的数据类型

### Hash
这种类型本质上就是一个HashMap 也就是嵌套了一个HashMap罢了 在Java中就像这样:

```sql
                        # Redis默认存String类似于这样:
                        Map<String, String> hash = new HashMap<>();
                        # Redis存Hash类型的数据类似于这样:
                        Map<String, Map<String, String>> hash = new HashMap<>();
```

它比较适合存储类这样的数据 由于值本身又是一个Map 因此我们可以在此Map中放入类的各种属性和值 以实现一个Hash数据类型存储一个类的数据

我们可以像这样来添加一个Hash类型的数据:

```sql
                        hset <key> [<字段> <值>]...
```

我们可以直接获取:

```sql
                        hget <key> <字段>
                        -- 如果想要一次性获取所有的字段和值
                        hgetall <key>
```

同样的 我们也可以判断某个字段是否存在:

```sql
                        hexists <key> <字段>
```

删除Hash中的某个字段:

```sql
                        hdel <key>
```

我们发现 在操作一个Hash时 实际上就是我们普通操作命令前面添加一个h 这样就能以同样的方式去操作Hash里面存放的键值对了 这里就不一一列出所有的操作了 我们来看看几个比较特殊的

我们现在想要知道Hash中一共存了多少个键值对:

```sql
                        hlen <key>
```

我们也可以一次性获取所有字段的值:

```sql
                        hvals <key>
```

唯一需要注意的是 Hash中只能存放字符串值 不允许出现嵌套的的情况

### List
我们接着来看List类型 实际上这个猜都知道 它就是一个列表 而列表中存放一系列的字符串 它支持随机访问 支持双端操作 就像我们使用Java中的LinkedList一样

我们可以直接向一个已存在或是不存在的List中添加数据 如果不存在 会自动创建:

```sql
                        -- 向列表头部添加元素
                        lpush <key> <element>...
                        -- 向列表尾部添加元素
                        rpush <key> <element>...
                        -- 在指定元素前面/后面插入元素
                        linsert <key> before/after <指定元素> <element>
```

同样的 获取元素也非常简单:

```sql
                        -- 根据下标获取元素
                        lindex <key> <下标>
                        -- 获取并移除头部元素
                        lpop <key>
                        -- 获取并移除尾部元素
                        rpop <key>
                        -- 获取指定范围内的
                        lrange <key> start stop
```

注意下标可以使用负数来表示从后到前数的数字(Python: 搁这儿抄呢是吧):

```sql
                        -- 获取列表a中的全部元素
                        lrange a 0 -1
```

没想到吧 push和pop还能连着用呢: 

```sql
                        -- 从前一个数组的最后取一个数出来放到另一个数组的头部 并返回元素
                        rpoplpush 当前数组 目标数组
```

它还支持阻塞操作 类似于生产者和消费者 比如我们想要等待列表中有了数据后再进行pop操作:

```sql
                        -- 如果列表中没有元素 那么就等待 如果指定时间(秒)内被添加了数据 那么就执行pop操作 如果超时就作废 支持同时等待多个列表 只要其中一个列表有元素了 那么就能执行
                        blpop <key>... timeout
```

### Set和SortedSet
Set集合其实就像Java中的HashSet一样(我们在JavaSE中已经讲解过了 HashSet本质上就是利用了一个HashMap 但是Value都是固定对象 仅仅是Key不同)
它不允许出现重复元素 不支持随机访问 但是能够利用Hash表提供极高的查找效率

向Set中添加一个或多个值

```sql
                        sadd <key> <value>...
```

查看Set集合中有多少个值:

```sql
                        scard <key>
```

判断集合中是否包含:

```sql
                        -- 是否包含指定值
                        sismember <key> <value>
                        -- 列出所有值
                        smembers <key>
```

集合之间的运算:

```sql
                        -- 集合之间的差集
                        sdiff <key1> <key2>
                        -- 集合之间的交集
                        sinter <key1> <key2>
                        -- 求并集
                        sunion <key1> <key2>
                        -- 将集合之间的差集存到目标集合中
                        sdiffstore 目标 <key1> <key2>
                        -- 同上
                        sinterstore 目标 <key1> <key2>
                        -- 同上
                        sunionstore 目标 <key1> <key2>         
```

移动指定值到另一个集合中:

```sql
                        smove <key> 目标 value
```

移除操作:

```sql
                        -- 随机移除一个幸运儿
                        spop <key>
                        -- 移除指定
                        srem <key> <value>...
```

那么如果我们要求Set集合中的数据按照我们指定的顺序进行排列怎么办呢? 这时就可以使用SortedSet 它支持我们为每个值设定一个分数 分数的大小决定了值的位置 所以它是有序的

我们可以添加一个带分数的值:

```sql
                        zadd <key> [<value> <score>]...
```

同样的:

```sql
                        -- 查询有多少个值
                        zcard <key>
                        -- 移除
                        zrem <key> <value>...
                        -- 获取区间内的所有
                        zrange <key> start stop
```

由于所有的值都有一个分数 我们也可以根据分数段来获取:

```sql
                        -- 通过分数段查看
                        zrangebyscore <key> start stop [withscores] [limit]
                        -- 统计分数段内的数量
                        zcount <key>  start stop
                        -- 根据分数获取指定值的排名
                        zrank <key> <value>
```

https://www.jianshu.com/p/32b9fe8c20e1

有关Bitmap, HyperLogLog和Geospatial等数据类型 这里暂时不做介绍 感兴趣可以自行了解