package com.olympus.common.thread.delay.test;

import com.olympus.common.thread.delay.DelayMission;
import com.olympus.common.thread.delay.DelayMissionQueueControl;
import com.olympus.common.thread.delay.DelayMissionService;
import com.olympus.common.time.DataTimeUtils;
import com.olympus.common.time.LocalTimeUtils;

import java.util.concurrent.TimeUnit;

/**
 * since 5/28/22
 *
 * @author eddie
 */
public class DelayMissionQueueTest {

    private static final DelayMissionQueueControl delayMissionQueueControl = new DelayMissionQueueControl();

    public static void main(String[] args) {
        DelayMission<String> delayMission = new DelayMission<>("这是一条测试数据", false,5000) {
            @Override
            protected boolean handleMission() {
                System.out.println(this.getMissionData());
                return true;
            }

            @Override
            public void rollback(Throwable throwable) {

            }
        };

        DelayMissionService<DelayMission<?>> delayMissionService = new DelayMissionService<>() {
            @Override
            public String getMissionName() {
                return "null";
            }
        };
        delayMissionService.addMission(delayMission);
        delayMissionQueueControl.registerDelayMission(delayMissionService);
        for (int i = 0; i < 5; i++) {
            DelayMission<String> delayMissionTimes =
                    new DelayMission<>("创建时间为：" + LocalTimeUtils.nowTime(), false,i * 1000) {
                        @Override
                        protected boolean handleMission() {
                            System.out.println(this.getMissionData());
                            return true;
                        }

                        @Override
                        public void rollback(Throwable throwable) {

                        }
                    };
            delayMissionService.addMission(delayMissionTimes);
        }
        try {
            TimeUnit.HOURS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
