import {ActivatedRouteSnapshot, CanActivateChild, RouterStateSnapshot} from "@angular/router";
import {CurrentUser} from "../security/current-user.service";
import {Injectable} from "@angular/core";
@Injectable()
export class IsAdminGuard implements CanActivateChild {

    constructor(private currentUser: CurrentUser) {
    }

    canActivateChild(childRoute: ActivatedRouteSnapshot, state: RouterStateSnapshot): Promise<boolean> {
        return new Promise((resolve) => {
            resolve(this.currentUser.isAvailable() && this.currentUser.get().isAdmin)
        });
    }

}