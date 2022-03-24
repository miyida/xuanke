package com.wd.xuanke.service.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.google.common.base.Preconditions;
import com.wd.xuanke.common.CodeMsg;
import com.wd.xuanke.dto.ExposerDTO;
import com.wd.xuanke.dto.ListDTO;
import com.wd.xuanke.dto.ResultDTO;
import com.wd.xuanke.entiy.ChooseTimeEntity;
import com.wd.xuanke.entiy.PlanEntity;
import com.wd.xuanke.entiy.ResultEntity;
import com.wd.xuanke.exception.GlobalException;
import com.wd.xuanke.repository.ChooseTimeRepository;
import com.wd.xuanke.repository.PlanRepository;
import com.wd.xuanke.repository.ResultRepository;
import com.wd.xuanke.service.ChooseService;
import com.wd.xuanke.service.ResultService;
import com.wd.xuanke.utils.MD5Util;
import com.wd.xuanke.utils.StudentIDUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ChooseServiceImpl implements ChooseService, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChooseServiceImpl.class);

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private ResultRepository resultRepository;

    //本地缓存
    @Autowired
    private Cache<Integer, Boolean> caffeineCache;



    @Autowired
    private ResultService resultService;

    @Autowired
    private ChooseTimeRepository chooseTimeRepository;


    //实现分页逻辑
    @Override
    public ListDTO<PlanEntity> getPlanEntityListPage(Integer pageNum, Integer size) {

        ListDTO<PlanEntity> listDTO = null;

        Pageable pageable = PageRequest.of(pageNum, size, Sort.by("pno"));

        //复杂条件查询,只查询余量不为0的课程
        Page<PlanEntity> page = planRepository.findAll((Specification<PlanEntity>) (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            //余量需要大于0
            predicates.add(builder.greaterThan(root.get("num"), 0));

            //只筛选选修课,代码大于0
            predicates.add(builder.greaterThan(root.get("naturecode"), 0));

            return builder.and(predicates.toArray(new Predicate[0]));

        }, pageable);

        ListDTO<PlanEntity> planDto = new ListDTO<>(page.stream().collect(Collectors.toList()), page.getNumber(), size, page.getTotalPages());

        return planDto;
    }

    @Override
    public ExposerDTO exposer(Integer pno) {
        //IP限流
        Integer sno = StudentIDUtils.getStudentIDFromMap();


        //mysql默认时间不是东八区，需要到数据库修改时区
        Date nowTime = new Date();

        String md5 = MD5Util.inputPassToFormPass(String.valueOf(pno));

        return new ExposerDTO(true, md5);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultDTO<String> doChoose(Integer pno, String md5) {

        //判断链接是否正确
        if(md5 == null || !md5.equals(MD5Util.inputPassToFormPass(String.valueOf(pno)))){
            throw new GlobalException(CodeMsg.LINK_ERROR);
        }

        // 1.本地标记，判断是否有余量
        Boolean over = caffeineCache.get(pno, (k) -> {
            Boolean flag = caffeineCache.getIfPresent(k);
            if (flag == null) return false;
            return flag;
        });

        //无余量直接返回
        if (over) {
//            LOGGER.info("通过本地标记判断已无余量");
            throw new GlobalException(CodeMsg.PlAN_OVER);
        }

        //2.判断是否重选
        Integer sno = StudentIDUtils.getStudentIDFromMap();
        // 先读取此节课的选课结果
        // 优先从redis中加载
        ResultEntity entity = resultService.findResultEntityByPnoAndSno(pno, sno);
        if (entity != null) {
            throw new GlobalException(CodeMsg.CHOOSE_REPEAT);
        }

        //3.执行
        executeChoose(sno, pno);
        //4.返回结果
        return new ResultDTO<>(CodeMsg.CHOOSE_END.getMsg());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void executeChoose(Integer sno, Integer pno) {

        ResultEntity entity = resultService.findResultEntityByPnoAndSno(pno, sno);
        Preconditions.checkArgument(entity==null, "重复选课！");

        //保存选课结果
        entity = new ResultEntity();
        entity.setPno(pno);
        entity.setSno(sno);
        entity.setCreateTime(new Date());
        resultRepository.saveAndFlush(entity);

        //余量减一
        Integer a = planRepository.reduceNumByPno(pno);
        if(a ==0) caffeineCache.put(pno, true);
        Preconditions.checkArgument(a != 0, "此节课已经没有剩余数量可选！");

    }


    @Override
    public void afterPropertiesSet() throws Exception {

        //系统启动时就将授课计划对应的余量加载进redis中
        List<PlanEntity> list = planRepository.findAll();



        //将选课开启结束时间主动加载进redis
        Optional<ChooseTimeEntity> optional = chooseTimeRepository.findById(1);
        Preconditions.checkArgument(optional.isPresent(), "数据库加载错误");
        ChooseTimeEntity entity = optional.get();


        //系统启动，将预选结果加载
        List<ResultEntity> resultEntities = resultRepository.findAll();

    }

}
