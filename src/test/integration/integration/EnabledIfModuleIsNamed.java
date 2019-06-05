package integration;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

public class EnabledIfModuleIsNamed implements ExecutionCondition {
  @Override
  public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext extensionContext) {
    var testClass = extensionContext.getRequiredTestClass();
    var module = testClass.getModule();
    return module.isNamed()
        ? ConditionEvaluationResult.enabled(module.toString())
        : ConditionEvaluationResult.disabled(testClass + " resides in " + module);
  }
}
