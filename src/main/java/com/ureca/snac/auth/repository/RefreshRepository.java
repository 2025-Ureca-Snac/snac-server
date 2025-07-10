package com.ureca.snac.auth.repository;

import com.ureca.snac.auth.refresh.Refresh;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RefreshRepository extends CrudRepository<Refresh, String> {

    Optional<Refresh> findByRefresh(String refresh);
}