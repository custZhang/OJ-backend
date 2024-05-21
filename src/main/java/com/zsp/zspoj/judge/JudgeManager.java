package com.zsp.zspoj.judge;

import com.zsp.zspoj.judge.strategy.DefaultJudgeStrategy;
import com.zsp.zspoj.judge.strategy.JavaLanguageJudgeStrategy;
import com.zsp.zspoj.judge.strategy.JudgeContext;
import com.zsp.zspoj.judge.strategy.JudgeStrategy;
import com.zsp.zspoj.judge.codesandbox.model.JudgeInfo;
import com.zsp.zspoj.model.entity.QuestionSubmit;
import org.springframework.stereotype.Service;

@Service
public class JudgeManager {

    /**
     * 根据judgeContext中的language调用对应的策略类
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext){
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
        if("java".equals(language)){
            judgeStrategy = new JavaLanguageJudgeStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);
    }
}
