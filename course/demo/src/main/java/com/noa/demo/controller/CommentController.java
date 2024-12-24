package com.noa.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.noa.demo.common.QueryPageParam;
import com.noa.demo.common.Result;
import com.noa.demo.entity.Comment;
import com.noa.demo.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    // 分页查询评论列表
    @PostMapping("/list")
    public Result list(@RequestBody QueryPageParam query) {
        HashMap<String, Object> param = query.getParam();
        Long postId = param.get("postId") != null ? Long.parseLong(param.get("postId").toString()) : null;

        Page<Comment> page = new Page<>();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());

        LambdaQueryWrapper<Comment> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (postId != null) {
            lambdaQueryWrapper.eq(Comment::getPostId, postId);
        }
        // 按创建时间倒序排序
        lambdaQueryWrapper.orderByDesc(Comment::getCreatedAt);

        return Result.suc(commentService.page(page, lambdaQueryWrapper).getRecords(),
                commentService.page(page, lambdaQueryWrapper).getTotal());
    }

    // 保存评论
    @PostMapping("/save")
    public Result save(@RequestBody Comment comment) {
        comment.setLikesCount(0); // 初始化点赞数为0
        return commentService.save(comment) ? Result.suc() : Result.fail();
    }

    // 删除评论
    @GetMapping("/del")
    public Result del(@RequestParam Long id) {
        return commentService.removeById(id) ? Result.suc() : Result.fail();
    }

    // 更新评论
    @PostMapping("/update")
    public Result update(@RequestBody Comment comment) {
        return commentService.updateById(comment) ? Result.suc() : Result.fail();
    }

    // 点赞评论
    @PostMapping("/like")
    public Result like(@RequestParam Long id) {
        boolean success = commentService.lambdaUpdate()
                .eq(Comment::getId, id)
                .setSql("likes_count = likes_count + 1")
                .update();
        return success ? Result.suc() : Result.fail();
    }
}