package com.noa.demo.mapper;

// PostBookmarkMapper.java
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.noa.demo.entity.Post;
import com.noa.demo.entity.PostBookmark;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

// PostBookmarkMapper.java
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

// PostBookmarkMapper.java
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PostBookmarkMapper extends BaseMapper<PostBookmark> {

    @Select("SELECT p.*, pb.created_at as bookmark_time FROM posts p " +
            "INNER JOIN post_bookmarks pb ON p.id = pb.post_id " +
            "WHERE pb.user_id = #{userId}")
    IPage<Post> getBookmarkedPosts(Page<Post> page, @Param("userId") String userId);

    @Select("SELECT COUNT(*) FROM post_bookmarks WHERE post_id = #{postId} AND user_id = #{userId}")
    int checkBookmarkExists(@Param("postId") Long postId, @Param("userId") String userId);

    @Select("<script>" +
            "SELECT post_id FROM post_bookmarks WHERE user_id = #{userId} " +
            "AND post_id IN " +
            "<foreach item='id' collection='postIds' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    List<Long> findBookmarkedPostIds(@Param("userId") String userId, @Param("postIds") List<Long> postIds);
}