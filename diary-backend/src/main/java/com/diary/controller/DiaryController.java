package com.diary.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.diary.common.R;
import com.diary.dto.ArchiveItem;
import com.diary.dto.DiaryRequest;
import com.diary.entity.Diary;
import com.diary.service.DiaryService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diary")
public class DiaryController {

    private final DiaryService diaryService;

    public DiaryController(DiaryService diaryService) {
        this.diaryService = diaryService;
    }

    private Long getUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @GetMapping
    public R<?> list(@RequestParam(defaultValue = "1") int page,
                     @RequestParam(defaultValue = "10") int size,
                     @RequestParam(defaultValue = "") String keyword,
                     @RequestParam(defaultValue = "") String mood,
                     @RequestParam(defaultValue = "") String startDate,
                     @RequestParam(defaultValue = "") String endDate) {
        Long userId = getUserId();
        IPage<Diary> result = diaryService.getPage(userId, page, size, keyword, mood, startDate, endDate);
        return R.ok(result);
    }

    @GetMapping("/{id}")
    public R<Diary> getDetail(@PathVariable Long id) {
        Long userId = getUserId();
        return R.ok(diaryService.getById(id, userId));
    }

    @PostMapping
    public R<?> create(@RequestBody DiaryRequest request) {
        Long userId = getUserId();
        diaryService.create(userId, request);
        return R.ok("日记创建成功");
    }

    @PutMapping("/{id}")
    public R<?> update(@PathVariable Long id, @RequestBody DiaryRequest request) {
        Long userId = getUserId();
        diaryService.update(id, userId, request);
        return R.ok("日记更新成功");
    }

    @DeleteMapping("/{id}")
    public R<?> delete(@PathVariable Long id) {
        Long userId = getUserId();
        diaryService.delete(id, userId);
        return R.ok("日记删除成功");
    }

    @GetMapping("/archive")
    public R<List<ArchiveItem>> getArchive() {
        Long userId = getUserId();
        return R.ok(diaryService.getArchive(userId));
    }
}