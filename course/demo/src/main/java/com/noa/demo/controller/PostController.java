package com.noa.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.noa.demo.common.QueryPageParam;
import com.noa.demo.common.Result;
import com.noa.demo.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.noa.demo.entity.Post;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

    // 查询所有帖子
    @GetMapping("/list")
    public List<Post> list() {
        return postService.list();
    }

    // 新增帖子
    @PostMapping("/save")
    public Result save(@RequestBody Post post) {
        return postService.save(post) ? Result.suc() : Result.fail();
    }

    // 更新帖子
    @PostMapping("/update")
    public Result update(@RequestBody Post post) {
        return postService.updateById(post) ? Result.suc() : Result.fail();
    }

    // 删除帖子
    @GetMapping("/del")
    public Result del(@RequestParam Long id) {
        return postService.removeById(id) ? Result.suc() : Result.fail();
    }

    // 模糊查询帖子（匹配 content）
    @PostMapping("/listP")
    public Result listP(@RequestBody Post post) {
        LambdaQueryWrapper<Post> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(post.getContent())) {
            lambdaQueryWrapper.like(Post::getContent, post.getContent());
        }
        return Result.suc(postService.list(lambdaQueryWrapper));
    }

    // 分页查询帖子
    @PostMapping("/listPage")
    public List<Post> listPage(@RequestBody QueryPageParam query) {
        HashMap<String, Object> param = query.getParam();
        String content = (String) param.get("content");

        Page<Post> page = new Page<>();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());

        LambdaQueryWrapper<Post> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(content)) {
            lambdaQueryWrapper.like(Post::getContent, content);
        }

        IPage<Post> result = postService.page(page, lambdaQueryWrapper);
        return result.getRecords();
    }

    // 分页查询帖子（带动态条件）
    @PostMapping("/listPageC1")
    public Result listPageC1(@RequestBody QueryPageParam query) {
        HashMap<String, Object> param = query.getParam();
        String content = (String) param.get("content");
        String userId = (String) param.get("user_id");

        Page<Post> page = new Page<>();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());

        LambdaQueryWrapper<Post> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 动态添加条件：content 模糊查询
        if (StringUtils.isNotBlank(content)) {
            lambdaQueryWrapper.like(Post::getContent, content);
        }
        // 动态添加条件：user_id 精确查询
        if (StringUtils.isNotBlank(userId)) {
            lambdaQueryWrapper.eq(Post::getUserId, userId);
        }

        // 执行分页查询
        IPage<Post> result = postService.page(page, lambdaQueryWrapper);

        // 返回结果
        return Result.suc(result.getRecords(), result.getTotal());
    }
    // 点赞帖子
    @PostMapping("/like")
    public Result like(@RequestParam Long id) {
        // 使用 MyBatis-Plus 的 update 方法实现点赞数 +1
        boolean success = postService.lambdaUpdate()
                .eq(Post::getId, id)
                .setSql("likes_count = likes_count + 1") // 更新语句：点赞数 +1
                .update();
        return success ? Result.suc() : Result.fail();
    }
}

