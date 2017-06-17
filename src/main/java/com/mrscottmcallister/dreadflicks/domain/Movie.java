package com.mrscottmcallister.dreadflicks.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A Movie.
 */
@Entity
@Table(name = "movie")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "movie")
public class Movie implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "director")
    private String director;

    @Column(name = "jhi_year")
    private Integer year;

    @Column(name = "country")
    private String country;

    @Column(name = "critic_score")
    private Integer criticScore;

    @Column(name = "user_score")
    private Integer userScore;

    @Column(name = "poster")
    private String poster;

    @Column(name = "rt_url")
    private String rtUrl;

    @Column(name = "imdb_rating")
    private Double imdbRating;

    @Column(name = "imdb_keywords")
    private String imdbKeywords;

    @Column(name = "image")
    private String image;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "movie_list_movie",
        joinColumns = @JoinColumn(name="movies_id", referencedColumnName="id"),
        inverseJoinColumns = @JoinColumn(name="movie_lists_id", referencedColumnName="id"))
    private Set<MovieList> movieLists = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public Movie title(String title) {
        this.title = title;
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDirector() {
        return director;
    }

    public Movie director(String director) {
        this.director = director;
        return this;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public Integer getYear() {
        return year;
    }

    public Movie year(Integer year) {
        this.year = year;
        return this;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getCountry() {
        return country;
    }

    public Movie country(String country) {
        this.country = country;
        return this;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getCriticScore() {
        return criticScore;
    }

    public Movie criticScore(Integer criticScore) {
        this.criticScore = criticScore;
        return this;
    }

    public void setCriticScore(Integer criticScore) {
        this.criticScore = criticScore;
    }

    public Integer getUserScore() {
        return userScore;
    }

    public Movie userScore(Integer userScore) {
        this.userScore = userScore;
        return this;
    }

    public void setUserScore(Integer userScore) {
        this.userScore = userScore;
    }

    public String getPoster() {
        return poster;
    }

    public Movie poster(String poster) {
        this.poster = poster;
        return this;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getRtUrl() {
        return rtUrl;
    }

    public Movie rtUrl(String rtUrl) {
        this.rtUrl = rtUrl;
        return this;
    }

    public void setRtUrl(String rtUrl) {
        this.rtUrl = rtUrl;
    }

    public Double getImdbRating() {
        return imdbRating;
    }

    public Movie imdbRating(Double imdbRating) {
        this.imdbRating = imdbRating;
        return this;
    }

    public void setImdbRating(Double imdbRating) {
        this.imdbRating = imdbRating;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImdbKeywords() {
        return imdbKeywords;
    }

    public Movie imdbKeywords(String imdbKeywords) {
        this.imdbKeywords = imdbKeywords;
        return this;
    }

    public void setImdbKeywords(String imdbKeywords) {
        this.imdbKeywords = imdbKeywords;
    }

    public Set<MovieList> getMovieLists() {
        return movieLists;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Movie movie = (Movie) o;
        if (movie.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, movie.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Movie{" +
            "id=" + id +
            ", title='" + title + "'" +
            ", director='" + director + "'" +
            ", year='" + year + "'" +
            ", country='" + country + "'" +
            ", criticScore='" + criticScore + "'" +
            ", userScore='" + userScore + "'" +
            ", poster='" + poster + "'" +
            ", rtUrl='" + rtUrl + "'" +
            ", imdbRating='" + imdbRating + "'" +
            ", imdbKeywords='" + imdbKeywords + "'" +
            ", image='" + image + "'" +
            '}';
    }
}
