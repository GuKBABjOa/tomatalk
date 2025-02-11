package team.overfullow.tolonbgeub.subject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
import team.overfullow.tolonbgeub.ApiTestSupport;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SubjectApiTest extends ApiTestSupport {

    @Autowired
    SubjectApiClient subjectApiClient;

    @Autowired
    SubjectRepository subjectRepository;

    @BeforeEach
    public void clean() throws Exception {
        subjectRepository.deleteAll();
    }

    @Nested
    @DisplayName("논제 단건 조회 테스트")
    class AuthenticationTest {
        @Test
        @DisplayName("id에 해당하는 논제 상세 정보를 조회한다")
        void authenticationSuccess() throws Exception {
            // given
            Subject subject = subjectRepository.save(Subject.builder()
                    .id(1L)
                    .subject("논제")
                    .description("논제에 대한 설명")
                    .build());

            // when
            ResultActions result = subjectApiClient.callGetById(subject.getId());

            // then
            result.andExpectAll(
                    status().isOk(),
                    jsonPath("$.subjectId").value(subject.getId().toString()),
                    jsonPath("$.subject").value(subject.getSubject()),
                    jsonPath("$.description").value(subject.getDescription())
            );
        }

        @Nested
        @DisplayName("논제 단건 조회에 실패한다")
        class Failure{
            @Test
            @DisplayName("subjectId에 해당하는 논제가 존재하지 않는 경우")
            void withNotFound() throws Exception {
                // given
                Long subjectId = 1L; // 존재하지 않는 토픽 Id

                // when
                ResultActions result = subjectApiClient.callGetById(subjectId);

                // then
                result.andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.message").isString()
                );
            }
        }
    }
}