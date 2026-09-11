package com.thaiopensource.resolver.load;

import com.thaiopensource.resolver.Identifier;
import com.thaiopensource.resolver.Input;
import com.thaiopensource.resolver.Resolver;
import com.thaiopensource.resolver.ResolverException;
import org.junit.jupiter.api.Test;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ResolverLoaderTest {
  @Test
  void loadsResolverImplementationDirectly() throws Exception {
    Resolver resolver = ResolverLoader.loadResolver(DirectResolver.class.getName(), null);
    assertNotNull(resolver);
    assertDoesNotThrow(() -> resolver.resolve(new Identifier("doc.xml"), new Input()));
  }

  @Test
  void wrapsUriResolverImplementation() throws Exception {
    Resolver resolver = ResolverLoader.loadResolver(TestUriResolver.class.getName(), null);
    assertNotNull(resolver);
    assertDoesNotThrow(() -> resolver.resolve(new Identifier("doc.xml"), new Input()));
  }

  @Test
  void wrapsEntityResolverImplementation() throws Exception {
    Resolver resolver = ResolverLoader.loadResolver(TestEntityResolver.class.getName(), null);
    assertNotNull(resolver);
    assertDoesNotThrow(() -> resolver.resolve(new Identifier("doc.xml"), new Input()));
  }

  @Test
  void combinesUriAndEntityResolvers() throws Exception {
    Resolver resolver = ResolverLoader.loadResolver(TestUriAndEntityResolver.class.getName(), null);
    assertNotNull(resolver);
    assertDoesNotThrow(() -> resolver.resolve(new Identifier("doc.xml"), new Input()));
  }

  @Test
  void rejectsUnsupportedType() {
    ResolverLoadException ex = assertThrows(
        ResolverLoadException.class,
        () -> ResolverLoader.loadResolver(UnsupportedType.class.getName(), null)
    );
    assertNotNull(ex.getMessage());
  }

  @Test
  void wrapsClassNotFoundAsLoadException() {
    assertThrows(ResolverLoadException.class, () -> ResolverLoader.loadResolver("missing.Type", null));
  }

  public static class DirectResolver implements Resolver {
    @Override
    public void resolve(Identifier id, Input input) {
      input.setUri("memory:resolved");
    }

    @Override
    public void open(Input input) {
      // no-op
    }
  }

  public static class TestUriResolver implements URIResolver {
    @Override
    public Source resolve(String href, String base) {
      return null;
    }
  }

  public static class TestEntityResolver implements EntityResolver {
    @Override
    public InputSource resolveEntity(String publicId, String systemId) {
      return null;
    }
  }

  public static class TestUriAndEntityResolver implements URIResolver, EntityResolver {
    @Override
    public Source resolve(String href, String base) throws TransformerException {
      return null;
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws IOException {
      return null;
    }
  }

  public static class UnsupportedType {
  }
}

