import {Component, ViewChild} from '@angular/core';
import {Participant} from '../../../admin/participants/participant.model';
import {ParticipantService} from '../../../admin/participants/participant.service';
import {ActivatedRoute, Router} from '@angular/router';
import {FormControl} from '@angular/forms';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/operator/catch';

@Component({templateUrl: './registration-form.component.html'})
export class RegistrationFormComponent {
  submitted = false;
  error: string;
  model: Participant;
  @ViewChild('registrationForm') form: FormControl;


  constructor(private service: ParticipantService, private route: ActivatedRoute, private router: Router) {
    const id = this.route.snapshot.params['id'];
    this.service.getOne(id)
      .catch(error => {
        this.error = 'Something went wrong or your token is incorrect. Please try again or contact us at confitura@confitura.pl';
        return Observable.throw(error);
      })
      .subscribe(
        participant => this.model = participant
      )
    ;
  }

  save() {
    this.submitted = true;
    if (this.form.valid) {
      this.service.save(this.model)
        .catch(error => {
          this.error = 'Something went wrong. Please try again or contact us at confitura@confitura.pl';
          return Observable.throw(error);
        })
        .subscribe(
          () => this.router.navigate(['/registration/finish']))
      ;
    }
  }
}
