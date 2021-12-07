import pandas as pd
import argparse
import matplotlib.pyplot as plt
import numpy as np

#  Written by Dominic Henley
#  Student ID: 1186484

parser = argparse.ArgumentParser()
parser.add_argument("file1", help="input filename 1")
parser.add_argument("file2", help="input filename 2")
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

# plots data onto a scatterplot
plot1 = plt.figure(1)
plt.scatter(x=countries['new_cases'], y=countries['case_fatality_rate'], s=8, c=countries['month'])
plt.colorbar(label="months")
plt.xlabel('new cases')
plt.xticks(np.arange(0, 20000000, 2500000))
plt.ylabel('case fatality rate')
plt.grid(True)
plt.title("Case fatality rate plotted against new cases")
plt.savefig(args.file1)
# plt.show()

# second scatterplot log (x-axis)
ticks2 = [10**x for x in range(0, 7)]
plot2 = plt.figure(2)
plt.scatter(x=countries['new_cases'], y=countries['case_fatality_rate'], s=8, c=countries['month'])
plt.colorbar(label="months")
plt.xlabel('new cases')
plt.xlim(1, 10000000)
plt.xticks(ticks2)
plt.ylabel('case fatality rate')
plt.xscale('log')
plt.grid(True)
plt.title("Case fatality rate plotted against new cases (logarithmic)")
plt.savefig(args.file2)
# plt.show()
