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

Optimization: 
Start -> transaction -> update -> insert -> commit/rollback

Redis install: 
```
Download from: https://redis.io/download
Unzip
In the direction, run “make” and “make install”
Start redis, run “redis-server”
Open new terminal, start client, run “redis-cli”, defalut port:6379
```
