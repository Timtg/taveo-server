package no.timesaver.tools;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
public class ObjectValidator {
    public boolean isValid(List<Object> objs) {
        if(objs == null){
            return false;
        }
        for (Object obj : objs) {
            if(obj == null){
                return false;
            }
            if(obj instanceof String){
                return StringUtils.isEmpty(obj);
            }
            try {
                if(Long.valueOf(obj.toString()) == 0){
                    return false;
                }
            } catch (NumberFormatException e) {
                return true;
            }
        }
        return true;
    }
}
