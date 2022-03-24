package com.wd.xuanke.service.impl;


import com.google.common.base.Preconditions;
import com.wd.xuanke.entiy.StudentEntity;
import com.wd.xuanke.repository.StudentRepository;
import com.wd.xuanke.service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class StudentServiceImpl implements StudentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StudentServiceImpl.class);

    @Autowired
    private StudentRepository studentRepository;

    @Override
    public StudentEntity findStudentById(Integer sno) {


        Optional<StudentEntity> optional = studentRepository.findById(sno);
        Preconditions.checkArgument(optional.isPresent(), "用户不存在！");
        StudentEntity studentEntity = optional.get();

        return studentEntity;
    }
}
