package integration;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;

@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(EnabledIf.Condition.class)
public @interface EnabledIf {
  Class<?> type() default Void.class; // "void" -> current test class

  String value();

  class Condition implements ExecutionCondition {

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
      var annotation = context.getRequiredTestMethod().getDeclaredAnnotation(EnabledIf.class);
      var type =
          annotation.type().equals(Void.class) ? context.getRequiredTestClass() : annotation.type();
      var methodObject = context.getTestInstance().orElse(null);
      try {
        var result = type.getDeclaredMethod(annotation.value()).invoke(methodObject);
        if (result instanceof ConditionEvaluationResult) {
          return (ConditionEvaluationResult) result;
        }
        if (result instanceof Boolean) {
          if ((boolean) result) {
            return ConditionEvaluationResult.enabled("ok");
          }
        }
        return ConditionEvaluationResult.disabled("");
      } catch (ReflectiveOperationException e) {
        return ConditionEvaluationResult.disabled("Error: " + e);
      }
    }
  }
}
