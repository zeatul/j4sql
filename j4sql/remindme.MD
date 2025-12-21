# 设计思路
构建SQL语句模型，模型完全独立。
再根据SQL方言规则，翻译成对应的SQL语句。

# TODO
WHERE HAVING ON 语句 最外层的括弧应该去掉。
SelectColumn 需要支持常量。
支持UNION，EXCEPT等关联运算。
支持字段间的四则运算，以及字符串拼接运算。
常用聚合函数和普通函数。
支持翻译成MYBATIS语法规则的sql语句。
支持批处理。
SqlEmitter支持sql方言，可定制。
SQl语句各部分需要提供自定义的扩展能力，来扩展对SQL方言的支持。
通过context来告知当前是属于处理哪种类型的子句的阶段，比如 where，select等等。
支持美化SQL语句。



