package com.thaiopensource.resolver;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SequenceResolverTest {
  @Test
  void invokesResolversInOrderForResolveAndOpen() throws IOException, ResolverException {
    List<String> calls = new ArrayList<>();
    Resolver first = new RecordingResolver("first", calls);
    Resolver second = new RecordingResolver("second", calls);
    SequenceResolver resolver = new SequenceResolver(first, second);

    resolver.resolve(new Identifier("doc.xml"), new Input());
    resolver.open(new Input());

    assertEquals(List.of("first.resolve", "second.resolve", "first.open", "second.open"), calls);
  }

  private static final class RecordingResolver implements Resolver {
    private final String name;
    private final List<String> calls;

    private RecordingResolver(String name, List<String> calls) {
      this.name = name;
      this.calls = calls;
    }

    @Override
    public void resolve(Identifier id, Input input) {
      calls.add(name + ".resolve");
    }

    @Override
    public void open(Input input) {
      calls.add(name + ".open");
    }
  }
}

