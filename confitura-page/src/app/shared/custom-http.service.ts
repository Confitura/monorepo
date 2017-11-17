import {Headers, Http, RequestOptionsArgs, Response} from '@angular/http';
import {Injectable} from '@angular/core';
import {HttpConfiguration} from './http-configuration.service';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/observable/of';
import 'rxjs/add/operator/catch';

import {CurrentUser} from '../security/current-user.service';

@Injectable()
export class CustomHttp {
  constructor(private config: HttpConfiguration,
              private currentUser: CurrentUser,
              private http: Http) {
  }

  get(url: string, options: RequestOptionsArgs = {}): Observable<Response> {
    return this.doExecute(() => this.http.get(this.toFullUrl(url), options), options);
  }


  post(url: string, body: any, options: RequestOptionsArgs = {}): Observable<Response> {
    return this.doExecute(() => this.http.post(this.toFullUrl(url), body, options), options);
  }

  remove(url: string, options: RequestOptionsArgs = {}): Observable<Response> {
    return this.doExecute(() => this.http.delete(this.toFullUrl(url), options), options);
  }

  private doExecute(call: Function, options: RequestOptionsArgs) {
    this.addAuthorizationHeader(options);
    const result: Observable<Response> = call();
    return result.catch((error) => {
      if (error.status === 401) {
        this.currentUser.logout();
      }
      return Observable.of(error);
    });
  }


  private addAuthorizationHeader(options: RequestOptionsArgs) {
    if (this.currentUser.isAvailable()) {
      if (options.headers === null) {
        options.headers = new Headers();
      }
      options.headers.set('Authorization', `Bearer ${this.currentUser.getToken()}`);
    }
  }

  private toFullUrl(url: string) {
    return `${this.config.apiServer}${url}`;
  }


}
