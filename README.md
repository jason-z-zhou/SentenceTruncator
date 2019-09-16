# chinese sentence truncate

This tool is used to truncate Chinese articles based on rules. It follows the below procedure

- Reads article from `src/main/resources/article` and parse it into xml. The parser is adapted from ltp: https://github.com/HIT-SCIR/ltp

- Converts the xml file `src/main/resources/xml` to an object, which will be used as basic units for truncation. Here we use three approaches to truncate a sentence.
  - syntatic approach
  - semantic approach
  - random approach
  
- All approaches follow the universal rules:
  - word count per line as specified with MAX_WORD_COUNT and MIN_WORD_COUNT
  - sentences should be seperated from each other. Sentence ends with Chinese puncations, such as `。` `！` `？` `……`
  - composite sentence should be subdived into simple sentences, based on punctuation such as `，` `；` `——`
  
- Stored the final result in `src/main/resources/result`
