package com.zsp.zspoj.model.dto.question;

import lombok.Data;

/**
 * 题目配置
 */
@Data
public class JudgeConfig {

    /**
     * 时间限制ms
     */
    private Long timeLimit;

    /**
     * 内存限制KB
     */
    private Long memoryLimit;

    /**
     * 堆栈限制KB
     */
    private Long stackLimit;
}
