This is the source code of imooc course: https://www.imooc.com/u/2145618/courses?sort=publish&skill_id=0

Ref: https://github.com/geekyijun

1. Generate mvn project:  http://blog.csdn.net/nangongyanya/article/details/72763876
```
mvn archetype:generate -DgroupId=org.seckill -DartifactId=seckill -DarchetypeArtifactId=maven-archetype-webapp
```

2. Docker run mysql and create db
```
docker run -d -e MYSQL_ROOT_PASSWORD=root --name myLocalDB -p 3306:3306 mysql 
docker exec -it myLocalDB ./bin/bash
mysql -u root -p
```

3. IntelliJ use tomcat
```
Generate artifacts .war file
Add Tomcat server in debug config
Provide the tomcat file path to the application server(down file from tomcat )
Add artifact under deployment tab
Click green run button 
```

4. Optimization: 
Start -> transaction -> update -> insert -> commit/rollback

5. Redis install: 
```
Download from: https://redis.io/download
Unzip
In the direction, run “make” and “make install”
Start redis, run “redis-server”
Open new terminal, start client, run “redis-cli”, defalut port:6379
```

<img width="718" alt="screen shot 2017-12-23 at 17 20 01" src="https://user-images.githubusercontent.com/10237409/34370056-d78b913a-ea75-11e7-8505-eb1c1f71e1bf.png">
<img width="600" alt="screen shot 2017-12-23 at 17 20 55" src="https://user-images.githubusercontent.com/10237409/34370060-da0b766e-ea75-11e7-984a-76588c9eb1e8.png">
<img width="739" alt="screen shot 2017-12-23 at 17 22 35" src="https://user-images.githubusercontent.com/10237409/34370061-dbc781dc-ea75-11e7-9d8d-f091cf9ba805.png">

大型系统部署架构
<img width="780" alt="screen shot 2017-12-26 at 13 20 00" src="https://user-images.githubusercontent.com/10237409/34370066-df61c474-ea75-11e7-97f2-85a6e4da2787.png">
