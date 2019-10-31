import {ActivatedRouteSnapshot, CanActivate, CanActivateChild, RouterStateSnapshot} from '@angular/router';
import {CurrentUser} from '../core/security/current-user.service';
import {Injectable} from '@angular/core';

@Injectable()
export class IsAdminGuard implements CanActivateChild, CanActivate {

  constructor(private currentUser: CurrentUser) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Promise<boolean> {
    return new Promise((resolve) => {
      resolve(this.currentUser.isAvailable() && this.currentUser.get().isAdmin);
    });
  }


  canActivateChild(childRoute: ActivatedRouteSnapshot, state: RouterStateSnapshot): Promise<boolean> {
    return this.canActivate(childRoute, state);
  }

}
