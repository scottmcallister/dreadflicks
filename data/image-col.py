import csv
import re


def main():
    filename = 'movies.csv'
    output = 'new-movies.csv'
    with open(filename) as csvin:
        readfile = csv.reader(csvin, delimiter=',')
        with open(output, 'w') as csvout:
            writefile = csv.writer(csvout, delimiter=',', lineterminator='\n')
            for row in readfile:
                if "poster_default.gif" in row[7]:
                    row.extend("/content/images/image-404-alt.png")
                else:
                    filtered_name = re.sub(r"([^\s\w]|_)+", "", row[1])
                    no_space_name = re.sub(r"\s", '_', filtered_name)
                    fixed_name = no_space_name.lower()
                    row.append(str("/content/images/"
                               + fixed_name + row[3] + ".png"))
                writefile.writerow(row)


if __name__ == "__main__":
    main()
