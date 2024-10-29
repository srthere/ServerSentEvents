package Prototype;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class SSEController {

    private static final int MAX_EXECUTIONS = 10;

    @GetMapping("/stream-sse")
    public SseEmitter streamTime() {
        SseEmitter sseEmitter = new SseEmitter(0L);

        AtomicInteger counter = new AtomicInteger();
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

        executorService.scheduleAtFixedRate(() -> {
                try {
                    if (counter.get() < MAX_EXECUTIONS) {
                        sseEmitter.send("Current Time : " + LocalTime.now() + " ");
                        counter.getAndIncrement();
                    } else {
                        sseEmitter.send("Emission Completed");
                        sseEmitter.complete();
                    }
                } catch (Exception exception) {
                    sseEmitter.completeWithError(exception);
                }
        }, 0, 1, TimeUnit.SECONDS);

        sseEmitter.onCompletion(executorService::shutdown);

        return sseEmitter;
    }

}
