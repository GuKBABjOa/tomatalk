package team.overfullow.tolonbgeub.matching.session;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CategoryUpdateDto {
    private String category;
    private int userCount;
    private List<UserStatus> users;
}