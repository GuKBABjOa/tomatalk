package team.overfullow.tolonbgeub.debate.session;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserStatus {
    private String userId;
    private SessionStatus status;
}
