package com.vp.core.domain.valueObjects;


import com.vp.core.domain.validation.ValidationHandler;
import com.vp.core.domain.validation.Validator;

public class DocumentValidator extends Validator {

    private final Document document;

    protected DocumentValidator(Document document, ValidationHandler aHandler) {
        super(aHandler);
        this.document = document;
    }

    @Override
    public void validate() {
    }
}
