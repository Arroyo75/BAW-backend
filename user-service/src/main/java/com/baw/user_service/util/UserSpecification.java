package com.baw.user_service.util;

import com.baw.user_service.model.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {
    public static Specification<User> matchesSearch(String search) {
        return (root, query, cb) -> search == null ? null :
                cb.or(
                        cb.like(cb.lower(root.get("username")), "%" + search.toLowerCase() + "%"),
                        cb.like(cb.lower(root.get("email")), "%" + search.toLowerCase() + "%")
                );
    }

    public static Specification<User> isHasDogs(Boolean hasDogs) {
        return (root, query, cb) -> hasDogs == null ? null :
                cb.equal(root.get("hasDogs"), hasDogs);
    }
}
