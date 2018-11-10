import {empty as observableEmpty, Observable} from 'rxjs';
import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {VoteStats} from './vote-stats.model';
import {CurrentUser} from '../../../core/security/current-user.service';


@Injectable()
export class VoteStatsServiceService {
  constructor(private http: HttpClient,
              private currentUser: CurrentUser) {
  }

  getAll(): Observable<VoteStats[]> {
    if (this.currentUser.isAdmin()) {
      return this.http.get<VoteStats[]>('/votes/statistics');
    } else {
      return observableEmpty();
    }
  }
}
