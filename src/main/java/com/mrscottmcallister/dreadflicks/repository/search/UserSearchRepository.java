package com.mrscottmcallister.dreadflicks.repository.search;

import com.mrscottmcallister.dreadflicks.domain.User;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the User entity.
 */
public interface UserSearchRepository extends ElasticsearchRepository<User, Long> {
}
