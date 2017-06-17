package com.mrscottmcallister.dreadflicks.service;

import com.mrscottmcallister.dreadflicks.domain.MovieList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing MovieList.
 */
public interface MovieListService {

    /**
     * Save a movieList.
     *
     * @param movieList the entity to save
     * @return the persisted entity
     */
    MovieList save(MovieList movieList);

    /**
     *  Get all the movieLists.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<MovieList> findAll(Pageable pageable);

    /**
     *  Get the "id" movieList.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    MovieList findOne(Long id);

    /**
     *  Delete the "id" movieList.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     *  Add a movie to the list
     *
     * @param listId id of the movie list
     * @param movieId id of the movie to add
     * @return
     */
    void addMovie(Long listId, Long movieId);

    /**
     *  Remove a movie from the list
     *
     * @param listId id of the movie list
     * @param movieId id of the movie to add
     * @return
     */
    void removeMovie(Long listId, Long movieId);

    /**
     * Search for the movieList corresponding to the query.
     *
     *  @param query the query of the search
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<MovieList> search(String query, Pageable pageable);
}
