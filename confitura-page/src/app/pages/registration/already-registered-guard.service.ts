import {ActivatedRouteSnapshot, CanActivate, CanActivateChild, Router, RouterStateSnapshot} from '@angular/router';
import {Injectable} from '@angular/core';
import {CurrentUser} from '../../core/security/current-user.service';
import {Observable} from 'rxjs/Observable';
import {ParticipantService} from '../../admin/participants/participant.service';
import {catchError, map} from 'rxjs/operators';
import {of as observableOf} from 'rxjs/observable/of';

@Injectable()
export class AlreadyRegisteredGuard implements CanActivateChild, CanActivate {

  constructor(private currentUser: CurrentUser, private router: Router, private service: ParticipantService) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    return this.service
      .getByUser(this.currentUser.get().jti)
      .pipe(
        map(it => {
          this.router.navigate(['/registration/form/' + it.id, route.params]);
          return false;
        }),
        catchError(it => observableOf(true)))
    ;
  }

  canActivateChild(childRoute: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    return this.canActivate(childRoute, state);
  }

}
