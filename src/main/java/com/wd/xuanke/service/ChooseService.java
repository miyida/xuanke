package com.wd.xuanke.service;


import com.wd.xuanke.dto.ExposerDTO;
import com.wd.xuanke.dto.ListDTO;
import com.wd.xuanke.dto.ResultDTO;
import com.wd.xuanke.entiy.PlanEntity;

public interface ChooseService {

    ListDTO<PlanEntity> getPlanEntityListPage(Integer pageNum, Integer size);
    ResultDTO<String> doChoose(Integer pno, String md5);

    void executeChoose(Integer sno, Integer pno);

    ExposerDTO exposer(Integer pno);
}
