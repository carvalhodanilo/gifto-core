package com.vp.core.domain.valueObjects;

import com.vp.core.domain.validation.DomainError;
import com.vp.core.domain.validation.ValidationHandler;
import com.vp.core.domain.validation.Validator;

import java.util.regex.Pattern;

public class EmailValidator extends Validator {

    private final Email email;

    protected EmailValidator(Email email, ValidationHandler aHandler) {
        super(aHandler);
        this.email = email;
    }

    @Override
    public void validate() {
        Pattern pattern = Pattern.compile("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
        if(!pattern.matcher(email.getValue()).find()){
            validationHandler().append(new DomainError("'e-mail' is not invalid"));
        }
    }
}
