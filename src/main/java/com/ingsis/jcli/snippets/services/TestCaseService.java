package com.ingsis.jcli.snippets.services;

import com.ingsis.jcli.snippets.common.requests.TestState;
import com.ingsis.jcli.snippets.dto.TestCaseDto;
import com.ingsis.jcli.snippets.models.Snippet;
import com.ingsis.jcli.snippets.models.TestCase;
import com.ingsis.jcli.snippets.repositories.TestCaseRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestCaseService {

  private final TestCaseRepository testCaseRepository;

  @Autowired
  public TestCaseService(TestCaseRepository testCaseRepository) {
    this.testCaseRepository = testCaseRepository;
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
}
