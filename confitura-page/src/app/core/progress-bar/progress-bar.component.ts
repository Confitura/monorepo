import {Component} from '@angular/core';
import {ProgressBarService} from './progress-bar.service';

@Component({
  selector: 'cf-progress-bar',
  templateUrl: './progress-bar.component.html',
  styleUrls: ['./progress-bar.component.scss']
})
export class ProgressBarComponent {

  constructor(private service: ProgressBarService) {
  }

  shouldShow() {
    return this.service.shouldShow();
  }

}
