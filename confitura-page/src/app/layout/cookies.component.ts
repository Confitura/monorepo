import {Component} from '@angular/core';
import {Cookie} from 'ng2-cookies/ng2-cookies';

@Component({
  selector: 'cf-cookies',
  templateUrl: './cookies.component.html',
  styleUrls: ['./cookies.component.scss'],
})
export class CookiesComponent {

  isNotAccepted() {
    return Cookie.get('cookies-accepted') !== 'true' || false;
  }

  accept() {
    Cookie.set('cookies-accepted', 'true');

  }

}
