# Part B Task 2
import re
import os
import sys
import argparse

#  Written by Dominic Henley
#  Student ID: 1186484

def preprocess(number):
    file = open(number, 'r')
    paragraph = file.read()

    # regex to match every non alphabetic character
    regex1 = r'[^a-zA-Z\s]+'

    # regex to match multiple whitespaces
    regex2 = r'\s+'

    # regex to change capitals to find uppercase letters, use in conjunction with .lower()
    regex3 = r'([A-Z]+)'

    # applying regexes to paragraph
    paragraph = re.sub(regex1, " ", paragraph)
    paragraph = re.sub(regex2, " ", paragraph)
    for char in re.findall(regex3, paragraph):
        paragraph = paragraph.replace(char, char.lower())
    file.close()
    file1 = open(number, 'w')
    file1.write(paragraph)
    return paragraph


# to prevent running of these on import
if __name__ == "__main__":
    # file_name will be used to store the file the user wishes to change
    file_name = ""

    # TODO: Change this to ./cricket before submitting
    path = "./cricket"
    os.chdir(path)

    # the parser allows the program to accept files as input in the command line
    parser = argparse.ArgumentParser()
    parser.add_argument("file", help="input the name of the file you want to work with")
    args = parser.parse_args()

    # matches only the {numbers}.txt part of the input given by the user
    input_regex = r'\d{3}.txt'
    file_number = re.search(input_regex, args.file)
    print(f"post-processing:\n{preprocess(file_number.group())}")



