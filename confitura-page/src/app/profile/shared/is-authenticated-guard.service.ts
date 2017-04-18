import {Injectable} from "@angular/core";
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from "@angular/router";
import {CurrentUser} from "../../security/current-user.service";
@Injectable()
export class IsAuthenticatedGuard implements CanActivate {
    constructor(private currentUser: CurrentUser, private router: Router) {
    }

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Promise<boolean> {
        return new Promise((resolve) => {
            let isAvailable = this.currentUser.isAvailable();
            if (!isAvailable) {
                this.router.navigate(["/login"]);
            }
            resolve(isAvailable)
        });
    }


}