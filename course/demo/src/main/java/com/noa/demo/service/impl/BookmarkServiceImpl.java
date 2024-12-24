package com.noa.demo.service.impl;


// BookmarkServiceImpl.java
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.noa.demo.common.QueryPageParam;
import com.noa.demo.common.Result;
import com.noa.demo.entity.Post;
import com.noa.demo.entity.PostBookmark;
import com.noa.demo.mapper.PostBookmarkMapper;
import com.noa.demo.mapper.PostMapper;
import com.noa.demo.service.BookmarkService;
import com.noa.demo.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BookmarkServiceImpl extends ServiceImpl<PostBookmarkMapper, PostBookmark> implements BookmarkService {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private PostService postService;

    @Override
    @Transactional
    public Result toggleBookmark(Long postId, String userId) {
        // 检查是否已经收藏
        int exists = baseMapper.checkBookmarkExists(postId, userId);

        if (exists > 0) {
            // 取消收藏
            remove(new LambdaQueryWrapper<PostBookmark>()
                    .eq(PostBookmark::getPostId, postId)
                    .eq(PostBookmark::getUserId, userId));

            // 更新帖子收藏数
            UpdateWrapper<Post> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", postId)
                    .setSql("bookmarks_count = bookmarks_count - 1");
            postService.update(updateWrapper);

            return Result.suc("取消收藏成功");
        } else {
            // 添加收藏
            PostBookmark bookmark = new PostBookmark();
            bookmark.setPostId(postId);
            bookmark.setUserId(userId);
            save(bookmark);

            // 更新帖子收藏数
            UpdateWrapper<Post> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", postId)
                    .setSql("bookmarks_count = bookmarks_count + 1");
            postService.update(updateWrapper);

            return Result.suc("收藏成功");
        }
    }

    @Override
    public Result getMyBookmarks(QueryPageParam query) {
        HashMap<String, Object> param = query.getParam();
        String userId = (String) param.get("userId");

        if (StringUtils.isBlank(userId)) {
            return Result.fail();
        }

        Page<Post> page = new Page<>(query.getPageNum(), query.getPageSize());
        IPage<Post> bookmarkedPosts = baseMapper.getBookmarkedPosts(page, userId);
        return Result.suc(bookmarkedPosts.getRecords(), bookmarkedPosts.getTotal());
    }

    @Override
    public Result getBookmarkStatus(String userId, List<Long> postIds) {
        try {
            if (postIds == null || postIds.isEmpty()) {
                return Result.suc(new HashMap<>());
            }

            List<Long> bookmarkedIds = baseMapper.findBookmarkedPostIds(userId, postIds);
            Map<Long, Boolean> statusMap = new HashMap<>();
            postIds.forEach(postId -> statusMap.put(postId, bookmarkedIds.contains(postId)));

            return Result.suc(statusMap);
        } catch (Exception e) {
            log.error("获取收藏状态失败", e);
            return Result.fail();
        }
    }
}