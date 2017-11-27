import {Injectable} from '@angular/core';
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/operator/catch';
import {CurrentUser} from './current-user.service';

@Injectable()
export class TokenInterceptor implements HttpInterceptor {
  constructor(private currentUser: CurrentUser) {

  }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    this.addAuthorizationHeader(request);
    return next.handle(request)
      .catch((error: any, caught: Observable<any>) => {
        console.log(caught);
        if (error.status === 401) {
          this.currentUser.logout();
        }
        return Observable.of(error);
      });
  }

  private addAuthorizationHeader(req: HttpRequest<any>) {
    if (this.currentUser.isAvailable()) {
      req.clone({
        setHeaders: {Authorization: `Bearer ${this.currentUser.getToken()}`}
      });
    }
  }

}
