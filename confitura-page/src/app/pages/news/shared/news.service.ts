import {Injectable} from '@angular/core';
import {News} from './news.model';
import {Observable} from 'rxjs';
import {HttpClient, HttpParams} from '@angular/common/http';
import {map} from 'rxjs/operators';

@Injectable()
export class NewsService {
  constructor(private http: HttpClient) {
  }

  getPage(page: number, size: number): Observable<News[]> {
    const params = new HttpParams()
      .set('page', `${page}`)
      .set('size', `${size}`)
      .set('sort', 'creationDate,desc');

    return this.http.get<EmbeddedNews>(`/news/search/published`, {params})
      .pipe(map(response => response._embedded.news));
  }
}

class EmbeddedNews {
  _embedded: { news: News[] };
}
