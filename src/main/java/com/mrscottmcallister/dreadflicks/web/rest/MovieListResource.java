package com.mrscottmcallister.dreadflicks.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.mrscottmcallister.dreadflicks.domain.MovieList;
import com.mrscottmcallister.dreadflicks.repository.MovieListRepository;
import com.mrscottmcallister.dreadflicks.service.MovieListService;
import com.mrscottmcallister.dreadflicks.web.rest.util.HeaderUtil;
import com.mrscottmcallister.dreadflicks.web.rest.util.PaginationUtil;
import io.swagger.annotations.ApiParam;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing MovieList.
 */
@RestController
@RequestMapping("/api")
public class MovieListResource {

    private final Logger log = LoggerFactory.getLogger(MovieListResource.class);

    private static final String ENTITY_NAME = "movieList";

    private final MovieListService movieListService;

    public MovieListResource(MovieListService movieListService) {
        this.movieListService = movieListService;
    }

    /**
     * POST  /movie-lists : Create a new movieList.
     *
     * @param movieList the movieList to create
     * @return the ResponseEntity with status 201 (Created) and with body the new movieList, or with status 400 (Bad Request) if the movieList has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/movie-lists")
    @Timed
    public ResponseEntity<MovieList> createMovieList(@RequestBody MovieList movieList) throws URISyntaxException {
        log.debug("REST request to save MovieList : {}", movieList);
        if (movieList.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new movieList cannot already have an ID")).body(null);
        }
        MovieList result = movieListService.save(movieList);
        return ResponseEntity.created(new URI("/api/movie-lists/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /movie-lists : Updates an existing movieList.
     *
     * @param movieList the movieList to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated movieList,
     * or with status 400 (Bad Request) if the movieList is not valid,
     * or with status 500 (Internal Server Error) if the movieList couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/movie-lists")
    @Timed
    public ResponseEntity<MovieList> updateMovieList(@RequestBody MovieList movieList) throws URISyntaxException {
        log.debug("REST request to update MovieList : {}", movieList);
        if (movieList.getId() == null) {
            return createMovieList(movieList);
        }
        MovieList result = movieListService.save(movieList);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, movieList.getId().toString()))
            .body(result);
    }

    /**
     * GET  /movie-lists : get all the movieLists.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of movieLists in body
     */
    @GetMapping("/movie-lists")
    @Timed
    public ResponseEntity<List<MovieList>> getAllMovieLists(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of MovieLists");
        Page<MovieList> page = movieListService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/movie-lists");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /movie-lists/:id : get the "id" movieList.
     *
     * @param id the id of the movieList to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the movieList, or with status 404 (Not Found)
     */
    @GetMapping("/movie-lists/{id}")
    @Timed
    public ResponseEntity<MovieList> getMovieList(@PathVariable Long id) {
        log.debug("REST request to get MovieList : {}", id);
        MovieList movieList = movieListService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(movieList));
    }

    /**
     * DELETE  /movie-lists/:id : delete the "id" movieList.
     *
     * @param id the id of the movieList to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/movie-lists/{id}")
    @Timed
    public ResponseEntity<Void> deleteMovieList(@PathVariable Long id) {
        log.debug("REST request to delete MovieList : {}", id);
        movieListService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * PUT /movie-list/:listId/movie/:movieId : add movie with "movieId" to list with "listId"
     *
     * @param listId
     * @param movieId
     * @return
     */
    @PutMapping("/movie-list/{listId}/movie/{movieId}")
    @Timed
    public ResponseEntity<Void> addMovieToList(@PathVariable Long listId,
                                               @PathVariable Long movieId) {
        log.debug("REST request to add movie {} to list {}", movieId, listId);
        movieListService.addMovie(listId, movieId);
        String responseMessage = "add movie " + movieId + " to list " + listId;
        return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, responseMessage)).build();
    }

    /**
     * SEARCH  /_search/movie-lists?query=:query : search for the movieList corresponding
     * to the query.
     *
     * @param query the query of the movieList search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/movie-lists")
    @Timed
    public ResponseEntity<List<MovieList>> searchMovieLists(@RequestParam String query, @ApiParam Pageable pageable) {
        log.debug("REST request to search for a page of MovieLists for query {}", query);
        Page<MovieList> page = movieListService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/movie-lists");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
