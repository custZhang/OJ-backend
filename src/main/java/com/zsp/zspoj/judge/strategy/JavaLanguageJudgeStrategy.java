package com.zsp.zspoj.judge.strategy;

import cn.hutool.json.JSONUtil;
import com.zsp.zspoj.model.dto.question.JudgeCase;
import com.zsp.zspoj.model.dto.question.JudgeConfig;
import com.zsp.zspoj.judge.codesandbox.model.JudgeInfo;
import com.zsp.zspoj.model.entity.Question;
import com.zsp.zspoj.model.enums.JudgeInfoMessageEnum;

import java.util.List;
import java.util.Optional;

public class JavaLanguageJudgeStrategy implements JudgeStrategy {
    @Override
    public JudgeInfo doJudge(JudgeContext judgeContext) {
        //5.根据执行结果对象executeCodeResponse.outputList 和 题目答案 比较，设置题目等待状态和信息
        //  设置变量，记录判题信息结果

        //先获取上下文对象里的judgeContext里的属性
        JudgeInfo judgeInfo = judgeContext.getJudgeInfo();
        Long time = Optional.ofNullable(judgeInfo.getTime()).orElse(0L);
        ;
        Long memory = Optional.ofNullable(judgeInfo.getMemory()).orElse(0L);
        ;
        List<String> inputList = judgeContext.getInputList();
        List<String> outputList = judgeContext.getOutputList();
        Question question = judgeContext.getQuestion();
        List<JudgeCase> judgeCaseList = judgeContext.getJudgeCaseList();

        JudgeInfoMessageEnum judgeInfoMessageEnum = JudgeInfoMessageEnum.ACCEPTED;
        //新建judgeInfo对象，最后回传该对象
        JudgeInfo judgeInfoResponse = new JudgeInfo();
        judgeInfoResponse.setTime(time);
        judgeInfoResponse.setMemory(memory);
        //  先判断题目输出数量是否和输入数量相等
        if (outputList.size() != inputList.size()) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }
        //  outputList里的每个结果，都和judgeCaseList的相等，则通过
        for (int i = 0; i < judgeCaseList.size(); i++) {
            JudgeCase judgeCase = judgeCaseList.get(i);//judgeCaseList里有多个judgeCase，每个都是含有input和output
            if (!(judgeCase.getOutput().equals(outputList.get(i)) || judgeCase.getOutput().equals(outputList.get(i) + "\n"))) {
                String message;
                if (i == 0) {//所有测试用例都无法通过，就是答案错误
                    judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
                    message = judgeInfoMessageEnum.getText() + "，通过用例（" + i + "/" + judgeCaseList.size() + "）"
                            + "\t输入：" + judgeCase.getInput()
                            + "\t预期输出：" + judgeCase.getOutput()
                            + "\t实际输出：" + outputList.get(i);
                    judgeInfoResponse.setMessage(message);
                } else if (i > 0) {//通过部分测试用例，就是部分错误
                    judgeInfoMessageEnum = JudgeInfoMessageEnum.PART_WRONG;
                    message = judgeInfoMessageEnum.getText() + "，通过用例（" + i + "/" + judgeCaseList.size() + "）"
                            + "\t输入：" + judgeCase.getInput()
                            + "\t预期输出：" + judgeCase.getOutput()
                            + "\t实际输出：" + outputList.get(i);
                    judgeInfoResponse.setMessage(message);
                }
                return judgeInfoResponse;
            }
        }
        //  判断时间空间限制是否符合
        //  题目限制条件
        String judgeConfigStr = question.getJudgeConfig();
        JudgeConfig judgeConfig = JSONUtil.toBean(judgeConfigStr, JudgeConfig.class);
        Long needTimeLimit = judgeConfig.getTimeLimit();
        Long needMemoryLimit = judgeConfig.getMemoryLimit();
        // Java 程序本身需要额外执行 10 秒钟
        long JAVA_PROGRAM_TIME_COST = 10000L;
        if ((time - JAVA_PROGRAM_TIME_COST) > needTimeLimit) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }

        if (memory > needMemoryLimit) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }
        judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
        return judgeInfoResponse;
    }
}
