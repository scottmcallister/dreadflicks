package com.mrscottmcallister.dreadflicks.repository.search;

import com.mrscottmcallister.dreadflicks.domain.MovieList;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the MovieList entity.
 */
public interface MovieListSearchRepository extends ElasticsearchRepository<MovieList, Long> {
}
