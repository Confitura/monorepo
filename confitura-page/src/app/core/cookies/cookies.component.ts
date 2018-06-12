import {Component} from '@angular/core';
import {Cookie} from 'ng2-cookies/ng2-cookies';

@Component({
  selector: 'cf-cookies',
  templateUrl: './cookies.component.html',
  styleUrls: ['./cookies.component.scss'],
})
export class CookiesComponent {
  private COOKIES_NAME = 'cookies-accepted-gdpr';
  isNotAccepted() {
    return Cookie.get(this.COOKIES_NAME) !== 'true' || false;
  }

  accept() {
    Cookie.set(this.COOKIES_NAME, 'true');

  }

}
