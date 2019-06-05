package integration;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.function.Predicate;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;

@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(EnabledIf.Condition.class)
public @interface EnabledIf {
  Class<? extends Predicate<ExtensionContext>> value();

  class Condition implements ExecutionCondition {

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext extensionContext) {
      try {
        return extensionContext
                .getRequiredTestMethod()
                .getDeclaredAnnotation(EnabledIf.class)
                .value()
                .getConstructor()
                .newInstance()
                .test(extensionContext)
            ? ConditionEvaluationResult.enabled("")
            : ConditionEvaluationResult.disabled("");
      } catch (ReflectiveOperationException e) {
        return ConditionEvaluationResult.disabled("Error: " + e);
      }
    }
  }
}
