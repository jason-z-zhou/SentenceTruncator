# SentenceTruncator

This tool is used to truncate Chinese articles based on rule-based. It follows the below procedure

1. It reads article from `src/main/resources/article` and parse it into xml. The parser we use is from ltp: https://github.com/HIT-SCIR/ltp

2. It converts the xml `src/main/resources/xml` data into memory as an object, which will be used to decide how the article will be truncated.

3. The final result stored in `src/main/resources/result`


How to truncate a sentence:
1. 如何确定一行的中文字符数？
  英文 52 字符
  复合句：并列句+从句 and, or, but, that…

2. 中文语法分行规则
  - 最高级别要求：单行最少和最多字符数
  - 句与句：句终分开：句号，感叹号，分号。。
  - 并列句：从连词前分开 and， or， but
  - 复合句：从从句引导词前分开 6个w， that 
  - 简单句：主语+谓语， 从谓语前分开
  - 如果划分后少于最少字符数，合并前后句
  - 如果划分后多于最多字符数，从此行根结点或者级别最高的节点之前分开