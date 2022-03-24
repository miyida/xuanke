package com.wd.xuanke.service;


import com.wd.xuanke.dto.ListDTO;
import com.wd.xuanke.entiy.GradeEntity;

public interface GradeService {

    ListDTO<GradeEntity> getGradeEntityListPage(Integer pageNum, Integer size);
}
