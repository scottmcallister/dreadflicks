package com.mrscottmcallister.dreadflicks.repository.search;

import com.mrscottmcallister.dreadflicks.domain.Movie;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Movie entity.
 */
public interface MovieSearchRepository extends ElasticsearchRepository<Movie, Long> {
}
