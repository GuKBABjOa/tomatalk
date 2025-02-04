package team.overfullow.tolonbgeub.topic;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@Component
@RequiredArgsConstructor
public class TopicApiClient {

    private final MockMvc mockMvc;

    public ResultActions getTopic(Long topicId) throws Exception {
        return mockMvc.perform(get("/api/topics/{topicId}", topicId));
    }
}
