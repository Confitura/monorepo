import {ActivatedRouteSnapshot, CanActivate, CanActivateChild, RouterStateSnapshot} from '@angular/router';
import {CurrentUser} from '../core/security/current-user.service';
import {Injectable} from '@angular/core';

@Injectable()
export class IsPrivilegedGuard implements CanActivateChild, CanActivate {

  constructor(private currentUser: CurrentUser) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Promise<boolean> {
    return new Promise((resolve) => {
      resolve(this.currentUser.isAvailable() && this.currentUser.isPrivileged());
    });
  }


  canActivateChild(childRoute: ActivatedRouteSnapshot, state: RouterStateSnapshot): Promise<boolean> {
    return this.canActivate(childRoute, state);
  }

}
