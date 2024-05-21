package com.zsp.zspoj.judge;

import cn.hutool.json.JSONUtil;
import com.zsp.zspoj.common.ErrorCode;
import com.zsp.zspoj.exception.BusinessException;
import com.zsp.zspoj.judge.codesandbox.CodeSandbox;
import com.zsp.zspoj.judge.codesandbox.CodeSandboxFactory;
import com.zsp.zspoj.judge.codesandbox.CodeSandboxProxy;
import com.zsp.zspoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.zsp.zspoj.judge.codesandbox.model.ExecuteCodeResponse;
import com.zsp.zspoj.judge.strategy.JudgeContext;
import com.zsp.zspoj.model.dto.question.JudgeCase;
import com.zsp.zspoj.judge.codesandbox.model.JudgeInfo;
import com.zsp.zspoj.model.entity.Question;
import com.zsp.zspoj.model.entity.QuestionSubmit;
import com.zsp.zspoj.model.enums.JudgeInfoMessageEnum;
import com.zsp.zspoj.model.enums.QuestionSubmitStatusEnum;
import com.zsp.zspoj.service.QuestionService;
import com.zsp.zspoj.service.QuestionSubmitService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class JudgeServiceImpl implements JudgeService {

    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private JudgeManager judgeManager;

    @Value("${codesandbox.type:example}")
    private String type;


    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {
        //1.创建题目的提交id，从数据库获取到 题目对象、题目提交对象
        QuestionSubmit questionSubmit = questionSubmitService.getById(questionSubmitId);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "提交信息不存在");
        }
        Long questionId = questionSubmit.getQuestionId();
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }
        //2.如果不为”等待中“状态，就直接返回（只有“等待中”状态才可以提交到代码沙箱判题）
        if (!questionSubmit.getStatus().equals(QuestionSubmitStatusEnum.WAITING.getValue())) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "该提交正在判题中");
        }
        //3.更新 题目提交 信息(运行中)到数据库
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);//得给对象设置这个id，不然不知道要更改的是哪个对象
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
        boolean update = questionSubmitService.updateById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新到数据库异常");
        }
        //4.调用沙箱（需要ExecuteCodeRequest对象，就需要测试用例inputList、代码code、语言language），
        // 获取执行结果ExecuteResponse对象
        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);
        codeSandbox = new CodeSandboxProxy(codeSandbox);
        String language = questionSubmit.getLanguage();//questionSubmit是第一行根据题目id从数据库查询到的对象
        String code = questionSubmit.getCode();
        //  获取输入用例（在上面的question对象里）
        String judgeCaseStr = question.getJudgeCase();
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCaseStr, JudgeCase.class);
        //      把judgeCaseList里的input提取成inputList
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());
        //      创建ExecuteCodeRequest对象，调用executeCode()方法
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        List<String> outputList = executeCodeResponse.getOuputList();
        //5.根据执行结果对象executeCodeResponse.outputList 和 题目答案 比较，设置题目等待状态和信息
        //  设置变量，记录判题信息结果
        //  将需要的信息记录到上下文对象judgeContext，然后传给judgeManager
        //  judgeManager根据传来的上下文对象，得到language，然后调用对应的策略
        if(executeCodeResponse.getStatus() == 3){//说明异常退出（编译或运行异常），就不用去比对结果了
            questionSubmitUpdate = new QuestionSubmit();
            questionSubmitUpdate.setId(questionSubmitId);
            //如果能执行到这里，则status就是成功的
            questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.RUN_WRONG.getValue());
            JudgeInfo judgeInfo = new JudgeInfo();
            judgeInfo.setMessage(executeCodeResponse.getMessage());
            questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        }else if(executeCodeResponse.getStatus() == 2){
            JudgeContext judgeContext = new JudgeContext();
            judgeContext.setJudgeInfo(executeCodeResponse.getJudgeInfo());
            judgeContext.setInputList(inputList);
            judgeContext.setOutputList(outputList);
            judgeContext.setJudgeCaseList(judgeCaseList);
            judgeContext.setQuestion(question);
            judgeContext.setQuestionSubmit(questionSubmit);
            JudgeInfo judgeInfo = judgeManager.doJudge(judgeContext);
            //6.修改数据库中的判题结果
            questionSubmitUpdate = new QuestionSubmit();
            questionSubmitUpdate.setId(questionSubmitId);
            //如果能执行到这里，完全执行了doJudge方法就是正确，否则就是部分错误
            if(judgeInfo.getMessage().equals(JudgeInfoMessageEnum.ACCEPTED.getValue())){
                questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
            }//还要区分是部分正确还是全部正确
            //如果是答案错误开头，则是答案错误
            else if(judgeInfo.getMessage().matches("^" + JudgeInfoMessageEnum.WRONG_ANSWER.getText() + "[\\s\\S]*")) {
                questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.ERROR.getValue());
            } else if (judgeInfo.getMessage().matches("^" + JudgeInfoMessageEnum.PART_WRONG.getText() + "[\\s\\S]*")) {
                questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.PART_WRONG.getValue());
            }
            questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        }
        update = questionSubmitService.updateById(questionSubmitUpdate);
        if(!update){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"题目状态更新错误");
        }
        QuestionSubmit questionSubmitResult = questionSubmitService.getById(questionSubmitId);
        return questionSubmitResult;
    }
}
