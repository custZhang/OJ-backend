package com.zsp.zspoj.service.impl;

import com.zsp.zspoj.annotation.AuthCheck;
import com.zsp.zspoj.service.AIService;
import io.github.briqt.spark4j.SparkClient;
import io.github.briqt.spark4j.constant.SparkApiVersion;
import io.github.briqt.spark4j.exception.SparkException;
import io.github.briqt.spark4j.model.SparkMessage;
import io.github.briqt.spark4j.model.SparkSyncChatResponse;
import io.github.briqt.spark4j.model.request.SparkRequest;
import io.github.briqt.spark4j.model.response.SparkTextUsage;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class AIServiceImpl implements AIService {

    @Resource
    private SparkClient sparkClient;

    private boolean status = true;

    @Override
    public boolean setStatus(boolean status) {
        this.status = status;
        return this.status;
    }

    @Override
    public String getJudgeCase(String content,int number) {
        String systemContent = "你是OJ在线判题系统的一位出题老师，接下来我会发给你一道题目的详细内容，你需要根据这道题目的内容生成可用于判题的输入用例和输出用例，并以[{\"input\":\"\",\"output\":\"\"},{\"input\":\"\",\"output\":\"\"}]这样的json格式返回给我，直接返回json数组且不要含有多余的字符。以{\"input\":\"\",\"output\":\"\"}为一组，你要写${生成数量}组数据\n" +
                "接下来我会按照以下固定格式给你提供内容：\n" +
                "生成数量：\n" +
                "{需要生成的json的组数}\n" +
                "题目详情：\n" +
                "{编程题目的内容}";
        String userContent = "生成数量：\n" + number + "\n题目详情：\n" + content;
        return sendToAI(systemContent,userContent);
    }

    @Override
    public String getPreCode(String content) {
        String systemContent = "你是OJ在线判题系统的一位出题老师。\n" +
                "下面我定义的“前置代码”和“后置代码”的一份示例（题目是两数相加），它们的作用是使核心代码模式的代码，" +
                "能在ACM模式的判题机内运行。\n" +
                "前置代码：\n" +
                "public class Main{\n" +
                "    public static void main(String[] args) {\n" +
                "        //根据题目获取题目参数\n" +
                "        Scanner scanner = new Scanner(System.in);\n" +
                "        String line = scanner.nextLine();\n" +
                "        String[] split = line.split(\" \");\n" +
                "        int num1 = Integer.parseInt(split[0]);\n" +
                "        int num2 = Integer.parseInt(split[1]);\n" +
                "        System.out.println(new Solution().twoSum(num1,num2));\n" +
                "    }\n" +
                "}\n" +
                "后置代码：\n" +
                "class Solution{\n" +
                "    public int twoSum(int num1,int num2){\n" +
                "        \n" +
                "    }\n" +
                "}\n" +
                "接下来我会发给你新的编程题目，你只需要使用Java生成这道题目的前置代码（不需要生成后置代码），" +
                "生成的代码不要有注释。生成的前置代码省略掉import java.util.Scanner;。" +
                "并且只需要回答代码不要有多余的文字说明。\n";
        String userContent = content;
        return sendToAI(systemContent,userContent);
    }

    @Override
    public String getAfterCode(String content) {
        String systemContent = "你是OJ在线判题系统的一位出题老师。\n" +
                "下面我定义的“前置代码”和“后置代码”的一份示例（题目是两数相加），它们的作用是使核心代码模式的代码，" +
                "能在ACM模式的判题机内运行\n" +
                "前置代码：\n" +
                "public class Main{\n" +
                "    public static void main(String[] args) {\n" +
                "        //根据题目获取题目参数\n" +
                "        Scanner scanner = new Scanner(System.in);\n" +
                "        String line = scanner.nextLine();\n" +
                "        String[] split = line.split(\" \");\n" +
                "        int num1 = Integer.parseInt(split[0]);\n" +
                "        int num2 = Integer.parseInt(split[1]);\n" +
                "        System.out.println(new Solution().twoSum(num1,num2));\n" +
                "    }\n" +
                "}\n" +
                "后置代码：\n" +
                "class Solution{\n" +
                "    public int twoSum(int num1,int num2){\n" +
                "        \n" +
                "    }\n" +
                "}\n" +
                "接下来我会发给你新的编程题目，你只需要使用Java生成这道题目的后置代码（不需要生成前置代码），" +
                "生成的代码不要有注释。生成的后置代码只需要提供方法体，不需要有方法的具体实现。" +
                "并且只需要回答代码不要有多余的文字说明。";
        String userContent = content;
        return sendToAI(systemContent,userContent);
    }

    @Override
    public String getAnswer(String content) {
        String systemContent = "使用Java解答下面这道题目，要求代码格式如下（下面是两数相加的例子），即main方法只做输入功能，核心代码写在Solution里，并且只回答代码，不要有其他文字说明\n" +
                "public class Main{\n" +
                "    public static void main(String[] args) {\n" +
                "        //根据题目获取题目参数\n" +
                "        Scanner scanner = new Scanner(System.in);\n" +
                "        String line = scanner.nextLine();\n" +
                "        String[] split = line.split(\" \");\n" +
                "        int num1 = Integer.parseInt(split[0]);\n" +
                "        int num2 = Integer.parseInt(split[1]);\n" +
                "        System.out.println(new Solution().twoSum(num1,num2));\n" +
                "    }\n" +
                "}\n" +
                "class Solution{\n" +
                "    public int twoSum(int num1,int num2){\n" +
                "        return num1 + num2;\n" +
                "    }\n" +
                "}";
        String userContent = content;
        return sendToAI(systemContent,userContent);
    }

    /**
     * 获取AI提示
     * @param content 题目内容
     * @return
     */
    @Override
    public String getTip(String content) {
        String systemContent = "现在你是一名编程学习的老师，有一个同学做编程题没有思路，你需要给他提示一下思路，但是不要回答代码，下面是题目.\n";
        String userContent = content;
        return sendToAI(systemContent,userContent);
    }


    @Override
    public String sendToAI(String systemContent,String userContent) {
        if(!status){
            return "ai功能已暂停";
        }
        // 消息列表，可以在此列表添加历史对话记录
        List<SparkMessage> messages = new ArrayList<>();
        // #设置对话背景或者模型角色
//        messages.add(SparkMessage.systemContent("请你扮演我的语文老师李老师，问我讲解问题问题，希望你可以保证知识准确，逻辑严谨。"));
        messages.add(SparkMessage.systemContent(systemContent));
        //设置问题
//        messages.add(SparkMessage.userContent("鲁迅和周树人小时候打过架吗？"));
        messages.add(SparkMessage.userContent(userContent));
        // 构造请求
        SparkRequest sparkRequest = SparkRequest.builder()
                // 消息列表
                .messages(messages)
                // 模型回答的tokens的最大长度,非必传，默认为2048。
                // V1.5取值为[1,4096]
                // V2.0取值为[1,8192]
                // V3.0取值为[1,8192]
                .maxTokens(2048)
                // 核采样阈值。用于决定结果随机性,取值越高随机性越强即相同的问题得到的不同答案的可能性越高 非必传,取值为[0,1],默认为0.5
                .temperature(0.2)
                // 指定请求版本，默认使用最新3.0版本
                .apiVersion(SparkApiVersion.V3_5)
                .build();

        try {
            // 同步调用
            SparkSyncChatResponse chatResponse = sparkClient.chatSync(sparkRequest);
            SparkTextUsage textUsage = chatResponse.getTextUsage();

//            System.out.println("\n回答：" + chatResponse.getContent());
//            System.out.println("\n提问tokens：" + textUsage.getPromptTokens()
//                    + "，回答tokens：" + textUsage.getCompletionTokens()
//                    + "，总消耗tokens：" + textUsage.getTotalTokens());
            return chatResponse.getContent();
        } catch (SparkException e) {
            System.out.println("发生异常了：" + e.getMessage());
        }
        return "AI调用异常";
    }
}
