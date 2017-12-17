import {Component} from '@angular/core';
import {NavigationEnd, Router} from '@angular/router';

@Component({
  selector: 'cf-application',
  templateUrl: './app.component.html'
})
export class AppComponent {


  constructor(private router: Router) {
  }

  onDeactivate() {
    this.router.events.subscribe((event) => {
      if (event instanceof NavigationEnd) {
        window.scrollTo(0, 0);
      }
    });
  }

}
