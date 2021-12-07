## Part B Task 3
import re
import sys
import pandas as pd
import nltk
import os

# when importing from different files, an import statement runs the entire module, we have to specify
# if __name__ == __main__ in the other module to prevent this
from partb2 import preprocess

#  Written by Dominic Henley
#  Student ID: 1186484

document_data = pd.read_csv("./partb1.csv", usecols=['filename', 'documentID'])
document_data.set_index('filename')

# TODO: CHANGE TO ./cricket before submission
path = "./cricket"
os.chdir(path)
keywords = sys.argv[1:]
docID = []
docName = []

for keyword in keywords:
    for file in os.listdir():
        preprocess(file)
        text = open(file, 'r')
        match = re.search(keyword, text.read())
        if match:
            docName.append(file)

pd.Series(docName)
matches = pd.DataFrame({'filename': docName})
matches = matches.drop_duplicates()

intersection = document_data.merge(matches, on="filename")
intersection = intersection[['documentID']]
print(intersection)