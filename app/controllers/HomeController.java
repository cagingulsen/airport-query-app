package controllers;

import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;

import views.html.*;

/**
 * Home page
 */
public class HomeController extends Controller {

    @Inject
    public HomeController() {}

    public Result index() {
        return ok(index.render("Welcome!"));
    }
}
            
