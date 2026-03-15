package com.vp.core.domain.valueObjects;


import com.vp.core.domain.validation.DomainError;
import com.vp.core.domain.validation.ValidationHandler;
import com.vp.core.domain.validation.Validator;

import java.util.regex.Pattern;

public class URLValidator extends Validator {

    private final URL url;

    protected URLValidator(URL url, ValidationHandler aHandler) {
        super(aHandler);
        this.url = url;
    }

    @Override
    public void validate() {
        Pattern pattern = Pattern.compile("^https?:\\/\\/(?:www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b(?:[-a-zA-Z0-9()@:%_\\+.~#?&\\/=]*)$");
        if(!pattern.matcher(url.getValue()).find()){
            validationHandler().append(new DomainError("'url' " + url.getValue() + " is invalid"));
        }
    }
}
