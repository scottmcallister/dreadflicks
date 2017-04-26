package com.mrscottmcallister.dreadflicks.web.rest;

import com.mrscottmcallister.dreadflicks.DreadflicksApp;

import com.mrscottmcallister.dreadflicks.domain.Movie;
import com.mrscottmcallister.dreadflicks.repository.MovieRepository;
import com.mrscottmcallister.dreadflicks.repository.search.MovieSearchRepository;
import com.mrscottmcallister.dreadflicks.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the MovieResource REST controller.
 *
 * @see MovieResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DreadflicksApp.class)
public class MovieResourceIntTest {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DIRECTOR = "AAAAAAAAAA";
    private static final String UPDATED_DIRECTOR = "BBBBBBBBBB";

    private static final Integer DEFAULT_YEAR = 1;
    private static final Integer UPDATED_YEAR = 2;

    private static final String DEFAULT_COUNTRY = "AAAAAAAAAA";
    private static final String UPDATED_COUNTRY = "BBBBBBBBBB";

    private static final Integer DEFAULT_CRITIC_SCORE = 1;
    private static final Integer UPDATED_CRITIC_SCORE = 2;

    private static final Integer DEFAULT_USER_SCORE = 1;
    private static final Integer UPDATED_USER_SCORE = 2;

    private static final String DEFAULT_POSTER = "AAAAAAAAAA";
    private static final String UPDATED_POSTER = "BBBBBBBBBB";

    private static final String DEFAULT_RT_URL = "AAAAAAAAAA";
    private static final String UPDATED_RT_URL = "BBBBBBBBBB";

    private static final Double DEFAULT_IMDB_RATING = 1D;
    private static final Double UPDATED_IMDB_RATING = 2D;

    private static final String DEFAULT_IMDB_KEYWORDS = "AAAAAAAAAA";
    private static final String UPDATED_IMDB_KEYWORDS = "BBBBBBBBBB";

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private MovieSearchRepository movieSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restMovieMockMvc;

    private Movie movie;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        MovieResource movieResource = new MovieResource(movieRepository, movieSearchRepository);
        this.restMovieMockMvc = MockMvcBuilders.standaloneSetup(movieResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Movie createEntity(EntityManager em) {
        Movie movie = new Movie()
            .title(DEFAULT_TITLE)
            .director(DEFAULT_DIRECTOR)
            .year(DEFAULT_YEAR)
            .country(DEFAULT_COUNTRY)
            .criticScore(DEFAULT_CRITIC_SCORE)
            .userScore(DEFAULT_USER_SCORE)
            .poster(DEFAULT_POSTER)
            .rtUrl(DEFAULT_RT_URL)
            .imdbRating(DEFAULT_IMDB_RATING)
            .imdbKeywords(DEFAULT_IMDB_KEYWORDS);
        return movie;
    }

    @Before
    public void initTest() {
        movieSearchRepository.deleteAll();
        movie = createEntity(em);
    }

    @Test
    @Transactional
    public void createMovie() throws Exception {
        int databaseSizeBeforeCreate = movieRepository.findAll().size();

        // Create the Movie
        restMovieMockMvc.perform(post("/api/movies")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(movie)))
            .andExpect(status().isCreated());

        // Validate the Movie in the database
        List<Movie> movieList = movieRepository.findAll();
        assertThat(movieList).hasSize(databaseSizeBeforeCreate + 1);
        Movie testMovie = movieList.get(movieList.size() - 1);
        assertThat(testMovie.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testMovie.getDirector()).isEqualTo(DEFAULT_DIRECTOR);
        assertThat(testMovie.getYear()).isEqualTo(DEFAULT_YEAR);
        assertThat(testMovie.getCountry()).isEqualTo(DEFAULT_COUNTRY);
        assertThat(testMovie.getCriticScore()).isEqualTo(DEFAULT_CRITIC_SCORE);
        assertThat(testMovie.getUserScore()).isEqualTo(DEFAULT_USER_SCORE);
        assertThat(testMovie.getPoster()).isEqualTo(DEFAULT_POSTER);
        assertThat(testMovie.getRtUrl()).isEqualTo(DEFAULT_RT_URL);
        assertThat(testMovie.getImdbRating()).isEqualTo(DEFAULT_IMDB_RATING);
        assertThat(testMovie.getImdbKeywords()).isEqualTo(DEFAULT_IMDB_KEYWORDS);

        // Validate the Movie in Elasticsearch
        Movie movieEs = movieSearchRepository.findOne(testMovie.getId());
        assertThat(movieEs).isEqualToComparingFieldByField(testMovie);
    }

    @Test
    @Transactional
    public void createMovieWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = movieRepository.findAll().size();

        // Create the Movie with an existing ID
        movie.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restMovieMockMvc.perform(post("/api/movies")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(movie)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Movie> movieList = movieRepository.findAll();
        assertThat(movieList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllMovies() throws Exception {
        // Initialize the database
        movieRepository.saveAndFlush(movie);

        // Get all the movieList
        restMovieMockMvc.perform(get("/api/movies?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(movie.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
            .andExpect(jsonPath("$.[*].director").value(hasItem(DEFAULT_DIRECTOR.toString())))
            .andExpect(jsonPath("$.[*].year").value(hasItem(DEFAULT_YEAR)))
            .andExpect(jsonPath("$.[*].country").value(hasItem(DEFAULT_COUNTRY.toString())))
            .andExpect(jsonPath("$.[*].criticScore").value(hasItem(DEFAULT_CRITIC_SCORE)))
            .andExpect(jsonPath("$.[*].userScore").value(hasItem(DEFAULT_USER_SCORE)))
            .andExpect(jsonPath("$.[*].poster").value(hasItem(DEFAULT_POSTER.toString())))
            .andExpect(jsonPath("$.[*].rtUrl").value(hasItem(DEFAULT_RT_URL.toString())))
            .andExpect(jsonPath("$.[*].imdbRating").value(hasItem(DEFAULT_IMDB_RATING.doubleValue())))
            .andExpect(jsonPath("$.[*].imdbKeywords").value(hasItem(DEFAULT_IMDB_KEYWORDS.toString())));
    }

    @Test
    @Transactional
    public void getMovie() throws Exception {
        // Initialize the database
        movieRepository.saveAndFlush(movie);

        // Get the movie
        restMovieMockMvc.perform(get("/api/movies/{id}", movie.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(movie.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE.toString()))
            .andExpect(jsonPath("$.director").value(DEFAULT_DIRECTOR.toString()))
            .andExpect(jsonPath("$.year").value(DEFAULT_YEAR))
            .andExpect(jsonPath("$.country").value(DEFAULT_COUNTRY.toString()))
            .andExpect(jsonPath("$.criticScore").value(DEFAULT_CRITIC_SCORE))
            .andExpect(jsonPath("$.userScore").value(DEFAULT_USER_SCORE))
            .andExpect(jsonPath("$.poster").value(DEFAULT_POSTER.toString()))
            .andExpect(jsonPath("$.rtUrl").value(DEFAULT_RT_URL.toString()))
            .andExpect(jsonPath("$.imdbRating").value(DEFAULT_IMDB_RATING.doubleValue()))
            .andExpect(jsonPath("$.imdbKeywords").value(DEFAULT_IMDB_KEYWORDS.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingMovie() throws Exception {
        // Get the movie
        restMovieMockMvc.perform(get("/api/movies/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateMovie() throws Exception {
        // Initialize the database
        movieRepository.saveAndFlush(movie);
        movieSearchRepository.save(movie);
        int databaseSizeBeforeUpdate = movieRepository.findAll().size();

        // Update the movie
        Movie updatedMovie = movieRepository.findOne(movie.getId());
        updatedMovie
            .title(UPDATED_TITLE)
            .director(UPDATED_DIRECTOR)
            .year(UPDATED_YEAR)
            .country(UPDATED_COUNTRY)
            .criticScore(UPDATED_CRITIC_SCORE)
            .userScore(UPDATED_USER_SCORE)
            .poster(UPDATED_POSTER)
            .rtUrl(UPDATED_RT_URL)
            .imdbRating(UPDATED_IMDB_RATING)
            .imdbKeywords(UPDATED_IMDB_KEYWORDS);

        restMovieMockMvc.perform(put("/api/movies")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedMovie)))
            .andExpect(status().isOk());

        // Validate the Movie in the database
        List<Movie> movieList = movieRepository.findAll();
        assertThat(movieList).hasSize(databaseSizeBeforeUpdate);
        Movie testMovie = movieList.get(movieList.size() - 1);
        assertThat(testMovie.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testMovie.getDirector()).isEqualTo(UPDATED_DIRECTOR);
        assertThat(testMovie.getYear()).isEqualTo(UPDATED_YEAR);
        assertThat(testMovie.getCountry()).isEqualTo(UPDATED_COUNTRY);
        assertThat(testMovie.getCriticScore()).isEqualTo(UPDATED_CRITIC_SCORE);
        assertThat(testMovie.getUserScore()).isEqualTo(UPDATED_USER_SCORE);
        assertThat(testMovie.getPoster()).isEqualTo(UPDATED_POSTER);
        assertThat(testMovie.getRtUrl()).isEqualTo(UPDATED_RT_URL);
        assertThat(testMovie.getImdbRating()).isEqualTo(UPDATED_IMDB_RATING);
        assertThat(testMovie.getImdbKeywords()).isEqualTo(UPDATED_IMDB_KEYWORDS);

        // Validate the Movie in Elasticsearch
        Movie movieEs = movieSearchRepository.findOne(testMovie.getId());
        assertThat(movieEs).isEqualToComparingFieldByField(testMovie);
    }

    @Test
    @Transactional
    public void updateNonExistingMovie() throws Exception {
        int databaseSizeBeforeUpdate = movieRepository.findAll().size();

        // Create the Movie

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restMovieMockMvc.perform(put("/api/movies")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(movie)))
            .andExpect(status().isCreated());

        // Validate the Movie in the database
        List<Movie> movieList = movieRepository.findAll();
        assertThat(movieList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteMovie() throws Exception {
        // Initialize the database
        movieRepository.saveAndFlush(movie);
        movieSearchRepository.save(movie);
        int databaseSizeBeforeDelete = movieRepository.findAll().size();

        // Get the movie
        restMovieMockMvc.perform(delete("/api/movies/{id}", movie.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean movieExistsInEs = movieSearchRepository.exists(movie.getId());
        assertThat(movieExistsInEs).isFalse();

        // Validate the database is empty
        List<Movie> movieList = movieRepository.findAll();
        assertThat(movieList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchMovie() throws Exception {
        // Initialize the database
        movieRepository.saveAndFlush(movie);
        movieSearchRepository.save(movie);

        // Search the movie
        restMovieMockMvc.perform(get("/api/_search/movies?query=id:" + movie.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(movie.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
            .andExpect(jsonPath("$.[*].director").value(hasItem(DEFAULT_DIRECTOR.toString())))
            .andExpect(jsonPath("$.[*].year").value(hasItem(DEFAULT_YEAR)))
            .andExpect(jsonPath("$.[*].country").value(hasItem(DEFAULT_COUNTRY.toString())))
            .andExpect(jsonPath("$.[*].criticScore").value(hasItem(DEFAULT_CRITIC_SCORE)))
            .andExpect(jsonPath("$.[*].userScore").value(hasItem(DEFAULT_USER_SCORE)))
            .andExpect(jsonPath("$.[*].poster").value(hasItem(DEFAULT_POSTER.toString())))
            .andExpect(jsonPath("$.[*].rtUrl").value(hasItem(DEFAULT_RT_URL.toString())))
            .andExpect(jsonPath("$.[*].imdbRating").value(hasItem(DEFAULT_IMDB_RATING.doubleValue())))
            .andExpect(jsonPath("$.[*].imdbKeywords").value(hasItem(DEFAULT_IMDB_KEYWORDS.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Movie.class);
    }
}
