package com.itheima.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentQueryParam {
    Integer page = 1;
    Integer pageSize = 10;

    String name;
    Integer degree;
    Integer clazzId;
}
