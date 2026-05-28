package com.diary.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.diary.entity.Diary;

public interface DiaryService {
    IPage<Diary> getPage(Long userId, int page, int size, String keyword, String mood, String startDate, String endDate);
    Diary getById(Long id, Long userId);
    void create(Long userId, com.diary.dto.DiaryRequest request);
    void update(Long id, Long userId, com.diary.dto.DiaryRequest request);
    void delete(Long id, Long userId);
    java.util.List<com.diary.dto.ArchiveItem> getArchive(Long userId);
}