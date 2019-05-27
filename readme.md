## 1、短信发送平台

主要对接腾讯短信平台，实现短信的单发、群发、批量发送等。

## 2、使用方式

只需要添加相应的appkey和appid即可使用。

## 3、技术内容

### 3.1、scala和java混合编程
分别使用scala和java两个资源文件夹作为源文件。
其次要在gradle中添加源文件便于编译：
```groovy
sourceSets {
    main{
        scala {
            srcDirs = ['src/main/scala', 'src/main/java']
        }
        java {
            srcDirs = []
        }
    }
}
```

### 3.2、其中编译打包使用的编码设置
默认情况下打包会根据系统默认编码打包，对于中文会出现乱码错误。gradle中添加如下配置可以指定编码格式：
```groovy
compileJava {
    options.encoding = "UTF-8"
}

compileScala {
    options.encoding = "UTF-8"
}
```

## 4、效果图

v1 版本
![效果图](https://github.com/houyafei/SMS/blob/master/image/first.png)

v2 版本v2

![效果图](https://github.com/houyafei/SMS/blob/master/image/v2.png)




