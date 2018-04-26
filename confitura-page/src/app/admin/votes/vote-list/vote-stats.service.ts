import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {HttpClient} from '@angular/common/http';
import {VoteStats} from './vote-stats.model';

@Injectable()
export class VoteStatsServiceService {
    constructor(private http: HttpClient) {
    }

    getAll(): Observable<VoteStats[]> {
        return this.http.get<VoteStats[]>('/votes/statistics');
    }
}
