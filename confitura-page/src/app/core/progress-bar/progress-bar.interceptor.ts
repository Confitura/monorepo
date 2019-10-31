import {Injectable} from '@angular/core';
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Observable} from 'rxjs';
import {ProgressBarService} from './progress-bar.service';
import {finalize} from 'rxjs/operators';

@Injectable()
export class ProgressBarInterceptor implements HttpInterceptor {
  constructor(private service: ProgressBarService) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    this.service.started();
    return next.handle(req)
      .pipe(finalize(() => {
        this.service.done();
      }));

  }

}
