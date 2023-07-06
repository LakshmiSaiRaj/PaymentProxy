package com.czar.service;

import com.czar.entity.Trans;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TransService {

    List<Trans> getAllTransactions();


}