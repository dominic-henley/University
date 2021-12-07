## Part B Task 5
import re
import pandas as pd
import os
import sys
import nltk
from nltk.stem.porter import *
from sklearn.feature_extraction.text import TfidfTransformer
from sklearn import preprocessing
import numpy as np
from numpy import dot
from numpy.linalg import norm

# when importing from different files, an import statement runs the entire module, we have to specify
# if __name__ == __main__ in the other module to prevent this
from partb2 import preprocess

#  Written by Dominic Henley
#  Student ID: 1186484


def cosine_similarity(v, d):
    return dot(v, d)/(norm(v)*norm(d))


nltk.download('punkt')
document_data = pd.read_csv("./partb1.csv", usecols=['filename', 'documentID'])
document_data.set_index('filename')

# TODO: CHANGE TO ./cricket before submission
path = "./cricket"
os.chdir(path)
keywords_raw = sys.argv[1:]
docID = []
docName_list = []
ps = PorterStemmer()
query_list = []
query_list_total = []
transformer = TfidfTransformer()

# stemming of keywords
keywords = [ps.stem(word).lower() for word in keywords_raw]

# loop through keywords to check if keyword exists in text and appends it to a list
for file in os.listdir():

    for keyword in keywords:
        preprocess(file)
        text_raw = open(file, 'r')
        word_list = nltk.word_tokenize(text_raw.read())
        text = [ps.stem(word) for word in word_list]

        if keyword in text:
            docName_list.append(file)

# list of document names with matches that do not have duplicates
docName_list_nodups = []

for name in docName_list:
    if name not in docName_list_nodups:
        docName_list_nodups.append(name)

query_vector_total = {}

# initialises the query vector and counts occurrences of keywords
for name in docName_list_nodups:
    query_vector_doc = {}
    for keyword in keywords:
        query_vector_doc[keyword] = 0
        text_raw = open(name, 'r')
        word_list = nltk.word_tokenize(text_raw.read())
        text = [ps.stem(word) for word in word_list]
        for word in text:
            if word == keyword:
                query_vector_doc[keyword] += 1
                if keyword not in query_vector_total:
                    query_vector_total[keyword] = 1
                else:
                    query_vector_total[keyword] += 1
    query_list.append(list(query_vector_doc.values()))

query_list_total.append(list(query_vector_total.values()))
query_array = np.asarray(query_list)
query_array_total = np.asarray(query_list_total)
query_array_tot_normalised = preprocessing.normalize(query_array_total)
query_array_normalised = preprocessing.normalize(query_array)
tfidf = transformer.fit_transform(query_array)

score = [cosine_similarity(query_array_tot_normalised, (tfidf.toarray())[ID]) for ID in range((tfidf.toarray()).shape[0])]

# series of documents with matches
pd.Series(docName_list_nodups)
matches = pd.DataFrame({'filename': docName_list_nodups})

# uses the partb1.csv dataframe to link matches with the document ID
intersection = document_data.merge(matches, on="filename")
intersection = intersection[['documentID']]
intersection['score'] = score
intersection = intersection.sort_values(by="score", ascending=False)
intersection = intersection.set_index("documentID")
print(intersection)
