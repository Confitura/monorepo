import {Injectable} from '@angular/core';
import {CurrentUser} from './current-user.service';
import {Observable} from 'rxjs/Observable';
import {map} from 'rxjs/operators';
import {JwtUser} from '../../pages/home/jwt-user.model';
import {HttpClient, HttpParams} from '@angular/common/http';

@Injectable()
export class LoginService {

  constructor(private http: HttpClient,
              private currentUser: CurrentUser) {
  }

  loginWithTwitter(token: string, verifier: string): Observable<JwtUser> {
    return this.doLogin('twitter', new HttpParams().set('oauth_token', token).set('oauth_verifier', verifier));
  }

  loginWithGitHub(code: string): Observable<JwtUser> {
    return this.doLogin('github', new HttpParams({fromString: `code=${code}`}));
  }

  loginWithFacebook(code: string): Observable<JwtUser> {
    return this.doLogin('facebook', new HttpParams({fromString: `code=${code}`}));
  }

  loginWithGoogle(code: string): Observable<JwtUser> {
    return this.doLogin('google', new HttpParams({fromString: `code=${code}`}));
  }


  logout() {
    this.currentUser.logout();
  }

  private doLogin(system: string, params: HttpParams) {
    return this.http.get(`/login/${system}/callback`, {params, responseType: 'text'})
      .pipe(
        map(token => {
          this.currentUser.set(token);
          return this.currentUser.get();
        })
      );

  }
}
