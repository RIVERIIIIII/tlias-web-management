package com.itheima.mapper;

import com.itheima.pojo.Student;
import com.itheima.pojo.StudentQueryParam;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface StudentMapper {
    @MapKey("name")
    List<Map> getStudentDegreeData();

    @MapKey("clazz")
    List<Map> getStudentCountData();

    List<Student> list(StudentQueryParam param);

    void insert(Student student);

    @Select("select * from student where id = #{id}")
    Student getById(Integer id);

    void updateById(Student student);

    void deleteByIds(List<Integer> ids);

    @Update("update student set violation_count = violation_count + 1, violation_score = violation_score + #{score} where id = #{id}")
    void violation(Integer id, Integer score);
}
