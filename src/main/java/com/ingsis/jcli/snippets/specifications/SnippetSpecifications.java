package com.ingsis.jcli.snippets.specifications;

import com.ingsis.jcli.snippets.models.Snippet;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class SnippetSpecifications {

  public static Specification<Snippet> isOwner(String ownerId) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("owner"), ownerId);
  }

  public static Specification<Snippet> isShared(List<Long> sharedSnippetIds) {
    return (root, query, criteriaBuilder) -> root.get("id").in(sharedSnippetIds);
  }

  public static Specification<Snippet> isLanguage(String language) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("language"), language);
  }

  public static Specification<Snippet> nameHasWordThatStartsWith(String match) {
    return (root, query, criteriaBuilder) -> {
      String startsWith = match.toLowerCase() + "%";
      String otherWordStartsWith = "% " + match.toLowerCase() + "%";

      return criteriaBuilder.or(
          criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), startsWith),
          criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), otherWordStartsWith));
    };
  }

  // TODO: validation spec
}
