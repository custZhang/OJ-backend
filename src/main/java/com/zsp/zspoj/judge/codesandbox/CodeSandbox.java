package com.zsp.zspoj.judge.codesandbox;

import com.zsp.zspoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.zsp.zspoj.judge.codesandbox.model.ExecuteCodeResponse;

/**
 * 代码沙箱接口
 */
public interface CodeSandbox {

    /**
     * 执行代码
     * @param executeCodeRequest
     * @return
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
