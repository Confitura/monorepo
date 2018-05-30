import {Component} from '@angular/core';
import {ActivatedRoute} from '@angular/router';

@Component({
  templateUrl: './registration-info.component.html',
  styleUrls: ['./registration-info.component.scss']

})
export class RegistrationInfoComponent {
  name: string;

  constructor(route: ActivatedRoute) {
    this.name = route.snapshot.params['name'] || 'registration-info';
  }
}
