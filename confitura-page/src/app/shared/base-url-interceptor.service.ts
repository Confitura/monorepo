import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {environment} from '../../environments/environment';
import {Injectable} from '@angular/core';

@Injectable()
export class BaseUrlInterceptor implements HttpInterceptor {

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const url = (environment.API_URL + req.url).replace(/([^:]\/)\/+/g, '$1');
    const clone = req.clone({url: url});
    return next.handle(clone);
  }

}
