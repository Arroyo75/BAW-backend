package com.baw.dog_service.util;

import com.baw.dog_service.model.Dog;
import org.springframework.data.jpa.domain.Specification;

public class DogSpecification {
    public static Specification<Dog> matchesSearch(String search) {
        return(root, query, cb) -> search == null ? null :
                cb.or(
                    cb.like(cb.lower(root.get("nickname")), "%" + search.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("breed")), "%" + search.toLowerCase() + "%")
                );
    }
}
