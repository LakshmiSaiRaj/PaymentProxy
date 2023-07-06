package com.czar.service;

import com.czar.entity.Trans;
import com.czar.repository.TransRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransServiceImp implements TransService {
    @Autowired
    TransRepository repo;


    @Override
    public List<Trans> getAllTransactions() {
        return repo.findAll();
    }


    public Page<Trans> findPaginated( Integer pageNo, Integer pageSize){

        Pageable pageable=PageRequest.of(pageNo-1,pageSize);

        return this.repo.findAll(pageable);
    }



}


