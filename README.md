# Graffiti-Client
A Graffiti and Painting APP with community, instant message, moments and any other social functions, which is mainly used to draw pictures and share! Now we are still developing its social functions.<br>
一款主打涂鸦绘图的APP，同时具备社区、即时通信、朋友圈等社交功能。现在我们仍在研发它的社交功能。

## Screenshot
![](http://yaochenkun.site/wp-content/uploads/2017/01/graffiti.jpg)
![](http://yaochenkun.site/wp-content/uploads/2017/01/momentsss.png)

![](http://yaochenkun.site/wp-content/uploads/2017/01/moments.png)
![](http://yaochenkun.site/wp-content/uploads/2017/01/momentss.png)

![](http://yaochenkun.site/wp-content/uploads/2017/01/moments4.png)
![](http://yaochenkun.site/wp-content/uploads/2017/01/moments2.png)

## Functions
### Module1:graffiti
This module consists of 2 main parts. The first part is __'Traditional Graffiti'__: it contains many traditional functions which a painting software should have. The other part is __'Magical Graffiti'__: it contains some creative functions which aim at improving the quality of interaction and finally promoting the UE(User Experience).

#### Traditional Graffiti
* 7 kinds of shapes(circle, rectangle, line, brokenline, polygon, curve and freehand) to draw. 
* 6 kinds of basic editing operation towards pel(select, transalte, zoom, rotate, copy and delete).
* Fill any region with any color in canvas.
* Color picker(i.e. adjust the color of your pen).
* Pen picker(i.e. adjust the style of your pen).
* Undo/Redo.
* Save/Load.
* Change background.
* Insert text.
* Insert picture.
* Effects processing of picture.
* Gallery(i.e. check your history pictures you have painted).
* Share(i.e. forwoard your picture to some main social platform such as Wechat, QQ, Facebook, Instagram and so on)

#### Magical Graffiti
* Paint with __'gravity'__: use 'Acceleration Sensor' to capture the acceleration of gravity.
* Edit pel with __'sensor'__: use 'Acceleration Sensor' and 'Orientation Sensor' to capture the parameters of sensors.
* Paint with __'speech recognition'__: you only need to call the name of the graph you want, then the target graph will appear on canvas.This is based on the open source of [IFLYTEK CO](http://www.iflytek.com/).
* Clear canvas by __'shaking the phone'__: use 'Acceleration Sensor'.
* Stop magical operations by __'blowing the microphone with mouth'__: use 'Microphone' to detect 'the frequency of voice'.
* Undo/Redo by __'pressing the volume buttons(+-)'__.
* Paint your walking path by __'GPS'__: use open source from 'Baidu Map'.

### Module2:moments
* Moments: refresh the shared pictures at any time on moments.
* Friends Circle: provide a paltform to follow and share with others, and it is a combination of Wechat and Weibo.
* Instant Messaging: based on open source from 'RongCloud'.

## Technology Stack
* Mobile Platform: __Android.__
* Framework: __ORMLite, Retrofix, RxJava, Fresco and Dagger2.__
* Pattern: __MVP__.
* Database: __SQLite.__

## Download
Just download [Graffiti.apk](https://github.com/yaochenkun/Graffiti/blob/master/Graffiti.apk) and install it on your smartphone. This APP requires many permissions, so don't be that scared when permission window pops.

## For More
If you want to learn more about the __interfaces or functions__ of this magical APP, you could visit the site below in my blog: <br>
* [基于多通道交互技术的绘图APP-意绘](http://yaochenkun.site/index.php/2016/07/11/yihui_article/)

If you want to roughly learn __'How to develop this APP'__, you could visit the following sites in my blog:
* [Android绘图软件开发(1)-框架概述](http://yaochenkun.site/index.php/2016/07/19/paintersum_article/)
* [Android绘图软件开发(2)-图形的编辑操作实现](http://yaochenkun.site/index.php/2016/07/19/editpel_article/)
* [Android绘图软件开发(3)-Undo/Redo的栈式实现](http://yaochenkun.site/index.php/2017/08/01/undoredo_article/)
* [Android绘图软件开发(4)-扫描线种子填充算法](http://yaochenkun.site/index.php/2017/08/05/scanline_article/)
