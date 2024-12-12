package com.secinfostore.controller;

import com.secinfostore.controller.interfaces.WindowMediator;

public abstract class BaseController {
    protected WindowMediator mediator;

    public void setMediator(WindowMediator mediator){
        this.mediator = mediator;
    }

    public abstract void setupSelectedController(Object data);
}
