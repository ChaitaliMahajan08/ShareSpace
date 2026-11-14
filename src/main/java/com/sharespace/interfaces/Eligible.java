package com.sharespace.interfaces;

import com.sharespace.model.Student;
import com.sharespace.model.Resource;

public interface Eligible {
    boolean isEligible(Student student, Resource resource);
    String getEligibilityMessage(Student student, Resource resource);
}
