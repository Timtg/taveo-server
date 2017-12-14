package no.timesaver.domain.mailTemplate;

import org.springframework.util.StringUtils;

public class ResetPwConfirmationModel extends AbstractTemplateModel {

    private final String name;

    public ResetPwConfirmationModel(String name) {
        if(StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Empty params given when constructing template model");
        }
        this.name = name;

    }

    public String getName() {
        return name;
    }
}
