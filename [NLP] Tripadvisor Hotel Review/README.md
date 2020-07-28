In this project I will show you how to predict the Rank or Sentiment from a text review using the NLP techniques and Naive Bayes as classifier.

1. download the following files:
db.zip (https://drive.google.com/file/d/0B7KzPm7u3GNbZFh1QXFiSXkyVXc/view?usp=sharing)
dataset.zip (https://drive.google.com/file/d/0B7KzPm7u3GNbQWFiZzdsNVhwdk0/view?usp=sharing)
lib.zip (https://drive.google.com/drive/folders/0B7KzPm7u3GNbcU1WelBmZGVnTEE?usp=sharing)

2. import the two db into db.zip

3. dit the my.cnf file of mysql using the my.cnf in the readme folder or editing those string into the original:
[mysqld]
tmp_table_size=1G
max_heap_table_size=1G
connect_timeout=3600

4. import the software using the build.xml 

5. use the "training" folder if you want train with five classes (rank 1...5) or user "training_bin" if you are only interested in the sentiment.
You must also use the respective testing folder according to training folder (eg. training-->testing, trainin_bin-->testing_bin.
