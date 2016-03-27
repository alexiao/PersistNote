package com.dv.persistnote.framework.core;

import com.dv.persistnote.business.RootController;
import com.dv.persistnote.business.account.AccountController;

public class ControllerFactory {

    public ControllerFactory() {
    }

    public AbstractController createControllerByID(BaseEnv environment, int controllerID) {
        AbstractController controller = null;
        switch(controllerID) {
            case ControllerID.ROOT_CONTROLLER:
                return new RootController(environment);
            case ControllerID.ACCOUNT_CONTROLLER:
                return new AccountController(environment);
            default:
                break;
        }
        
        return controller;
    }
}
