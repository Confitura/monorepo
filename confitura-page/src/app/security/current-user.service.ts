import {Injectable, EventEmitter, OnInit} from "@angular/core";
import {User} from "../pages/home/user.model";
@Injectable()
export class CurrentUser {

    onLogin:EventEmitter<User> = new EventEmitter<User>();

    constructor(){
        if(this.isAvailable()){
            console.log("user avrailable ", this.get());
            this.onLogin.emit(this.get());
        }
    }

    set(token: string) {
        let user = atob(token.split(".")[1]);
        sessionStorage.setItem("user", user);
        console.log("setting usr", user);
        sessionStorage.setItem("token", token);
        this.onLogin.emit(this.get());
    }

    get(): User {
        return JSON.parse(sessionStorage.getItem("user")) as User;
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