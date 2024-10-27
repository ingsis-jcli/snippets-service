package com.ingsis.jcli.snippets.services;

import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ingsis.jcli.snippets.common.language.LanguageVersion;
import com.ingsis.jcli.snippets.common.requests.TestState;
import com.ingsis.jcli.snippets.common.requests.TestType;
import com.ingsis.jcli.snippets.dto.TestCaseDto;
import com.ingsis.jcli.snippets.models.Snippet;
import com.ingsis.jcli.snippets.models.TestCase;
import com.ingsis.jcli.snippets.producers.TestCaseRunProducer;
import com.ingsis.jcli.snippets.producers.factory.LanguageProducerFactory;
import com.ingsis.jcli.snippets.repositories.TestCaseRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TestCaseServiceTest {

  @Mock private TestCaseRepository testCaseRepository;
  @Mock private LanguageProducerFactory languageProducerFactory;
  @Mock private TestCaseRunProducer testCaseRunProducer;

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
            TestType.VALID,
            TestState.PENDING);
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
            TestType.VALID,
            TestState.PENDING);
    testCase.setId(testCaseId);

    when(testCaseRepository.findById(testCaseId)).thenReturn(Optional.of(testCase));

    Optional<TestCase> result = testCaseService.getTestCase(testCaseId);
    assertTrue(result.isPresent(), "Test case should be present");
    assertEquals(testCaseId, result.get().getId(), "Test case ID should match");
  }

  @Test
  void testRunAllTestCases() {
    Snippet snippet = new Snippet("name", "url", "1", new LanguageVersion("printscript", "1.1"));
    snippet.setId(1L);

    TestCase testCase1 =
        new TestCase(
            snippet,
            "Test Case 1",
            Arrays.asList("input1"),
            Arrays.asList("output1"),
            TestType.VALID,
            TestState.PENDING);
    testCase1.setId(1L);

    TestCase testCase2 =
        new TestCase(
            snippet,
            "Test Case 2",
            Arrays.asList("input2"),
            Arrays.asList("output2"),
            TestType.INVALID,
            TestState.PENDING);
    testCase2.setId(2L);

    List<TestCase> testCaseList = Arrays.asList(testCase1, testCase2);

    when(testCaseRepository.findAllBySnippet(snippet)).thenReturn(testCaseList);
    when(languageProducerFactory.getTestCaseRunProducer("printscript"))
        .thenReturn(testCaseRunProducer);

    testCaseService.runAllTestCases(snippet);

    verify(testCaseRunProducer, times(1)).run(testCase1, "1.1");
    verify(testCaseRunProducer, times(1)).run(testCase2, "1.1");
  }

  @Test
  void testUpdateTestCaseState() {
    Long testCaseId = 1L;
    Snippet snippet = new Snippet();
    snippet.setId(1L);

    TestCase testCase =
        new TestCase(
            snippet,
            "Test Case",
            Arrays.asList("input1"),
            Arrays.asList("output1"),
            TestType.VALID,
            TestState.PENDING);
    testCase.setId(testCaseId);

    when(testCaseRepository.save(testCase)).thenReturn(testCase);
    testCaseService.updateTestCaseState(testCase, TestState.SUCCESS);
    assertEquals(TestState.SUCCESS, testCase.getState(), "Test state should be updated to SUCCESS");
    verify(testCaseRepository, times(1)).save(testCase);
  }
}
