package com.zsp.zspoj.judge.codesandbox.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.zsp.zspoj.common.ErrorCode;
import com.zsp.zspoj.exception.BusinessException;
import com.zsp.zspoj.judge.codesandbox.CodeSandbox;
import com.zsp.zspoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.zsp.zspoj.judge.codesandbox.model.ExecuteCodeResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 远程代码沙箱
 */
@Component
public class RemoteCodeSandbox implements CodeSandbox {

    //定义鉴权请求头和密钥
    private static final String AUTH_REQUEST_HEADER = "auth";

    private static final String AUTH_REQUEST_SECRET = "secretKey";

    //此处当前对象RemoteCodeSandbox是new创建出来的，所以无法读取
    @Value("${codesandbox.url}")
    private String url;
//    private static final String url = "http://121.37.193.247:8102/executeCode";
//    private static final String url = "http://localhost:8102/executeCode";

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        //todo 这里最好改成在配置类写地址
        String json = JSONUtil.toJsonStr(executeCodeRequest);
        String responseStr = HttpUtil.createPost(url)
                .header(AUTH_REQUEST_HEADER,AUTH_REQUEST_SECRET)
                .body(json)
                .execute()
                .body();
        if(StringUtils.isBlank(responseStr)){
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR,"execute remoteSandbox error, message =" + responseStr);
        }
        ExecuteCodeResponse str = JSONUtil.toBean(responseStr,ExecuteCodeResponse.class);
        return JSONUtil.toBean(responseStr,ExecuteCodeResponse.class);
    }
}
