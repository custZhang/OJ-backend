package com.zsp.zspoj.controller;

import com.zsp.zspoj.annotation.AuthCheck;
import com.zsp.zspoj.constant.UserConstant;
import com.zsp.zspoj.service.AIService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/ai")
public class AIController {

    @Resource
    private AIService aiService;

    /**
     * 生成判题用例
     * @param content 题目内容
     * @param number 生成数量
     * @return
     */
    @PostMapping("/getJudgeCase")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public String getJudgeCase(@RequestBody String content,int number) {
        return aiService.getJudgeCase(content,number);
    }

    /**
     * 生成前置代码
     * @param content 题目内容
     * @return
     */
    @PostMapping("/getPreCode")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public String getPreCode(@RequestBody String content) {
        return aiService.getPreCode(content);
    }

    /**
     * 生成前置代码
     * @param content 题目内容
     * @return
     */
    @PostMapping("/getAfterCode")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public String getAfterCode(@RequestBody String content) {
        return aiService.getAfterCode(content);
    }

    /**
     * 生成题目答案
     * @param content 题目内容
     * @return
     */
    @PostMapping("/getAnswer")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public String getAnswer(@RequestBody String content){
        return aiService.getAnswer(content);
    }

    /**
     * 生成题目提示
     * @param content 题目内容
     * @return
     */
    @PostMapping("/getTip")
    public String getTip(@RequestBody String content){
        return aiService.getTip(content);
    }

    @GetMapping("/status")
    public String aiStatus(@RequestParam boolean status){
        return aiService.setStatus(status) ? "AI服务已开启" : "AI服务已关闭";
    }


}
