package com.ingsis.jcli.snippets.services;

import com.ingsis.jcli.snippets.common.requests.TestState;
import com.ingsis.jcli.snippets.dto.TestCaseDto;
import com.ingsis.jcli.snippets.models.Snippet;
import com.ingsis.jcli.snippets.models.TestCase;
import com.ingsis.jcli.snippets.producers.TestCaseRunProducer;
import com.ingsis.jcli.snippets.producers.factory.LanguageProducerFactory;
import com.ingsis.jcli.snippets.repositories.TestCaseRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestCaseService {

  private final TestCaseRepository testCaseRepository;
  private final LanguageProducerFactory languageProducerFactory;

  @Autowired
  public TestCaseService(
      TestCaseRepository testCaseRepository, LanguageProducerFactory languageProducerFactory) {
    this.testCaseRepository = testCaseRepository;
    this.languageProducerFactory = languageProducerFactory;
  }

  public Long createTestCase(TestCaseDto testCaseDto, Snippet snippet) {
    TestCase testCase =
        new TestCase(
            snippet,
            testCaseDto.name(),
            testCaseDto.input(),
            testCaseDto.output(),
            testCaseDto.type(),
            TestState.PENDING);
    testCaseRepository.save(testCase);
    return testCase.getId();
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
    TestCaseRunProducer testCaseRunProducer =
        languageProducerFactory.getTestCaseRunProducer(snippet.getLanguageVersion().getLanguage());
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
}
