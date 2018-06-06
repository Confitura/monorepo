import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {Voucher} from './voucher.model';
import {map} from 'rxjs/operators';
import {Participant} from '../participants/participant.model';

@Injectable({
  providedIn: 'root'
})
export class VouchersService {

  constructor(private http: HttpClient) {
  }

  getVouchers(): Observable<Voucher[]> {
    return this.http.get<EmbeddedVouchers>('/vouchers')
      .pipe(map(response => response._embedded.vouchers));
  }
}

class EmbeddedVouchers {
  _embedded: { vouchers: Voucher[] };
}
