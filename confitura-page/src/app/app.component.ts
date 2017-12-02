import {Component} from '@angular/core';

@Component({
  selector: 'cf-application',
  templateUrl: './app.component.html'
})
export class AppComponent {

  onDeactivate() {
    document.body.scrollTop = 0;
  }

}
