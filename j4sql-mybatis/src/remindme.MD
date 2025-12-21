# 设计思路
repository -> mapper
repository层提供可读性，易用性较强的参数和方法，进行参数校验或者参数补充。
mapper层接收repository层传递过来的参数，将其翻译成mybatis接受的参数，执行，结果返回repository。



