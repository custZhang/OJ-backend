package com.zsp.zspoj.service;

public interface AIService {

    boolean setStatus(boolean status);

    /**
     * 获取判题用例
     * @param content 题目内容
     * @return ai回答
     */
    String getJudgeCase(String content,int number);

    /**
     * 获取前置代码
     * @param content 题目内容
     * @return ai回答
     */
    String getPreCode(String content);

    /**
     * 获取后置代码
     * @param content 题目内容
     * @return ai回答
     */
    String getAfterCode(String content);

    /**
     * 获取答案
     * @param content 题目内容
     * @return ai回答
     */
    String getAnswer(String content);

    /**
     * 获取提示
     * @param content 题目内容
     * @return ai回答
     */
    String getTip(String content);

    /**
     * 调用AI的方法
     * @param systemContent 预设的对话背景或模型角色
     * @param userContent 设置问题
     * @return AI的回答
     */
    String sendToAI(String systemContent,String userContent);


}
