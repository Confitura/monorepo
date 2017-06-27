import {ActivatedRouteSnapshot, CanActivate, CanActivateChild, RouterStateSnapshot} from "@angular/router";
import {CurrentUser} from "../security/current-user.service";
import {Injectable} from "@angular/core";
@Injectable()
export class IsVolunteerGuard implements CanActivateChild, CanActivate {

    constructor(private currentUser: CurrentUser) {
    }

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Promise<boolean> {
        return new Promise((resolve) => {
            resolve(this.currentUser.isAvailable() && this.currentUser.get().isVolunteer)
        });
    }


    canActivateChild(childRoute: ActivatedRouteSnapshot, state: RouterStateSnapshot): Promise<boolean> {
        return this.canActivate(childRoute, state);
    }

}