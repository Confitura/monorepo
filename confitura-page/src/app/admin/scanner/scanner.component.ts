import {Component} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {ParticipantService} from '../participants/participant.service';
import {Participant} from '../participants/participant.model';
import {Observable} from 'rxjs/Observable';
import {catchError} from 'rxjs/operators';

@Component({
  templateUrl: './scanner.component.html',
  styleUrls: ['./scanner.component.scss']
})
export class ScannerComponent {
  model: Participant;
  error: string;

  constructor(route: ActivatedRoute, service: ParticipantService) {
    const id = route.snapshot.params['id'];
    if (id) {
      service.arrived(id)
        .pipe(
          catchError(error => {
            this.error = 'Invalid token!';
            return Observable.throw(error);
          })
        )
        .subscribe((response: { status: number, json: Participant }) => {
          this.model = response.json;
          if (response.status === 409) {
            this.error = 'User already registered!';
          }
        });
    }
  }
}
