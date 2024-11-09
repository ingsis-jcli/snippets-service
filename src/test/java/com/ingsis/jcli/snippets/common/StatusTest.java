package com.ingsis.jcli.snippets.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ingsis.jcli.snippets.common.status.ProcessStatus;
import com.ingsis.jcli.snippets.common.status.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StatusTest {

  private Status status;
  private Status anotherStatus;

  @BeforeEach
  void setUp() {
    status = new Status();
    anotherStatus = new Status();
  }

  @Test
  void testEquals() {
    assertEquals(status, anotherStatus);

    anotherStatus.setFormatting(ProcessStatus.COMPLIANT);
    assertNotEquals(status, anotherStatus);
  }

  @Test
  void testHashCode() {
    assertEquals(status.hashCode(), anotherStatus.hashCode());

    anotherStatus.setLinting(ProcessStatus.COMPLIANT);
    assertNotEquals(status.hashCode(), anotherStatus.hashCode());
  }

  @Test
  void testCanEqual() {
    assertTrue(status.equals(anotherStatus));

    assertNotEquals(status, new Object());
  }

  @Test
  void testDefaultStatusValues() {
    assertEquals(ProcessStatus.NOT_STARTED, status.getFormatting());
    assertEquals(ProcessStatus.NOT_STARTED, status.getLinting());
  }

  @Test
  void testSetFormattingStatus() {
    status.setFormatting(ProcessStatus.COMPLIANT);
    assertEquals(ProcessStatus.COMPLIANT, status.getFormatting());

    status.setFormatting(ProcessStatus.NON_COMPLIANT);
    assertEquals(ProcessStatus.NON_COMPLIANT, status.getFormatting());
  }

  @Test
  void testSetLintingStatus() {
    status.setLinting(ProcessStatus.COMPLIANT);
    assertEquals(ProcessStatus.COMPLIANT, status.getLinting());

    status.setLinting(ProcessStatus.NON_COMPLIANT);
    assertEquals(ProcessStatus.NON_COMPLIANT, status.getLinting());
  }
}
