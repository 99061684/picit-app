package com.pickmin.logic.model;

import java.util.List;

import com.pickmin.config.GlobalConfig;
import com.pickmin.logic.json.JsonHandler;

public class BranchManagement {
    private static List<Branch> branches;

    static {
        branches = JsonHandler.loadBranchesFromJson();
    }

    public static void getBranchCities() {
        
    }

    public static void addBranch(Branch branch) {
        branches.add(branch);
        if (GlobalConfig.SAVE_BRANCHES_AFTER_CREATE) {
            JsonHandler.saveBranchesToJson(branches);
        }
    }

    public static void updateBranch(Branch branch) {
        for (int i = 0; i < branches.size(); i++) {
            if (branches.get(i).getId().equals(branch.getId())) {
                branches.set(i, branch);
                break;
            }
        }
        if (GlobalConfig.SAVE_BRANCHES_AFTER_CHANGE) {
            JsonHandler.saveBranchesToJson(branches);
        }
    }

    public static void removeBranch(Branch branch) {
        branches.remove(branch);
        if (GlobalConfig.SAVE_BRANCHES_AFTER_DELETE) {
            JsonHandler.saveBranchesToJson(branches);
        }
    }

    public static List<Branch> getBranches() {
        return branches;
    }
}
