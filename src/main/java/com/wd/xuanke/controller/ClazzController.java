package com.wd.xuanke.controller;


import com.wd.xuanke.dto.ListDTO;
import com.wd.xuanke.entiy.ClazzEntity;
import com.wd.xuanke.entiy.PlanEntity;
import com.wd.xuanke.service.ClazzService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/clazz")
public class ClazzController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChooseController.class);

    @Autowired
    private ClazzService clazzService;

    //课程表页面   必修课课程表
    @GetMapping("/list")
    public String getClazzList(Model model, @RequestParam(value = "pageNum", defaultValue = "0")Integer pageNum,
                               @RequestParam(value = "size", defaultValue = "10") Integer size) {

        ListDTO<ClazzEntity> page = clazzService.getClazzEntityListPage(pageNum, size);
        model.addAttribute("Clazz", page);

        return "clazz";

    }

    //跳转到每个课程对应的详细授课计划
    @GetMapping("/{cno}/detail")
    public String getClazzDetail(Model model, @PathVariable(name = "cno") String cno, @RequestParam(value = "pageNum", defaultValue = "0")Integer pageNum,
                                 @RequestParam(value = "size", defaultValue = "10") Integer size) {

        if(cno == null){
            return "major";
        }

        ListDTO<PlanEntity> page = clazzService.getClazzOfPlanEntityPage(cno, pageNum, size);
        model.addAttribute("detail", page);
        //留给选必修课页面分页传参用
        model.addAttribute("cno", cno);

        return "major";

    }

}
