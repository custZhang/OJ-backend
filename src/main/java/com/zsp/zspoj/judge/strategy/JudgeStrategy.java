package com.zsp.zspoj.judge.strategy;

import com.zsp.zspoj.judge.codesandbox.model.JudgeInfo;

public interface JudgeStrategy {

    JudgeInfo doJudge(JudgeContext judgeContext);
}
