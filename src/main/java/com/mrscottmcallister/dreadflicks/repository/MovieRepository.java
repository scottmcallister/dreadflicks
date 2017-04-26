package com.mrscottmcallister.dreadflicks.repository;

import com.mrscottmcallister.dreadflicks.domain.Movie;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Movie entity.
 */
@SuppressWarnings("unused")
public interface MovieRepository extends JpaRepository<Movie,Long> {

}
