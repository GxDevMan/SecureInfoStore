package com.secinfostore.secureinfostore.controller;

import com.secinfostore.secureinfostore.controller.interfaces.WindowMediator;

public abstract class BaseController {
    protected WindowMediator mediator;

    public void setMediator(WindowMediator mediator){
        this.mediator = mediator;
    }

    public abstract void setupSelectedController(Object data);
}
