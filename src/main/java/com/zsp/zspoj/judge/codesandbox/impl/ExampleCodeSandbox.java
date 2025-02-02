package com.zsp.zspoj.judge.codesandbox.impl;

import com.zsp.zspoj.judge.codesandbox.CodeSandbox;
import com.zsp.zspoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.zsp.zspoj.judge.codesandbox.model.ExecuteCodeResponse;
import com.zsp.zspoj.judge.codesandbox.model.JudgeInfo;
import com.zsp.zspoj.model.enums.JudgeInfoMessageEnum;
import com.zsp.zspoj.model.enums.QuestionSubmitStatusEnum;

import java.util.List;

/**
 * 示例代码沙箱（假数据，仅为了跑通业务流程）
 */
public class ExampleCodeSandbox implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> inputList = executeCodeRequest.getInputList();
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setOuputList(inputList);
        executeCodeResponse.setMessage("测试执行成功");
        executeCodeResponse.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setMessage(JudgeInfoMessageEnum.ACCEPTED.getText());
        judgeInfo.setMemory(100L);
        judgeInfo.setTime(100L);
        executeCodeResponse.setJudgeInfo(judgeInfo);
        return executeCodeResponse;
    }
}
