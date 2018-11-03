## 自己写的用java方式调用aliyun的api, 然后更改指定的IP, 适合家里的动态IP定时运行

#### 访问 ip.cn来获得IP, 并且访问Aliyun, 判断当前IP是否和获取的IP不一致, 如果不一致则更新IP, 否则跳过更新, 同时将IP写入本地, 下次除非ip.cn的数据发生更改, 才会更改Aliyun一次, 否则在IP没变化期间, 不会访问Aliyun的API

## 使用方式
```
java -jar compileJar.jar --domain=YOUR_RR_VALUE.YOUR_DOMAIN.com --access=ALIYUN_ACCESS_KEY --secret=ALIYUN_SECRET_KEY
```
