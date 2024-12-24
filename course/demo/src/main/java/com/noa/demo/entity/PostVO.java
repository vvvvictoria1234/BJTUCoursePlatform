package com.noa.demo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostVO extends Post {
    private Boolean isBookmarked; // 是否已收藏
    private LocalDateTime bookmarkTime; // 收藏时间
}