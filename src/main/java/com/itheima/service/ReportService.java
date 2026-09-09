package com.itheima.service;

import com.itheima.pojo.JobOption;
import com.itheima.pojo.StudentCount;

import java.util.List;
import java.util.Map;

public interface ReportService {
    JobOption getEmpJobData();

    List<Map> getEmpGenderData();

    StudentCount getStudentCountData();

    List<Map> getStudentDegreeData();
}
