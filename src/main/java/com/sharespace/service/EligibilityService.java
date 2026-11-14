package com.sharespace.service;

import com.sharespace.interfaces.Eligible;
import com.sharespace.model.Resource;
import com.sharespace.model.Student;

/**
 * Checks if a student is eligible to borrow a specific resource.
 * Implements the Eligible interface (Milestone 1 requirement).
 *
 * Eligibility rules:
 * - Student must be active.
 * - Student must be in year >= 1.
 * - For LAB_EQUIPMENT category: student must be year 2 or above.
 * - For SPECIAL_EQUIPMENT: student must be year 3 or above.
 */
public class EligibilityService implements Eligible {

    @Override
    public boolean isEligible(Student student, Resource resource) {
        if (student == null || resource == null) return false;
        if (!student.isActive()) return false;
        if (!student.isEligibleToBorrow()) return false;
        if (!resource.isActive()) return false;
        if (resource.getAvailableQuantity() <= 0) return false;

        String category = resource.getCategory().toUpperCase();

        switch (category) {
            case "LAB_EQUIPMENT":
            case "LAB EQUIPMENT":
                return student.getYear() >= 2;
            case "SPECIAL_EQUIPMENT":
            case "SPECIAL EQUIPMENT":
                return student.getYear() >= 3;
            default:
                return true;
        }
    }

    @Override
    public String getEligibilityMessage(Student student, Resource resource) {
        if (student == null) return "Invalid student.";
        if (resource == null) return "Invalid resource.";
        if (!student.isActive()) return "Your account is inactive.";
        if (!resource.isActive()) return "Resource is not active.";
        if (resource.getAvailableQuantity() <= 0) return "Resource is currently unavailable (no units available).";

        String category = resource.getCategory().toUpperCase();

        if ((category.contains("LAB_EQUIPMENT") || category.contains("LAB EQUIPMENT"))
                && student.getYear() < 2) {
            return "Lab equipment is only available to 2nd year students and above. You are in year " + student.getYear() + ".";
        }
        if ((category.contains("SPECIAL_EQUIPMENT") || category.contains("SPECIAL EQUIPMENT"))
                && student.getYear() < 3) {
            return "Special equipment is only available to 3rd year students and above. You are in year " + student.getYear() + ".";
        }

        if (isEligible(student, resource)) {
            return "Eligible: You can request this resource.";
        }
        return "Not eligible for this resource.";
    }
}
