package com.ingsis.jcli.snippets.specifications;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.ingsis.jcli.snippets.common.language.LanguageVersion;
import com.ingsis.jcli.snippets.models.Snippet;
import com.ingsis.jcli.snippets.repositories.SnippetRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.jpa.domain.Specification;

public class SnippetSpecificationTest {

  @Mock private SnippetRepository snippetRepository;

  private Snippet snippet1;
  private Snippet snippet2;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    // Set up sample data
    snippet1 = new Snippet();
    snippet1.setId(1L); // Assign a non-null id
    snippet1.setOwner("owner1");
    snippet1.setLanguageVersion(new LanguageVersion("printscript", "1.0"));
    snippet1.setName("Example Snippet");

    snippet2 = new Snippet();
    snippet2.setId(2L); // Assign a non-null id
    snippet2.setOwner("owner2");
    snippet2.setLanguageVersion(new LanguageVersion("java", "1.0"));
    snippet2.setName("Another Snippet");
  }

  @Test
  void testIsOwner() {
    Specification<Snippet> specification = SnippetSpecifications.isOwner("owner1");
    when(snippetRepository.findAll(any(Specification.class))).thenReturn(List.of(snippet1));
    List<Snippet> result = snippetRepository.findAll(specification);
    assertEquals(1, result.size());
    assertEquals("owner1", result.get(0).getOwner());
  }

  @Test
  void testIsShared() {
    Specification<Snippet> specification =
        SnippetSpecifications.isShared(List.of(snippet1.getId()));
    when(snippetRepository.findAll(any(Specification.class))).thenReturn(List.of(snippet1));
    List<Snippet> result = snippetRepository.findAll(specification);
    assertEquals(1, result.size());
    assertEquals(snippet1.getId(), result.get(0).getId());
  }

  @Test
  void testIsLanguage() {
    Specification<Snippet> specification = SnippetSpecifications.isLanguage("printscript");
    when(snippetRepository.findAll(any(Specification.class))).thenReturn(List.of(snippet1));
    List<Snippet> result = snippetRepository.findAll(specification);
    assertEquals(1, result.size());
    assertEquals("printscript", result.get(0).getLanguageVersion().getLanguage());
  }

  @Test
  void testNameHasWordThatStartsWith() {
    Specification<Snippet> specification =
        SnippetSpecifications.nameHasWordThatStartsWith("Another");
    when(snippetRepository.findAll(any(Specification.class))).thenReturn(List.of(snippet2));
    List<Snippet> result = snippetRepository.findAll(specification);
    assertEquals(1, result.size());
    assertTrue(result.get(0).getName().contains("Another"));
  }
}
