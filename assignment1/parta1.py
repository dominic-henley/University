import pandas as pd
import argparse

#  Written by Dominic Henley
#  Student ID: 1186484

# the parser allows the program to accept files as input in the command line
parser = argparse.ArgumentParser()
parser.add_argument("output_file", help="input the name of the output file")
args = parser.parse_args()

countries = pd.read_csv("./owid-covid-data.csv", usecols=['location', 'date', 'total_cases', 'new_cases', 'total_deaths', 'new_deaths'])

# drops records that do not contain a location or a date, or continent (this is to remove all continent data like Asia
# and World)
countries = countries.dropna(subset=['location', 'date'])

# slices the DataFrame to include only dates in 2020
countries['date'] = pd.to_datetime(countries['date'])
boolean = (countries['date'] >= '2020-01-01') & (countries['date'] < '2021-01-01')
countries = countries.loc[boolean]

# converts the datetime to only month
countries['month'] = pd.DatetimeIndex(countries['date']).month

# groups the data by location and month and sums them up
total_cases = countries.groupby(by=['month', 'location'])['total_cases'].nth(-1)
new_cases = countries.groupby(by=['month', 'location'])['new_cases'].sum()
total_deaths = countries.groupby(by=['month', 'location'])['total_deaths'].nth(-1)
new_deaths = countries.groupby(by=['month', 'location'])['new_deaths'].sum()

countries = pd.DataFrame({'total_cases': total_cases, 'new_cases': new_cases, 'total_deaths': total_deaths, 'new_deaths': new_deaths})

# sorts the DataFrame by month then location
countries = countries.sort_values(by=['location', 'month'], ascending=[True, True])

# calculates the case_fatality_rate
case_fatality_rate = countries.apply(lambda row: row.new_deaths/row.new_cases if row.new_cases != 0 else None, axis=1)
countries['case_fatality_rate'] = case_fatality_rate
countries = countries.reset_index()
countries = countries[['location', 'month', 'case_fatality_rate', 'total_cases', 'new_cases', 'total_deaths', 'new_deaths']]

print(countries.head())
countries.to_csv(args.output_file)

