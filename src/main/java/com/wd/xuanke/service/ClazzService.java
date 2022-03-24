package com.wd.xuanke.service;


import com.wd.xuanke.dto.ListDTO;
import com.wd.xuanke.entiy.ClazzEntity;
import com.wd.xuanke.entiy.PlanEntity;

public interface ClazzService {

    ListDTO<ClazzEntity> getClazzEntityListPage(Integer pageNum, Integer size);

    ListDTO<PlanEntity> getClazzOfPlanEntityPage(String cno, Integer pageNum, Integer size);
}
