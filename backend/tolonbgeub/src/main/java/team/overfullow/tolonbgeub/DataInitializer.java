package team.overfullow.tolonbgeub;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import team.overfullow.tolonbgeub.debate.Category;
import team.overfullow.tolonbgeub.debate.debate.DebateService;
import team.overfullow.tolonbgeub.user.dto.UserRequest;
import team.overfullow.tolonbgeub.user.dto.UserResponse;
import team.overfullow.tolonbgeub.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserService userService;
    private final DebateService debateService;

    @Bean
    public ApplicationRunner dataLoader() {
        return args -> initializeData();
    }

    @Transactional
    public void initializeData() {
        List<Long> userIds = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            String username = "user" + i;
            UserResponse user = userService.createUser(UserRequest.builder()
                    .email(String.format("%s@test.email", username))
                    .nickname(username)
                    .build());
            userIds.add(user.userId());
        }
        Random random = new Random();
        for (int i = 0; i < userIds.size() / 4; i++) {
            int anInt = random.nextInt(100);
            Category category = Category.values()[(anInt % Category.values().length)];
            debateService.create(category, userIds.subList(i * 4, i * 4 + 4));
        }
    }
}

