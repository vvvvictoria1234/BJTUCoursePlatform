package com.noa.demo.controller;

// BookmarkController.java
import com.noa.demo.common.QueryPageParam;
import com.noa.demo.common.Result;
import com.noa.demo.service.BookmarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bookmarks")
public class BookmarkController {

    @Autowired
    private BookmarkService bookmarkService;

    // 收藏/取消收藏
    @PostMapping("/{postId}")
    public Result toggleBookmark(@PathVariable Long postId, @RequestParam String userId) {
        return bookmarkService.toggleBookmark(postId, userId);
    }

    // 获取我的收藏列表
    @PostMapping("/my")
    public Result getMyBookmarks(@RequestBody QueryPageParam query) {
        return bookmarkService.getMyBookmarks(query);
    }

    // 获取收藏状态
    @GetMapping("/status")
    public Result getBookmarkStatus(
            @RequestParam String userId,
            @RequestParam String postIds) {
        List<Long> postIdList = Arrays.stream(postIds.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());
        return bookmarkService.getBookmarkStatus(userId, postIdList);
    }
}