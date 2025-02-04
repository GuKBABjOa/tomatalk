package team.overfullow.tolonbgeub.topic;

import lombok.Builder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
import team.overfullow.tolonbgeub.ApiTestSupport;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TopicApiTest extends ApiTestSupport {

    @Autowired
    TopicApiClient topicApiClient;

    @Autowired
    TopicRepository topicRepository;

    @BeforeEach
    public void clean() throws Exception {
        topicRepository.deleteAll();
    }

    @Nested
    @DisplayName("주제 단건 조회 테스트")
    class AuthenticationTest {
        @Test
        @DisplayName("topicId에 해당하는 주제 상세 정보를 조회한다")
        void authenticationSuccess() throws Exception {
            // given
            Topic topic = topicRepository.save(Topic.builder()
                    .id(1L)
                    .topic("주제")
                    .description("주제에 대한 설명")
                    .build());

            // when
            ResultActions result = topicApiClient.getTopic(topic.getId());

            // then
            result.andExpectAll(
                    status().isOk(),
                    jsonPath("$.topicId").value(topic.getId().toString()),
                    jsonPath("$.topic").value(topic.getTopic()),
                    jsonPath("$.description").value(topic.getDescription())
            );
        }

        @Nested
        @DisplayName("주제 단건 조회에 실패한다")
        class Failure{
            @Test
            @DisplayName("topicId에 해당하는 주제가 존재하지 않는 경우")
            void withNotFound() throws Exception {
                // given
                Long topicId = 1L; // 존재하지 않는 토픽 Id

                // when
                ResultActions result = topicApiClient.getTopic(topicId);

                // then
                result.andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.message").isString()
                );
            }
        }
    }
}