package team.overfullow.tolonbgeub.auth.util;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class UniqueIdGenerator implements IdGenerator {
    private static AtomicInteger sequence = new AtomicInteger();
    public Long generate(){
        long id = (((long) Instant.now().getNano())) << 31 | sequence.getAndIncrement();
        sequence.compareAndSet(Integer.MAX_VALUE, 0);
        return id; //todo timestamp:serverId:seq 형태의 유일 아이디 생성
    }
}
