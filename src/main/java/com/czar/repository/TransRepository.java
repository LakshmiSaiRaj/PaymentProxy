package com.czar.repository;

import com.czar.entity.Trans;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Repository
@Transactional
public interface TransRepository extends JpaRepository<Trans, Long>{

    Page<Trans>findAll(Pageable pageable);
    @Query(value = "select * FROM public.trans where start_date > '2023-04-09 23:59:59' ORDER BY start_date DESC limit 800",nativeQuery = true)
    List<Trans> findAllOrderByStartDateDsc();
}
