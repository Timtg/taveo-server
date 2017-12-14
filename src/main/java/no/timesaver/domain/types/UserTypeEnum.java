package no.timesaver.domain.types;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum UserTypeEnum {
    A("admin"),N("normal"),M("moderator"),P("Picker");

    private final String description;

    static Map<String,UserTypeEnum> descriptionToEnum = Arrays.stream(UserTypeEnum.values()).collect(Collectors.toMap(UserTypeEnum::toString, Function.identity()));


    UserTypeEnum(String typeDescription) {
        this.description=typeDescription;
    }

    public static UserTypeEnum ofDescription(String description) {
        return descriptionToEnum.get(description);
    }

    public static boolean isValid(String description){
        return descriptionToEnum.containsKey(description);
    }

    @Override
    public String toString() {
        return description;
    }
}
