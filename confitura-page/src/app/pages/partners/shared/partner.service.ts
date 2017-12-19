import {Partner} from './partner.model';
import {Observable} from 'rxjs/Observable';
import {map} from 'rxjs/operators';
import {Injectable} from '@angular/core';
import {CurrentUser} from '../../../core/security/current-user.service';
import {HttpClient} from '@angular/common/http';

@Injectable()
export class PartnerService {
  constructor(private http: HttpClient,
              private currentUser: CurrentUser) {
  }

  getAll(): Observable<Partner[]> {
    const url = this.currentUser.isAdmin() ? '/partners' : '/partners/search/published';
    return this.http.get<EmbeddedPartners>(url)
      .pipe(
        map((response: EmbeddedPartners) => response._embedded.partners)
      );
  }

  getBy(id: string): Observable<Partner> {
    return this.http.get<Partner>(`/partners/${id}`);
  }

  save(partner: Partner) {
    return this.http.post<Partner>('/partners', partner);
  }

  delete(partner: Partner) {
    return this.http.delete(`/partners/${partner.id}`);
  }
}

class EmbeddedPartners {
  _embedded: { partners: Partner[] };
}
