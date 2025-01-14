package com.kaidey.yakchatproject.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ReportDto {
    private Long id;
    private Long reporterId;
    private Long reportedUserId;
    private String reason;
    private LocalDateTime reportedAt;
    private String status;
    private String evidence;
    private String handler;
    private String result;
}