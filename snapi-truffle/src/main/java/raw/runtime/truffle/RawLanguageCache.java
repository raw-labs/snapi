package raw.runtime.truffle;

import com.typesafe.config.ConfigFactory;
import raw.compiler.base.CompilerContext;
import raw.creds.api.CredentialsService;
import raw.creds.api.CredentialsServiceProvider;
import raw.inferrer.api.InferrerService;
import raw.inferrer.api.InferrerServiceProvider;
import raw.sources.api.SourceContext;
import raw.utils.AuthenticatedUser;
import raw.utils.RawSettings;
import scala.Some;

import java.util.concurrent.ConcurrentHashMap;

public class RawLanguageCache {

    private final ClassLoader classLoader = RawLanguage.class.getClassLoader();

    public final RawSettings rawSettings =
            new RawSettings(ConfigFactory.load(), ConfigFactory.empty());
    public final CredentialsService credentialsService =
            CredentialsServiceProvider.apply(classLoader, rawSettings);

    private final ConcurrentHashMap<AuthenticatedUser, Value> map = new ConcurrentHashMap<>();

    private static class Value {
        private final CompilerContext compilerContext;
        private final SourceContext sourceContext;
        private final InferrerService inferrer;

        Value(CompilerContext compilerContext, SourceContext sourceContext, InferrerService inferrer) {
            this.compilerContext = compilerContext;
            this.sourceContext = sourceContext;
            this.inferrer = inferrer;
        }

        public CompilerContext getCompilerContext() {
            return compilerContext;
        }

        public SourceContext getSourceContext() {
            return sourceContext;
        }

        public InferrerService getInferrer() {
            return inferrer;
        }
    }

//    private static class Key {
//        private final String str1;
//        private final String str2;
//        private final Scopes scope;
//
//        Key(String str1, String str2, Scopes scope) {
//            this.str1 = str1;
//            this.str2 = str2;
//            this.scope = scope;
//        }
//
//        @Override
//        public boolean equals(Object o) {
//            if (this == o) return true;
//            if (o == null || getClass() != o.getClass()) return false;
//            Key key = (Key) o;
//            return Objects.equals(str1, key.str1) &&
//                    Objects.equals(str2, key.str2) &&
//                    Objects.equals(scope, key.scope);
//        }
//
//        @Override
//        public int hashCode() {
//            return Objects.hash(str1, str2, scope);
//        }
//    }

    private Value get(AuthenticatedUser user) {
      return  map.computeIfAbsent(user, k -> {
            SourceContext sourceContext = new SourceContext(user, credentialsService, rawSettings, new Some<>(classLoader));
            InferrerService inferrer = InferrerServiceProvider.apply(classLoader, sourceContext);
            CompilerContext compilerContext = new CompilerContext("rql2-truffle", user, inferrer, sourceContext, new Some<>(classLoader), rawSettings);
            return new Value(compilerContext, sourceContext, inferrer);
        });
    }

    public SourceContext getSourceContext(AuthenticatedUser user) {
        return get(user).getSourceContext();
    }

    public CompilerContext getCompilerContext(AuthenticatedUser user) {
        return get(user).getCompilerContext();
    }

    public InferrerService getInferrer(AuthenticatedUser user) {
        return get(user).getInferrer();
    }

    public void reset() {
        // Close all inferrer services and credential services.
        map.values().forEach(v -> {
            v.getInferrer().stop();
            v.getSourceContext().credentialsService().stop();
        });
        map.clear();
    }

}
