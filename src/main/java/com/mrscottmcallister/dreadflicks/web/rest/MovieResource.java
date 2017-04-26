package com.mrscottmcallister.dreadflicks.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.mrscottmcallister.dreadflicks.domain.Movie;

import com.mrscottmcallister.dreadflicks.repository.MovieRepository;
import com.mrscottmcallister.dreadflicks.repository.search.MovieSearchRepository;
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
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Movie.
 */
@RestController
@RequestMapping("/api")
public class MovieResource {

    private final Logger log = LoggerFactory.getLogger(MovieResource.class);

    private static final String ENTITY_NAME = "movie";
        
    private final MovieRepository movieRepository;

    private final MovieSearchRepository movieSearchRepository;

    public MovieResource(MovieRepository movieRepository, MovieSearchRepository movieSearchRepository) {
        this.movieRepository = movieRepository;
        this.movieSearchRepository = movieSearchRepository;
    }

    /**
     * POST  /movies : Create a new movie.
     *
     * @param movie the movie to create
     * @return the ResponseEntity with status 201 (Created) and with body the new movie, or with status 400 (Bad Request) if the movie has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/movies")
    @Timed
    public ResponseEntity<Movie> createMovie(@RequestBody Movie movie) throws URISyntaxException {
        log.debug("REST request to save Movie : {}", movie);
        if (movie.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new movie cannot already have an ID")).body(null);
        }
        Movie result = movieRepository.save(movie);
        movieSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/movies/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /movies : Updates an existing movie.
     *
     * @param movie the movie to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated movie,
     * or with status 400 (Bad Request) if the movie is not valid,
     * or with status 500 (Internal Server Error) if the movie couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/movies")
    @Timed
    public ResponseEntity<Movie> updateMovie(@RequestBody Movie movie) throws URISyntaxException {
        log.debug("REST request to update Movie : {}", movie);
        if (movie.getId() == null) {
            return createMovie(movie);
        }
        Movie result = movieRepository.save(movie);
        movieSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, movie.getId().toString()))
            .body(result);
    }

    /**
     * GET  /movies : get all the movies.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of movies in body
     */
    @GetMapping("/movies")
    @Timed
    public ResponseEntity<List<Movie>> getAllMovies(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of Movies");
        Page<Movie> page = movieRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/movies");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /movies/:id : get the "id" movie.
     *
     * @param id the id of the movie to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the movie, or with status 404 (Not Found)
     */
    @GetMapping("/movies/{id}")
    @Timed
    public ResponseEntity<Movie> getMovie(@PathVariable Long id) {
        log.debug("REST request to get Movie : {}", id);
        Movie movie = movieRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(movie));
    }

    /**
     * DELETE  /movies/:id : delete the "id" movie.
     *
     * @param id the id of the movie to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/movies/{id}")
    @Timed
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        log.debug("REST request to delete Movie : {}", id);
        movieRepository.delete(id);
        movieSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/movies?query=:query : search for the movie corresponding
     * to the query.
     *
     * @param query the query of the movie search 
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/movies")
    @Timed
    public ResponseEntity<List<Movie>> searchMovies(@RequestParam String query, @ApiParam Pageable pageable) {
        log.debug("REST request to search for a page of Movies for query {}", query);
        Page<Movie> page = movieSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/movies");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
