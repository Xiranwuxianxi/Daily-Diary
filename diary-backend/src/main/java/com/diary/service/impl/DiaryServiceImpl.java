package com.diary.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.diary.dto.ArchiveItem;
import com.diary.dto.DiaryRequest;
import com.diary.entity.Diary;
import com.diary.mapper.DiaryMapper;
import com.diary.service.DiaryService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class DiaryServiceImpl implements DiaryService {

    private final DiaryMapper diaryMapper;

    public DiaryServiceImpl(DiaryMapper diaryMapper) {
        this.diaryMapper = diaryMapper;
    }

    @Override
    public IPage<Diary> getPage(Long userId, int page, int size, String keyword, String mood, String startDate, String endDate) {
        Page<Diary> pageRequest = new Page<>(page, size);
        return diaryMapper.selectPageWithFilter(pageRequest, userId, keyword, mood, startDate, endDate);
    }

    @Override
    public Diary getById(Long id, Long userId) {
        Diary diary = diaryMapper.selectById(id);
        if (diary == null || !diary.getUserId().equals(userId)) {
            throw new RuntimeException("日记不存在或无权限访问");
        }
        return diary;
    }

    @Override
    public void create(Long userId, DiaryRequest request) {
        Diary diary = new Diary();
        diary.setUserId(userId);
        diary.setTitle(request.getTitle());
        diary.setContent(request.getContent());
        diary.setMood(request.getMood() == null ? "开心" : request.getMood());
        diary.setWeather(request.getWeather() == null ? "晴" : request.getWeather());
        diary.setTags(request.getTags() == null ? "" : request.getTags());
        diary.setIsPublic(request.getIsPublic() != null && request.getIsPublic() ? 1 : 0);
        diaryMapper.insert(diary);
    }

    @Override
    public void update(Long id, Long userId, DiaryRequest request) {
        Diary diary = getById(id, userId);

        diary.setTitle(request.getTitle());
        diary.setContent(request.getContent());
        if (request.getMood() != null) diary.setMood(request.getMood());
        if (request.getWeather() != null) diary.setWeather(request.getWeather());
        if (request.getTags() != null) diary.setTags(request.getTags());
        if (request.getIsPublic() != null) diary.setIsPublic(request.getIsPublic() ? 1 : 0);
        diaryMapper.updateById(diary);
    }

    @Override
    public void delete(Long id, Long userId) {
        getById(id, userId);
        diaryMapper.deleteById(id);
    }

    @Override
    public List<ArchiveItem> getArchive(Long userId) {
        LambdaQueryWrapper<Diary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Diary::getUserId, userId);
        wrapper.orderByDesc(Diary::getCreateTime);
        List<Diary> diaries = diaryMapper.selectList(wrapper);

        java.util.Map<String, Long> countMap = new java.util.LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        for (Diary diary : diaries) {
            String yearMonth = diary.getCreateTime().format(formatter);
            countMap.put(yearMonth, countMap.getOrDefault(yearMonth, 0L) + 1);
        }

        List<ArchiveItem> result = new ArrayList<>();
        for (java.util.Map.Entry<String, Long> entry : countMap.entrySet()) {
            result.add(new ArchiveItem(entry.getKey(), entry.getValue()));
        }
        return result;
    }
}