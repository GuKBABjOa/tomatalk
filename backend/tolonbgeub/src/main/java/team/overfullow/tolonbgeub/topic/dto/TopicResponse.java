package team.overfullow.tolonbgeub.topic.dto;

import lombok.Builder;

public record TopicResponse(String topicId,
                            String topic,
                            String description,
                            Integer bookmarkCount,
                            String createdAt,
                            String lastModifiedAt) {

    @Builder
    public TopicResponse{
    }

}
