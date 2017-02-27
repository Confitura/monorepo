import {Injectable, EventEmitter} from "@angular/core";
import {JwtUser} from "../pages/home/jwt-user.model";
import {Base64} from "js-base64";
@Injectable()
export class CurrentUser {

    onLogin: EventEmitter<JwtUser> = new EventEmitter<JwtUser>();

    constructor() {
        if (this.isAvailable()) {
            console.log("user avrailable ", this.get());
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


    logout() {
        sessionStorage.clear();
        this.onLogin.emit(this.get());
    }
}