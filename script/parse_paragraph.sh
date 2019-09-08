#!/usr/bin/env bash
input="/Users/jaszhou/dev/aurora/SentenceTruncator/src/main/resources/article/A10.txt"
filename=$(basename "$input" .txt)
echo $filename

line_number=1
while IFS= read -r line
do
  echo "$line"
  curl -d "s=$line&f=xml&t=all" 127.0.0.1:8080/ltp >> /Users/jaszhou/dev/aurora/SentenceTruncator/src/main/resources/xml/${filename}_${line_number}.xml
  line_number=$((line_number+1))
done < "$input"