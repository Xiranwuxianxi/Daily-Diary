package com.diary.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.diary.entity.Diary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DiaryMapper extends BaseMapper<Diary> {

    IPage<Diary> selectPageWithFilter(Page<Diary> page,
                                       @Param("userId") Long userId,
                                       @Param("keyword") String keyword,
                                       @Param("mood") String mood,
                                       @Param("startDate") String startDate,
                                       @Param("endDate") String endDate);
}