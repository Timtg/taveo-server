package no.timesaver.domain;

public interface UserProperty {

    long getAssociatedUserId();
    String getEntityType();
    boolean moderatorOrPickerHasAccess(User currentUser);
}
