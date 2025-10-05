package com.cosmicdoc.common.model;

import java.util.List;

public interface PersonProfile extends PersistableEntity {
    String getEmail();
    String getDisplayName();
    List<String> getTypes();
    boolean hasType(String type);// Can be "USER", "CUSTOMER", "STAFF" etc. to differentiate
}
