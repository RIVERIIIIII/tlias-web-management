package com.itheima.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

//分页结果封装类
@Data//包含：getter、setter、toString、equals、hashCode
@AllArgsConstructor
@NoArgsConstructor
public class PageResult<T> {
    private long total;//总记录数
    private List<T> rows;//当前页数据
}
