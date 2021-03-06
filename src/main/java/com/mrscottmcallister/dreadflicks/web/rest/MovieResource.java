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

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.*;
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
     * SEARCH  /_search/movies?query=:query : search for the movie corresponding
     * to the query.
     *
     * @param query the search keywords of the movie search
     * @param criticMax the maximum critic score of the search
     * @param criticMin the minimum critic score of the search
     * @param userMax the maximum user score of the search
     * @param userMin the minimum user score of the search
     * @param yearMax the maximum year of the movie
     * @param yearMin the minimum year of the movie
     * @param types a comma delimited list of movie types (Zombies, Found Footage, etc.)
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/movies")
    @Timed
    public ResponseEntity<List<Movie>> searchMovies(@RequestParam String query,
                                                    @RequestParam Integer criticMax,
                                                    @RequestParam Integer criticMin,
                                                    @RequestParam Integer userMax,
                                                    @RequestParam Integer userMin,
                                                    @RequestParam Integer yearMax,
                                                    @RequestParam Integer yearMin,
                                                    @RequestParam Double imdbMax,
                                                    @RequestParam Double imdbMin,
                                                    @RequestParam String types,
                                                    @ApiParam Pageable pageable) throws UnsupportedEncodingException {
        log.debug("REST request to search for a page of Movies for query {}", query);
        Page<Movie> page;
        String utf8 = java.nio.charset.StandardCharsets.UTF_8.toString();
        if (types.length() < 1) {
            log.debug("no type set");
            page = movieSearchRepository.search(
                boolQuery()
                    .must(queryStringQuery(URLDecoder.decode(query, utf8)))
                    .must(rangeQuery("year").lte(yearMax).gte(yearMin))
                    .must(
                        boolQuery()
                            .should(rangeQuery("criticScore").lte(criticMax).gte(criticMin))
                            .should(boolQuery().mustNot(existsQuery("criticScore")))
                            .minimumNumberShouldMatch(1))
                    .must(
                        boolQuery()
                            .should(rangeQuery("userScore").lte(criticMax).gte(criticMin))
                            .should(boolQuery().mustNot(existsQuery("userScore")))
                            .minimumNumberShouldMatch(1))
                    .must(
                        boolQuery()
                            .should(rangeQuery("imdbRating").lte(criticMax).gte(criticMin))
                            .should(boolQuery().mustNot(existsQuery("imdbRating")))
                            .minimumNumberShouldMatch(1))
                , pageable);
        } else {
            try{
                String typeListDecoded = URLDecoder.decode(types, utf8);
                ArrayList<String> typeList = new ArrayList<>(Arrays.asList(typeListDecoded.split(",|\\s")));
                page = movieSearchRepository.search(
                    boolQuery()
                        .must(queryStringQuery(URLDecoder.decode(query, utf8)))
                        .must(rangeQuery("year").lte(yearMax).gte(yearMin))
                        .must(termsQuery("imdbKeywords", typeList))
                        .must(
                            boolQuery()
                                .should(rangeQuery("criticScore").lte(criticMax).gte(criticMin))
                                .should(boolQuery().mustNot(existsQuery("criticScore")))
                                .minimumNumberShouldMatch(1))
                        .must(
                            boolQuery()
                                .should(rangeQuery("userScore").lte(criticMax).gte(criticMin))
                                .should(boolQuery().mustNot(existsQuery("userScore")))
                                .minimumNumberShouldMatch(1))
                        .must(
                            boolQuery()
                                .should(rangeQuery("imdbRating").lte(criticMax).gte(criticMin))
                                .should(boolQuery().mustNot(existsQuery("imdbRating")))
                                .minimumNumberShouldMatch(1))
                    , pageable);
            } catch(UnsupportedEncodingException e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
        HttpHeaders headers
            = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/movies");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
