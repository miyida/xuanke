package com.wd.xuanke.service;


import com.wd.xuanke.dto.ListDTO;
import com.wd.xuanke.dto.ResultDTO;
import com.wd.xuanke.entiy.ResultEntity;

public interface ResultService {

    ListDTO<ResultEntity> getResultListPageBySno(Integer pageNum, Integer size, Integer sno);

    ResultDTO<String> noChoose(Integer pno);

    ResultEntity findResultEntityByPnoAndSno(Integer pno, Integer sno);
}
