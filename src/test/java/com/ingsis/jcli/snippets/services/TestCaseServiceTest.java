package com.ingsis.jcli.snippets.services;

import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.ingsis.jcli.snippets.common.requests.TestType;
import com.ingsis.jcli.snippets.dto.TestCaseDto;
import com.ingsis.jcli.snippets.models.Snippet;
import com.ingsis.jcli.snippets.models.TestCase;
import com.ingsis.jcli.snippets.repositories.TestCaseRepository;
import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TestCaseServiceTest {

  @Mock private TestCaseRepository testCaseRepository;

  @InjectMocks private TestCaseService testCaseService;

  public TestCaseServiceTest() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testCreateTestCase() {
    Snippet snippet = new Snippet();
    TestCaseDto testCaseDto =
        new TestCaseDto(
            "Test Case",
            snippet.getId(),
            Arrays.asList("input1"),
            Arrays.asList("output1"),
            TestType.VALID);

    TestCase testCase =
        new TestCase(
            snippet,
            "Test Case",
            Arrays.asList("input1"),
            Arrays.asList("output1"),
            TestType.VALID);
    when(testCaseRepository.save(testCase)).thenReturn(testCase);

    Long id = testCaseService.createTestCase(testCaseDto, snippet);
    assertEquals(testCase.getId(), id);
  }

  @Test
  void testGetTestCase() {
    Long testCaseId = 1L;
    Snippet snippet = new Snippet();
    snippet.setId(1L);

    TestCase testCase =
        new TestCase(
            snippet,
            "Test Case",
            Arrays.asList("input1"),
            Arrays.asList("output1"),
            TestType.VALID);
    testCase.setId(testCaseId);

    when(testCaseRepository.findById(testCaseId)).thenReturn(Optional.of(testCase));

    Optional<TestCase> result = testCaseService.getTestCase(testCaseId);
    assertTrue(result.isPresent(), "Test case should be present");
    assertEquals(testCaseId, result.get().getId(), "Test case ID should match");
  }
}
