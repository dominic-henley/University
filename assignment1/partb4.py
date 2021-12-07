## Part B Task 4
import re
import pandas as pd
import os
import sys
import nltk
from nltk.stem.porter import *


# when importing from different files, an import statement runs the entire module, we have to specify
# if __name__ == __main__ in the other module to prevent this
from partb2 import preprocess

#  Written by Dominic Henley
#  Student ID: 1186484

nltk.download('punkt')
document_data = pd.read_csv("./partb1.csv", usecols=['filename', 'documentID'])
document_data.set_index('filename')

# TODO: CHANGE TO ./cricket before submission
path = "./cricket"
os.chdir(path)
keywords_raw = sys.argv[1:]
docID = []
docName = []
ps = PorterStemmer()

# stemming of keywords
keywords = [ps.stem(word).lower() for word in keywords_raw]

# loop through keywords to check if keyword exists in text
for keyword in keywords:
    for file in os.listdir():
        preprocess(file)
        text_raw = open(file, 'r')
        word_list = nltk.word_tokenize(text_raw.read())
        text = [ps.stem(word) for word in word_list]
        if keyword in text:
            docName.append(file)

# series of documents with matches
pd.Series(docName)
matches = pd.DataFrame({'filename': docName})
matches = matches.drop_duplicates()

# uses the partb1.csv dataframe to link matches with the document ID
intersection = document_data.merge(matches, on="filename")
intersection = intersection[['documentID']]
print(intersection)
