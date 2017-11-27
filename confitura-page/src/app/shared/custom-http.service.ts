import {Response} from '@angular/http';
import {Injectable} from '@angular/core';
import {HttpConfiguration} from './http-configuration.service';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/observable/of';
import 'rxjs/add/operator/catch';
import {HttpClient} from '@angular/common/http';

@Injectable()
export class CustomHttp {
  constructor(private httpClient: HttpClient,
              private config: HttpConfiguration) {
  }

  get(url: string): Observable<Response> {
    return this.httpClient.get(this.toFullUrl(url));
  }


  post(url: string, body: any): Observable<Response> {
    return this.httpClient.post(this.toFullUrl(url), body);
  }

  remove(url: string): Observable<Response> {
    return this.httpClient.delete(this.toFullUrl(url));
  }


  private toFullUrl(url: string) {
    return `${this.config.apiServer}${url}`;
  }


}
