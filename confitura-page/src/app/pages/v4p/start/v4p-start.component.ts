import {Component} from '@angular/core';
import {Router} from '@angular/router';
import * as jsSHA from 'jssha';

@Component({
  templateUrl: './v4p-start.component.html',
  styleUrls: ['./v4p-start.component.scss']

})
export class V4pStartComponent {
  constructor(private router: Router) {
    if (localStorage.getItem('v4p-token')) {
      this.router.navigate(['/v4p/voting']);
    }
  }

  start() {
    this.generateToken();
    this.router.navigate(['/v4p/voting']);
  }

  private generateToken() {
    const sha = new jsSHA('SHA-256', 'TEXT');
    sha.update(`${new Date().getMilliseconds()}${Math.random()}`);
    const token = sha.getHash('HEX');
    localStorage.setItem('v4p-token', token);
  }
}
