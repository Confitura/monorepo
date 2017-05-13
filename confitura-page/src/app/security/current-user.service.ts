import {Injectable, EventEmitter} from "@angular/core";
import {JwtUser} from "../pages/home/jwt-user.model";
import {Base64} from "js-base64";
import {Router} from "@angular/router";
@Injectable()
export class CurrentUser {

    onLogin: EventEmitter<JwtUser> = new EventEmitter<JwtUser>();

    constructor(private router:Router) {
        if (this.isAvailable()) {
            console.log("user available ", this.get());
            this.onLogin.emit(this.get());
        }
    }

    set(token: string) {
        let user = Base64.decode(token.split(".")[1]);
        sessionStorage.setItem("user", user);
        sessionStorage.setItem("token", token);
        this.onLogin.emit(this.get());
    }

    get(): JwtUser {
        return JSON.parse(sessionStorage.getItem("user")) as JwtUser;
    }

    getToken() {
        return sessionStorage.getItem("token");
    }

    isAvailable() {
        return this.getToken() != null;
    }

    isAdmin() {
        return this.isAvailable() && this.get().isAdmin;
    }


    logout() {
        sessionStorage.clear();
        this.onLogin.emit(this.get());
        this.router.navigate(["/"]);
    }
}