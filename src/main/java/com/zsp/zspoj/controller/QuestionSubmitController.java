package com.zsp.zspoj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zsp.zspoj.annotation.AuthCheck;
import com.zsp.zspoj.common.BaseResponse;
import com.zsp.zspoj.common.ErrorCode;
import com.zsp.zspoj.common.ResultUtils;
import com.zsp.zspoj.constant.UserConstant;
import com.zsp.zspoj.exception.BusinessException;
import com.zsp.zspoj.judge.JudgeService;
import com.zsp.zspoj.model.dto.question.QuestionQueryRequest;
import com.zsp.zspoj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.zsp.zspoj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.zsp.zspoj.model.entity.Question;
import com.zsp.zspoj.model.entity.QuestionSubmit;
import com.zsp.zspoj.model.entity.User;
import com.zsp.zspoj.model.vo.QuestionSubmitVO;
import com.zsp.zspoj.service.QuestionSubmitService;
import com.zsp.zspoj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 题目提交接口
 *
   * @author <a href="https://zsp2024.cn">和风</a>
 */
//@RestController
//@RequestMapping("/question_submit")
@Slf4j
@Deprecated
public class QuestionSubmitController {

//    @Resource
//    private QuestionSubmitService questionSubmitService;
//
//    @Resource
//    private UserService userService;
//
//
//
//    /**
//     * 提交题目
//     *
//     * @param questionSubmitAddRequest
//     * @param request
//     * @return 提交记录的id
//     */
//    @PostMapping("/")
//    public BaseResponse<Long> doQuestionSubmit(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest,
//            HttpServletRequest request) {
//        if (questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() <= 0) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        // 登录才能点赞
//        final User loginUser = userService.getLoginUser(request);
//        long questionId = questionSubmitAddRequest.getQuestionId();
//        long questionSubmitId = questionSubmitService.doQuestionSubmit(questionSubmitAddRequest, loginUser);
//        return ResultUtils.success(questionSubmitId);
//    }
//
//    /**
//     * 分页获取题目提交列表（非管理员的普通用户，只能看到非答案、提交代码等公开信息）
//     *
//     * @param questionSubmitQueryRequest
//     * @param request
//     * @return
//     */
//    @PostMapping("/list/page")
//    public BaseResponse<Page<QuestionSubmitVO>> listQuestionSubmitByPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest,
//                                                                         HttpServletRequest request) {
//        long current = questionSubmitQueryRequest.getCurrent();
//        long size = questionSubmitQueryRequest.getPageSize();
//        Page<QuestionSubmit> questionSubmitPage = questionSubmitService.page(new Page<>(current, size),
//                questionSubmitService.getQueryWrapper(questionSubmitQueryRequest));
//        //脱敏
//        final User loginUser = userService.getLoginUser(request);
//        return ResultUtils.success(questionSubmitService.getQuestionSubmitVOPage(questionSubmitPage,loginUser));
//    }

}
