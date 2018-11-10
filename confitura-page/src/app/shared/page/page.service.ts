import {Injectable} from '@angular/core';
import {Page} from './page.model';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';

@Injectable()
export class PageService {

  constructor(private http: HttpClient) {

  }

  get(name: string): Observable<Page> {
    return this.http.get<Page>(`/pages/${name}`);
  }

  save(page: Page): Observable<Page> {
    return this.http.post<Page>(`/pages`, page);
  }

}
