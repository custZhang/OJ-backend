package com.zsp.zspoj.model.dto.questionsubmit;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建请求
 *
   * @author <a href="https://zsp2024.cn">和风</a>
 */
@Data
public class QuestionSubmitAddRequest implements Serializable {

    /**
     * 编程语言
     */
    private String language;

    /**
     * 用户代码
     */
    private String code;

    /**
     * 题目id
     */
    private Long questionId;

    /**
     * 代码的模式，
     */
    private String codeMode;


    private static final long serialVersionUID = 1L;
}