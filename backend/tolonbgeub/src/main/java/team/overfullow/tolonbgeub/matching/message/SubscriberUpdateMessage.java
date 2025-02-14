package team.overfullow.tolonbgeub.matching.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import team.overfullow.tolonbgeub.debate.Category;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriberUpdateMessage {
    private Category category;
    private int subscriberCount;
    private List<String> subscribers;
}
