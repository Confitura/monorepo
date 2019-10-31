import {Component} from '@angular/core';
import {ProgressBarService} from './progress-bar.service';
import {animate, style, transition, trigger} from '@angular/animations';

@Component({
  selector: 'cf-progress-bar',
  templateUrl: './progress-bar.component.html',
  styleUrls: ['./progress-bar.component.scss'],
  animations: [
    trigger('visibleState', [
      transition(':enter', [
        style({
          opacity: 0,
        }),
        animate('300ms ease-in')
      ]),
      transition(':leave', [
        animate('300ms ease-in', style({
          opacity: 0,
        }))
      ])
    ])
  ]
})
export class ProgressBarComponent {

  constructor(private service: ProgressBarService) {
  }

  shouldShow() {
    return this.service.shouldShow();
  }

}
