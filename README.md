# SentenceTruncator

This tool is used to truncate Chinese articles based on rule-based. It follows the below procedure

1. It reads article from `src/main/resources/article` and parse it into xml. The parser we use is from ltp: https://github.com/HIT-SCIR/ltp

2. It converts the xml `src/main/resources/xml` data into memory as an object, which will be used to decide how the article will be truncated.

3. The final result stored in `src/main/resources/result`