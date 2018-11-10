import {Component} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {ParticipantService} from '../participants/participant.service';
import {Participant} from '../participants/participant.model';
import {throwError} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {of} from 'rxjs/internal/observable/of';

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
            if (error.status === 409) {
              return of({status: 409, json: error.error});
            }
            this.error = 'Invalid token!';
            return throwError(error);
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
