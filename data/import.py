import psycopg2

dbname = "dreadflicks"
user = "postgres"
password = ""
host = "127.0.0.1"
port = 5432
SQL_STATEMENT = """
    COPY movie(id, title, director, jhi_year, country,
    critic_score, user_score, poster, rt_url, imdb_rating, imdb_keywords, image)
    FROM STDIN WITH
        CSV
        HEADER
        DELIMITER AS ','
    """
con = psycopg2.connect(database=dbname, user=user, password=password,
                       host=host, port=port)
cur = con.cursor()
f = open('./new-movies.csv')
cur.copy_expert(sql=SQL_STATEMENT, file=f)
con.commit()
con.close()
