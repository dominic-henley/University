## Part B Task 1

import re
import pandas as pd
import os
import argparse

#  Written by Dominic Henley
#  Student ID: 1186484

# the parser allows the program to accept files as input in the command line
parser = argparse.ArgumentParser()
parser.add_argument("output_file", help="input the name of the output file")
args = parser.parse_args()

docName = []
docID = []
path = "./cricket"
os.chdir(path)
regex = r'([a-zA-Z]{4})-\d{3}[A-Z]?'

# os.listdir returns a list of file names from ./cricket
for file in os.listdir():
    text = open(file, 'r')
    doc_id = re.search(regex, text.read())
    if doc_id:
        docName.append(file)
        docID.append(doc_id.group())

pd.Series(docName)
pd.Series(docID)

document_data = pd.DataFrame({'filename': docName, 'documentID': docID})
document_data.set_index('filename', inplace=True)

os.chdir("..")
document_data.to_csv(args.output_file)
