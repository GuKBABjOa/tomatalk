package team.overfullow.tolonbgeub.debate;

import java.util.Optional;

public enum Category {
    POLITICS,
    ECONOMY,
    ETHICS,
    SCIENCE,
    EDUCATION;

    public static Optional<Category> fromString(String category) {
        try {
            return Optional.of(Category.valueOf(category.toUpperCase()));
        } catch (IllegalArgumentException | NullPointerException e) {
            return Optional.empty();
        }
    }
}