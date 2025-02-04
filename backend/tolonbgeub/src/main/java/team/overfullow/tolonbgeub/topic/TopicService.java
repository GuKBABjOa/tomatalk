package team.overfullow.tolonbgeub.topic;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.overfullow.tolonbgeub.topic.dto.TopicResponse;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TopicService {

    private final TopicRepository topicRepository;

    public TopicResponse getById(Long topicId) {
        return toTopicResponse(topicRepository.findById(topicId)
                .orElseThrow(() -> new TopicException(HttpStatus.NOT_FOUND, "주제를 찾을 수 없습니다")));
    }

    private TopicResponse toTopicResponse(Topic domain) {
        return TopicResponse.builder()
                .topicId(domain.getId().toString())
                .topic(domain.getTopic())
                .description(domain.getDescription())
                .bookmarkCount(0) // todo 북마크 기능 구현 이후 추가
                .createdAt(domain.getCreatedAt().toString())
                .lastModifiedAt(domain.getLastModifiedAt().toString())
                .build();
    }
}