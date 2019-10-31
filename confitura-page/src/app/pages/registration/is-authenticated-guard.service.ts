import {ActivatedRouteSnapshot, CanActivate, CanActivateChild, Router, RouterStateSnapshot} from '@angular/router';
import {Injectable} from '@angular/core';
import {CurrentUser} from '../../core/security/current-user.service';

@Injectable()
export class IsAuthenticatedGuard implements CanActivateChild, CanActivate {

  constructor(private currentUser: CurrentUser, private router: Router) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    const authenticated = this.currentUser.isAvailable();
    if (!authenticated) {
      this.router.navigate(['login'], {queryParams: {returnUrl: state.url}});
    }
    return authenticated;
  }

  canActivateChild(childRoute: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    return this.canActivate(childRoute, state);
  }

}
