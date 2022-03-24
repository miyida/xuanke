package com.wd.xuanke.service;


import com.wd.xuanke.dto.ListDTO;
import com.wd.xuanke.entiy.KaoShiEntity;

public interface KaoShiService {

    ListDTO<KaoShiEntity> getKaoShiEntityListPage(Integer pageNum, Integer size);
}
