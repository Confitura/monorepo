import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {LoginService} from '../../core/security/login.service';
import {JwtUser} from '../home/jwt-user.model';
import {environment} from '../../../environments/environment';


@Component({
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  user: JwtUser;

  constructor(private route: ActivatedRoute,
              private router: Router,
              private login: LoginService) {
  }

  ngOnInit(): void {
    this.user = JSON.parse(sessionStorage.getItem('user')) as JwtUser;
    const origin = this.route.snapshot.params['origin'];
    const queryParams = this.route.snapshot.queryParams;
    if (origin === 'twitter') {
      this.doLogin(() =>
        this.login.loginWithTwitter(queryParams['oauth_token'], queryParams['oauth_verifier']));
    } else if (origin === 'github') {
      this.doLogin(() => this.login.loginWithGitHub(queryParams['code']));
    } else if (origin === 'facebook') {
      this.doLogin(() => this.login.loginWithFacebook(queryParams['code']));
    } else if (origin === 'google') {
      this.doLogin(() => this.login.loginWithGoogle(queryParams['code']));
    }
  }

  loginLinkTo(service: string): string {
    return `${environment.API_URL}login/${service}`;
  }

  private doLogin(callback: Function) {
    callback()
      .subscribe((user: JwtUser) => {
        this.user = user;
        if (user.isNew) {
          this.router.navigate([`/profile/${user.jti}/edit`]);
        } else {
          this.router.navigate([`/profile/${user.jti}`]);
        }
      });
  }

}
