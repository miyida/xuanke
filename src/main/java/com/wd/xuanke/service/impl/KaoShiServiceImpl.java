package com.wd.xuanke.service.impl;

import com.wd.xuanke.dto.ListDTO;
import com.wd.xuanke.entiy.KaoShiEntity;
import com.wd.xuanke.repository.KaoShiRepository;
import com.wd.xuanke.service.KaoShiService;
import com.wd.xuanke.utils.StudentIDUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class KaoShiServiceImpl implements KaoShiService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KaoShiServiceImpl.class);

    @Autowired
    private KaoShiRepository kaoShiRepository;


    @Override
    public ListDTO<KaoShiEntity> getKaoShiEntityListPage(Integer pageNum, Integer size) {

        Integer sno = StudentIDUtils.getStudentIDFromMap();
        ListDTO<KaoShiEntity> listDTO = null;

        Pageable pageable = PageRequest.of(pageNum, size);

        Page<KaoShiEntity> page = kaoShiRepository.findAll((Specification<KaoShiEntity>)(root, query, builder)->{

            List<Predicate> predicates = new ArrayList<>();

            //查询该学生对应的考试计划
            predicates.add(builder.equal(root.get("sno"), StudentIDUtils.getStudentIDFromMap()));

            return builder.and(predicates.toArray(new Predicate[0]));
        }, pageable);

        listDTO = new ListDTO<KaoShiEntity>(page.stream().collect(Collectors.toList()), pageNum, size, page.getTotalPages());

        return listDTO;
    }
}
