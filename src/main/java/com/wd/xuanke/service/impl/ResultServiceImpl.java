package com.wd.xuanke.service.impl;

import com.wd.xuanke.common.CodeMsg;
import com.wd.xuanke.dto.ListDTO;
import com.wd.xuanke.dto.ResultDTO;
import com.wd.xuanke.entiy.ResultEntity;
import com.wd.xuanke.exception.GlobalException;
import com.wd.xuanke.repository.PlanRepository;
import com.wd.xuanke.repository.ResultRepository;
import com.wd.xuanke.service.ResultService;
import com.wd.xuanke.utils.StudentIDUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ResultServiceImpl implements ResultService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResultServiceImpl.class);

    @Autowired
    private ResultRepository resultRepository;

    @Autowired
    private PlanRepository planRepository;


    @Override
    public ListDTO<ResultEntity> getResultListPageBySno(Integer pageNum, Integer size, Integer sno) {

        ListDTO<ResultEntity> listDTO = null;

        //分页查询,按选课时间降序排序，目的是看到最新选的课
        Pageable pageable = PageRequest.of(pageNum, size, Sort.by(Sort.Direction.DESC, "createTime"));

        //jpa复杂查询
        Page<ResultEntity> page = resultRepository.findAll((Specification<ResultEntity>) (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            //根据学号查询
            if (sno != null) {
                predicates.add(builder.equal(root.get("sno"), sno));
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        }, pageable);

        listDTO = new ListDTO<ResultEntity>(page.stream().collect(Collectors.toList()), pageNum, size, page.getTotalPages());

        return listDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultDTO<String> noChoose(Integer pno) {

        Integer sno = StudentIDUtils.getStudentIDFromMap();

        //判断是否存在此选课结果
        ResultEntity resultEntity = findResultEntityByPnoAndSno(pno, sno);
        if(resultEntity == null){
            throw new GlobalException(CodeMsg.RESULT_NOT_EXIST);
        }

        //选课结果表删除掉该条选课记录
        resultRepository.delete(resultEntity);

        //授课计划余量加一
        Integer a = planRepository.increaseNumByPno(resultEntity.getPno());
        if(a == 0){
            throw new GlobalException(CodeMsg.PLAN_NUM_ERROR);
        }


        return new ResultDTO<>(CodeMsg.RESULT_NO_CHOOSE_SUCCESS.getMsg());
    }

    @Override
    public ResultEntity findResultEntityByPnoAndSno(Integer pno, Integer sno) {

        ResultEntity entity ;

        entity = resultRepository.findResultEntityByPnoAndSno(pno, sno);

        return entity;
    }
}
