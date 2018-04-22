import {Component} from '@angular/core';
import {Router} from '@angular/router';

@Component({
  templateUrl: './v4p-end.component.html'
})
export class V4pEndComponent {
  constructor(private router: Router) {
    if (localStorage.getItem('v4p-token') == null) {
      this.router.navigate(['/v4p']);
    }
  }


}
