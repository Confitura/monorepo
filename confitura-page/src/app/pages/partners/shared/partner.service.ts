import {Partner} from './partner.model';
import {Observable} from 'rxjs/Observable';
import {map, reduce, switchMap} from 'rxjs/operators';
import {Injectable} from '@angular/core';
import {CurrentUser} from '../../../core/security/current-user.service';
import {ImageResizer} from '../../../core/ImageResizer.service';
import {HttpClient} from '@angular/common/http';

@Injectable()
export class PartnerService {
  constructor(private http: HttpClient,
              private currentUser: CurrentUser,
              private resizer: ImageResizer) {
  }

  getAll(): Observable<Partner[]> {
    const url = this.currentUser.isAdmin() ? '/partners' : '/partners/search/published';
    return this.http.get<EmbeddedPartners>(url)
      .pipe(
        switchMap((response: EmbeddedPartners) => response._embedded.partners),
        map(partner => ({...partner, logo: this.resizer.applyResizing(partner.logo)})),
        reduce<Partner, Partner[]>((list, partner) => [...list, partner], [])
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
