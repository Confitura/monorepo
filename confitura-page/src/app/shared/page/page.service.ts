import {Injectable} from '@angular/core';
import {Page} from './page.model';
import {Observable} from 'rxjs/Observable';
import {HttpClient} from '@angular/common/http';

@Injectable()
export class PageService {

  constructor(private http: HttpClient) {

  }

  get(name: string): Observable<Page> {
    return this.http.get<Page>(`/pages/${name}`);
  }

}
