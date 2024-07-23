package com.zsp.zspoj.service.impl;

import com.alibaba.excel.util.IntUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zsp.zspoj.RabbitMq.MyMessageProducer;
import com.zsp.zspoj.common.ErrorCode;
import com.zsp.zspoj.constant.CommonConstant;
import com.zsp.zspoj.constant.MqConstant;
import com.zsp.zspoj.exception.BusinessException;
import com.zsp.zspoj.judge.JudgeService;
import com.zsp.zspoj.model.dto.question.QuestionQueryRequest;
import com.zsp.zspoj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.zsp.zspoj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.zsp.zspoj.model.entity.Question;
import com.zsp.zspoj.model.entity.QuestionSubmit;
import com.zsp.zspoj.model.entity.QuestionSubmit;
import com.zsp.zspoj.model.entity.User;
import com.zsp.zspoj.model.enums.QuestionSubmitLanguageEnum;
import com.zsp.zspoj.model.enums.QuestionSubmitStatusEnum;
import com.zsp.zspoj.model.vo.QuestionSubmitVO;
import com.zsp.zspoj.model.vo.QuestionVO;
import com.zsp.zspoj.model.vo.UserVO;
import com.zsp.zspoj.service.QuestionService;
import com.zsp.zspoj.service.QuestionSubmitService;
import com.zsp.zspoj.service.QuestionSubmitService;
import com.zsp.zspoj.mapper.QuestionSubmitMapper;
import com.zsp.zspoj.service.UserService;
import com.zsp.zspoj.utils.SqlUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static net.sf.jsqlparser.util.validation.metadata.NamedObject.user;

/**
 * @author j
 * @description 针对表【question_submit(题目提交)】的数据库操作Service实现
 * @createDate 2023-11-24 16:56:03
 */
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
        implements QuestionSubmitService {
    @Resource
    private QuestionService questionService;

    @Resource
    private UserService userService;

    @Resource
    @Lazy
    private JudgeService judgeService;

    @Resource
    private MyMessageProducer myMessageProducer;



    /**
     * 提交题目
     *
     * @param
     * @param loginUser
     * @return
     */
    @Override
    public long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser) {
        String language = questionSubmitAddRequest.getLanguage();//验编程语言是否合法
        QuestionSubmitLanguageEnum languageEnum = QuestionSubmitLanguageEnum.getEnumByValue(language);
        if (languageEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编程语言错误");
        }
        long questionId = questionSubmitAddRequest.getQuestionId();
        // 判断实体是否存在，根据类别获取实体
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 是否已提交题目
        long userId = loginUser.getId();
        // 拼接code
        String code = questionSubmitAddRequest.getCode();
        if(questionSubmitAddRequest.getCodeMode() != null && "核心代码模式".equals(questionSubmitAddRequest.getCodeMode())){//核心代码模式
            code = "import java.util.Scanner;\r\n" + code + "\r\n" + question.getPre_code();
        }
        // 每个用户串行提交题目
        // 锁必须要包裹住事务方法
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setUserId(userId);
        questionSubmit.setQuestionId(questionId);
        questionSubmit.setCode(code);
        questionSubmit.setLanguage(language);
        //设置初始状态
        questionSubmit.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());
        questionSubmit.setJudgeInfo("{}");
        boolean save = this.save(questionSubmit);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据插入失败");
        }
        //执行判题服务
        Long questionSubmitId = questionSubmit.getId();
//        CompletableFuture.runAsync(() -> {
//            judgeService.doJudge(questionSubmitId);
//        });
        //消息队列执行判题服务
        myMessageProducer.sendMessage(MqConstant.DIRECT_EXCHANGE, "oj", String.valueOf(questionSubmitId));
        return questionSubmitId;
    }

    /**
     * 获取查询包装类(用户根据了哪些字段查询，根据前端传来的请求对象，得到mybatis框架支持的查询QueryWrapper类)
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest) {
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        if (questionSubmitQueryRequest == null) {
            return queryWrapper;
        }
        String language = questionSubmitQueryRequest.getLanguage();
        Integer status = questionSubmitQueryRequest.getStatus();
        Long questionId = questionSubmitQueryRequest.getQuestionId();
        Long userId = questionSubmitQueryRequest.getUserId();
        String sortField = questionSubmitQueryRequest.getSortField();
        String sortOrder = questionSubmitQueryRequest.getSortOrder();


        // 拼接查询条件
        queryWrapper.eq(StringUtils.isNotBlank(language), "language", language);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        queryWrapper.eq(QuestionSubmitStatusEnum.getEnumByValue(status) != null, "status", status);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Lazy
    private Map<Long,UserVO> userVOMap = new HashMap<>();

    @Lazy
    private Map<Long,QuestionVO> questionVOMap = new HashMap<>();


    @Override
    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser) {
        QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
        long loginUserId = loginUser.getId();
        if (loginUserId != questionSubmit.getUserId() && userService.isAdmin(loginUser)) {//如果不是提交者，也不是管理员的话
            questionSubmitVO.setCode(null);
        }
        //往QuestionSubmitVO里设置userVO
        Long userId = questionSubmit.getUserId();
        UserVO userVO = userVOMap.get(userId);//先查询map里有没有，没有再去数据库获取
        if(userVO == null){
            User questionUser = userService.getById(userId);//获取当前questionSubmit的user对象
            userVO = userService.getUserVO(questionUser);//转成uservo对象
            userVOMap.put(userId,userVO);//首次查询后，放进去
        }
        questionSubmitVO.setUserVO(userVO);
        //往QuestionSubmitVO里设置QuestionVO
        Long questionId = questionSubmitVO.getQuestionId();
        QuestionVO questionVO = questionVOMap.get(questionId);
        if(questionVO == null){
            Question question = questionService.getById(questionId);//获取当前question对象
            questionVO = QuestionVO.objToVo(question);
            questionVOMap.put(questionId,questionVO);//首次查询后，放进去
        }
        questionSubmitVO.setQuestionVO(questionVO);
        return questionSubmitVO;
    }

    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser) {
        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();
        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionSubmitPage.getCurrent(), questionSubmitPage.getSize(), questionSubmitPage.getTotal());
        if (CollectionUtils.isEmpty(questionSubmitList)) {
            return questionSubmitVOPage;
        }
        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream()
                .map(questionSubmit -> getQuestionSubmitVO(questionSubmit, loginUser))
                .collect(Collectors.toList());
        questionSubmitVOPage.setRecords(questionSubmitVOList);
        return questionSubmitVOPage;
    }


}




