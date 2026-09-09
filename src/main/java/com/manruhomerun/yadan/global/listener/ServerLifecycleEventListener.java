package com.manruhomerun.yadan.global.listener;

import com.manruhomerun.yadan.global.client.DiscordWebhookClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ServerLifecycleEventListener {

    private final DiscordWebhookClient discordWebhookClient;
    private boolean serverStarted;

    @EventListener(ApplicationReadyEvent.class)
    public void handleServerStarted() {
        // 애플리케이션이 요청을 받을 준비를 마친 뒤 시작 알림을 전송합니다.
        serverStarted = true;
        discordWebhookClient.sendServerLifecycle("시작");
    }

    @EventListener(ContextClosedEvent.class)
    public void handleServerStopped() {
        // 시작 완료 전 초기화 실패로 컨텍스트가 닫히면 종료 알림을 보내지 않습니다.
        if (!serverStarted) {
            return;
        }

        serverStarted = false;
        discordWebhookClient.sendServerLifecycle("종료");
    }
}
