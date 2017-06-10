package com.mrscottmcallister.dreadflicks.service.impl;

import com.mrscottmcallister.dreadflicks.service.MovieListService;
import com.mrscottmcallister.dreadflicks.domain.MovieList;
import com.mrscottmcallister.dreadflicks.repository.MovieListRepository;
import com.mrscottmcallister.dreadflicks.repository.search.MovieListSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing MovieList.
 */
@Service
@Transactional
public class MovieListServiceImpl implements MovieListService{

    private final Logger log = LoggerFactory.getLogger(MovieListServiceImpl.class);

    private final MovieListRepository movieListRepository;

    private final MovieListSearchRepository movieListSearchRepository;

    public MovieListServiceImpl(MovieListRepository movieListRepository, MovieListSearchRepository movieListSearchRepository) {
        this.movieListRepository = movieListRepository;
        this.movieListSearchRepository = movieListSearchRepository;
    }

    /**
     * Save a movieList.
     *
     * @param movieList the entity to save
     * @return the persisted entity
     */
    @Override
    public MovieList save(MovieList movieList) {
        log.debug("Request to save MovieList : {}", movieList);
        MovieList result = movieListRepository.save(movieList);
        movieListSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the movieLists.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<MovieList> findAll(Pageable pageable) {
        log.debug("Request to get all MovieLists");
        return movieListRepository.findAll(pageable);
    }

    /**
     *  Get one movieList by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public MovieList findOne(Long id) {
        log.debug("Request to get MovieList : {}", id);
        return movieListRepository.findOneWithEagerRelationships(id);
    }

    /**
     *  Delete the  movieList by id.
     *
     *  @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete MovieList : {}", id);
        movieListRepository.delete(id);
        movieListSearchRepository.delete(id);
    }

    /**
     * Search for the movieList corresponding to the query.
     *
     *  @param query the query of the search
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<MovieList> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of MovieLists for query {}", query);
        Page<MovieList> result = movieListSearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }
}
