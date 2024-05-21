package com.zsp.zspoj.model.vo;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.*;
import com.zsp.zspoj.model.dto.question.JudgeConfig;
import com.zsp.zspoj.model.entity.Question;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.List;

/**
 * 题目
 * @TableName question
 */
@TableName(value ="question")
@Data
public class QuestionEditVO {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表（json 数组）
     */
    private List<String> tags;

    /**
     * 题目答案
     */
    private String answer;


    /**
     * 判题用例(json数组)
     */
//    private List<String> judgeCase;
    private String judgeCase;

    /**
     * 判题配置(json对象)
     */
    private JudgeConfig judgeConfig;

    /**
     * 核心代码模式需拼接代码头
     */
    private String pre_code;

    /**
     * 核心代码模式默认代码
     */
    private String after_code;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 包装类转对象
     *
     * @param questionEditVO
     * @return
     */
    public static Question voToObj(QuestionEditVO questionEditVO) {
        if (questionEditVO == null) {
            return null;
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionEditVO, question);
        List<String> tagList = questionEditVO.getTags();
        if (tagList != null) {
            question.setTags(JSONUtil.toJsonStr(tagList));
        }
//        List<String> vojudgeCase = questionEditVO.getJudgeCase();
//        if(vojudgeCase != null){
//            question.setJudgeCase(JSONUtil.toJsonStr(vojudgeCase));
//        }
        JudgeConfig vojudgeConfig = questionEditVO.getJudgeConfig();
        if(vojudgeConfig != null){
            question.setJudgeConfig(JSONUtil.toJsonStr(vojudgeConfig));
        }
        return question;
    }

    /**
     * 对象转包装类
     *
     * @param question
     * @return
     */
    public static QuestionEditVO objToVo(Question question) {
        if (question == null) {
            return null;
        }
        QuestionEditVO questionEditVO = new QuestionEditVO();
        BeanUtils.copyProperties(question, questionEditVO);
        List<String> tagList = JSONUtil.toList(question.getTags(), String.class);
        questionEditVO.setTags(tagList);
//        List<String> judgeCaseList = JSONUtil.toList(question.getJudgeCase(), String.class);
//        questionEditVO.setJudgeCase(judgeCaseList);
        String judgeConfigStr = question.getJudgeConfig();
        questionEditVO.setJudgeConfig(JSONUtil.toBean(judgeConfigStr,JudgeConfig.class));
        return questionEditVO;
    }

    private static final long serialVersionUID = 1L;
}
