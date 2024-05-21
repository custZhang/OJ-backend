package com.zsp.zspoj.judge.codesandbox;

import com.zsp.zspoj.judge.codesandbox.impl.ExampleCodeSandbox;
import com.zsp.zspoj.judge.codesandbox.impl.RemoteCodeSandbox;
import com.zsp.zspoj.judge.codesandbox.impl.ThirdPartyCodeSandbox;
import com.zsp.zspoj.utils.SpringContextUtils;

/**
 * 代码沙箱工厂，根据传来的参数创建对应的实现类
 */
public class CodeSandboxFactory {

    /**
     * 创建代码沙箱示例
     * @param type
     * @return
     */
    public static CodeSandbox newInstance(String type){
        switch (type){
            case "example":
                return new ExampleCodeSandbox();
            case "remote":
//                return new RemoteCodeSandbox();
                return SpringContextUtils.getBean(RemoteCodeSandbox.class);
            case "thirdParty":
                return new ThirdPartyCodeSandbox();
            default:
                return new ExampleCodeSandbox();
        }
    }
}
