package com.noa.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.noa.demo.common.QueryPageParam;
import com.noa.demo.common.Result;
import com.noa.demo.entity.PostBookmark;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

public interface BookmarkService extends IService<PostBookmark> {
    Result toggleBookmark(Long postId, String userId);


    Result getMyBookmarks(QueryPageParam query);

    // 获取收藏状态
    Result getBookmarkStatus(String userId, List<Long> postIds);
}