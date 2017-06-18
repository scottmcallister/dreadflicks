package com.mrscottmcallister.dreadflicks.web.rest;

import com.mrscottmcallister.dreadflicks.DreadflicksApp;

import com.mrscottmcallister.dreadflicks.domain.MovieList;
import com.mrscottmcallister.dreadflicks.repository.MovieListRepository;
import com.mrscottmcallister.dreadflicks.service.MovieListService;
import com.mrscottmcallister.dreadflicks.repository.search.MovieListSearchRepository;
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
 * Test class for the MovieListResource REST controller.
 *
 * @see MovieListResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DreadflicksApp.class)
public class MovieListResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    @Autowired
    private MovieListRepository movieListRepository;

    @Autowired
    private MovieListService movieListService;

    @Autowired
    private MovieListSearchRepository movieListSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restMovieListMockMvc;

    private MovieList movieList;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        MovieListResource movieListResource = new MovieListResource(movieListService);
        this.restMovieListMockMvc = MockMvcBuilders.standaloneSetup(movieListResource)
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
    public static MovieList createEntity(EntityManager em) {
        MovieList movieList = new MovieList()
            .name(DEFAULT_NAME);
        return movieList;
    }

    @Before
    public void initTest() {
        movieListSearchRepository.deleteAll();
        movieList = createEntity(em);
    }

    @Test
    @Transactional
    public void createMovieList() throws Exception {
        int databaseSizeBeforeCreate = movieListRepository.findAll().size();

        // Create the MovieList
        restMovieListMockMvc.perform(post("/api/movie-lists")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(movieList)))
            .andExpect(status().isCreated());

        // Validate the MovieList in the database
        List<MovieList> movieListList = movieListRepository.findAll();
        assertThat(movieListList).hasSize(databaseSizeBeforeCreate + 1);
        MovieList testMovieList = movieListList.get(movieListList.size() - 1);
        assertThat(testMovieList.getName()).isEqualTo(DEFAULT_NAME);

        // Validate the MovieList in Elasticsearch
        MovieList movieListEs = movieListSearchRepository.findOne(testMovieList.getId());
        assertThat(movieListEs).isEqualToComparingFieldByField(testMovieList);
    }

    @Test
    @Transactional
    public void createMovieListWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = movieListRepository.findAll().size();

        // Create the MovieList with an existing ID
        movieList.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restMovieListMockMvc.perform(post("/api/movie-lists")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(movieList)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<MovieList> movieListList = movieListRepository.findAll();
        assertThat(movieListList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getMovieList() throws Exception {
        // Initialize the database
        movieListRepository.saveAndFlush(movieList);

        // Get the movieList
        restMovieListMockMvc.perform(get("/api/movie-lists/{id}", movieList.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(movieList.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingMovieList() throws Exception {
        // Get the movieList
        restMovieListMockMvc.perform(get("/api/movie-lists/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateMovieList() throws Exception {
        // Initialize the database
        movieListService.save(movieList);

        int databaseSizeBeforeUpdate = movieListRepository.findAll().size();

        // Update the movieList
        MovieList updatedMovieList = movieListRepository.findOne(movieList.getId());
        updatedMovieList
            .name(UPDATED_NAME);

        restMovieListMockMvc.perform(put("/api/movie-lists")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedMovieList)))
            .andExpect(status().isOk());

        // Validate the MovieList in the database
        List<MovieList> movieListList = movieListRepository.findAll();
        assertThat(movieListList).hasSize(databaseSizeBeforeUpdate);
        MovieList testMovieList = movieListList.get(movieListList.size() - 1);
        assertThat(testMovieList.getName()).isEqualTo(UPDATED_NAME);

        // Validate the MovieList in Elasticsearch
        MovieList movieListEs = movieListSearchRepository.findOne(testMovieList.getId());
        assertThat(movieListEs).isEqualToComparingFieldByField(testMovieList);
    }

    @Test
    @Transactional
    public void updateNonExistingMovieList() throws Exception {
        int databaseSizeBeforeUpdate = movieListRepository.findAll().size();

        // Create the MovieList

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restMovieListMockMvc.perform(put("/api/movie-lists")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(movieList)))
            .andExpect(status().isCreated());

        // Validate the MovieList in the database
        List<MovieList> movieListList = movieListRepository.findAll();
        assertThat(movieListList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteMovieList() throws Exception {
        // Initialize the database
        movieListService.save(movieList);

        int databaseSizeBeforeDelete = movieListRepository.findAll().size();

        // Get the movieList
        restMovieListMockMvc.perform(delete("/api/movie-lists/{id}", movieList.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean movieListExistsInEs = movieListSearchRepository.exists(movieList.getId());
        assertThat(movieListExistsInEs).isFalse();

        // Validate the database is empty
        List<MovieList> movieListList = movieListRepository.findAll();
        assertThat(movieListList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchMovieList() throws Exception {
        // Initialize the database
        movieListService.save(movieList);

        // Search the movieList
        restMovieListMockMvc.perform(get("/api/_search/movie-lists?query=id:" + movieList.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(movieList.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MovieList.class);
        MovieList movieList1 = new MovieList();
        movieList1.setId(1L);
        MovieList movieList2 = new MovieList();
        movieList2.setId(movieList1.getId());
        assertThat(movieList1).isEqualTo(movieList2);
        movieList2.setId(2L);
        assertThat(movieList1).isNotEqualTo(movieList2);
        movieList1.setId(null);
        assertThat(movieList1).isNotEqualTo(movieList2);
    }
}
