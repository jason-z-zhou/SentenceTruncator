#!/usr/bin/env bash

PROJECT_PATH="/Users/jaszhou/dev/aurora/SentenceTruncator"

for input in $PROJECT_PATH/src/main/resources/article/A?.txt; do
    content=$(<$input)
    echo $content
    filename=$(basename "$input" .txt)
    echo writing xml to $filename.xml
    curl -d "s=$content&f=xml&t=all" 127.0.0.1:8080/ltp >> $PROJECT_PATH/src/main/resources/xml/$filename.xml
done


