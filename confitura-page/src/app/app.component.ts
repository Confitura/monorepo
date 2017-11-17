import {Component} from '@angular/core';
import './page.component.scss';
import './app.component.scss';
import 'ng-loading-bar/loading-bar.css';

@Component({
  selector: 'cf-application',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']


})
export class AppComponent {
  onDeactivate() {
    document.body.scrollTop = 0;
  }
}
