package com.rokid.rkengine.utils;

/**
 * Created by fanfeng on 2017/4/25.
 */

public class DemoTestUtils {

    //Test1: 我要打人
    public static final String TEST_NLP1 = "{\"cloud\":false,\"confidence\":1,\"domain\":\"custom.skill.A6DF1E1D61754997A5195FC1F28C2CA3\",\"intent\":\"beat\",\"pattern\":\"我要打人\",\"posEnd\":4,\"posStart\":0,\"slots\":{},\"version\":\"custom.skill.A6DF1E1D61754997A5195FC1F28C2CA3:0.0.0\"}";
    public static final String TEST_ACTION1 = "{\"appId\":\"AC191AE1C6A74549AB813AB0D57546E7\",\"response\":{\"action\":{\"shoudEndSession\":false,\"voice\":{\"item\":{},\"needEventCallback\":false}},\"shot\":\"CUT\"},\"startWithActiveWord\":false,\"version\":\"2.0.0\"}";
    public static final String TEST_ASR1 = ".";

    //Test2: 关机
    public static final String TEST_NLP2 = "{\"cloud\":false,\"confidence\":1,\"domain\":\"custom.skill.B0BAB1A9E2FE41AC90D4EFE6EE61EA12\",\"intent\":\"poweroff\",\"pattern\":\"关机\",\"posEnd\":2,\"posStart\":0,\"slots\":{},\"version\":\"custom.skill.B0BAB1A9E2FE41AC90D4EFE6EE61EA12:0.0.0\"}";
    public static final String TEST_ACTION2 = "{\"appId\":\"ECC64B9B30B945A3BB005DFB2EA9A9C5\",\"response\":{\"action\":{\"name\":\"Weather\",\"shouldEndSession\":true,\"type\":\"NORMAL\",\"version\":\"2.0.0\",\"voice\":{\"behaviour\":\"REPLACE_ALL\",\"item\":{\"tts\":\"产品经经理是只旺旺\"},\"needEventCallback\":false}},\"domain\":\"custom.skill.B0BAB1A9E2FE41AC90D4EFE6EE61EA12\",\"resType\":\"INTENT\",\"respId\":\"995E8F6BAA0D41D39D84DBF63F41BC59\",\"shot\":\"SCENE\"},\"startWithActiveWord\":false,\"version\":\"2.0.0\"}";
    public static final String TEST_ASR2 = "..";

    //Test3: 最近的厕所在哪里
    public static final String TEST_NLP3 = "{\"cloud\":false,\"confidence\":1,\"domain\":\"custom.skill.D75F715F836F41DC99A9852ACA8101AF\",\"intent\":\"shit\",\"pattern\":\"最近的厕所在哪里\",\"posEnd\":8,\"posStart\":0,\"slots\":{},\"version\":\"custom.skill.D75F715F836F41DC99A9852ACA8101AF:0.0.0\"}";
    public static final String TEST_ACTION3 = "{\"appId\":\"D25D9DCD70FE4FE99B9A03D5D7D9CE98\",\"response\":{\"action\":{\"name\":\"Weather\",\"shouldEndSession\":true,\"type\":\"NORMAL\",\"version\":\"2.0.0\",\"voice\":{\"behaviour\":\"REPLACE_ALL\",\"item\":{\"tts\":\"产品经经理是只旺旺\"},\"needEventCallback\":false}},\"domain\":\"custom.skill.D75F715F836F41DC99A9852ACA8101AF\",\"resType\":\"INTENT\",\"respId\":\"AECAA550DFDA4B40BD410D9B2560BD7F\",\"shot\":\"SCENE\"},\"startWithActiveWord\":false,\"version\":\"2.0.0\"}";
    public static final String TEST_ASR3 = "...";

    //Test4: 不打了
    public static final String TEST_NLP4 = "{\"cloud\":false,\"confidence\":1,\"domain\":\"custom.skill.A6DF1E1D61754997A5195FC1F28C2CA3\",\"intent\":\"stop\",\"pattern\":\"不打了\",\"posEnd\":3,\"posStart\":0,\"slots\":{},\"version\":\"custom.skill.A6DF1E1D61754997A5195FC1F28C2CA3:0.0.0\"}";
    public static final String TEST_ACTION4 = "{\"appId\":\"AB2D9DCD70FE4FE99B9A03D5D7D9CE98\",\"response\":{\"action\":{\"name\":\"Weather\",\"shouldEndSession\":true,\"type\":\"NORMAL\",\"version\":\"2.0.0\",\"voice\":{\"behaviour\":\"REPLACE_ALL\",\"item\":{\"tts\":\"产品经经理是只旺旺\"},\"needEventCallback\":false}},\"domain\":\"custom.skill.A6DF1E1D61754997A5195FC1F28C2CA3\",\"resType\":\"INTENT\",\"respId\":\"33B55DC1ECC34F8C9DE06168CC61D3CF\",\"shot\":\"CUT\"},\"startWithActiveWord\":false,\"version\":\"2.0.0\"}";
    public static final String TEST_ASR4 = "....";

    //Test5: test for cloudapp
    public static final String TEST_NLP5 = "{\"cloud\":true,\"confidence\":1,\"domain\":\"custom.skill.841f3558-f3d4-43f6-911a-6f80b62b352d\",\"intent\":\"sayhello\",\"pattern\":\"打个招呼\",\"posEnd\":4,\"posStart\":0,\"slots\":{},\"version\":\"custom.skill.841f3558-f3d4-43f6-911a-6f80b62b352d:0.0.0\"}";
    public static final String TEST_ACTION5 = "{\"response\":{\"action\":{\"shoudEndSession\":false,\"type\":\"NORMAL\",\"version\":\"2.0.0\",\"voice\":{\"behaviour\":\"REPLACE_ALL\",\"item\":{\"tts\":\"你好，我是Mini，很高兴见到大家\"},\"needEventCallback\":false}},\"appId\":\"Cloud\",\"domain\":\"custom.skill.841f3558-f3d4-43f6-911a-6f80b62b352d\",\"resType\":\"INTENT\",\"respId\":\"C6C545CAE64E4C50896DF72CC32DB376\",\"shot\":\"CUT\"},\"startWithActiveWord\":true,\"version\":\"2.0.0\"}";
    public static final String TEST_ASR5 = "若琪打个招呼";

}
