import {Injectable} from '@angular/core';
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {catchError} from 'rxjs/operators';
import {CurrentUser} from './current-user.service';

@Injectable()
export class TokenInterceptor implements HttpInterceptor {
  constructor(private currentUser: CurrentUser) {

  }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const clone = this.addAuthorizationHeader(request);
    return next.handle(clone)
      .pipe(
        catchError<HttpEvent<any>, HttpEvent<any>>((error: any) => {
          if (error.status === 401) {
            this.currentUser.logout();
          }
          throw error;
        }));
  }

  private addAuthorizationHeader(req: HttpRequest<any>) {
    if (this.currentUser.isAvailable()) {
      return req.clone({
        setHeaders: {Authorization: `Bearer ${this.currentUser.getToken()}`}
      });
    }
    return req;
  }

}
