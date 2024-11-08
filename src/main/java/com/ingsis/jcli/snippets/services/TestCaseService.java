package com.ingsis.jcli.snippets.services;

import com.ingsis.jcli.snippets.common.requests.TestState;
import com.ingsis.jcli.snippets.dto.TestCaseDto;
import com.ingsis.jcli.snippets.models.Snippet;
import com.ingsis.jcli.snippets.models.TestCase;
import com.ingsis.jcli.snippets.producers.TestCaseRunProducer;
import com.ingsis.jcli.snippets.repositories.TestCaseRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestCaseService {

  private final TestCaseRepository testCaseRepository;
  private final TestCaseRunProducer testCaseRunProducer;

  @Autowired
  public TestCaseService(
      TestCaseRepository testCaseRepository, TestCaseRunProducer testCaseRunProducer) {
    this.testCaseRepository = testCaseRepository;
    this.testCaseRunProducer = testCaseRunProducer;
  }

  public TestCase createTestCase(TestCaseDto testCaseDto, Snippet snippet) {
    TestCase testCase =
        new TestCase(
            snippet,
            testCaseDto.name(),
            testCaseDto.input(),
            testCaseDto.output(),
            testCaseDto.type(),
            TestState.PENDING);
    testCaseRepository.save(testCase);
    return testCase;
  }

  public Optional<TestCase> getTestCase(Long id) {
    return testCaseRepository.findById(id);
  }

  public void updateTestCaseState(TestCase testCase, TestState testState) {
    testCase.setState(testState);
    testCaseRepository.save(testCase);
  }

  public void runAllTestCases(Snippet snippet) {
    List<TestCase> testCaseList = testCaseRepository.findAllBySnippet(snippet);
    for (TestCase testCase : testCaseList) {
      testCaseRunProducer.run(testCase, snippet.getLanguageVersion().getVersion());
    }
  }

  public List<TestCase> getTestCaseByUser(String userId) {
    return testCaseRepository.findAllBySnippet_Owner(userId);
  }

  public void deleteTestCase(TestCase testCase) {
    testCaseRepository.delete(testCase);
  }

  public List<TestCase> getTestCaseBySnippet(Snippet snippet) {
    return testCaseRepository.findTestCaseBySnippet(snippet);
  }
}
