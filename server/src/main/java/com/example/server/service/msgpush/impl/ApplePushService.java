package com.example.server.service.msgpush.impl;

import com.eatthepath.pushy.apns.ApnsClient;
import com.eatthepath.pushy.apns.ApnsClientBuilder;
import com.eatthepath.pushy.apns.PushNotificationResponse;
import com.eatthepath.pushy.apns.auth.ApnsSigningKey;
import com.eatthepath.pushy.apns.metrics.dropwizard.DropwizardApnsClientMetricsListener;
import com.eatthepath.pushy.apns.util.SimpleApnsPushNotification;
import com.eatthepath.pushy.apns.util.TokenUtil;
import com.eatthepath.pushy.apns.util.concurrent.PushNotificationFuture;
import com.example.common.util.StringUtils;
import com.example.server.service.msgpush.PushService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.CountDownLatch;

public class ApplePushService implements PushService {

    private static final Logger logger = LoggerFactory.getLogger(ApplePushService.class);
    private static ApnsClient apnsClient = null;

    public ApnsClient createApnsClient() {
        try {
            return apnsClient = new ApnsClientBuilder()
                    .setApnsServer(ApnsClientBuilder.DEVELOPMENT_APNS_HOST)
                    .setSigningKey(ApnsSigningKey.loadFromPkcs8File(new File("/path/to/key.p8"),
                            "TEAMID1234", "KEYID67890"))
                    .setMetricsListener(new DropwizardApnsClientMetricsListener())
                    .build();
        } catch (Exception e) {
            logger.error("创建apnsClient失败", e);
        }
        return null;
    }

    public void pushNotification(String payload, String... tokens) {
        try {
            if (StringUtils.isEmpty(payload) || tokens == null) {
                logger.error("要推送的消息或deviceToken为空");
                return;
            }
            if (apnsClient == null && (apnsClient = createApnsClient()) == null) {
                return;
            }
            final CountDownLatch countDownLatch = new CountDownLatch(tokens.length);
            for (String token : tokens) {
                token = TokenUtil.sanitizeTokenString(token);
                SimpleApnsPushNotification notification = new SimpleApnsPushNotification(token, "com.example.myApp", payload);
                PushNotificationFuture<SimpleApnsPushNotification, PushNotificationResponse<SimpleApnsPushNotification>>
                        sendFuture = apnsClient.sendNotification(notification);
                sendFuture.whenComplete((response, cause) -> {
                    countDownLatch.countDown();
                    if (response == null) {
                        logger.error("apns推送消息发生错误", cause);
                    } else if (response.isAccepted()) {
                        logger.debug("apns消息推送成功{}", response.getApnsId());
                    } else {
                        logger.error("apns消息推送被拒收:{}", response.getRejectionReason());
                        response.getTokenInvalidationTimestamp().ifPresent(timestamp -> logger.error("token 过期：{}", timestamp));
                    }
                });
            }
            countDownLatch.await();
            logger.debug("apns消息推送完成");
        } catch (Exception e) {
            logger.error(null, e);
        }
    }
}
