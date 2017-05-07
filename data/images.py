import urllib.request
import csv
import re


def convert_name(name):
    s = re.sub(r'([^\s\w]|_)+', '', name) \
        .replace(' ', '_').lower()
    return "".join(i for i in s if ord(i) < 128)


def get_image_name(movie):
    return "../static/images/" + convert_name(movie["title"]) + \
        movie["year"] + ".png"


def main():
    csvfile = open('movies.csv', "rt", encoding='iso-8859-15')
    reader = csv.DictReader(csvfile)
    for row in reader:
        if(row["poster"] != ''):
            image_name = get_image_name(row)
            print(image_name)
            urllib.request.urlretrieve(row["poster"], image_name)


if __name__ == "__main__":
    main()
