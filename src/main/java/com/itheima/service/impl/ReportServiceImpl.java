package com.itheima.service.impl;

import com.itheima.mapper.EmpMapper;
import com.itheima.mapper.StudentMapper;
import com.itheima.pojo.JobOption;
import com.itheima.pojo.StudentCount;
import com.itheima.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private EmpMapper empMapper;
    @Autowired
    StudentMapper studentMapper;
    @Override
    public JobOption getEmpJobData() {
        List<Map> list = empMapper.countEmpJobData();
        //封装数据（map是双列集合，无法使用stream）
        List<Object> jobList = list.stream().map(m -> m.get("pos")).toList();
        List<Object> dataList = list.stream().map(m -> m.get("total")).toList();
        return new JobOption(jobList, dataList);
    }

    @Override
    public List<Map> getEmpGenderData() {
        return empMapper.countEmpGenderData();
    }

    @Override
    public StudentCount getStudentCountData() {
        List<Map> list = studentMapper.getStudentCountData();
        //封装数据
        List clazzList = list.stream().map(m -> m.get("clazz")).toList();
        List dataList = list.stream().map(m -> m.get("total")).toList();
        return new StudentCount(clazzList, dataList);
    }

    @Override
    public List<Map> getStudentDegreeData() {
        return studentMapper.getStudentDegreeData();
    }
}
