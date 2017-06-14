package com.mrscottmcallister.dreadflicks.repository;

import com.mrscottmcallister.dreadflicks.domain.MovieList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;

/**
 * Spring Data JPA repository for the MovieList entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MovieListRepository extends JpaRepository<MovieList,Long> {

    @Query("select movie_list from MovieList movie_list where movie_list.user.login = ?#{principal.username}")
    Page<MovieList> findByUserIsCurrentUser(Pageable pageable);

    @Query("select distinct movie_list from MovieList movie_list left join fetch movie_list.movies")
    List<MovieList> findAllWithEagerRelationships();

    @Query("select movie_list from MovieList movie_list left join fetch movie_list.movies where movie_list.id =:id")
    MovieList findOneWithEagerRelationships(@Param("id") Long id);

}
