package com.thaiopensource.resolver;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BasicResolverTest {
  @Test
  void resolveUriResolvesRelativeAgainstBase() throws ResolverException {
    Identifier id = new Identifier("schema.rng", "https://example.com/path/");
    assertEquals("https://example.com/path/schema.rng", BasicResolver.resolveUri(id));
  }

  @Test
  void resolveUriKeepsAbsoluteUri() throws ResolverException {
    Identifier id = new Identifier("https://example.com/schema.rng", "https://ignored.example/");
    assertEquals("https://example.com/schema.rng", BasicResolver.resolveUri(id));
  }

  @Test
  void resolveSetsUriWhenInputUnresolved() throws Exception {
    Input input = new Input();
    Identifier id = new Identifier("schema.rng", "https://example.com/path/");

    BasicResolver.getInstance().resolve(id, input);

    assertEquals("https://example.com/path/schema.rng", input.getUri());
  }

  @Test
  void openRejectsRelativeUri() {
    Input input = new Input();
    input.setUri("relative/path.rng");

    assertThrows(ResolverException.class, () -> BasicResolver.getInstance().open(input));
  }
}

