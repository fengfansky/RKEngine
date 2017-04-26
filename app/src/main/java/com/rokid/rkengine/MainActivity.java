package com.rokid.rkengine;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.rokid.rkengine.scheduler.AppManagerImp;
import com.rokid.rkengine.parser.ParserProxy;
import com.rokid.rkengine.scheduler.AppStack;
import com.rokid.rkengine.utils.DemoTestUtils;

public class MainActivity extends Activity {

    private AppManagerImp appManager = null;

    private ParserProxy parserProxy;

    private static final String MSG1 = "msg1";
    private static final String MSG2 = "msg2";
    private static final String MSG3 = "msg3";
    private static final String MSG4 = "msg4";
    private static final String MSG5 = "msg5";

    private static final int TIME_ONE_SECOND = 1000;
    private static final int TIME_TWO_SECOND = 2000;
    private static final int TIME_THREE_SECOND = 3000;
    private static final int TIME_FOUR_SECOND = 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("MainActivity", "onCreate");

        appManager = AppManagerImp.getInstance();
        AppStack.getInstance().clearAppStack();

        if (appManager == null) {
            return;
        }
        appManager.bindService(this);
        parserProxy = ParserProxy.getInstance();

        //test
        new Thread() {
            @Override
            public void run() {
                super.run();
                test8();
            }
        }.start();

    }

    //app1:cut app2:cut app3:scene

    // test cut 自身
    private void test1() {
        sendMessage(MSG1, TIME_ONE_SECOND);
        sendMessage(MSG4, TIME_TWO_SECOND);
    }

    //test cut1 + cut2
    private void test2() {
        sendMessage(MSG1, TIME_ONE_SECOND);
        sendMessage(MSG2, TIME_TWO_SECOND);
    }

    //test cut + scene
    private void test3() {
        sendMessage(MSG2, TIME_ONE_SECOND);
        sendMessage(MSG3, TIME_TWO_SECOND);
    }

    //test scene + cut
    private void test4() {
        sendMessage(MSG3, TIME_ONE_SECOND);
        sendMessage(MSG2, TIME_TWO_SECOND);
    }

    //test cut + scene + cut
    private void test5() {
        sendMessage(MSG2, TIME_ONE_SECOND);
        sendMessage(MSG3, TIME_TWO_SECOND);
        sendMessage(MSG1, TIME_THREE_SECOND);
    }

    //test scene + cut + scene
    private void test6() {
        sendMessage(MSG3, TIME_ONE_SECOND);
        sendMessage(MSG1, TIME_TWO_SECOND);
        sendMessage(MSG4, TIME_THREE_SECOND);
    }

    //test scene + cut + scene + cut
    private void test7() {
        sendMessage(MSG3, TIME_ONE_SECOND);
        sendMessage(MSG1, TIME_TWO_SECOND);
        sendMessage(MSG4, TIME_THREE_SECOND);
        sendMessage(MSG2, TIME_FOUR_SECOND);
    }

    //test cut + cut + scene + scene
    private void test8() {
        sendMessage(MSG1, TIME_ONE_SECOND);
        sendMessage(MSG2, TIME_TWO_SECOND);
        sendMessage(MSG3, TIME_THREE_SECOND);
        sendMessage(MSG4, TIME_FOUR_SECOND);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (appManager == null)
            return;
        appManager.unBindService();
    }

    private void sendMessage(String testMsg, int time) {
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("TEST", testMsg);
        message.setData(bundle);
        myHandler.sendMessageDelayed(message, time);
    }


    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String value = msg.getData().getString("TEST");
            switch (value) {
                case MSG1:
                    parserProxy.startParse(MainActivity.this, DemoTestUtils.TEST_NLP1, DemoTestUtils.TEST_ASR1, DemoTestUtils.TEST_ACTION1);
                    break;

                case MSG2:
                    parserProxy.startParse(MainActivity.this, DemoTestUtils.TEST_NLP2, DemoTestUtils.TEST_ASR2, DemoTestUtils.TEST_ACTION2);
                    break;

                case MSG3:
                    parserProxy.startParse(MainActivity.this, DemoTestUtils.TEST_NLP3, DemoTestUtils.TEST_ASR3, DemoTestUtils.TEST_ACTION3);
                    break;

                case MSG4:
                    parserProxy.startParse(MainActivity.this, DemoTestUtils.TEST_NLP4, DemoTestUtils.TEST_ASR4, DemoTestUtils.TEST_ACTION4);
                    break;

                case MSG5:
                    parserProxy.startParse(MainActivity.this, DemoTestUtils.TEST_NLP5, DemoTestUtils.TEST_ASR5, DemoTestUtils.TEST_ACTION5);
                    break;

            }

            super.handleMessage(msg);
        }
    };


}
