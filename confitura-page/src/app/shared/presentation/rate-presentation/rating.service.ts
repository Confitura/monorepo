import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Rate} from './rating.model';
import {tap} from 'rxjs/operators';
import {Observable} from 'rxjs/Observable';

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
      return Observable.empty();
    }
  }

  getRate(presentationId: string): Rate {
    return this.rates[presentationId];
  }
}
