import {Injectable} from '@angular/core';
import {Response} from '@angular/http';
import {Page} from './page.model';
import {Observable} from 'rxjs/Observable';
import {CustomHttp} from '../../shared/custom-http.service';

@Injectable()
export class PageService {

  constructor(private http: CustomHttp) {

  }

  get(name: string): Observable<Page> {
    return this.http.get(`/pages/${name}`)
      .map((response: Response) => response.ok ? response.json() as Page : new Page());
  }


}
