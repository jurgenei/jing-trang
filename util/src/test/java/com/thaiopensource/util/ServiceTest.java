package com.thaiopensource.util;

import com.thaiopensource.util.servicetest.TestSpi;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ServiceTest {
  @Test
  void loadsProvidersAndSkipsMissingImplementations() {
    Service<TestSpi> service = Service.newInstance(TestSpi.class);
    Iterator<TestSpi> providers = service.getProviders();

    List<String> ids = new ArrayList<>();
    while (providers.hasNext()) {
      ids.add(providers.next().id());
    }

    assertEquals(List.of("A", "B"), ids);
  }

  @Test
  void iteratorRemoveNotSupported() {
    Service<TestSpi> service = Service.newInstance(TestSpi.class);
    Iterator<TestSpi> providers = service.getProviders();
    assertThrows(UnsupportedOperationException.class, providers::remove);
  }
}

