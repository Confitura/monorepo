import {empty as observableEmpty, Observable} from 'rxjs';
import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Rate} from './rating.model';
import {map, tap} from 'rxjs/operators';

const rates = [
  {no: 1, name: 'TERRIBLE', description: 'Terrible'},
  {no: 2, name: 'BAD', description: 'Bad'},
  {no: 3, name: 'IT_WAS_FINE', description: 'It was fine'},
  {no: 4, name: 'GREAT', description: 'Great'},
  {no: 5, name: 'AWESOME', description: 'Awesome!!!'}
];

@Injectable({
  providedIn: 'root'
})
export class RatingService {
  private rates: { [presentationId: string]: Rate } = {};

  constructor(private client: HttpClient) {
    this.loadRatings();
  }

  save(rate: Rate, presentationId: string) {
    if (this.rates[presentationId]) {
      return this.update(presentationId, rate, this.rates[presentationId].id);
    } else {
      return this.create(presentationId, rate);
    }
  }

  private create(presentationId: string, rate: Rate) {
    return this.client.post<Rate>(`presentations/${presentationId}/ratings`, rate).pipe(
      tap(r => {
        this.add(presentationId, r);
      })
    );
  }

  private update(presentationId: string, rate: Rate, id) {
    return this.client.put<Rate>(`presentations/${presentationId}/ratings/${id}`, rate).pipe(
      tap(r => {
        rate.id = this.rates[presentationId].id;
        this.rates[presentationId] = rate;
        this.saveRatings();
      })
    );
  }

  private add(presentationId: string, rate: Rate) {
    this.rates[presentationId] = rate;
    this.saveRatings();
  }

  private saveRatings() {
    localStorage.setItem('userRatings', JSON.stringify(this.rates));
  }

  private loadRatings() {
    const userRatings = localStorage.getItem('userRatings');
    if (userRatings) {
      this.rates = JSON.parse(userRatings);
    }
  }

  addComment(comment: string, presentationId: string) {
    const rate = this.rates[presentationId];
    console.log('rate', rate);
    if (rate) {
      return this.save(new Rate({value: rate.value, comment: comment}), presentationId);
    } else {
      return observableEmpty();
    }
  }

  getRate(presentationId: string): Rate {
    return this.rates[presentationId];
  }

  getRateMetaByName(value: string) {
    return rates.find(it => it.name === value);
  }

  getRateMetaByIndex(index: number) {
    return rates[index];
  }

  getRateMetaByNumericValue(v) {
    return rates.find(it => it.no === v);
  }

  getRates(presentationId: string): Observable<Rate[]> {
    return this.client.get<EmbeddedRates>(`/presentations/${presentationId}/ratings`)
      .pipe(map(it => it._embedded.rates));
  }
}

class EmbeddedRates {
  _embedded: { rates: Rate[] };
}
