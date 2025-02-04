package team.overfullow.tolonbgeub.topic.dto;

import lombok.Builder;

@Builder
public record TopicResponse(String topicId,
                            String topic,
                            String description,
                            Integer bookmarkCount,
                            String createdAt,
                            String lastModifiedAt) {
}
