# intercom
该工程的目标是实现Android局域网内的语音对讲。参考了论文：[Android real-time audio communications over local wireless](http://www.iteam.upv.es/pdf_articles/22.pdf)

目前已实现的功能：
- [通过UDP广播实现Android局域网Peer Discovering](http://www.jianshu.com/p/cc62e070a6d2)（2017/4/8）
- [实时Android语音对讲系统架构](http://www.jianshu.com/p/ce88092fabfa)(2017/4/22)
- [改进Android语音对讲系统的方法](http://www.jianshu.com/p/2345d5b5c33b)(2017/5/25)

2017-08-18：
master分支为多进程的实现，偏向工程应用；
dev分支为单进程的实现，偏向兼容低配置设备以及性能优化，建议优先使用此分支。