package com.ureca.snac.trade.repository;

import com.ureca.snac.trade.dto.DisputeSearchCond;
import com.ureca.snac.trade.entity.Dispute;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DisputeRepositoryCustom {
    Page<Dispute> search(DisputeSearchCond cond, Pageable pageable);
}