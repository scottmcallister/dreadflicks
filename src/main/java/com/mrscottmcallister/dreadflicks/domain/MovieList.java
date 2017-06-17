package com.mrscottmcallister.dreadflicks.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A MovieList.
 */
@Entity
@Table(name = "movie_list")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "movielist")
public class MovieList implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToOne
    private User user;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "movie_list_movie",
               joinColumns = @JoinColumn(name="movie_lists_id", referencedColumnName="id"),
               inverseJoinColumns = @JoinColumn(name="movies_id", referencedColumnName="id"))
    private Set<Movie> movies = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public MovieList name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }

    public MovieList user(User user) {
        this.user = user;
        return this;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<Movie> getMovies() {
        return movies;
    }

    public MovieList movies(Set<Movie> movies) {
        this.movies = movies;
        return this;
    }

    public MovieList addMovie(Movie movie) {
        this.movies.add(movie);
        movie.getMovieLists().add(this);
        return this;
    }

    public MovieList removeMovie(Movie movie) {
        this.movies.remove(movie);
        movie.getMovieLists().remove(this);
        return this;
    }

    public void setMovies(Set<Movie> movies) {
        this.movies = movies;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MovieList movieList = (MovieList) o;
        if (movieList.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), movieList.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "MovieList{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
