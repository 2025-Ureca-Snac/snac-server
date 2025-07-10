package com.ureca.snac.auth.repository;

import com.ureca.snac.auth.refresh.Refresh;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface RefreshRepository extends CrudRepository<Refresh, String> {

    Boolean existsByRefresh(String refresh);

    @Transactional
    void deleteByRefresh(String refresh);
}