import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import * as jsSHA from 'jssha';


const TOKEN_NAME = 'like-token';

@Injectable({
  providedIn: 'root'
})
export class LikeService {

    private cache: Like[];
    private token: string;

    constructor(private http: HttpClient) {
    }

    getLikes(): Observable<Like[]> {
        const token: string = this.getToken();
        return this.http.get('/likes', {params: {token: token}})
            .pipe(map(it => <Like[]> it));
    }

    like(presentationId: string) {
        const token: string = this.getToken();
        return this.http.post(`/presentations/${presentationId}/likes`, {token: token});
    }

    unlike(likeId: string) {
        return this.http.delete(`/likes/${likeId}`);
    }

    private getToken() {
        if (!this.token) {
            return this.loadToken();
        } else {
            return this.token;
        }
    }

    private loadToken() {
        let token = localStorage.getItem(TOKEN_NAME);
        if (token == null) {
            token = this.generateToken();
            localStorage.setItem(TOKEN_NAME, token);
        }
        return token;
    }


    private generateToken() {
        const sha = new jsSHA('SHA-256', 'TEXT');
        sha.update(`${new Date().getMilliseconds()}${Math.random()}`);
        const token = sha.getHash('HEX');
        return token;
    }

    getSummary() {
        return this.http.get('/likes/summary');
    }
}

export interface Like {
    id: string;
    presentationId: string;
}
