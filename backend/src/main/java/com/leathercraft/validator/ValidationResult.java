package com.leathercraft.validator;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult {
    private boolean valid;
    private List<String> errors;
    private List<String> warnings;

    public ValidationResult() {
        this.valid = true;
        this.errors = new ArrayList<>();
        this.warnings = new ArrayList<>();
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }

    public void addError(String error) {
        this.valid = false;
        this.errors.add(error);
    }

    public void addWarning(String warning) {
        this.warnings.add(warning);
    }

    public void merge(ValidationResult other) {
        if (other != null) {
            if (!other.isValid()) {
                this.valid = false;
            }
            this.errors.addAll(other.getErrors());
            this.warnings.addAll(other.getWarnings());
        }
    }

    public String getErrorSummary() {
        return String.join("；", errors);
    }

    public String getWarningSummary() {
        return String.join("；", warnings);
    }
}
