package com.wd.xuanke.service.impl;


import com.wd.xuanke.dto.ListDTO;
import com.wd.xuanke.entiy.ClazzEntity;
import com.wd.xuanke.entiy.PlanEntity;
import com.wd.xuanke.repository.ClazzRepository;
import com.wd.xuanke.repository.PlanRepository;
import com.wd.xuanke.service.ClazzService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ClazzServiceImpl implements ClazzService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClazzServiceImpl.class);

    @Autowired
    private ClazzRepository clazzRepository;

    @Autowired
    private PlanRepository planRepository;



    //得到必修课课程列表
    @Override
    @Cacheable(cacheNames = "forClazz", key = "#pageNum", sync = true, cacheManager = "publicInfo")  //名称就是key，必需指定
    public ListDTO<ClazzEntity> getClazzEntityListPage(Integer pageNum, Integer size) {

        //分页
        Pageable pageable = PageRequest.of(pageNum,size);

        Page<ClazzEntity> page = clazzRepository.findAll(pageable);


        return new ListDTO<ClazzEntity>(page.stream().collect(Collectors.toList()), pageNum, size, page.getTotalPages());
    }

    //得到该必修课对应的授课计划
    @Override
    public ListDTO<PlanEntity> getClazzOfPlanEntityPage(String cno, Integer pageNum, Integer size) {

        ListDTO<PlanEntity> listDTO = null;

        Pageable pageable = PageRequest.of(pageNum, size, Sort.by("pno"));

        //复杂条件查询,只查询余量不为0的课程
        Page<PlanEntity> page = planRepository.findAll((Specification<PlanEntity>)(root, query, builder)->{
            List<Predicate> predicates = new ArrayList<>();

            //得到该课程对应的授课计划
            predicates.add(builder.equal(root.get("cno"), cno));

            //余量需要大于0
            predicates.add(builder.greaterThan(root.get("num"), 0));


            return builder.and(predicates.toArray(new Predicate[0]));

        }, pageable);

        listDTO = new ListDTO<PlanEntity>(page.stream().collect(Collectors.toList()), pageNum, size, page.getTotalPages());

        return listDTO;

    }
}
